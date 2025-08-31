package org.rebecalang.modelchecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modelchecker.setting.CoreRebecaModelCheckerSetting;
import org.rebecalang.modelchecker.setting.ModelCheckerSetting;
import org.rebecalang.modelchecker.utils.StateSpaceUtil;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class CoreRebecaModelCheckerTest {
	
	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/corerebeca/"; 

	@Autowired
	@Qualifier("CORE_REBECA")
	public CoreRebecaModelChecker coreRebecaModelChecker = new CoreRebecaModelChecker();
	public ExceptionContainer exceptionContainer = new ExceptionContainer();
	protected GenericApplicationContext appContext = new GenericApplicationContext();

	@Test
	public void GIVEN_RebecaModel_WHEN_No_Error() throws ModelCheckingException, FileNotFoundException {
		String filename = "pingpong.rebeca";
		Policy policy = Policy.COARSE_GRAINED_POLICY;

		File model = new File(MODEL_FILES_BASE + filename);
		ModelCheckerSetting modelCheckerSetting = new CoreRebecaModelCheckerSetting(new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3, policy);

		coreRebecaModelChecker.modelCheck(model, modelCheckerSetting);

//		if(!exceptionContainer.exceptionsIsEmpty())
//			System.out.println(exceptionContainer);
//
//		Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

		StateSpace<State<? extends BaseActorState<?>>> stateSpace = coreRebecaModelChecker.getStateSpace();
		State<ActorState> initialState = (State<ActorState>) stateSpace.getInitialState();
		StateSpaceUtil.printStateSpace(initialState,
				new PrintStream(new FileOutputStream(new File(policy + filename))));
	}
}
