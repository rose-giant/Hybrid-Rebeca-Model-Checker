package org.rebecalang.modelchecker.timedrebeca;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.timedrebeca.rilinterpreter.CallTimedMsgSrvInstructionInterpreter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.CallTimedMsgSrvInstructionBean;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaModelChecker extends CoreRebecaModelChecker {

    public final static String CURRENT_TIME = "current_time";
    public final static String RESUMING_TIME = "resuming_time";

    public TimedRebecaModelChecker() {
        super();
    }

    @Override
    protected void addRequiredScopeToScopeStack(BaseActorState baseActorState, ArrayList<ReactiveClassDeclaration> actorSeries) {
        addTimedScopeToScopeStack(baseActorState);
        for (ReactiveClassDeclaration actor : actorSeries) {
            baseActorState.pushInActorScope(actor.getName());
            addStateVarsToRelatedScope(baseActorState, actor);
        }
    }

    private void addTimedScopeToScopeStack(BaseActorState baseActorState) {
        baseActorState.pushInActorScope("TimedRebec");
        baseActorState.addVariableToRecentScope(CURRENT_TIME, 0);
        baseActorState.addVariableToRecentScope(RESUMING_TIME, 0);
        baseActorState.addVariableToRecentScope("self", baseActorState);
    }

    private TimedState executeNewState(
            TimedState currentState,
            TimedActorState actorState,
            RILModel transformedRILModel,
            int stateCounter,
            boolean resume,
            TimedMessageSpecification msg) {

        TimedState newState = (TimedState) cloneState(currentState);
        TimedActorState newActorState = (TimedActorState) newState.getActorState(actorState.getName());
        if (resume)
            newActorState.resumeExecution(newState, transformedRILModel, modelCheckingPolicy);
        else
            newActorState.execute(newState, transformedRILModel, modelCheckingPolicy, msg);
        String transitionLabel = calculateTransitionLabel(actorState, newActorState, msg);
        Long stateKey = (long) newState.hashCode();
        if (!statespace.hasStateWithKey(stateKey)) {
            newState.setId(stateCounter++);
            statespace.addState(stateKey, newState);
            newState.clearLinks();
            currentState.addChildState(transitionLabel, newState);
            newState.addParentState(transitionLabel, currentState);
        } else {
            State repeatedState = statespace.getState(stateKey);
            currentState.addChildState(transitionLabel, repeatedState);
            repeatedState.addParentState(transitionLabel, currentState);
        }
        return newState;
    }


    @Override
    protected void doModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;
            PriorityQueue<TimedPriorityQueueItem<TimedState>> nextStatesQueue = new PriorityQueue<TimedPriorityQueueItem<TimedState>>();

            TimedState initialState = (TimedState) statespace.getInitialState();
            nextStatesQueue.add(new TimedPriorityQueueItem(initialState.getEnablingTime(), initialState));

            while (!nextStatesQueue.isEmpty()) {
                TimedPriorityQueueItem timePriorityQueueItem = nextStatesQueue.poll();
                TimedState currentState = (TimedState) timePriorityQueueItem.getItem();
                int enablingTime = currentState.getEnablingTime();
                currentState.checkForTimeStep(enablingTime);
                List<TimedActorState> enabledActors = currentState.getEnabledActors(enablingTime);

                for (TimedActorState currentActorState : enabledActors) {
                    do {
                        if (currentActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                            TimedState newState = executeNewState(currentState, currentActorState, transformedRILModel,
                                    stateCounter, true, null);
                            nextStatesQueue.add(new TimedPriorityQueueItem(newState.getEnablingTime(), newState));
                        } else {
                            for (TimedMessageSpecification msg : currentActorState.getEnabledMsgs(enablingTime)) {
                                TimedState newState = executeNewState(currentState, currentActorState, transformedRILModel,
                                        stateCounter, false, msg);
                                nextStatesQueue.add(new TimedPriorityQueueItem(newState.getEnablingTime(), newState));
                            }
                        }
                    } while (StatementInterpreterContainer.getInstance().hasNondeterminism());
                }
        }
    }

    protected TimedState createFreshState() {
        return new TimedState();
    }

    protected TimedActorState createFreshActorState() {
        return new TimedActorState();
    }

    protected void initializeStatementInterpreterContainer() {
        super.initializeStatementInterpreterContainer();

        StatementInterpreterContainer.getInstance().registerInterpreter(CallTimedMsgSrvInstructionBean.class,
                new CallTimedMsgSrvInstructionInterpreter());
    }
}
