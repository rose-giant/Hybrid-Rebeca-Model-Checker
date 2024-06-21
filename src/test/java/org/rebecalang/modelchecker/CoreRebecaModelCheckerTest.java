package org.rebecalang.modelchecker;

import java.io.File;
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
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class}) 
@SpringJUnitConfig
public class CoreRebecaModelCheckerTest {
	
	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/"; 

	@Autowired
	public CoreRebecaModelChecker coreRebecaModelChecker;
	
	@Autowired
	public ExceptionContainer exceptionContainer;

	
	@ParameterizedTest
	@MethodSource("modelToStateSpace")
	public void GIVEN_RebecaModel_WHEN_CoarseGrainedPolicy(String filename, int statespaceSize) throws ModelCheckingException {
		File model = new File(MODEL_FILES_BASE + filename);
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.FINE_GRAINED_POLICY);
		coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_1);
		
		
		if(!exceptionContainer.exceptionsIsEmpty())
			System.out.println(exceptionContainer);
		
		Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

		Assertions.assertEquals(statespaceSize, coreRebecaModelChecker.getStatespace().size());
		
		StateSpaceUtil.printStateSpace(coreRebecaModelChecker.getStatespace().getInitialState());
		
	}
	
	protected static Stream<Arguments> modelToStateSpace() {
	    return Stream.of(
	    		Arguments.arguments("pingpong.rebeca", 3)//,
//	    		Arguments.arguments("DiningPhilosophers.rebeca", 105),
//	    		Arguments.arguments("corerebeca/UntimedWSAN.rebeca", 122)
	    );
	}

}
