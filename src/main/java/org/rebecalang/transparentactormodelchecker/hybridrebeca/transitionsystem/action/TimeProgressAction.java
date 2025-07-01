package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action;

public class TimeProgressAction extends Action {

    private float timeProgress;

    public float getTimeProgress() {
        return timeProgress;
    }

    public void setTimeProgress(float timeProgress) {
        this.timeProgress = timeProgress;
    }

    @Override
    public String getActionLabel() {
        return "tau";
    }
}
