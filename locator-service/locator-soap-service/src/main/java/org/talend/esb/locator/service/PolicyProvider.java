package org.talend.esb.locator.service;

import org.apache.cxf.Bus;
import org.apache.neethi.Policy;

public interface PolicyProvider {

    Policy getTokenPolicy();

    Policy getSamlPolicy();

    void register(Bus cxf);

}
