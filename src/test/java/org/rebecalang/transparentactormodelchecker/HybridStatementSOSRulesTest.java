package org.rebecalang.transparentactormodelchecker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.HybridRebecaSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class})
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class HybridStatementSOSRulesTest {

    @Autowired
    HybridRebecaSOSRule sosRule;
    HybridRebecaSystemState hybridRebecaSystemState;

    @Autowired
    RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    Rebeca2RILModelTransformer rebeca2RIL;

    public final static String ACTOR_1_ID = "actor1";
    public final static String ACTOR_2_ID = "actor2";

    protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    @BeforeEach
    public void setup() {
        hybridRebecaSystemState = new HybridRebecaSystemState();
        hybridRebecaSystemState.setEnvironment(new Environment());
        hybridRebecaSystemState.setNetworkState(new HybridRebecaNetworkState());
        hybridRebecaSystemState.setActorState(ACTOR_1_ID, new HybridRebecaActorState(ACTOR_1_ID));
        hybridRebecaSystemState.setActorState(ACTOR_2_ID, new HybridRebecaActorState(ACTOR_2_ID));
    }

    private String HYBRID_MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/hybridrebeca/";

    @Test
    public void testyTest() {
        String modelName = "heaterWithOnePhysicalClass";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println(counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }


    }
}














