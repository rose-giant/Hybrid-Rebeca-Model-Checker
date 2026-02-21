package org.rebecalang.transparentactormodelchecker.realtime.systemRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.RealTimeRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaCompositionLevelExecuteStatementTest {

    RealTimeRebecaCompositionLevelExecuteStatementSOSRule realTimeRebecaCompositionLevelExecuteStatementSOSRule =
            new RealTimeRebecaCompositionLevelExecuteStatementSOSRule();
    RealTimeRebecaSystemState realTimeRebecaSystemState = new RealTimeRebecaSystemState();
    RealTimeRebecaActorState actorState1 = new RealTimeRebecaActorState("actor1");
    RealTimeRebecaNetworkState networkState = new RealTimeRebecaNetworkState();

    @Test
    public void noTransitionAfterTimeIsUp() {
        Pair<Float, Float> rt = new Pair<>((float)1, (float)3);
        Pair<Float, Float> now = new Pair<>((float)1, (float)2);
        realTimeRebecaSystemState.setInputInterval(rt);
        realTimeRebecaSystemState.setNow(now);
        RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState> result =
                (RealTimeRebecaNondeterministicTransition<RealTimeRebecaSystemState>)
                        realTimeRebecaCompositionLevelExecuteStatementSOSRule.applyRule(realTimeRebecaSystemState);

        assertEquals(0, result.getDestinations().size());
    }

    @Test
    public void oneTransitionAfterTimeIsNotUpAndExistingExecutableStatement() {
        realTimeRebecaSystemState.setNetworkState(networkState);
        Object assignee = new Object();
        Float delayLowerBound = (float)1;
        Float delayUpperBound = (float)2;
        Pair<Float, Float> resumeTime = new Pair<>((float)0, (float)0.5);
        actorState1.setResumeTime(resumeTime);
        ContnuousNonDetInstructionBean contnuousNonDetInstructionBean = new ContnuousNonDetInstructionBean(assignee, delayLowerBound, delayUpperBound);
        Pair<RealTimeRebecaActorState, InstructionBean> delayInput = new Pair<>(actorState1, contnuousNonDetInstructionBean);
        delayInput.setFirst(actorState1);
        realTimeRebecaSystemState.setActorState(actorState1.getId(), actorState1);
    }
}
