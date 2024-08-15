package org.rebecalang.modelchecker.timedrebeca;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

@SuppressWarnings("serial")
public class TimedState extends State<TimedActorState> {
    // Flag to distinguish between FTTS and FGTS
    private boolean isFTTS;

    public void setFTTS(boolean isFTTS) {
        this.isFTTS = isFTTS;
    }

    public boolean isFTTS() {
        return isFTTS;
    }

    public void checkForTimeStep(int enablingTime) {
        List<TimedActorState> allActorStates = getAllActorStates();
        if (isFTTS) {
            for (TimedActorState actorState : allActorStates) {
                int currentTime = actorState.getCurrentTime();
                if (enablingTime > currentTime) {
                    actorState.setFTTS(isFTTS);
                    actorState.setCurrentTime(enablingTime);
                }
            }
        } else {
            if (!allActorStates.isEmpty()) {
                int currentTime = allActorStates.get(0).getCurrentTime();
                if (enablingTime > currentTime) {
                    for (TimedActorState actorState : allActorStates) {
                        actorState.setCurrentTime(enablingTime);
                    }
                }
            }
        }
    }

    public int getEnablingTime() throws ModelCheckingException {
        int minExecutionTime = Integer.MAX_VALUE;
        for (TimedActorState actorState : getAllActorStates()) {
            actorState.setFTTS(isFTTS);

            if (isFTTS) {
                int firstTimeActorCanPeekNewMsg = actorState.firstTimeActorCanPeekNewMessage();
                minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
            } else {
                if (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                    minExecutionTime = Math.min(minExecutionTime, actorState.getResumingTime());
                } else {
                    int firstTimeActorCanPeekNewMsg = actorState.firstTimeActorCanPeekNewMessage();
                    minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
                }
            }
        }
        if (minExecutionTime == Integer.MAX_VALUE)
            throw new ModelCheckingException("Deadlock");
        if (!isFTTS) {
            int currentTime = getAllActorStates().get(0).getCurrentTime();
            if (minExecutionTime < currentTime) minExecutionTime = currentTime;
        }
        return minExecutionTime;
    }

    public List<TimedActorState> getEnabledActors(int enablingTime) {
        List<TimedActorState> enabledActors = new ArrayList<>();
        for (TimedActorState actorState : getAllActorStates()) {
            actorState.setFTTS(isFTTS);

            if (isFTTS) {
                if (actorState.firstTimeActorCanPeekNewMessage() <= enablingTime) {
                    enabledActors.add(actorState);
                }
            } else {
                if ((actorState.variableIsDefined(InstructionUtilities.PC_STRING))) {
                    if (actorState.getResumingTime() <= enablingTime) {
                        enabledActors.add(actorState);
                    }
                } else if (actorState.firstTimeActorCanPeekNewMessage() <= enablingTime) {
                    enabledActors.add(actorState);
                }
            }
        }
        return enabledActors;
    }
}
