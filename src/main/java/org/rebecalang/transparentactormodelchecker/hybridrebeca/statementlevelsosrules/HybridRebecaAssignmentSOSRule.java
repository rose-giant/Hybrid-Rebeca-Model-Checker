package org.rebecalang.transparentactormodelchecker.hybridrebeca.statementlevelsosrules;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractHybridSOSRule;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.transition.HybridRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class HybridRebecaAssignmentSOSRule extends AbstractHybridSOSRule<Pair<HybridRebecaActorState, InstructionBean>> {

    private Object getValue(Object reference, HybridRebecaActorState actorState) {
        if (reference instanceof Variable) {
            String varName = ((Variable) reference).getVarName();
            return actorState.getVariableValue(varName);
        }
        return reference;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Pair<HybridRebecaActorState, InstructionBean> source) {
        AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
        Object valueFirst = getValue(aib.getFirstOperand(), source.getFirst());
        Object valueSecond = getValue(aib.getSecondOperand(), source.getFirst());
        String operator = aib.getOperator();
        Object rightSideResult = valueFirst;
        if (operator != null) {
            if (valueFirst instanceof HybridRebecaActorState) {
                if (operator.equals("=="))
                    rightSideResult = (((HybridRebecaActorState) valueFirst).getId()
                            .equals(((HybridRebecaActorState) valueSecond).getId()));
                else if (operator.equals("!="))
                    rightSideResult = !(((HybridRebecaActorState) valueFirst).getId()
                            .equals(((HybridRebecaActorState) valueSecond).getId()));
                else
                    throw new RebecaRuntimeInterpreterException(
                            "this case should have been reported as an error by the compiler.");
            } else
                rightSideResult = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
        }

        source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);
        source.getFirst().movePCtoTheNextInstruction();

        HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>> result =
                new HybridRebecaDeterministicTransition<Pair<HybridRebecaActorState, InstructionBean>>();
        result.setDestination(source);
        result.setAction(Action.TAU);
        return result;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<Pair<HybridRebecaActorState, InstructionBean>> applyRule(Action synchAction, Pair<HybridRebecaActorState, InstructionBean> source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaActorState> applyRule(Action synchAction, HybridRebecaActorState source) {
        return null;
    }

    @Override
    public HybridRebecaAbstractTransition<HybridRebecaSystemState> applyRule(org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action.Action synchAction, HybridRebecaSystemState source) {
        throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
    }
}
