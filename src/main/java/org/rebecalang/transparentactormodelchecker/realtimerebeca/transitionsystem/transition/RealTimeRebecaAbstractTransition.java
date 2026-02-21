package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;

import java.util.List;

public abstract class RealTimeRebecaAbstractTransition<H> {
    private List<Pair<? extends Action, H>> destinations;
    public List<Pair<? extends Action,H>> getDestinations() {
        return destinations;
    }
}
