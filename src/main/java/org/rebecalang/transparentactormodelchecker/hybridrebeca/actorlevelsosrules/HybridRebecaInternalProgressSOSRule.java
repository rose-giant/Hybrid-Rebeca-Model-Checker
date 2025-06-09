package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.*;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class HybridRebecaInternalProgressSOSRule extends AbstractHybridSOSRule<HybridRebecaActorState> {
    @Autowired
    HybridRebecaAssignmentSOSRule assignmentSOSRule;

    @Autowired
    HybridRebecaSendMessageSOSRule sendMessageSOSRule;

    @Autowired
    HybridRebecaVariableDeclarationSOSRule variableDeclarationSOSRule;

    @Autowired
    HybridRebecaPopSOSRule popSOSRule;

    @Autowired
    HybridRebecaPushSOSRule pushSOSRule;

    @Autowired
    HybridRebecaEndMsgsrvSOSRule endMSGSrvSOSRule;

    List<Pair<? extends Action, HybridRebecaActorState>> decorateStatementExecutionResult(
            Pair<? extends Action, Pair<HybridRebecaActorState, InstructionBean>> statementExecutionResult) {
        return Arrays.asList(new Pair<Action, HybridRebecaActorState>(Action.TAU, statementExecutionResult.getSecond().getFirst()));
    }

    HybridRebecaDeterministicTransition<HybridRebecaActorState> convertStatementResultToActorResult(
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result) {
        return new HybridRebecaDeterministicTransition<HybridRebecaActorState>(
                result.getAction(), result.getDestination().getFirst());
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(HybridRebecaActorState source) {
        HybridRebecaAbstractTransition<HybridRebecaActorState> destinations = null;
        InstructionBean instruction = source.getEnabledInstruction();
        if(instruction instanceof AssignmentInstructionBean) {
            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
                    executionResult = assignmentSOSRule.applyRule(new Pair<>(source, instruction));
            destinations = convertStatementResultToActorResult(executionResult);
        }
//        else if(instruction instanceof MsgsrvCallInstructionBean) {
//            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
//                    executionResult = sendMessageSOSRule.applyRule(new Pair<>(source, instruction));
//            destinations = convertStatementResultToActorResult(executionResult);
//        } else if(instruction instanceof DeclarationInstructionBean) {
//            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
//                    executionResult = variableDeclarationSOSRule.applyRule(new Pair<>(source, instruction));
//            destinations = convertStatementResultToActorResult(executionResult);
//        } else if(instruction instanceof PushARInstructionBean) {
//            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
//                    executionResult = pushSOSRule.applyRule(new Pair<>(source, instruction));
//            destinations = convertStatementResultToActorResult(executionResult);
//        } else if(instruction instanceof PopARInstructionBean) {
//            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
//                    executionResult = popSOSRule.applyRule(new Pair<>(source, instruction));
//            destinations = convertStatementResultToActorResult(executionResult);
//        } else if(instruction instanceof EndMsgSrvInstructionBean) {
//            HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>
//                    executionResult = endMSGSrvSOSRule.applyRule(new Pair<>(source, instruction));
//            destinations = convertStatementResultToActorResult(executionResult);
//        } else {
//            throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
//        }
        return destinations;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action synchAction, HybridRebecaSystemState source) {
        return null;
    }
}
