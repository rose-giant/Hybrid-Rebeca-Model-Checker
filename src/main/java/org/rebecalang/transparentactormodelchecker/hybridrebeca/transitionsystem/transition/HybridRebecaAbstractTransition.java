package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;

import java.util.List;

public abstract class HybridRebecaAbstractTransition<H> {
    public List<Pair<? extends Action,H>> getDestinations() {
        return null;
    }
}
