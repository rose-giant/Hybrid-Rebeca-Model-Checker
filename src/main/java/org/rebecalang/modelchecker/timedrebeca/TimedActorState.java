package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.timedrebeca.utils.SchedulingPolicy;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import static org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker.CURRENT_TIME;
import static org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker.RESUMING_TIME;

@SuppressWarnings("serial")
public class TimedActorState extends BaseActorState<TimedMessageSpecification> {
    // Flag to distinguish between FTTS and FGTS
    private boolean isFTTS;

    private SchedulingPolicy schedulingPolicy = SchedulingPolicy.SCHEDULING_ALGORITHM_FIFO;

    private PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>> queue;

    public void removeCustomMsgFromQueue(TimedMessageSpecification timedMessageSpecification) {
        queue.removeIf(timedPriorityQueueItem -> timedPriorityQueueItem.getItem().equals(timedMessageSpecification));
    }

    protected int priority = Integer.MAX_VALUE;

    public void setFTTS(boolean isFTTS) {
        this.isFTTS = isFTTS;
    }

    public boolean isFTTS() {
        return isFTTS;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSchedulingPolicy(SchedulingPolicy schedulingPolicy) {
        this.schedulingPolicy = schedulingPolicy;
    }

    public SchedulingPolicy getSchedulingPolicy() {
        return schedulingPolicy;
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

    public void increaseCurrentTime(int delay) {
        this.setVariableValue(CURRENT_TIME, getCurrentTime() + delay);
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
        int result = super.hashCode();
        if (queue.size() >= 1) {
            for (TimedPriorityQueueItem<TimedMessageSpecification> timedPriorityQueueItem : queue) {
                result = prime * result + timedPriorityQueueItem.hashCode();
            }
        } else {
            result = prime * result;
        }
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
    public void addToQueue(TimedMessageSpecification timedMsgSpec) {
        queue.add(new TimedPriorityQueueItem<>
                (timedMsgSpec.getMinStartTime(), timedMsgSpec));
    }

    @Override
    public boolean actorQueueIsEmpty() {
        return queue.isEmpty();
    }

    @Override
    public TimedMessageSpecification getMessage(boolean isPeek) {
        return getTimedPriorityQueueItem(isPeek) != null ? getTimedPriorityQueueItem(isPeek).getItem() : null;
    }

    public int firstTimeActorCanPeekNewMessage() {
        if (this.variableIsDefined(InstructionUtilities.PC_STRING)) {
            throw new RuntimeException("This version supports coarse grained execution.");
        } else {
            if (!this.actorQueueIsEmpty()) {
                int resumingTime = getResumingTime();
                int firstMsgTime = getMessage(true).getMinStartTime();
                return Math.max(resumingTime, firstMsgTime);
            }
        }
        return Integer.MAX_VALUE;
    }

    public LinkedList<TimedMessageSpecification> getEnabledMsgs(int enablingTime) throws ModelCheckingException {
        LinkedList<TimedMessageSpecification> enabledMsgs = new LinkedList<>();

        for (TimedPriorityQueueItem<TimedMessageSpecification> timedPriorityQueueItem : this.getQueueItem(true)) {
            if (timedPriorityQueueItem.getTime() <= enablingTime && !enabledMsgs.contains(timedPriorityQueueItem.getItem())) {
                TimedMessageSpecification curMsg = timedPriorityQueueItem.getItem();

                if (curMsg.getMaxStartTime() < getCurrentTime()) {
                    throw new ModelCheckingException("Deadline missed!");
                }

                enabledMsgs.add(curMsg);
            }
        }

        return enabledMsgs;
    }

    @Override
    protected InstructionBean getInheritanceInstruction(RILModel transformedRILModel, InstructionBean instruction){
        instruction = super.getInheritanceInstruction(transformedRILModel, instruction);

        if (instruction instanceof TimedMsgsrvCallInstructionBean) {
            TimedMsgsrvCallInstructionBean tmcib = (TimedMsgsrvCallInstructionBean) instruction;
            String newMethodName = resolveDynamicBindingOfMethodCall(transformedRILModel, tmcib);
            if(!tmcib.getMethodName().equals(newMethodName)) {
                int currentTime = getCurrentTime();
                int after = (int) tmcib.getAfter();
                int deadline = (int) tmcib.getDeadline();

                if (isFTTS()) {
                    after += currentTime;
                    if (deadline != Integer.MAX_VALUE) deadline += currentTime;
                }
                instruction = new TimedMsgsrvCallInstructionBean(
                        tmcib.getBase(), newMethodName, tmcib.getParameters(), after, deadline);
                ((TimedMsgsrvCallInstructionBean)instruction).setParameters(
                        tmcib.getParameters());
            }
        }

        return instruction;
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

    public List<TimedPriorityQueueItem<TimedMessageSpecification>> getQueueItem(boolean isPeek) {
        if (queue.size() <= 1) {
            return queue.peek() != null ? (isPeek ? List.of(queue.peek()) : List.of(queue.poll())) : List.of();
        }

        List<TimedPriorityQueueItem<TimedMessageSpecification>> selectedMessages = new ArrayList<>();
        PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>> tempQueue = new PriorityQueue<>(queue);

        int currentTime = getCurrentTime();

        while (!tempQueue.isEmpty()) {
            TimedPriorityQueueItem<TimedMessageSpecification> msgItem = tempQueue.poll();

            // Filter messages that have arrived at or before the current time
            if (msgItem.getItem().getMinStartTime() <= currentTime) {
                // If there are no selected messages yet, or if the current message is preferred based on the scheduling policy
                if (selectedMessages.isEmpty() || SchedulingPolicy.compare(schedulingPolicy, msgItem.getItem(), selectedMessages.get(0).getItem(), "<")) {
                    selectedMessages.clear(); // Clear previous selections
                    selectedMessages.add(msgItem); // Add the new preferred message
                } else if (SchedulingPolicy.compare(schedulingPolicy, selectedMessages.get(0).getItem(), msgItem.getItem(), "==")) {
                    selectedMessages.add(msgItem); // Add this message if it is also preferred
                }
            } else {
                // Since the queue is ordered by minStartTime, we can break early
                break;
            }
        }

        if (!selectedMessages.isEmpty()) {
            return selectedMessages;
        }

        return queue.peek() != null ? (isPeek ? List.of(queue.peek()) : List.of(queue.poll())) : List.of();
    }

    public TimedPriorityQueueItem<TimedMessageSpecification> getTimedPriorityQueueItem(boolean isPeek) {
        if (queue.size() <= 1) {
            return queue.peek() != null ? (isPeek ? queue.peek() : queue.poll()) : null;
        }

        TimedPriorityQueueItem<TimedMessageSpecification> selectedMessage = null;
        PriorityQueue<TimedPriorityQueueItem<TimedMessageSpecification>> tempQueue = new PriorityQueue<>(queue);

        int currentTime = getCurrentTime();

        while (!tempQueue.isEmpty()) {
            TimedPriorityQueueItem<TimedMessageSpecification> msgItem = tempQueue.poll();

            // Filter messages that have arrived at or before the current time
            if (msgItem.getItem().getMinStartTime() <= currentTime) {
                if (selectedMessage == null || SchedulingPolicy.compare(schedulingPolicy, msgItem.getItem(), selectedMessage.getItem(), "<")) {
                    selectedMessage = msgItem;
                }
            } else {
                // Since the queue is ordered by minStartTime, we can break early
                break;
            }
        }

        // If we found a valid message, return it (either peek or remove from the queue)
        if (selectedMessage != null) {
            if (!isPeek) {
                queue.remove(selectedMessage);
            }
            return selectedMessage;
        }

        // No valid message found
        return queue.peek() != null ? (isPeek ? queue.peek() : queue.poll()) : null;
    }
}
