package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartSetModeInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.RealTimeRebecaSetModeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaSetModeTest {
    RealTimeRebecaSetModeSOSRule realTimeRebecaSetModeSOSRule = new RealTimeRebecaSetModeSOSRule();
    RealTimeRebecaActorState realTimeRebecaActorState = new RealTimeRebecaActorState("actor1");
    StartSetModeInstructionBean setModeInstructionBean = new StartSetModeInstructionBean("on");

    @Test
    public void setModeResultsInADeterministicTransition() {
        Pair<RealTimeRebecaActorState, InstructionBean> source = new Pair<>(realTimeRebecaActorState, setModeInstructionBean);
        RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result =
                (RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>>)
                        realTimeRebecaSetModeSOSRule.applyRule(source);

        RealTimeRebecaActorState physicalState = result.getDestination().getFirst();
        assertTrue(physicalState.getActiveMode() == setModeInstructionBean.getModeName());
    }

}
