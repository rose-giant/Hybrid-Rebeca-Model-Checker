package org.rebecalang.transparentactormodelchecker.hybrid;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.compositionlevelsosrules.ApplySystemLevelRules;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils.ActorClassMakerFromRIL;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils.GenerateInitialState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils.RILEquivalentActorClass;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.*;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class StateSpaceGenTest {
    @Autowired
    Rebeca2RILModelTransformer rebeca2RIL;

    @Autowired
    RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    public ExceptionContainer exceptionContainer;

    private String HYBRID_MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/hybridrebeca/";

    protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    @Test
    public void InvariantConditionIsTransformedToRils() {
        String modelName = "sample3";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
//        ActorClassMakerFromRIL actorClassMakerFromRIL = new ActorClassMakerFromRIL(transformModel);
        GenerateInitialState generateInitialState = new GenerateInitialState(transformModel);
        ApplySystemLevelRules applySystemLevelRules = new ApplySystemLevelRules(generateInitialState.getInitialState());
//        System.out.println(generateInitialState.getInitialState());

        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }

    @Test
    public void messageSerialization() {
        HybridRebecaMessage message = new HybridRebecaMessage();
        message.setReceiver(new HybridRebecaActorState("sender"));
        message.setSender(new HybridRebecaActorState("receiver"));
        message.setMessageArrivalInterval(new Pair<>((float)1,(float)3));
        message.setName("message");
        HybridRebecaMessage clone = HybridRebecaStateSerializationUtils.clone(message);
        System.out.println(clone);
    }

    @Test
    public void actorSerialization() {
        HybridRebecaActorState actorState = new HybridRebecaActorState("actor");
        actorState.addScope("method0");
        actorState.setRilEquivalentActorClass(new RILEquivalentActorClass());
        actorState.setNow(new Pair<>((float)1,(float)3));
        actorState.setResumeTime(new Pair<>((float)1,(float)3));
        actorState.setEnvironment(new Environment());
        actorState.setActiveMode("none");
        RILModel rilModel = new RILModel();
        ArrayList<InstructionBean> instructions = new ArrayList<>();
        Object a = new Object();
        Object b = new Object();
        instructions.add(new AssignmentInstructionBean(a, b, null, "="));
        rilModel.addMethod("method0", instructions);
        actorState.setRILModel(rilModel);

//        actorState.setSigma(instructions);

        HybridRebecaActorState clone = HybridRebecaStateSerializationUtils.clone(actorState);
        System.out.println(clone);

        HybridRebecaSystemState systemState = new HybridRebecaSystemState();
        systemState.setInputInterval(new Pair<>((float)1,(float)3));
        systemState.setActorState("actor", actorState);
        systemState.setNetworkState(new HybridRebecaNetworkState());
        systemState.setEnvironment(new Environment());
        systemState.setNow(new Pair<>((float)1,(float)3));

        HybridRebecaSystemState clone2 = HybridRebecaStateSerializationUtils.clone(systemState);
        System.out.println(clone2);
    }

    @Test
    public void networkSerialization() {
        HybridRebecaNetworkState networkState = new HybridRebecaNetworkState();
        Pair<String, String > firstPair = new Pair<>("sender", "receiver");
        ArrayList<HybridRebecaMessage> messages = new ArrayList<>();

        HybridRebecaMessage message = new HybridRebecaMessage();
        message.setReceiver(new HybridRebecaActorState("sender"));
        message.setSender(new HybridRebecaActorState("receiver"));
        message.setMessageArrivalInterval(new Pair<>((float)1,(float)3));
        message.setName("message");

        messages.add(message);

        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> receivedMessages = new HashMap<>();
        receivedMessages.put(firstPair, messages);
        networkState.setReceivedMessages(receivedMessages);
        HybridRebecaNetworkState clone_ = HybridRebecaStateSerializationUtils.clone(networkState);
    }

    @Test
    public void envSerialization() {
        Environment environment = new Environment();
        HashMap<String, Serializable> envVars = new HashMap<>();
        Serializable variable = new Variable("varName");
        envVars.put("var0", variable);
        environment.setVariableValue("var0", variable);
        Environment clone = HybridRebecaStateSerializationUtils.clone(environment);
    }

    @Test
    public void systemStateSerialization() {
        HybridRebecaSystemState systemState = new HybridRebecaSystemState();
//        HybridRebecaActorState actorState = new HybridRebecaActorState("actor0");
        HybridRebecaActorState actorState = new HybridRebecaActorState("actor");
//        actorState.setSigma(new ArrayList<>());
        RILEquivalentActorClass rilEq = new RILEquivalentActorClass();
        ArrayList<InstructionBean> instructions = new ArrayList<>();
        instructions.add(new PushARInstructionBean());
        Object a = new Object();
        Object b = new Object();
        instructions.add(new AssignmentInstructionBean(a, b, null, "="));
        instructions.add(new EndMethodInstructionBean());
        instructions.add(new PopARInstructionBean());
        HashMap<String, ArrayList<InstructionBean>> methods = new HashMap<>();
        rilEq.setMethods(methods);
        actorState.setRilEquivalentActorClass(rilEq);
        actorState.setNow(new Pair<>((float)1,(float)3));
        actorState.setResumeTime(new Pair<>((float)1,(float)3));
        actorState.setEnvironment(new Environment());
        actorState.setActiveMode("none");


//        actorState.setSigma(instructions);

        HybridRebecaMessage message2 = new HybridRebecaMessage();
        message2.setReceiver(new HybridRebecaActorState("sender"));
        message2.setSender(new HybridRebecaActorState("receiver"));
        message2.setMessageArrivalInterval(new Pair<>((float)1,(float)3));
        message2.setName("message");
        actorState.receiveMessage(message2);

        HybridRebecaNetworkState networkState = new HybridRebecaNetworkState();
        Pair<String, String > firstPair = new Pair<>("sender", "receiver");
        ArrayList<HybridRebecaMessage> messages = new ArrayList<>();
        HybridRebecaMessage message = new HybridRebecaMessage();
        message.setReceiver(new HybridRebecaActorState("sender"));
        message.setSender(new HybridRebecaActorState("receiver"));
        message.setMessageArrivalInterval(new Pair<>((float)1,(float)3));
        message.setName("message");
        messages.add(message);
        HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> receivedMessages = new HashMap<>();
        receivedMessages.put(firstPair, messages);
        networkState.setReceivedMessages(receivedMessages);

        Environment environment = new Environment();
        HashMap<String, Serializable> envVars = new HashMap<>();
        Serializable variable = new Variable("varName");
        envVars.put("var0", variable);
        environment.setVariableValue("var0", variable);

        Pair<Float, Float> now = new Pair<>((float) 1, (float) 2);
        Pair<Float, Float> inputInterval = new Pair<>((float) 1, (float) 2);

        systemState.setNow(now);
        systemState.setInputInterval(inputInterval);
        systemState.setActorState("actor0",actorState);
        systemState.setNetworkState(networkState);
        systemState.setEnvironment(environment);
        HybridRebecaSystemState clone = HybridRebecaStateSerializationUtils.clone(systemState);
    }

}