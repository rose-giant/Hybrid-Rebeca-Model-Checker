package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.timedrebeca.rilinterpreter.TimedMsgsrvCallInstructionInterpreter;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("TIMED_REBECA")
public class TimedRebecaModelChecker extends CoreRebecaModelChecker {

    public final static String CURRENT_TIME = "current_time";
    public final static String RESUMING_TIME = "resuming_time";

	protected void initializeStatementInterpreterContainer() {
		super.initializeStatementInterpreterContainer();
		statementInterpreterContainer.registerInterpreter(TimedMsgsrvCallInstructionBean.class,
				appContext.getBean(TimedMsgsrvCallInstructionInterpreter.class));
	}
	@Autowired
	public void setTypeSystem(@Qualifier("TIMED_REBECA") AbstractTypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}
	
    public TimedRebecaModelChecker() {
        super();
    }

}
