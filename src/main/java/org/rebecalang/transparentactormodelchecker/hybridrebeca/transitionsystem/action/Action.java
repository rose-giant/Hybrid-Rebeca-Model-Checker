package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action;

public abstract class Action {
    public final static org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action TAU = new org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action() {
        @Override
        public String getActionLable() {
            return "tau";
        }
    };

    public abstract String getActionLabel();
}
