package org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.HybridRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.utils.HybridRebecaStateSerializationUtils;

import java.util.Iterator;

import static java.lang.Math.min;

public class HybridRebecaResumeSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {
    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        Pair<Float, Float> now = source.getFirst().getNow();
        Pair<Float, Float> resumeTime = source.getFirst().getResumeTime();
        if (resumeTime.getFirst().floatValue() < now.getSecond().floatValue() &&
            now.getSecond().floatValue() < resumeTime.getSecond().floatValue()) {
            HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaNondeterministicTransition<>();
            HybridRebecaActorState backup1 = HybridRebecaStateSerializationUtils.clone(source.getFirst());
            backup1.setSuspent(false);
            backup1.setResumeTime(backup1.getNow());
            Pair<HybridRebecaActorState, InstructionBean> newSource = new Pair<>(backup1, source.getSecond());
            result.addDestination(Action.TAU, newSource);

            //postpone transition
            HybridRebecaActorState backup2 = HybridRebecaStateSerializationUtils.clone(source.getFirst());
            backup2.setResumeTime(new Pair<>(now.getSecond(), resumeTime.getSecond()));
            backup2.setSuspent(true);
            Pair<HybridRebecaActorState, InstructionBean> newSource2 = new Pair<>(backup2, source.getSecond());
            result.addDestination(Action.TAU, newSource2);
//            System.out.println("Resume and Postpone" + backup2.getResumeTime() + backup1.getResumeTime());
            return result;
        }
        else if ((resumeTime.getFirst().floatValue() < now.getSecond().floatValue() ||
                now.getSecond().floatValue() == resumeTime.getSecond().floatValue())
                && !(now.getSecond().floatValue() < resumeTime.getSecond().floatValue()) ) {
            HybridRebecaActorState backup = HybridRebecaStateSerializationUtils.clone(source.getFirst());
            backup.setResumeTime(backup.getNow());
            backup.setSuspent(false);
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaDeterministicTransition<>();
            source.setFirst(backup);
            result.setDestination(source);
            result.setAction(Action.TAU);
//            System.out.println("Resume" + backup.getResumeTime());
            return result;
        }

        //TODO: what if none is applicable?
        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result = new HybridRebecaDeterministicTransition<>();
        result.setAction(null);
        result.setDestination(source);
        return result;
    }

    public HybridRebecaAbstractTransition<HybridRebecaSystemState> systemLevelResumePostpone(HybridRebecaSystemState source) {
        HybridRebecaAbstractTransition<HybridRebecaSystemState> result = null;
        boolean applicable = false;
        for(String actorId : source.getActorsState().keySet()) {
            HybridRebecaActorState hybridRebecaActorState = source.getActorState(actorId);
            RILModel rilModel = hybridRebecaActorState.getRILModel();
            if (hybridRebecaActorState.isSuspent()){
//                HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
                HybridRebecaActorState backupActor = HybridRebecaStateSerializationUtils.clone(hybridRebecaActorState);
                backupActor.setRILModel(hybridRebecaActorState.getRILModel());
                HybridRebecaResumeSOSRule rebecaResumeSOSRule = new HybridRebecaResumeSOSRule();
                HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> resumePostponeResult = rebecaResumeSOSRule.applyRule(new Pair<>(backupActor, new InstructionBean() {}));

                if (resumePostponeResult instanceof HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) {
                    if (((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) resumePostponeResult).getAction() == null){
                        return null;
                    }
                    HybridRebecaSystemState backup = HybridRebecaStateSerializationUtils.clone(source);
                    result = new HybridRebecaDeterministicTransition<>();
                    HybridRebecaActorState newState = ((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) resumePostponeResult).getDestination().getFirst();
                    newState.setRILModel(rilModel);
                    backup.setActorState(actorId, newState);
                    applicable = true;
                    ((HybridRebecaDeterministicTransition<HybridRebecaSystemState>) result).setDestination(backup);
                    ((HybridRebecaDeterministicTransition<HybridRebecaSystemState>) result).setAction(Action.TAU);
                    return result;
                }
                else if(resumePostponeResult instanceof HybridRebecaNondeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) {
                    result = new HybridRebecaNondeterministicTransition<>();
                    Iterator<Pair<? extends Action, Pair<HybridRebecaActorState, InstructionBean>>> transitionsIterator = resumePostponeResult.getDestinations().iterator();
                    while(transitionsIterator.hasNext()) {
                        HybridRebecaSystemState backupb = HybridRebecaStateSerializationUtils.clone(source);
                        Pair<? extends Action, Pair<HybridRebecaActorState, InstructionBean>> transition = transitionsIterator.next();
                        HybridRebecaActorState actorState = transition.getSecond().getFirst();
                        actorState.setRILModel(rilModel);
                        backupb.setActorState(actorState.getId(), actorState);
                        ((HybridRebecaNondeterministicTransition<HybridRebecaSystemState>) result).addDestination(Action.TAU, backupb);
                    }
                    applicable = true;
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }
}