package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

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

}
