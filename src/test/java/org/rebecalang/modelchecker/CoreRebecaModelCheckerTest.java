package org.rebecalang.modelchecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.utils.StateSpaceUtil;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class CoreRebecaModelCheckerTest {
	
	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/corerebeca/"; 

	@Autowired
	public CoreRebecaModelChecker coreRebecaModelChecker;
	
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@ParameterizedTest
	@MethodSource("modelToStateSpace")
	public void GIVEN_RebecaModel_WHEN_No_Error(String filename, int statespaceSize, String policy) throws ModelCheckingException, FileNotFoundException {
		File model = new File(MODEL_FILES_BASE + filename);
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		coreRebecaModelChecker.configPolicy(policy);
		coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
		
		if(!exceptionContainer.exceptionsIsEmpty())
			System.out.println(exceptionContainer);
		
		Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

		StateSpaceUtil.printStateSpace(coreRebecaModelChecker.getStatespace().getInitialState(),
				new PrintStream(new FileOutputStream(new File(policy + filename))));
		
		Assertions.assertEquals(statespaceSize, coreRebecaModelChecker.getStatespace().size());
	}
	
	protected static Stream<Arguments> modelToStateSpace() {
	    return Stream.of(
	    		Arguments.arguments("pingpong.rebeca", 3, CoreRebecaModelChecker.COARSE_GRAINED_POLICY)
	    		, Arguments.arguments("pingpong.rebeca", 12, CoreRebecaModelChecker.FINE_GRAINED_POLICY)
	    		, Arguments.arguments("DiningPhilosophers.rebeca", 105, CoreRebecaModelChecker.COARSE_GRAINED_POLICY)
	    		, Arguments.arguments("DiningPhilosophers.rebeca", 98339, CoreRebecaModelChecker.FINE_GRAINED_POLICY)
	    		, Arguments.arguments("UntimedWSAN.rebeca", 122, CoreRebecaModelChecker.COARSE_GRAINED_POLICY)
	    		, Arguments.arguments("UntimedWSAN.rebeca", 37504, CoreRebecaModelChecker.FINE_GRAINED_POLICY)
	    		, Arguments.arguments("LeaderElection.rebeca", 1626, CoreRebecaModelChecker.COARSE_GRAINED_POLICY)
	    );
	}

}
