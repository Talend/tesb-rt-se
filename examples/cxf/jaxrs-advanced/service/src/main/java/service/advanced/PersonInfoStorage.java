/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service.advanced;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import common.advanced.Person;

/**
 * Storage used by both PersonService and SearchService
 */
public class PersonInfoStorage {
    private static AtomicLong ID = new AtomicLong();
    private Map<Long, Person> persons = Collections.synchronizedMap(new LinkedHashMap<Long, Person>());

    public PersonInfoStorage() {
        init();
    }

    public Person getPerson(Long id) {
        return persons.get(id);
    }

    public Long addPerson(Person person) {
        long id = ID.incrementAndGet();
        person.setId(id);

        System.out.println("Adding new person : name - " + person.getName() + ", id - " + id);

        persons.put(id, person);
        return id;
    }

    public List<Person> getAll() {
        return new ArrayList<Person>(persons.values());
    }

    public List<Person> getPersons(int start, int count) {
        List<Person> pList = new ArrayList<Person>(persons.values());

        if (start < 0 || count < -1) {
            // illegal parameters, bad client request
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        if (count == 0 || start > pList.size()) {
            // returning null will result in the client getting HTTP 204
            // if needed, an empty collection can be returned instead
            return null;
        } else if (count == -1 || pList.size() < start + count) {
            count = pList.size() - start;
        }

        return pList.subList(start, start + count);
    }

    private void init() {

        Person mother = new Person("Lorraine", 50);
        addPerson(mother);

        Person father = new Person("John", 55);
        addPerson(father);

        Person partner = new Person("Catherine", 28);
        addPerson(partner);

        Person p = new Person("Fred", 30, mother, father, partner);
        addPerson(p);
    }
}
