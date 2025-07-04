package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelTransformerConfig.class})
@Component
public class TransparentActorModelChecker {
    @Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected ExceptionContainer exceptionContainer;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;

    @Autowired
    protected ExternalMethodRepository externalMethodRepository;

    @Autowired
    protected ConfigurableApplicationContext appContext;

    protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    public TransparentActorModelChecker() {
//        rebecaModelCompiler = new RebecaModelCompiler();
//        exceptionContainer = new ExceptionContainer();

        String modelName = "main";
        File model = new File("./" + modelName + ".rebeca");
        Set<CompilerExtension> extension;
        extension = new HashSet<>();
        extension.add(CompilerExtension.HYBRID_REBECA);
        Pair<RebecaModel, SymbolTable> compilationResult = compileModel(model, extension, CoreVersion.CORE_2_3);
        RILModel transformModel = rebeca2RILModelTransformer.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
        System.out.println(transformModel);
    }

    public static void main(String[] args) {
        TransparentActorModelChecker transparentActorModelChecker = new TransparentActorModelChecker();
//        transparentActorModelChecker.TransparentActorModelChecker();
    }

}

