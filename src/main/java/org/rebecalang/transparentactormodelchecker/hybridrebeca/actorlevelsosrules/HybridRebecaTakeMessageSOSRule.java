package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import java.util.HashMap;
import java.util.Map;

public class HybridRebecaTakeMessageSOSRule extends AbstractHybridSOSRule<HybridRebecaActorState> {
    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        if(source.messageQueueIsEmpty())
            throw new RebecaRuntimeInterpreterException("Execution rule is disabled");
        HybridRebecaMessage message = source.getFirstMessage();
        source.pushToScope();
        HashMap<String,Object> parameters = message.getParameters();
        for(Map.Entry<String, Object> entry : parameters.entrySet()) {
            source.addVariableToScope(entry.getKey(), entry.getValue());
        }
        source.addVariableToScope("sender", message.getSender());
        Pair<String, Integer> pc = new Pair<String, Integer>();
        pc.setFirst(message.getName());
        pc.setSecond(0);
        source.addVariableToScope(HybridRebecaActorState.PC, pc);

        HybridRebecaDeterministicTransition<HybridRebecaActorState> result =
                new HybridRebecaDeterministicTransition<HybridRebecaActorState>();
        result.setAction(new TakeMessageAction(message));
        result.setDestination(source);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        throw new RebecaRuntimeInterpreterException("Actor Level take message rule does not accept input action");
    }

    public boolean isEnable(HybridRebecaActorState source) {
        return !source.hasVariableInScope(HybridRebecaActorState.PC) &&
                !source.messageQueueIsEmpty();
    }
}
