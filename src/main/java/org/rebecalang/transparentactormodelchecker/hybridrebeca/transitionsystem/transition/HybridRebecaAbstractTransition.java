package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;

import java.util.List;

public abstract class HybridRebecaAbstractTransition<H> {
    private List<Pair<? extends Action, H>> destinations;
    public List<Pair<? extends Action,H>> getDestinations() {
        return destinations;
    }
}
