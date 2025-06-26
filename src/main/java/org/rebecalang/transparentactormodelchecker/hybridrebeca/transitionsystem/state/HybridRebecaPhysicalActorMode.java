package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;

public class HybridRebecaPhysicalActorMode {
    public Expression invariant;
    public BlockStatement invariantBlock;
    public Expression guard;
    public BlockStatement guardBlock;
    public String modeName;
}
