package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modeltransformer.ril.RILModel;

import java.util.List;
import java.util.PriorityQueue;

public class FGTSModelChecker  extends TimedRebecaModelChecker{


    public FGTSModelChecker() {
        super();
        this.modelCheckerSetting.setPolicy(Policy.FINE_GRAINED_POLICY);
    }

    @Override
    protected void doModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;
        PriorityQueue<TimedPriorityQueueItem<TimedState>> nextStatesQueue = new PriorityQueue<>();

        TimedState initialState = (TimedState) stateSpace.getInitialState();
        nextStatesQueue.add(new TimedPriorityQueueItem<>(initialState.getEnablingTime(), initialState));

        while (!nextStatesQueue.isEmpty()) {
            TimedPriorityQueueItem<TimedState> timePriorityQueueItem = nextStatesQueue.poll();
            TimedState currentState = timePriorityQueueItem.getItem();
            int enablingTime = currentState.getEnablingTime();
            currentState.checkForTimeStep(enablingTime);
            List<TimedActorState> enabledActors = currentState.getEnabledActors(enablingTime);

            if (enabledActors.isEmpty())
                throw new ModelCheckingException("Deadlock");

            for (TimedActorState currentActorState : enabledActors) {
                do {
                    if (currentActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                        TimedState newState = executeNewState(currentState, currentActorState, statementInterpreterContainer, transformedRILModel,
                                stateCounter, true, null);
                        nextStatesQueue.add(new TimedPriorityQueueItem<>(newState.getEnablingTime(), newState));
                    } else {
                        for (TimedMessageSpecification msg : currentActorState.getEnabledMsgs(enablingTime)) {
                            TimedState newState = executeNewState(currentState, currentActorState, statementInterpreterContainer, transformedRILModel,
                                    stateCounter, false, msg);
                            nextStatesQueue.add(new TimedPriorityQueueItem<>(newState.getEnablingTime(), newState));
                        }
                    }
                } while (statementInterpreterContainer.hasNondeterminism());
            }
        }
    }
}
