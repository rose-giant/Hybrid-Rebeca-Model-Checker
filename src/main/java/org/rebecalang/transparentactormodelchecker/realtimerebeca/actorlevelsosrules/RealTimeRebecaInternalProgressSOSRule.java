package org.rebecalang.transparentactormodelchecker.realtimerebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.MsgsrvCallWithAfterInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartSetModeInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractRealTimeSOSRule;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.statementlevelsosrules.*;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaActorState;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.transition.RealTimeRebecaNondeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Configurable
@Component
public class RealTimeRebecaInternalProgressSOSRule extends AbstractRealTimeSOSRule<RealTimeRebecaActorState> {
    @Autowired
    RealTimeRebecaAssignmentSOSRule assignmentSOSRule = new RealTimeRebecaAssignmentSOSRule();

    @Autowired
    RealTimeRebecaSendMessageSOSRule sendMessageSOSRule = new RealTimeRebecaSendMessageSOSRule();

    @Autowired
    RealTimeRebecaVariableDeclarationSOSRule variableDeclarationSOSRule = new RealTimeRebecaVariableDeclarationSOSRule();

    @Autowired
    RealTimeRebecaPopSOSRule popSOSRule = new RealTimeRebecaPopSOSRule();

    @Autowired
    RealTimeRebecaPushSOSRule pushSOSRule = new RealTimeRebecaPushSOSRule();

    @Autowired
    RealTimeRebecaEndMSGSRVSOSRule endMSGSrvSOSRule = new RealTimeRebecaEndMSGSRVSOSRule();

    @Autowired
    RealTimeRebecaJumpSOSRule jumpSOSRule = new RealTimeRebecaJumpSOSRule();

    @Autowired
    RealTimeRebecaMethodCallSOSRule methodCallSOSRule = new RealTimeRebecaMethodCallSOSRule();

    @Autowired
    RealTimeRebecaSetModeSOSRule setModeSOSRule = new RealTimeRebecaSetModeSOSRule();

    @Autowired
    RealTimeRebecaDelaySOSRule delaySOSRule = new RealTimeRebecaDelaySOSRule();

    RealTimeRebecaResumeSOSRule rebecaResumeSOSRule = new RealTimeRebecaResumeSOSRule();

    RealTimeRebecaEndMethodSOSRule endMethodSOSRule = new RealTimeRebecaEndMethodSOSRule();

    List<Pair<? extends Action, RealTimeRebecaActorState>> decorateStatementExecutionResult(
            Pair<? extends Action, Pair<RealTimeRebecaActorState, InstructionBean>> statementExecutionResult) {
        return Arrays.asList(new Pair<Action, RealTimeRebecaActorState>(Action.TAU, statementExecutionResult.getSecond().getFirst()));
    }

    RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> convertStatementResultToActorResult(
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>> result) {

        if (result instanceof RealTimeRebecaDeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> det) {
            return new RealTimeRebecaDeterministicTransition<>(det.getAction(), det.getDestination().getFirst());
        }
        else if (result instanceof RealTimeRebecaNondeterministicTransition<Pair<RealTimeRebecaActorState, InstructionBean>> nondet) {
            RealTimeRebecaNondeterministicTransition<RealTimeRebecaActorState> newTransition = new RealTimeRebecaNondeterministicTransition<>();
            for (Pair<? extends Action, Pair<RealTimeRebecaActorState, InstructionBean>> d: nondet.getDestinations()) {
                newTransition.addDestination(d.getFirst(), d.getSecond().getFirst());
            }
            return newTransition;
        }
        throw new IllegalArgumentException("Unknown transition type: " + result.getClass());
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> applyRule(RealTimeRebecaActorState source) {
        RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> destinations = null;
        InstructionBean instruction = source.getInstruction();

        if (source.noScopeInstructions()) {
            RealTimeRebecaDeterministicTransition<RealTimeRebecaActorState> result = new RealTimeRebecaDeterministicTransition<>();
            result.setDestination(source);
            result.setAction(Action.TAU);
            return result;
        }
        if(instruction instanceof AssignmentInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean> >
                    executionResult = assignmentSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof MsgsrvCallInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = sendMessageSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof DeclarationInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = variableDeclarationSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof PushARInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = pushSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof PopARInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = popSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof EndMsgSrvInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = endMSGSrvSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof JumpIfNotInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = jumpSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof MethodCallInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = methodCallSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof EndMethodInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = endMethodSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof StartSetModeInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = setModeSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof ContnuousNonDetInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = delaySOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof MsgsrvCallWithAfterInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = sendMessageSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else if(instruction instanceof StartUnbreakableConditionInstructionBean) {
            RealTimeRebecaAbstractTransition<Pair<RealTimeRebecaActorState, InstructionBean>>
                    executionResult = assignmentSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
        else {
            throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
        }

        return destinations;
    }

    @Override
    public RealTimeRebecaAbstractTransition<RealTimeRebecaActorState> applyRule(Action synchAction, RealTimeRebecaActorState source) {
        return null;
    }
}
