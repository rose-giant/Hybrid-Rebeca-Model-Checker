package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
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
public class ActorState extends BaseActorState<MessageSpecification> {
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

	public void execute(State<? extends ActorState> systemState,
						StatementInterpreterContainer statementInterpreterContainer,
						RILModel transformedRILModel, RebecaModel rebecaModel, AbstractPolicy policy) {

		super.startExecutionOfNewMessageServer(transformedRILModel, policy, null);

		do {
			ProgramCounter pc = getPC();
			int lineNumber = pc.getLineNumber();
			String methodName = getPC().getMethodName();

			ArrayList<InstructionBean> instructionsList =
					transformedRILModel.getInstructionList(methodName);

			InstructionBean instruction = instructionsList.get(lineNumber);
			InstructionInterpreter interpreter = statementInterpreterContainer.retrieveInterpreter(instruction);
			policy.executedInstruction(instruction);

			interpreter.interpret(getInheritanceInstruction(transformedRILModel, instruction), this, systemState, rebecaModel);
		} while (!policy.isBreakable());
	}

	@Override
	public String toString() {
		String retValue = super.toString();
		retValue += "\n queue:[";
		for(MessageSpecification ms : queue) {
			retValue += RILUtils.convertToString(ms) + ",";
		}
		return retValue + "]";
	}

	@Override
	protected void exportQueueContent(PrintStream output) {
		output.println("<queue>");
		for (MessageSpecification messageSpecification : queue) {
			messageSpecification.export(output);
		}
		output.println("</queue>");
	}


//	private boolean continueExecutionOfMessageServer() {
//		return variableIsDefined(InstructionUtilities.PC_STRING);
//	}
}
