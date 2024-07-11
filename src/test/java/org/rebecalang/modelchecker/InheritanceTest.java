package org.rebecalang.modelchecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.StateSpace;
import org.rebecalang.modelchecker.utils.StateSpaceUtil;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class InheritanceTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/inheritance/";

    @Autowired
    public CoreRebecaModelChecker coreRebecaModelChecker;

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Test
    public void checkInstanceof() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "pingpongpung.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
        StateSpace<ActorState> statespace = coreRebecaModelChecker.getStatespace();
		Assertions.assertEquals(14, statespace.size());
//		StateSpaceUtil.printStateSpace(statespace.getInitialState(),
//				new PrintStream(new FileOutputStream(new File("ppp.dot"))));
    }

    @Test
    public void useParentMsgsrvs() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "useParentMsgsrvs.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
        StateSpace<ActorState> statespace = coreRebecaModelChecker.getStatespace();
		Assertions.assertEquals(2, statespace.size());
//		StateSpaceUtil.printStateSpace(statespace.getInitialState(),
//				new PrintStream(new FileOutputStream(new File("ppp.dot"))));
    }

    private void printExceptions() {
        Collection<Set<Exception>> exceptions = exceptionContainer.getExceptions().values();
        for (Set<Exception> exceptionCollection : exceptions) {
            for (Exception exception : exceptionCollection) {
                System.out.println(exception);
            }

        }

    }
}
