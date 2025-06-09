package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action;

import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;

public class TakeMessageAction {
    private HybridRebecaMessage message;

    public TakeMessageAction(HybridRebecaMessage message) {
        this.message = message;
    }
    public HybridRebecaMessage getMessage() {
        return message;
    }
    public void setMessage(HybridRebecaMessage message) {
        this.message = message;
    }

    public String getActionLabel() {
        return message.getName();
    }

    public String toString() {
        return message.toString();
    }
}
