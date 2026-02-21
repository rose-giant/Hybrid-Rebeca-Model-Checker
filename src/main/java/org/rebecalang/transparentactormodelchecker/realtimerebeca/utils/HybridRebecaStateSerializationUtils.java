package org.rebecalang.transparentactormodelchecker.realtimerebeca.utils;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.rilutils.RILEquivalentActorClass;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.*;
import org.springframework.util.SerializationUtils;

public class HybridRebecaStateSerializationUtils {
    public static RealTimeRebecaNetworkState clone(RealTimeRebecaNetworkState object) {
        RealTimeRebecaNetworkState clone = SerializationUtils.clone(object);
        return clone;
    }

    public static Pair clone(Pair pair) {
        Pair clone = SerializationUtils.clone(pair);
        return clone;
    }

    public static Environment clone(Environment environment) {
        Environment clone = SerializationUtils.clone(environment);
        return clone;
    }

    public static RealTimeRebecaActorState clone(RealTimeRebecaActorState object) {
        RealTimeRebecaActorState clone = SerializationUtils.clone(object);
        return clone;
    }
    public static RealTimeRebecaSystemState clone(RealTimeRebecaSystemState object) {
        RealTimeRebecaSystemState clone = SerializationUtils.clone(object);

        RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
        for(RealTimeRebecaActorState state : clone.getActorsStatesValues())
            state.setRILModel(rilModel);

        return clone;
    }

    public static RealTimeRebecaMessage clone(RealTimeRebecaMessage object) {
        RealTimeRebecaMessage clone = SerializationUtils.clone(object);
        return clone;
    }

//    public static

    public static RILEquivalentActorClass clone(RILEquivalentActorClass object) {
        RILEquivalentActorClass clone = SerializationUtils.clone(object);

//        RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
//        for(HybridRebecaActorState state : clone.getActorsStatesValues())
//            state.setRILModel(rilModel);

        return clone;
    }

    public static ActorScope clone(ActorScope actorScope) {
        ActorScope clone = SerializationUtils.clone(actorScope);
        return clone;
    }
}
