package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

import java.io.PrintStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseActorState implements Serializable {
    protected ActorScopeStack actorScopeStack;
    protected String name;
    protected String typeName;
    transient protected AbstractTypeSystem typeSystem;

    public BaseActorState() {
        initializeQueue();
    }

    public abstract void initializeQueue();

    public abstract void addToQueue(MessageSpecification msgSpec);

    public abstract boolean actorQueueIsEmpty();

    public abstract MessageSpecification getMessage(boolean isPeek);

    public void initializePC(String methodName, int lineNum) {
        addVariableToRecentScope(InstructionUtilities.PC_STRING, new ProgramCounter(methodName, lineNum));
//        addVariableToRecentScope(AbstractExpressionTranslator.RETURN_VALUE, 0);
    }

    public void setPC(String methodName, int lineNum) {
        ProgramCounter pc = (ProgramCounter) retrieveVariableValue(InstructionUtilities.PC_STRING);
        pc.setLineNumber(lineNum);
        pc.setMethodName(methodName);
    }

    public void increasePC() {
        ProgramCounter pc = (ProgramCounter) retrieveVariableValue(InstructionUtilities.PC_STRING);
        pc.setLineNumber(pc.getLineNumber() + 1);
    }

    public ProgramCounter getPC() {
        return (ProgramCounter) retrieveVariableValue(InstructionUtilities.PC_STRING);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void popFromActorScope() {
        actorScopeStack.popFromScopeStack();
    }

    public void addVariableToRecentScope(String varName, Object valueObject) {
        actorScopeStack.addVariable(varName, valueObject);
    }

    public void addVariableToExactScope(String varName, Object valueObject, int scopeIndex) {
        actorScopeStack.addVariable(varName, valueObject, scopeIndex);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void initializeScopeStack() {
        actorScopeStack = new ActorScopeStack();
        actorScopeStack.initialize();
    }

    public Object retrieveVariableValue(Variable variable) {
        return retrieveVariableValue(variable.getVarName());
    }

    public Object retrieveVariableValue(String varName) {
        return actorScopeStack.retrieveVariableValue(varName);
    }

    public void setVariableValue(String varName, Object valueObject) {
        actorScopeStack.setVariableValue(varName, valueObject);
    }

    public boolean variableIsDefined(String varName) {
        return actorScopeStack.variableIsDefined(varName);
    }

    public ActorScopeStack getActorScopeStack() {
        return actorScopeStack;
    }

    public void setTypeSystem(AbstractTypeSystem typeSystem) {
        this.typeSystem = typeSystem;
    }
    
	public AbstractTypeSystem getTypeSystem() {
		return typeSystem;
	}

	public void export(PrintStream output) {
		output.println("<rebec name=\""+ name + "\">");
		output.println("<statevariables>");
		actorScopeStack.export(output);
		exportQueueContent(output);
		output.println("</statevariables>");
		output.println("</rebec>");
	}

	protected abstract void exportQueueContent(PrintStream output);

	public String toString() {
		String retValue = "name:" + typeName + "." + name;
		retValue += "\n vars: [";
		for(ActivationRecord ar : actorScopeStack.getActivationRecords()) {
			retValue += ar;
		}
		return retValue + "]";
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actorScopeStack == null) ? 0 : actorScopeStack.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseActorState other = (BaseActorState) obj;
		if (actorScopeStack == null) {
			if (other.actorScopeStack != null)
				return false;
		} else if (!actorScopeStack.equals(other.actorScopeStack))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

	public void pushInScopeStackForInsideMethod() {
		actorScopeStack.pushInScopeStackForInsideMethod();
	}

	public void pushInScopeStackForMethodCallInitialization(String relatedRebecType) {
		actorScopeStack.pushInScopeStackForMethodCallInitialization(relatedRebecType);
	}
	
	public void pushInScopeStackForInheritanceStack(String typeName) {
		actorScopeStack.pushInScopeStackForInheritanceStack(typeName);
	}
}
