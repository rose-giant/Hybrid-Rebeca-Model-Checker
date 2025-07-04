package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HybridRebecaActorState extends HybridRebecaAbstractState implements Serializable {
    public final static String PC = "$PC$";

    transient RILModel rilModel;

    private String id;
    private Environment environment;
    private ArrayList<HashMap<String, Object>> scope;
    private ArrayList<HybridRebecaMessage> queue;

    public HybridRebecaActorState(String id) {
        this.id = id;
        scope = new ArrayList<HashMap<String,Object>>();
        scope.add(new HashMap<String, Object>());
        queue = new ArrayList<HybridRebecaMessage>();
    }

    public String getId() {
        return id;
    }

    public RILModel getRILModel() {
        return rilModel;
    }
    public void setRILModel(RILModel rilModel) {
        this.rilModel = rilModel;
    }

    public void addVariableToScope(String varName) {
        addVariableToScope(varName, null);
    }

    public void addVariableToScope(String varName, Object value) {
        scope.get(0).put(varName, value);
    }

    public void setVariableValue(Variable leftVarName, Object value) {
        for(int cnt = 0; cnt < scope.size(); cnt++) {
            if(!scope.get(cnt).containsKey(leftVarName.getVarName()))
                continue;
            scope.get(cnt).put(leftVarName.getVarName(), value);
            return;
        }
        throw new RebecaRuntimeInterpreterException("variable \"" + leftVarName + "\" not found");
    }

    public Object getVariableValue(String varName) {
        for(int cnt = 0; cnt < scope.size(); cnt++) {
            if(scope.get(cnt).containsKey(varName))
                return scope.get(cnt).get(varName);
        }
        System.out.println("after return");
        return environment.getVariableValue(varName);
    }

    public boolean hasVariableInScope(String varName) {
        for(int cnt = 0; cnt < scope.size(); cnt++) {
            if(scope.get(cnt).containsKey(varName))
                return true;
        }
        return environment.hasVariableInScope(varName);
    }

    public boolean messageQueueIsEmpty() {
        return queue.isEmpty();
    }

    public HybridRebecaMessage getFirstMessage() {
        return queue.remove(0);
    }

    public void receiveMessage(HybridRebecaMessage newMessage) {
        queue.add(newMessage);
        queue = sortMessages(queue);
    }

    private ArrayList<HybridRebecaMessage> sortMessages(ArrayList<HybridRebecaMessage> queue) {
        ArrayList<HybridRebecaMessage> sortedQueue = new ArrayList<>(queue);
        sortedQueue.sort(Comparator.comparing(msg -> msg.getMessageArrivalInterval().getFirst()));
        return sortedQueue;
    }

    public void pushToScope() {
        scope.add(new HashMap<String, Object>());
    }

    public void popFromScope() {
        scope.remove(scope.size() - 1);
    }

    @SuppressWarnings("unchecked")
    public InstructionBean getEnabledInstruction() {
        Pair<String, Integer> pc = (Pair<String, Integer>) getVariableValue(PC);
        ArrayList<InstructionBean> instructionsList =
                rilModel.getInstructionList(pc.getFirst());

        return instructionsList.get(pc.getSecond());
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

//    public String toString() {
//        return id + "\n[scope:(" + scope + "),\n queue:(" + queue + ")]";
//    }

    @SuppressWarnings("unchecked")
    public void movePCtoTheNextInstruction() {
        Pair<String, Integer> pc = (Pair<String, Integer>) getVariableValue(PC);
        pc.setSecond(pc.getSecond() + 1);
    }

    //ask Ehsan --> is line number the same as pc??
    public void jumpToBranchInstruction(int lineNumber) {
        Pair<String, Integer> pc = (Pair<String, Integer>) getVariableValue(PC);
        pc.setSecond(lineNumber);
    }

    private Pair<Float, Float> resumeTimeInterval;
    private Pair<Float, Float> nowInterval;

    public Pair<Float, Float> getNow() {
        return nowInterval;
    }

    public float getUpperBound(Pair<Float, Float> interval) {
        return interval.getSecond();
    }

    public float getLowerBound(Pair<Float, Float> interval) {
        return interval.getFirst();
    }

    public Pair<Float, Float> getResumeTime() {
        return resumeTimeInterval;
    }

    public void setResumeTime(Pair<Float, Float> resumeTimeInterval) {
        this.resumeTimeInterval = resumeTimeInterval;
    }

    public void setNow(Pair<Float, Float> nowInterval) {
        this.nowInterval = nowInterval;
    }

    private String activeMode;

    public String getActiveMode() {
        return activeMode;
    }

    public void setActiveMode(String activeMode) {
        this.activeMode = activeMode;
    }

    public float getMinETA() {
       return this.getFirstMessage().getMessageArrivalInterval().getFirst();
    }
}
