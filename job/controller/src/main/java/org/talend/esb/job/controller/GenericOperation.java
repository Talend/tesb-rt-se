package org.talend.esb.job.controller;

public interface GenericOperation {
    Object invoke(Object payload) throws Exception;
}
