package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;

import java.io.Serializable;

public abstract class HybridRebecaAbstractState extends CoreRebecaActorState implements Serializable {
    public HybridRebecaAbstractState(String id) {
        super(id);
    }
}
