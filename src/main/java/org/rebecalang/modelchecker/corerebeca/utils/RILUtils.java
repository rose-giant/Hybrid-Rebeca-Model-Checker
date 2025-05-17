package org.rebecalang.modelchecker.corerebeca.utils;

import java.util.Iterator;

import org.rebecalang.modelchecker.corerebeca.MessageSpecification;

public class RILUtils {
	public static String convertToString(MessageSpecification ms) {
		String retValue = ms.getSenderActorState().getName() + "->" +
				ms.getMessageName() + "(";
		for(Iterator<String> iterator = ms.getParameters().keySet().iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			retValue += o + "->" + ms.getParameters().get(o);
			if (iterator.hasNext())
				retValue += ", ";
		}
		return retValue + ")";
	}
}
