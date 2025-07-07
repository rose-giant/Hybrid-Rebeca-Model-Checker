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
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils.ActorClassMakerFromRIL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.io.File;
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
        String modelName = "main";  // Using the simple "main" model here
        File model = new File(HYBRID_MODEL_FILES_BASE + modelName + ".rebeca");
        System.out.println("model is" + model);
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        // Transform Rebeca model to RILS
        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        ActorClassMakerFromRIL actorClassMakerFromRIL = new ActorClassMakerFromRIL(transformModel);
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