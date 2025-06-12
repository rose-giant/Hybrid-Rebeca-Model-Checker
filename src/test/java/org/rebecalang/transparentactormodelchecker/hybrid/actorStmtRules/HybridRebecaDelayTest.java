package org.rebecalang.transparentactormodelchecker.hybrid.actorStmtRules;

import org.junit.jupiter.api.Test;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.HybridRebecaSendMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;

public class HybridRebecaDelayTest {
    HybridRebecaSendMessageSOSRule hybridRebecaSendMessageSOSRule = new HybridRebecaSendMessageSOSRule();
    HybridRebecaActorState hybridRebecaActorState1 = new HybridRebecaActorState("actor1");
    HybridRebecaActorState hybridRebecaActorState2 = new HybridRebecaActorState("actor2");
    Environment environment = new Environment();

    @Test
    public void nonDetDelayMakesResumeTimeProgress() {

    }

}
