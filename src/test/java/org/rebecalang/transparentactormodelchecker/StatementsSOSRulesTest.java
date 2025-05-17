package org.rebecalang.transparentactormodelchecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaAssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaVariableDeclarationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class StatementsSOSRulesTest {

	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/transparentactormodelchecker/"; 

	@Autowired
	public ExceptionContainer exceptionContainer;

	@Autowired
	protected GenericApplicationContext appContext;

    @Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;

    @Autowired
    protected CoreRebecaAssignmentSOSRule assignmentSOSRule;
    @Autowired
    protected CoreRebecaVariableDeclarationSOSRule variableDeclarationSOSRule;

    CoreRebecaActorState coreRebecaActorState;

    @BeforeEach
    public void setup() {
    	coreRebecaActorState = new CoreRebecaActorState("actor1");
    }

//    @Test
//    public void GIVEN_ActorStateIsEmpty_WHEN_DeclarationInstructionIsExecuted_THEN_ANewVariableIsAddedToTheState() {
//    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1");
//    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, dib);
//		variableDeclarationSOSRule.applyRule(state);
//
//		Variable v = new Variable("var1");
//    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
//		state.setSecond(aib);
//		assignmentSOSRule.applyRule(state);
//
//		Assertions.assertEquals(10, state.getFirst().getVariableValue("var1"));
//    }
//
//    @Test
//    public void GIVEN_ActorStateHasThreeVariables_WHEN_AssignmentInstructionIsExecuted_THEN_CalculatedValueHasToSetInTheState() {
//
//    	coreRebecaActorState.addVariableToScope("var1", 1);
//    	coreRebecaActorState.addVariableToScope("var2", 2);
//    	coreRebecaActorState.addVariableToScope("var3", 3);
//
//		Variable v1 = new Variable("var1");
//		Variable v2 = new Variable("var2");
//		Variable v3 = new Variable("var3");
//    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v1, v2, v3, "-");
//    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, aib);
//		state.setSecond(aib);
//		assignmentSOSRule.applyRule(state);
//
//		Assertions.assertEquals(state.getFirst().getVariableValue("var1"), -1);
//    }
//
	@ParameterizedTest
	@MethodSource("modelToStateSpace")
	public void GIVEN_RebecaModel_WHEN_No_Error(String filename, int statespaceSize, Policy policy) throws ModelCheckingException, FileNotFoundException {
		File model = new File(MODEL_FILES_BASE + filename);

		Pair<RebecaModel, SymbolTable> compiledRebecaFile =
				rebecaModelCompiler.compileRebecaFile(model, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);

		for(String methodName : transformedRILModel.getMethodNames()) {
			System.out.println(methodName);
			int counter = 0;
			for(InstructionBean instruction : transformedRILModel.getInstructionList(methodName)) {
				System.out.println("" + counter++ +":" + instruction);
			}
			System.out.println("...............................................");
		}

//		coreRebecaModelChecker.modelCheck(model, modelCheckerSetting);
//
//		if(!exceptionContainer.exceptionsIsEmpty())
//			System.out.println(exceptionContainer);
//
//		Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
//
//		StateSpace<State<? extends BaseActorState<?>>> stateSpace = coreRebecaModelChecker.getStateSpace();
//		State<ActorState> initialState = (State<ActorState>) stateSpace.getInitialState();
//		StateSpaceUtil.printStateSpace(initialState,
//				new PrintStream(new FileOutputStream(new File(policy + filename))));
//
//		Assertions.assertEquals(statespaceSize, stateSpace.size());
	}

	protected static Stream<Arguments> modelToStateSpace() {
	    return Stream.of(
//	    		Arguments.arguments("pingpong.rebeca", 3, Policy.COARSE_GRAINED_POLICY)
	    		Arguments.arguments("pingpong.rebeca", 12, Policy.FINE_GRAINED_POLICY)
	    );
	}
}