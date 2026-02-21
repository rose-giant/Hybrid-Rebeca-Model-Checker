package org.rebecalang.transparentactormodelchecker.realtimerebeca;

import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules.ApplySystemLevelRules;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.rilutils.GenerateInitialState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class RealTimeRebecaRunner {
    private static String RealTime_MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/realtimerebeca/";
    @Autowired
    static Rebeca2RILModelTransformer rebeca2RIL;

    @Autowired
    static RebecaModelCompiler rebecaModelCompiler = new RebecaModelCompiler();

    protected static Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        CompilerConfig.class,
                        ModelTransformerConfig.class
                );

        rebecaModelCompiler = context.getBean(RebecaModelCompiler.class);
        rebeca2RIL = context.getBean(Rebeca2RILModelTransformer.class);

        String modelName = "motivatingexample";
        File model = new File(RealTime_MODEL_FILES_BASE + modelName + ".rebeca");

        System.out.println("Model is: " + model.getAbsolutePath());

        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);

        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        Pair<Float, Float> inputInterval = new Pair<>(0f, 300f);

        RILModel transformModel = rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        GenerateInitialState generateInitialState =
                new GenerateInitialState(transformModel, inputInterval);

        ApplySystemLevelRules applySystemLevelRules =
                new ApplySystemLevelRules(generateInitialState.getInitialState());

        System.out.println("Execution started successfully and output was written in output.dot, paste the content in graghviz online to see the visualized state space nicely!");
    }
}
