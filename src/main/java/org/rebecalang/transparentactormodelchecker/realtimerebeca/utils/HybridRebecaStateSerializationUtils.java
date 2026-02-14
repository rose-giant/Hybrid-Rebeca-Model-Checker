package org.rebecalang.transparentactormodelchecker.realtimerebeca.utils;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.rilutils.RILEquivalentActorClass;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.*;
import org.springframework.util.SerializationUtils;

public class HybridRebecaStateSerializationUtils {
    public static HybridRebecaNetworkState clone(HybridRebecaNetworkState object) {
        HybridRebecaNetworkState clone = SerializationUtils.clone(object);
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

    public static HybridRebecaActorState clone(HybridRebecaActorState object) {
        HybridRebecaActorState clone = SerializationUtils.clone(object);
        return clone;
    }
    public static HybridRebecaSystemState clone(HybridRebecaSystemState object) {
        HybridRebecaSystemState clone = SerializationUtils.clone(object);

        RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
        for(HybridRebecaActorState state : clone.getActorsStatesValues())
            state.setRILModel(rilModel);

        return clone;
    }

    public static HybridRebecaMessage clone(HybridRebecaMessage object) {
        HybridRebecaMessage clone = SerializationUtils.clone(object);
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
