package org.rebecalang.modelchecker.corerebeca.policy;

import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modeltransformer.ril.rilinstructions.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.PushARInstructionBean;

public class FineGrainedPolicy extends AbstractPolicy{

	@Override
	public void executedInstruction(InstructionBean ib) {
		if(ib instanceof PopARInstructionBean)
			breakable = false;
		else if(ib instanceof PushARInstructionBean)
			breakable = false;
		else if(ib instanceof DeclarationInstructionBean)
			breakable = false;
		else
			breakable = true;
	}

	@Override
	public void pick(MessageSpecification executableMessage) {
		breakable = true;
	}
}
