package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action;

public abstract class Action {
    public final static Action TAU = new Action() {
        public String getActionLabel() {
            return "tau";
        }
    };

    public abstract String getActionLabel();
}
