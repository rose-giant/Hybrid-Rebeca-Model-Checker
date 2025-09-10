package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action;

import org.rebecalang.compiler.utils.Pair;

public class TimeProgressAction extends Action {

    private Pair<Float, Float> intervalTimeProgress;
    private Pair<Pair<Float, Float>, Pair<Float, Float>> nonDetIntervalTimeProgress;

    public void setTimeProgress(Pair<Float, Float> intervalTimeProgress) {
        this.intervalTimeProgress = intervalTimeProgress;
    }
    public Pair<Float, Float> getIntervalTimeProgress() {
        return intervalTimeProgress;
    }

    public void setTimeIntervalProgress(Pair<Pair<Float, Float>, Pair<Float, Float>> intervalTimeProgress) {
        this.nonDetIntervalTimeProgress = intervalTimeProgress;
    }
    public Pair<Pair<Float, Float>, Pair<Float, Float>> getNonDetIntervalTimeProgress() {
        return nonDetIntervalTimeProgress;
    }

    @Override
    public String getActionLabel() {
        return "tau";
    }
}
