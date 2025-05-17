package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MessageSpecification implements Serializable {
	protected String messageName;
	protected Map<String, Object> parameters;
	protected BaseActorState<?> senderActorState;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageName == null) ? 0 : messageName.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((senderActorState == null) ? 0 : senderActorState.getName().hashCode());
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
		MessageSpecification other = (MessageSpecification) obj;
		if (messageName == null) {
			if (other.messageName != null)
				return false;
		} else if (!messageName.equals(other.messageName))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else {
			List<String> list = new ArrayList<String>(parameters.keySet());
			java.util.Collections.sort(list);
			for (String key : list) {
				Object value1 = parameters.get(key);
				Object value2 = other.parameters.get(key);

				if (value1 == null) {
					if (value2 != null) {
						return false;
					}
				} else if (!value1.equals(value2)) {
					return false;
				}
			}
		}

		if (senderActorState == null) {
			return other.senderActorState == null;
		} else
			return senderActorState.getName().equals(other.senderActorState.getName());
	}

    public MessageSpecification(String messageName, Map<String, Object> parameters, BaseActorState<?> baseActorState) {
        super();
        this.messageName = messageName;
        this.parameters = parameters;
        this.senderActorState = baseActorState;
    }

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

    public BaseActorState<?> getSenderActorState() {
        return senderActorState;
    }

    public void setSenderActorState(BaseActorState<?> senderActorState) {
        this.senderActorState = senderActorState;
    }

	public void export(PrintStream output) {
		output.print("<message sender=\"" + senderActorState.getName() + "\">" + messageName + "(");
		for (Entry<String, Object> entry : parameters.entrySet())
			output.print(entry.getKey() + "->" + entry.getValue() + ", ");
		output.println(")</message>");
	}
}
