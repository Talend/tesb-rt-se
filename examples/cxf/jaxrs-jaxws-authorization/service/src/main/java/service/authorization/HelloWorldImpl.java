/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service.authorization;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jws.WebService;
import common.authorization.HelloWorld;
import common.authorization.User;

@WebService(endpointInterface = "common.authorization.HelloWorld", 
            serviceName = "HelloWorld")
public class HelloWorldImpl implements HelloWorld {
    Map<Integer, User> users = new LinkedHashMap<Integer, User>();

    public String sayHi(String text) {
        return "Hello " + text;
    }

    public String sayHiToUser(User user) {
        users.put(users.size() + 1, user);
        return "Hello " + user.getName();
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Map<Integer, User> echoUsers(Map<Integer, User> us) {
        if (!users.equals(us)) {
            throw new RuntimeException();
        }
        return us;
    }

}
