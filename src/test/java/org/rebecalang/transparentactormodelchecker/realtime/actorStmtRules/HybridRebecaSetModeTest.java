package org.rebecalang.transparentactormodelchecker.realtime.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartSetModeInstructionBean;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.HybridRebecaSetModeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HybridRebecaSetModeTest {
    HybridRebecaSetModeSOSRule hybridRebecaSetModeSOSRule = new HybridRebecaSetModeSOSRule();
    HybridRebecaActorState hybridRebecaActorState = new HybridRebecaActorState("actor1");
    StartSetModeInstructionBean setModeInstructionBean = new StartSetModeInstructionBean("on");

    @Test
    public void setModeResultsInADeterministicTransition() {
        Pair<HybridRebecaActorState, InstructionBean> source = new Pair<>(hybridRebecaActorState, setModeInstructionBean);
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                (HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>)
                        hybridRebecaSetModeSOSRule.applyRule(source);

        HybridRebecaActorState physicalState = result.getDestination().getFirst();
        assertTrue(physicalState.getActiveMode() == setModeInstructionBean.getModeName());
    }

}
