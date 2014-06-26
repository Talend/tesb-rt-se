package org.talend.esb.auxiliary.storage.common;

public interface  AuxiliaryObjectFactory<E> {

	public String marshalObject(E ctx);

	public E unmarshallObject(String marshalledData);

	public String createObjectKey(E ctx);
}
