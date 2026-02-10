package org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.AbstractTransparentActorState;
import org.rebecalang.transparentactormodelchecker.TransparentActorStateSpace;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.HybridRebecaResumeSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.*;

public class ApplySystemLevelRules {
    Map<HybridRebecaSystemState, Integer> stateIds = new IdentityHashMap<>();
    int nextStateId = 0;

    private int getStateId(HybridRebecaSystemState s) {
        return stateIds.computeIfAbsent(s, k -> nextStateId++);
    }

    ArrayList<HybridRebecaSystemState> states = new ArrayList<>();
    TransparentActorStateSpace transparentActorStateSpace = new TransparentActorStateSpace();
    HybridRebecaSystemState initialState;
    public ApplySystemLevelRules(HybridRebecaSystemState initialState) {
        levelExecuteStatementSOSRule = new HybridRebecaCompositionLevelExecuteStatementSOSRule();
        this.initialState = initialState;
        startApplyingRules(initialState);
        System.out.println(states.size());
        dot.writeToFile("output.dot");
    }

    HybridRebecaCompositionLevelExecuteStatementSOSRule levelExecuteStatementSOSRule;
    HybridRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule =
            new HybridRebecaCompositionLevelNetworkDeliverySOSRule();

    public void startApplyingRules(HybridRebecaSystemState initialState) {
        HybridRebecaDeterministicTransition<HybridRebecaSystemState> t =
                new HybridRebecaDeterministicTransition<>();

        t.setDestination(initialState);
        t.setAction(Action.TAU);

        getStateId(initialState); // ensure s0 exists

        runSystemRules(initialState, t);
    }

    private DotExporter dot = new DotExporter();
    public void printState(
            int sourceId,
            String transitionType,
            Object action,
            HybridRebecaSystemState destState
    ) {
        int destId = getStateId(destState);

        String actionStr = "TAU";
        if (action instanceof MessageAction) {
            actionStr = ((MessageAction) action).getActionLabel();
        } else if (action instanceof TimeProgressAction) {
            actionStr = ((TimeProgressAction) action)
                    .getIntervalTimeProgress().toString();
        }

        // OLD:
        // System.out.println(...);

        // NEW:
        dot.addTransition(sourceId, transitionType, actionStr, destId);
    }

    public void runSystemRules(HybridRebecaSystemState sourceState,
            HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult) {
        currentStateIdx ++;
        if (executionResult instanceof HybridRebecaDeterministicTransition) {
            HybridRebecaDeterministicTransition<HybridRebecaSystemState> t =
                    (HybridRebecaDeterministicTransition<HybridRebecaSystemState>) executionResult;
            HybridRebecaSystemState dest = HybridRebecaStateSerializationUtils.clone(t.getDestination());
            printState(getStateId(sourceState), "", t.getAction(), dest);
            if (dest.getNow().getFirst() >= dest.getInputInterval().getSecond()) {
                return;
            }

            HybridRebecaAbstractTransition<HybridRebecaSystemState> next = runApplicableRule(dest);
            runSystemRules(dest, next);
        }

        else if (executionResult instanceof HybridRebecaNondeterministicTransition) {
            HybridRebecaNondeterministicTransition<HybridRebecaSystemState> t =
                    (HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) executionResult;

            int sourceId = getStateId(sourceState);

            List<HybridRebecaSystemState> successors = new ArrayList<>();

            // Phase 1: print all nondet edges
            for (Pair<? extends Action, HybridRebecaSystemState> p : t.getDestinations()) {
                HybridRebecaSystemState dest = HybridRebecaStateSerializationUtils.clone(p.getSecond());
                printState(sourceId, "", p.getFirst(), dest);
                if (dest.getNow().getFirst() <= dest.getInputInterval().getSecond()) {
                    successors.add(dest);
                }
            }

            // Phase 2: explore
            for (HybridRebecaSystemState dest : successors) {
                HybridRebecaAbstractTransition<HybridRebecaSystemState> next = runApplicableRule(dest);
                runSystemRules(dest, next);
            }
        }

    }

    int currentStateIdx = 0;
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> runApplicableRule(HybridRebecaSystemState backup) {
        if (backup.thereIsSuspension()) {
            HybridRebecaResumeSOSRule rebecaResumeSOSRule = new HybridRebecaResumeSOSRule();
            HybridRebecaAbstractTransition<HybridRebecaSystemState> result = rebecaResumeSOSRule.systemLevelResumePostpone(backup);
            if (result != null) {
                return result;
            }
        }

        if(systemCanExecuteStatements(backup)) {
            HybridRebecaAbstractTransition<HybridRebecaSystemState> result = levelExecuteStatementSOSRule.applyRule(backup);
            if (result != null) {
                return result;
            }
        }

        HybridRebecaCompositionLevelTakeMessageSOSRule takeMessageSOSRule = new HybridRebecaCompositionLevelTakeMessageSOSRule();
        HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult = takeMessageSOSRule.applyRule(backup);
        if (executionResult != null) {
            return executionResult;
        }

        if (backup.getNetworkState().getReceivedMessages().size() > 0) {
            HybridRebecaAbstractTransition<HybridRebecaSystemState> deliveryResult = networkDeliverySOSRule.applyRule(backup);
            if (deliveryResult != null) {
                return deliveryResult;
            }
        }

//        System.out.println("time to sync!");
        HybridRebecaCompositionLevelEnvProgressSOSRule envProgressSOSRule = new HybridRebecaCompositionLevelEnvProgressSOSRule();
        executionResult = envProgressSOSRule.applyRule(backup);
        return executionResult;
    }

    public boolean systemCanExecuteStatements(HybridRebecaSystemState initialState) {
        for(String actorId : initialState.getActorsState().keySet()) {
            HybridRebecaActorState hybridRebecaActorState = initialState.getActorState(actorId);
            if (!hybridRebecaActorState.noScopeInstructions() && !hybridRebecaActorState.isSuspent()) {
                return true;
            }
        }

        return false;
    }
}