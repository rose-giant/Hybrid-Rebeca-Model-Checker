package org.rebecalang.transparentactormodelchecker.realtime.systemRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.HybridRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaCompositionLevelExecuteStatementTest {

    HybridRebecaCompositionLevelExecuteStatementSOSRule hybridRebecaCompositionLevelExecuteStatementSOSRule =
            new HybridRebecaCompositionLevelExecuteStatementSOSRule();
    HybridRebecaSystemState hybridRebecaSystemState = new HybridRebecaSystemState();
    HybridRebecaActorState actorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaNetworkState networkState = new HybridRebecaNetworkState();

    @Test
    public void noTransitionAfterTimeIsUp() {
        Pair<Float, Float> rt = new Pair<>((float)1, (float)3);
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        hybridRebecaSystemState.setInputInterval(rt);
        hybridRebecaSystemState.setNow(now);
        HybridRebecaNondeterministicTransition<HybridRebecaSystemState> result =
                (HybridRebecaNondeterministicTransition<HybridRebecaSystemState>)
                        hybridRebecaCompositionLevelExecuteStatementSOSRule.applyRule(hybridRebecaSystemState);

        assertEquals(0, result.getDestinations().size());
    }

    @Test
    public void oneTransitionAfterTimeIsNotUpAndExistingExecutableStatement() {
        hybridRebecaSystemState.setNetworkState(networkState);
        Object assignee = new Object();
        Float delayLowerBound = (float)1;
        Float delayUpperBound = (float)2;
        Pair<Float, Float> resumeTime = new Pair<>((float)0, (float)0.5);
        actorState1.setResumeTime(resumeTime);
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = new ContnuousNonDetInstructionBean(assignee, delayLowerBound, delayUpperBound);
        Pair<HybridRebecaActorState, InstructionBean> delayInput = new Pair<>(actorState1, contnuousNonDetInstructionBean);
        delayInput.setFirst(actorState1);
        hybridRebecaSystemState.setActorState(actorState1.getId(), actorState1);
    }
}
