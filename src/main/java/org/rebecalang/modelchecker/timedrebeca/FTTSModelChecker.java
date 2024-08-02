package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modeltransformer.ril.RILModel;

import java.util.List;
import java.util.PriorityQueue;

public class FTTSModelChecker extends TimedRebecaModelChecker {

    public FTTSModelChecker() {
        super();
        this.modelCheckerSetting.setPolicy(Policy.COARSE_GRAINED_POLICY);
    }

    @Override
    protected void doModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;
        PriorityQueue<TimedPriorityQueueItem<TimedState>> nextStatesQueue = new PriorityQueue<>();

        TimedState initialState = (TimedState) stateSpace.getInitialState();
        initialState.setFTTS(true);  // Enable FTTS mode
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
                for (TimedMessageSpecification msg : currentActorState.getEnabledMsgs(enablingTime)) {
                    TimedState newState = executeNewState(currentState, currentActorState, statementInterpreterContainer, transformedRILModel,
                            stateCounter, false, msg);
                    nextStatesQueue.add(new TimedPriorityQueueItem<>(newState.getEnablingTime(), newState));
                }
            }
        }
    }

    @Override
    protected TimedState executeNewState(
            TimedState currentState,
            TimedActorState actorState,
            StatementInterpreterContainer statementInterpreterContainer,
            RILModel transformedRILModel,
            int stateCounter,
            boolean resume,
            TimedMessageSpecification msg) {

        TimedState newState = cloneState(currentState);
        TimedActorState newActorState = (TimedActorState) newState.getActorState(actorState.getName());

        newActorState.execute(newState, statementInterpreterContainer, transformedRILModel, modelCheckingPolicy, msg);

        String transitionLabel = calculateTransitionLabel(actorState, newActorState);

        Long stateKey = Long.valueOf(newState.hashCode());

        if (!stateSpace.hasStateWithKey(stateKey)) {
            newState.setId(stateCounter++);
            stateSpace.addState(stateKey, newState);
            newState.clearLinks();
            currentState.addChildState(transitionLabel, newState);
            newState.addParentState(transitionLabel, currentState);
        } else {
            TimedState repeatedState = (TimedState) stateSpace.getState(stateKey);
            currentState.addChildState(transitionLabel, repeatedState);
            repeatedState.addParentState(transitionLabel, currentState);
        }

        // Set the current time of the actor after executing the message server
        newActorState.setCurrentTime(actorState.getCurrentTime() + msg.getMinStartTime());

        return newState;
    }
}
