package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action;

import org.rebecalang.compiler.utils.Pair;

public class TimeProgressAction extends Action {

    private float timeProgress;
    private Pair<Float, Float> intervalTimeProgress;

    public float getTimeProgress() {
        return timeProgress;
    }

    public void setTimeProgress(Pair<Float, Float> intervalTimeProgress) {
        this.intervalTimeProgress = intervalTimeProgress;
    }

    public Pair<Float, Float> getIntervalTimeProgress() {
        return intervalTimeProgress;
    }

//    public void setTimeIntervalProgress(float timeProgress) {
//        this.timeProgress = timeProgress;
//    }

    @Override
    public String getActionLabel() {
        return "tau";
    }
}
