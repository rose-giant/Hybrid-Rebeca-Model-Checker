package org.rebecalang.modelchecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modelchecker.setting.TimedRebecaModelCheckerSetting;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelCheckerFactory;
import org.rebecalang.modelchecker.timedrebeca.TimedState;
import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;
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
public class TimedRebecaTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/timedrebeca/";

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Autowired
    protected TimedRebecaModelCheckerFactory timedRebecaModelCheckerFactory;

    @Test
//    @Disabled
    public void testPingPongForFGTS() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "ping_pong.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        extension.add(CompilerExtension.TIMED_REBECA);
        TimedRebecaModelCheckerSetting timedRebecaModelCheckerSetting = new TimedRebecaModelCheckerSetting(extension, CoreVersion.CORE_2_3, TransitionSystem.TRANSITION_SYSTEM_FGTS, true);

        TimedRebecaModelChecker timedRebecaModelChecker = timedRebecaModelCheckerFactory.getModelChecker(timedRebecaModelCheckerSetting.getTransitionSystem());
        timedRebecaModelChecker.modelCheck(model, timedRebecaModelCheckerSetting);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

        StateSpace<State<? extends BaseActorState<?>>> stateSpace = timedRebecaModelChecker.getStateSpace();
        State<TimedActorState> initialState = (State<TimedActorState>) stateSpace.getInitialState();
        StateSpaceUtil.printTimedStateSpace(initialState,
                new PrintStream(new FileOutputStream(new File(Policy.FINE_GRAINED_POLICY + "ping_pong.rebeca"))));
    }
    @Test
//    @Disabled
    public void testPingPongForFTTS() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "ping_pong.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        extension.add(CompilerExtension.TIMED_REBECA);
        TimedRebecaModelCheckerSetting timedRebecaModelCheckerSetting = new TimedRebecaModelCheckerSetting(extension, CoreVersion.CORE_2_3, TransitionSystem.TRANSITION_SYSTEM_FTTS, true);

        TimedRebecaModelChecker timedRebecaModelChecker = timedRebecaModelCheckerFactory.getModelChecker(timedRebecaModelCheckerSetting.getTransitionSystem());
        timedRebecaModelChecker.modelCheck(model, timedRebecaModelCheckerSetting);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

        StateSpace<State<? extends BaseActorState<?>>> stateSpace = timedRebecaModelChecker.getStateSpace();
        State<TimedActorState> initialState = (State<TimedActorState>) stateSpace.getInitialState();
        StateSpaceUtil.printTimedStateSpace(initialState,
                new PrintStream(new FileOutputStream(new File(Policy.COARSE_GRAINED_POLICY + "ping_pong.rebeca"))));
    }

    @Test
    @Disabled
    public void testDynamicPolymorphism() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "dynamic_polymorphism_in_time.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        extension.add(CompilerExtension.TIMED_REBECA);
        TimedRebecaModelCheckerSetting timedRebecaModelCheckerSetting = new TimedRebecaModelCheckerSetting(extension, CoreVersion.CORE_2_3, TransitionSystem.TRANSITION_SYSTEM_FGTS, true);

        TimedRebecaModelChecker timedRebecaModelChecker = timedRebecaModelCheckerFactory.getModelChecker(timedRebecaModelCheckerSetting.getTransitionSystem());
        timedRebecaModelChecker.modelCheck(model, timedRebecaModelCheckerSetting);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
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
