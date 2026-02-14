package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.TransparentActorStateSpace;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.HybridRebecaResumeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.TimeProgressAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;

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
        dot.writeToFile("output.dot");
        System.out.println(currentStateIdx);
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
        currentStateIdx++;

        String actionStr = "TAU";
        if (action instanceof MessageAction) {
            actionStr = ((MessageAction) action).getActionLabel();
        } else if (action instanceof TimeProgressAction) {
            actionStr = ((TimeProgressAction) action)
                    .getIntervalTimeProgress().toString();
        }

        dot.addTransition(sourceId, transitionType, actionStr, destId);
    }

    public void runSystemRules(HybridRebecaSystemState sourceState,
            HybridRebecaAbstractTransition<HybridRebecaSystemState> executionResult) {

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
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> runApplicableRule(HybridRebecaSystemState state) {
        HybridRebecaAbstractTransition<HybridRebecaSystemState> result;

        // ===== highest priority: resume =====
        if (state.thereIsSuspension()) {
            HybridRebecaResumeSOSRule rule = new HybridRebecaResumeSOSRule();
            result = rule.systemLevelResumePostpone(state);
            if (result != null) {
                return result;
            }
        }

        // ===== then execute statements =====
        if (systemCanExecuteStatements(state)) {
            result = levelExecuteStatementSOSRule.applyRule(state);
            if (result != null) {
                return result;
            }
        }

        // ===== take message =====
        HybridRebecaCompositionLevelTakeMessageSOSRule takeRule =
                new HybridRebecaCompositionLevelTakeMessageSOSRule();
        result = takeRule.applyRule(state);
        if (result != null) {
            return result;
        }

        // ===== network delivery =====
        if (!state.getNetworkState().getReceivedMessages().isEmpty()) {
            result = networkDeliverySOSRule.applyRule(state);
            if (result != null) {
                return result;
            }
        }

        // ===== environment progress =====
        HybridRebecaCompositionLevelEnvProgressSOSRule envRule =
                new HybridRebecaCompositionLevelEnvProgressSOSRule();
        return envRule.applyRule(state);
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