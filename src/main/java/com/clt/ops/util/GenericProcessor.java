package com.clt.ops.util;

public interface GenericProcessor<T> {

	public T process(T inData);
}
