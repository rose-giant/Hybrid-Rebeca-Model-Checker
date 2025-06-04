package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import java.io.Serializable;

public class HybridRebecaActorState extends CoreRebecaActorState implements Serializable {
    private Pair<Integer, Integer> resumeTimeInterval;
    private Pair<Integer, Integer> nowInterval;

    public Pair<Integer, Integer> getNow() {
        return nowInterval;
    }

    public int getLowerBound(Pair<Integer, Integer> interval) {
        return interval.getFirst();
    }

    public int getUpperBound(Pair<Integer, Integer> interval) {
        return interval.getSecond();
    }

    public Pair<Integer, Integer> getResumeTime() {
        return resumeTimeInterval;
    }

    public void setResumeTime(Pair<Integer, Integer> resumeTimeInterval) {
        this.resumeTimeInterval = resumeTimeInterval;
    }

    public void setNow(Pair<Integer, Integer> nowInterval) {
        this.nowInterval = nowInterval;
    }

    public HybridRebecaActorState(String id) {
        super(id);
    }

}
