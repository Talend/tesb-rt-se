/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import com.talend.tif.examples.springsecurity.common.HelloWorld;
import com.talend.tif.examples.springsecurity.common.User;
import com.talend.tif.examples.springsecurity.common.UserImpl;

public class HelloWorldImpl implements HelloWorld {

    Map<Integer, User> users = new LinkedHashMap<Integer, User>();
    
    public HelloWorldImpl() {
    	users.put(1, new UserImpl("TestUser"));
    }

    @RolesAllowed("ROLE_USER")
    public String sayHi(String text) {
        System.out.println("sayHi called with text: " + text);
        return "Hello " + text;
    }

    @RolesAllowed("ROLE_USER")
    public String sayHiToUser(User user) {
        System.out.println("sayHi to user called for user : " + user.getName());
        users.put(users.size() + 1, user);
        return "Hello " + user.getName();
    }

    @RolesAllowed("ROLE_ADMIN")
    public Map<Integer, User> getUsers() {
        System.out.println("getUsers called");
        return users;
    }

}
