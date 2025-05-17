package org.rebecalang.transparentactormodelchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.corerebeca.CoreRebecaSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessage;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class TransitionSystemSOSRulesTest {

	@Autowired
	CoreRebecaSOSRule sosRule;

    CoreRebecaSystemState coreRebecaSystemState;

    public final static String ACTOR_1_ID = "actor1";
    public final static String ACTOR_2_ID = "actor2";

    @BeforeEach
    public void setup() {
    	coreRebecaSystemState = new CoreRebecaSystemState();
    	coreRebecaSystemState.setEnvironment(new Environment());
    	coreRebecaSystemState.setNetworkState(new CoreRebecaNetworkState());
    	coreRebecaSystemState.setActorState(ACTOR_1_ID, new CoreRebecaActorState(ACTOR_1_ID));
    	coreRebecaSystemState.setActorState(ACTOR_2_ID, new CoreRebecaActorState(ACTOR_2_ID));
    }

    @Test
    public void GIVEN_TwoActorsHaveMesssages_WHEN_StartExecution_THEN_TwoTargetStatesHaveToBeGenerated() {
    	CoreRebecaMessage message1 = new CoreRebecaMessage("m1", new HashMap<String, Object>());
    	CoreRebecaActorState actor1 = coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	message1.setReceiver(actor1);
    	actor1.receiveMessage(message1);

		PushARInstructionBean puib = new PushARInstructionBean();
    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1");
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
    	PopARInstructionBean poib = new PopARInstructionBean();
    	EndMsgSrvInstructionBean emib = new EndMsgSrvInstructionBean();
		RILModel rilModel = new RILModel();
		rilModel.addMethod("m1", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));

    	CoreRebecaMessage message2 = new CoreRebecaMessage("m2", new HashMap<String, Object>());
    	CoreRebecaActorState actor2 = coreRebecaSystemState.getActorState(ACTOR_2_ID);
    	message2.setReceiver(actor2);
		actor2.receiveMessage(message2);
    	aib = new AssignmentInstructionBean(v, 5, null, null);
    	rilModel = new RILModel();
    	rilModel.addMethod("m2", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));

    	actor1.setRILModel(rilModel);
    	actor2.setRILModel(rilModel);

    	CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transition = 
    			(CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState);
    	transition = (CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState);

		for (int i = 0; i < transition.getDestinations().size(); i++) {
			System.out.println("destination " + i + " is " + transition.getDestinations().get(i));
		}
		Assertions.assertEquals(2, transition.getDestinations().size());
    }

    @Test
    public void GIVEN_TwoActors_WHEN_SendMessageStatementIsExecuted_THEN_SentMessageHasToBedeliveredToTheTarget() {
    	CoreRebecaActorState actor1 = coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	CoreRebecaActorState actor2 = coreRebecaSystemState.getActorState(ACTOR_2_ID);

    	actor1.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("m1", 0));
    	actor1.addVariableToScope("actor2", actor2);
		PushARInstructionBean puib = new PushARInstructionBean();
		Variable receiver = new Variable(ACTOR_2_ID);
    	MsgsrvCallInstructionBean mcib = new MsgsrvCallInstructionBean(receiver, "m2");
    	PopARInstructionBean poib = new PopARInstructionBean();
    	EndMsgSrvInstructionBean emib = new EndMsgSrvInstructionBean();
		RILModel rilModel = new RILModel();
		rilModel.addMethod("m1", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, mcib, poib, emib)));

		actor1.setRILModel(rilModel);
    	actor2.setRILModel(rilModel);
    	CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transition = 
    			(CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState);
    	transition = (CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState);
		Assertions.assertEquals(1, transition.getDestinations().size());

		transition = (CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState);
		Assertions.assertEquals(2, transition.getDestinations().size());

		transition = (CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState);
		Assertions.assertEquals(2, transition.getDestinations().size());
    }

}