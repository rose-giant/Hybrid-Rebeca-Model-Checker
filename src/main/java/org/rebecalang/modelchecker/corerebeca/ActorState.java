package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modelchecker.corerebeca.utils.RILUtils;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AbstractCallingInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class ActorState extends BaseActorState {
	private LinkedList<MessageSpecification> queue;

	public ActorState() {
		initializeQueue();
	}

	public void initializeQueue() {
		setQueue(new LinkedList<>());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorState other = (ActorState) obj;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		return true;
	}


	public MessageSpecification getMessage(boolean isPeek) {
		return queue.peek() != null ? (isPeek ? queue.peek() : queue.poll()) : null;
	}

	public LinkedList<MessageSpecification> getQueue() {
		return queue;
	}

	public void setQueue(LinkedList<MessageSpecification> queue) {
		this.queue = queue;
	}

	@Override
	public void addToQueue(MessageSpecification msgSpec) {
		queue.add(msgSpec);
	}

	@Override
	public boolean actorQueueIsEmpty() {
		return queue.isEmpty();
	}

	private String getMessageName(String messageName, RILModel transformedRILModel) {
		String msgName = messageName;
		Type currentType = null;
		try {
			currentType = typeSystem.getType(messageName.split("\\.")[0]);
		} catch (CodeCompilationException e) {
			e.printStackTrace();
		}

		while (!transformedRILModel.getMethodNames().contains(msgName)) {
			try {
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) typeSystem.getMetaData(currentType);
				if (rcd.getExtends() == null)
					break;
				currentType = rcd.getExtends();
				msgName = currentType.getTypeName() + "." + messageName.split("\\.")[1];
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
		}
		return msgName;
	}
	
	public void execute(State<? extends ActorState> systemState,
			StatementInterpreterContainer statementInterpreterContainer,
			RILModel transformedRILModel, AbstractPolicy policy) {

		startExecutionOfNewMessageServer(transformedRILModel, policy);
		do {
			ProgramCounter pc = getPC();
			int lineNumber = pc.getLineNumber();
			String methodName = getPC().getMethodName();
			
			ArrayList<InstructionBean> instructionsList = 
					transformedRILModel.getInstructionList(methodName);

			InstructionBean instruction = instructionsList.get(lineNumber);
			InstructionInterpreter interpreter = statementInterpreterContainer.retrieveInterpreter(instruction);
			policy.executedInstruction(instruction);
			
			if(instruction instanceof MsgsrvCallInstructionBean) {
				MsgsrvCallInstructionBean mcib = (MsgsrvCallInstructionBean) instruction;
				String newMethodName = resolveDynamicBindingOfMethodCall(transformedRILModel, mcib);
				if(!mcib.getMethodName().equals(newMethodName)) {
					instruction = new MsgsrvCallInstructionBean(
							mcib.getBase(), newMethodName);
					((MsgsrvCallInstructionBean)instruction).setParameters(
							mcib.getParameters());
				}
			} else if(instruction instanceof MethodCallInstructionBean) {
				MethodCallInstructionBean mcib = (MethodCallInstructionBean) instruction;
				String newMethodName = resolveDynamicBindingOfMethodCall(transformedRILModel, mcib);
				if(!mcib.getMethodName().equals(newMethodName)) {
					instruction = new MethodCallInstructionBean(
							mcib.getBase(), newMethodName, mcib.getParameters(), mcib.getFunctionCallResult());
					((MethodCallInstructionBean)instruction).setParameters(
							mcib.getParameters());
				}
			}
			interpreter.interpret(instruction, this, systemState);
		} while (!policy.isBreakable());
	}
	
	private String resolveDynamicBindingOfMethodCall(
			RILModel transformedRILModel, 
			AbstractCallingInstructionBean instruction) {
		ActorState callerActor = 
				(ActorState) actorScopeStack.retrieveVariableValue(
						instruction.getBase().getVarName());
		String methodName = 
				rewriteMethodNameType(callerActor.getTypeName(), 
						instruction.getMethodName());
		try {
			Type currentType = typeSystem.getType(callerActor.getTypeName());
			while (transformedRILModel.getInstructionList(methodName) == null) {
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) typeSystem.getMetaData(currentType);
				currentType = rcd.getExtends();
				methodName = rewriteMethodNameType(currentType.getTypeName(), methodName);
			}
			return methodName;
		} catch (CodeCompilationException e) {
			throw new RebecaRuntimeInterpreterException(e.getMessage());
		}
	}

	private String rewriteMethodNameType(String typeName, String methodName) {
		return typeName + "." + methodName.split("\\.")[1];
	}

	private void startExecutionOfNewMessageServer(RILModel transformedRILModel, AbstractPolicy policy) {
		if(variableIsDefined(InstructionUtilities.PC_STRING))
			return;

		MessageSpecification executableMessage = queue.poll();
		policy.pick(executableMessage);

		String msgName = getMessageName(executableMessage.getMessageName(), 
				transformedRILModel);

		String relatedRebecType = msgName.split("\\.")[0];
		actorScopeStack.pushInScopeStackForMethodCallInitialization(relatedRebecType);
		addVariableToRecentScope("sender", executableMessage.getSenderActorState());
		initializePC(msgName, 0);

        addParamersValuesToScope(executableMessage);
	}

	public void addParamersValuesToScope(MessageSpecification executableMessage) {
		for (Entry<String, Object> entry : executableMessage.getParameters().entrySet()) {
        	addVariableToRecentScope(entry.getKey(), entry.getValue());
        }
	}
//
//	private boolean continueExecutionOfMessageServer() {
//		return variableIsDefined(InstructionUtilities.PC_STRING);
//	}

	@Override
	protected void exportQueueContent(PrintStream output) {
		output.println("<queue>");
		for (MessageSpecification messageSpecification : queue) {
			messageSpecification.export(output);
		}
		output.println("</queue>");
	}

	public String toString() {
		String retValue = super.toString();
		retValue += "\n queue:[";
		for(MessageSpecification ms : queue) {
			retValue += RILUtils.convertToString(ms) + ",";
		}
		return retValue + "]";
	}
}
