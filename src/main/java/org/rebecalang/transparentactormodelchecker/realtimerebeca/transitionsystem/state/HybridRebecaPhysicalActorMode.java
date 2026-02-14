package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;

public class HybridRebecaPhysicalActorMode {
    public Expression invariant;
    public BlockStatement invariantBlock;
    public Expression guard;
    public BlockStatement guardBlock;
    public String modeName;
}
