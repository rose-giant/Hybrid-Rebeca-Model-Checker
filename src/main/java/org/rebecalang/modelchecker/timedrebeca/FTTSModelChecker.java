package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modelchecker.setting.ModelCheckerSetting;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.PriorityQueue;

@Component
@Qualifier("FTTS_TIMED_REBECA")
public class FTTSModelChecker extends TimedRebecaModelChecker {

    @Override
    protected void setModelCheckerSetting(ModelCheckerSetting modelCheckerSetting) throws ModelCheckingException {
        super.setModelCheckerSetting(modelCheckerSetting);

        this.modelCheckerSetting.setPolicy(Policy.COARSE_GRAINED_POLICY);
    }

    @Override
    protected void doModelChecking(RILModel transformedRILModel, RebecaModel rebecaModel) throws ModelCheckingException {
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
                    TimedState newState = executeNewState(currentState, currentActorState, statementInterpreterContainer, transformedRILModel, rebecaModel, msg);
                    if (!newState.getParentStates().isEmpty()) {
                        nextStatesQueue.add(new TimedPriorityQueueItem<>(newState.getEnablingTime(), newState));
                    }
                }
            }
        }
    }

    @Override
    protected TimedActorState executeNewTimedActorState(
            TimedState newState,
            String actorName,
            RILModel transformedRILModel,
            RebecaModel rebecaModel,
            TimedMessageSpecification timedMessageSpecification
    ) {
        TimedActorState newActorState = super.executeNewTimedActorState(newState, actorName, transformedRILModel, rebecaModel, timedMessageSpecification);
        // Set the current time of the actor after executing the message server
        newActorState.increaseCurrentTime(timedMessageSpecification.getMinStartTime());

        return newActorState;
    }
}
