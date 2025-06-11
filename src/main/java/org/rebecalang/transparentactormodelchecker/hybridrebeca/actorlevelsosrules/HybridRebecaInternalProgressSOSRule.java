package org.rebecalang.transparentactormodelchecker.hybridrebeca.actorlevelsosrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules.*;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
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
    HybridRebecaEndMSGSRVSOSRule endMSGSrvSOSRule;

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
        InstructionBean instruction = source.getEnabledInstruction();
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
        else {
            throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
        }
        return destinations;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }
}
