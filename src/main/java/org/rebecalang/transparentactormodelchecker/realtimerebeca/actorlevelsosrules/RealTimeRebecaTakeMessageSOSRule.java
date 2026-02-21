package org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class RealTimeRebecaTakeMessageSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaActorState> {
    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> applyRule(RealTimeRebecaActorState source) {
        if(source.messageQueueIsEmpty())
            throw new RebecaRuntimeInterpreterException("Execution rule is disabled");
        RealTimeRebecaMessage message = source.getFirstMessage();
        source.addScope(message.getName());
        HashMap<String,Object> parameters = message.getParameters();
        for(Map.Entry<String, Object> entry : parameters.entrySet()) {
            source.addVariableToScope(entry.getKey(), entry.getValue());
        }
        source.addVariableToScope("sender", message.getSender());
        Pair<String, Integer> pc = new Pair<String, Integer>();
        pc.setFirst(message.getName());
        pc.setSecond(0);
        source.addVariableToScope(RealTimeRebecaActorState.PC, pc);
        source.setNow(message.getMessageArrivalInterval());

        RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result =
                new RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState>();
        result.setAction(new TakeMessageAction(message));
        result.setDestination(source);
        return result;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> applyRule(Action synchAction, RealTimeRebecaActorState source) {
        throw new RebecaRuntimeInterpreterException("Actor Level take message rule does not accept input action");
    }

    public boolean isEnable(RealTimeRebecaActorState source) {
        return !source.messageQueueIsEmpty();
    }
}
