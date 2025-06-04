package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import java.io.Serializable;

public class HybridRebecaPhysicalState extends HybridRebecaActorState implements Serializable {
    public HybridRebecaPhysicalState(String id) {
        super(id);
    }

    private String activeMode;

    public String getActiveMode() {
        return activeMode;
    }

    public void setActiveMode(String activeMode) {
        this.activeMode = activeMode;
    }
}
