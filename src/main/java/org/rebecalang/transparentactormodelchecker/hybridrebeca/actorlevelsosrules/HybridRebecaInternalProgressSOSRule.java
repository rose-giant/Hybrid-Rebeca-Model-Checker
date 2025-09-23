package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.ContnuousNonDetInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.MsgsrvCallWithAfterInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartSetModeInstructionBean;
import org.rebecalang.modeltransformer.ril.hybrid.rilinstruction.StartUnbreakableConditionInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.*;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.utils.HybridRebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Configurable
@Component
public class HybridRebecaInternalProgressSOSRule extends AbstractHybridSOSRule<HybridRebecaActorState> {
    @Autowired
    HybridRebecaAssignmentSOSRule assignmentSOSRule = new HybridRebecaAssignmentSOSRule();

    @Autowired
    HybridRebecaSendMessageSOSRule sendMessageSOSRule = new HybridRebecaSendMessageSOSRule();

    @Autowired
    HybridRebecaVariableDeclarationSOSRule variableDeclarationSOSRule = new HybridRebecaVariableDeclarationSOSRule();

    @Autowired
    HybridRebecaPopSOSRule popSOSRule = new HybridRebecaPopSOSRule();

    @Autowired
    HybridRebecaPushSOSRule pushSOSRule = new HybridRebecaPushSOSRule();

    @Autowired
    HybridRebecaEndMSGSRVSOSRule endMSGSrvSOSRule = new HybridRebecaEndMSGSRVSOSRule();

    @Autowired
    HybridRebecaJumpSOSRule jumpSOSRule = new HybridRebecaJumpSOSRule();

    @Autowired
    HybridRebecaMethodCallSOSRule methodCallSOSRule = new HybridRebecaMethodCallSOSRule();

    @Autowired
    HybridRebecaSetModeSOSRule setModeSOSRule = new HybridRebecaSetModeSOSRule();

    @Autowired
    HybridRebecaDelaySOSRule delaySOSRule = new HybridRebecaDelaySOSRule();

    HybridRebecaResumeSOSRule rebecaResumeSOSRule = new HybridRebecaResumeSOSRule();

    HybridRebecaEndMethodSOSRule endMethodSOSRule = new HybridRebecaEndMethodSOSRule();

    List<Pair<? extends Action, HybridRebecaActorState>> decorateStatementExecutionResult(
            Pair<? extends Action, Pair<HybridRebecaActorState, InstructionBean>> statementExecutionResult) {
        return Arrays.asList(new Pair<Action, HybridRebecaActorState>(Action.TAU, statementExecutionResult.getSecond().getFirst()));
    }

    HybridRebecaAbstractTransition<HybridRebecaActorState> convertStatementResultToActorResult(
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result) {
        return new HybridRebecaDeterministicTransition<HybridRebecaActorState>(
                result.getAction(), result.getDestination().getFirst());
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        HybridRebecaAbstractTransition<HybridRebecaActorState> destinations = null;
//        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> postponeResult =
//                (HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>)
//                        rebecaResumeSOSRule.applyPostponeRule(new Pair<>(source, new DeclarationInstructionBean("postpone")));

        if (source.noScopeInstructions()) {
            HybridRebecaDeterministicTransition<HybridRebecaActorState> result = new HybridRebecaDeterministicTransition<>();
            result.setDestination(source);
            result.setAction(Action.TAU);
            return result;
        }
        InstructionBean instruction = source.getInstruction();

        //t = temp_exp is not being handled
        if(instruction instanceof AssignmentInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean> >
                    executionResult = assignmentSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof MsgsrvCallInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = sendMessageSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof DeclarationInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = variableDeclarationSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof PushARInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = pushSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof PopARInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = popSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof EndMsgSrvInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = endMSGSrvSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof JumpIfNotInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = jumpSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof MethodCallInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = methodCallSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof EndMethodInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = endMethodSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof StartSetModeInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = setModeSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof ContnuousNonDetInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = delaySOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof MsgsrvCallWithAfterInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = sendMessageSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else if(instruction instanceof StartUnbreakableConditionInstructionBean) {
            HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = assignmentSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult((HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>) executionResult);
        }
        else {
            throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
        }
        return destinations;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
