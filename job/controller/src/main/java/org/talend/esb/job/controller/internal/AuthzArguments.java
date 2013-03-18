package org.talend.esb.job.controller.internal;

import org.apache.neethi.Policy;

public class AuthzArguments {

    private Policy policy;
    private String role;

    public AuthzArguments(Policy policy, String role) {
    	this.policy = policy;
    	this.role = role;
    }

    public Policy getPolicy() {
        return policy;
    }

    public String getRole() {
        return role;
    }

}
