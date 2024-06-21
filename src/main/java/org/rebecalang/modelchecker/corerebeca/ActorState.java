package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.io.PrintStream;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class ActorState extends BaseActorState {
	private LinkedList<MessageSpecification> queue;

	public ActorState() {
		setQueue(new LinkedList<>());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actorScopeStack == null) ? 0 : actorScopeStack.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorState other = (ActorState) obj;
		if (actorScopeStack == null) {
			if (other.actorScopeStack != null)
				return false;
		} else if (!actorScopeStack.equals(other.actorScopeStack))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		if (typeName == null) {
			return other.typeName == null;
		} else
			return typeName.equals(other.typeName);
	}

	public MessageSpecification getMessage() {
		return queue.peek();
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
	
	public void execute(State systemState,
			StatementInterpreterContainer statementInterpreterContainer,
			RILModel transformedRILModel, AbstractPolicy policy) {

		do {
			if(startExecutionOfNewMessageServer()) {
				MessageSpecification executableMessage = queue.poll();
				policy.pick(executableMessage);

				String msgName = getMessageName(executableMessage.getMessageName(), 
						transformedRILModel);

				String relatedRebecType = msgName.split("\\.")[0];
				actorScopeStack.pushInScopeStack(getTypeName(), relatedRebecType);
				addVariableToRecentScope("sender", executableMessage.getSenderActorState());
				initializePC(msgName, 0);

			}
			if (continueExecutionOfMessageServer()) {
				ProgramCounter pc = getPC();

				String methodName = pc.getMethodName();
				Type currentType = null;
				try {
					currentType = typeSystem.getType(typeName);
				} catch (CodeCompilationException e) {
					throw new RebecaRuntimeInterpreterException(e.getMessage());
				}

				while (transformedRILModel.getInstructionList(methodName) == null) {
					try {
						ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) typeSystem.getMetaData(currentType);
						if (rcd.getExtends() == null)
							break;
						currentType = rcd.getExtends();
						methodName = currentType.getTypeName() + "." + pc.getMethodName().split("\\.")[1];
					} catch (CodeCompilationException e) {
						e.printStackTrace();
					}
				}

				int lineNumber = pc.getLineNumber();

				InstructionBean instruction = transformedRILModel.getInstructionList(methodName).get(lineNumber);
				InstructionInterpreter interpreter = statementInterpreterContainer.retrieveInterpreter(instruction);
				policy.executedInstruction(instruction);
				interpreter.interpret(instruction, this, systemState);

			} else if (!queue.isEmpty()) {
				MessageSpecification executableMessage = queue.poll();
				policy.pick(executableMessage);

				String msgName = executableMessage.getMessageName();
				Type currentType = null;
				try {
					currentType = typeSystem.getType(executableMessage.getMessageName().split("\\.")[0]);
				} catch (CodeCompilationException e) {
					e.printStackTrace();
				}

				while (!transformedRILModel.getMethodNames().contains(msgName)) {
					try {
						ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) typeSystem.getMetaData(currentType);
						if (rcd.getExtends() == null)
							break;
						currentType = rcd.getExtends();
						msgName = currentType.getTypeName() + "." + executableMessage.getMessageName().split("\\.")[1];
					} catch (CodeCompilationException e) {
						e.printStackTrace();
					}
				}

				String relatedRebecType = msgName.split("\\.")[0];
				actorScopeStack.pushInScopeStack(getTypeName(), relatedRebecType);
				addVariableToRecentScope("sender", executableMessage.getSenderActorState());
				initializePC(msgName, 0);

			} else
				throw new RebecaRuntimeInterpreterException("this case should not happen!");
		} while (!policy.isBreakable());
	}



	private boolean startExecutionOfNewMessageServer() {
		return !variableIsDefined(InstructionUtilities.PC_STRING);
	}

	private boolean continueExecutionOfMessageServer() {
		return variableIsDefined(InstructionUtilities.PC_STRING);
	}

	@Override
	protected void exportQueueContent(PrintStream output) {
		output.println("<queue>");
		for (MessageSpecification messageSpecification : queue) {
			messageSpecification.export(output);
		}
		output.println("</queue>");
	}
}
