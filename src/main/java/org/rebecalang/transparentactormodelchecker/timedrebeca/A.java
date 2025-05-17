package org.rebecalang.transparentactormodelchecker.timedrebeca;

import scala.collection.immutable.Set;

public abstract class A<T, Y> {
	public abstract T x(Y y);
}

class B extends A<Integer, Integer> {

	@Override
	public Integer x(Integer i) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

class C extends A<Set<Integer>, Integer> {

	@Override
	public Set<Integer> x(Integer y) {
		// TODO Auto-generated method stub
		return null;
	}
	
}