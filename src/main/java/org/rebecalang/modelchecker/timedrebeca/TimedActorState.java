package org.rebecalang.modelchecker.timedrebeca;

import static org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker.CURRENT_TIME;
import static org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker.RESUMING_TIME;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.PriorityQueue;

import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

@SuppressWarnings("serial")
public class TimedActorState extends BaseActorState<TimedMessageSpecification> {
    private PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>> queue;

    // Flag to distinguish between FTTS and FGTS
    private boolean isFTTS;

    public void setFTTS(boolean isFTTS) {
        this.isFTTS = isFTTS;
    }

    public boolean isFTTS() {
        return isFTTS;
    }

    public void initializeQueue() {
        setQueue(new PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>>());
    }

    public int getCurrentTime() {
        return (int) this.retrieveVariableValue(CURRENT_TIME);
    }

    public void setCurrentTime(int currentTime) {
        this.setVariableValue(CURRENT_TIME, currentTime);
    }

    public int getResumingTime() {
        return (int) this.retrieveVariableValue(RESUMING_TIME);
    }

    public void setResumingTime(int currentTime) {
        this.setVariableValue(RESUMING_TIME, currentTime);
    }

    public void increaseResumingTime(int delay) {
        this.setVariableValue(RESUMING_TIME, getCurrentTime() + delay);
    }

    public TimedActorState() {
        initializeQueue();
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
        TimedActorState other = (TimedActorState) obj;
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
        } else return typeName.equals(other.typeName);
    }

    public PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>> getQueue() {
        return queue;
    }

    public void setQueue(PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>> queue) {
        this.queue = queue;
    }

    @Override
    public void addToQueue(MessageSpecification msgSpec) {
        TimedMessageSpecification timedMsgSpec = ((TimedMessageSpecification) msgSpec);
        queue.add(new TimedPriorityQueueItem<TimedMessageSpecification>
               (timedMsgSpec.getMinStartTime(), timedMsgSpec));
    }

    @Override
    public boolean actorQueueIsEmpty() {
        return queue.isEmpty();
    }

//    public void resumeExecution(TimedState systemState, StatementInterpreterContainer statementInterpreterContainer, RILModel transformedRILModel, AbstractPolicy policy) {
//        do {
//            ProgramCounter pc = getPC();
//            int lineNumber = pc.getLineNumber();
//            String methodName = getPC().getMethodName();
//
//            ArrayList<InstructionBean> instructionsList =
//                    transformedRILModel.getInstructionList(methodName);
//
//            InstructionBean instruction = instructionsList.get(lineNumber);
//            InstructionInterpreter interpreter = statementInterpreterContainer.retrieveInterpreter(instruction);
//            policy.executedInstruction(instruction);
//
//            interpreter.interpret(getInheritanceInstruction(transformedRILModel, instruction), this, systemState);
//        } while (!policy.isBreakable());
//    }
//
//    public void execute(TimedState state,
//                        StatementInterpreterContainer statementInterpreterContainer,
////                        RILModel transformedRILModel, AbstractPolicy policy, TimedMessageSpecification timedMessageSpecification) {
////
////        super.startExecutionOfNewMessageServer(transformedRILModel, policy, timedMessageSpecification);
////
////        resumeExecution(state, statementInterpreterContainer, transformedRILModel, policy);
//    }

    @Override
    public MessageSpecification getMessage(boolean isPeek) {
        return queue.peek() != null ? (isPeek ? queue.peek() : queue.poll()).getItem() : null;
    }

    public int firstTimeActorCanPeekNewMessage() {
        if (this.variableIsDefined(InstructionUtilities.PC_STRING)) {
            throw new RuntimeException("This version supports coarse grained execution.");
        } else {
            if (!this.actorQueueIsEmpty()) {
                int resumingTime = getResumingTime();
                int firstMsgTime = queue.peek().getItem().getMinStartTime();
                return Math.max(resumingTime, firstMsgTime);
            }
        }
        return Integer.MAX_VALUE;
    }

    public ArrayList<TimedMessageSpecification> getEnabledMsgs(int enablingTime) throws ModelCheckingException {
        ArrayList<TimedMessageSpecification> enabledMsgs = new ArrayList<>();
        while (this.queue.peek() != null && this.queue.peek().getTime() <= enablingTime) {
            TimedMessageSpecification curMsg = this.queue.poll().getItem();
            if (curMsg.getMaxStartTime() < getCurrentTime()) throw new ModelCheckingException("Deadlock");
            enabledMsgs.add(curMsg);
        }
        return enabledMsgs;
    }
    
	@Override
	protected void exportQueueContent(PrintStream output) {
		output.println("<queue>");
		for(TimedPriorityQueueItem<TimedMessageSpecification> timedPriorityQueueItem : queue) {
			TimedMessageSpecification timedMessageSpecification = timedPriorityQueueItem.getItem();
			timedMessageSpecification.export(output);
		}
		output.println("</queue>");
	}

}
