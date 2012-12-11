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

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.SearchConditionVisitor;
import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.apache.cxf.jaxrs.ext.search.jpa.JPACriteriaQueryVisitor;
import org.apache.cxf.jaxrs.ext.search.jpa.JPATypedQueryVisitor;

import common.advanced.Person;
import common.advanced.PersonInfo;
import common.advanced.Person_;

/**
 * Storage used by both PersonService and SearchService
 */
public class PersonInfoStorage {
    private static AtomicLong ID = new AtomicLong();
    private Map<Long, Person> persons = Collections.synchronizedMap(new LinkedHashMap<Long, Person>());
    
    private EntityManager em;
    private Map<String, String> beanPropertiesMap = Collections.emptyMap();
    
    public PersonInfoStorage() {
    }

    public void setEntityManager(EntityManager em) {
    	this.em = em;
    }
    
    public void setBeanProperties(Map<String, String> beanProperties) {
    	this.beanPropertiesMap = beanProperties;
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

    public List<Person> getTypedQueryPerson(SearchContext context) {
    	// Get search condition encapsulating the query expression
    	SearchCondition<Person> filter = getSearchCondition(context);
    	
    	// Initialise JPA2 visitor which can convert the captured search expression
    	// into JPA2 TypedQuery
        SearchConditionVisitor<Person, TypedQuery<Person>> jpa = 
                new JPATypedQueryVisitor<Person>(em, Person.class);
        
        // Convert
        filter.accept(jpa);
        
        // Get TypedQuery
        TypedQuery<Person> typedQuery = jpa.getQuery(); 
        
        // Run the query and return the results
        return typedQuery.getResultList();
    }
    
    public List<PersonInfo> getTypedQueryTuple(SearchContext context,
    		                                   String expression) {
    	// Get search condition encapsulating the query expression
    	SearchCondition<Person> filter = getSearchCondition(context, expression);
    	
    	// Initialise JPA2 visitor which can convert the captured search expression
    	// into JPA2 TypedQuery
    	JPACriteriaQueryVisitor<Person, Tuple> jpa = 
                new JPACriteriaQueryVisitor<Person, Tuple>(em, Person.class, Tuple.class);
        
        // Convert
        filter.accept(jpa);
        
        // Shape the response data with selections and Tuple 
        List<SingularAttribute<Person, ?>> selections = 
                new ArrayList<SingularAttribute<Person, ?>>();
        selections.add(Person_.id);
        
        jpa.selectTuple(selections);
        
        // Get CriteriaQuery and create TypedQuery
        CriteriaQuery<Tuple> cquery = jpa.getQuery();
        TypedQuery<Tuple> typedQuery = em.createQuery(cquery);
        
        // Run the query
        List<Tuple> tuples = typedQuery.getResultList();
        
        // Return the results
        List<PersonInfo> infos = new ArrayList<PersonInfo>(tuples.size());
        for (Tuple tuple : tuples) {
        	infos.add(new PersonInfo(tuple.get(Person_.id.getName(), Long.class)));
        }
        return infos;
        
    }
    
    private SearchCondition<Person> getSearchCondition(SearchContext context) {
    	return getSearchCondition(context, null);
    }
    
    private SearchCondition<Person> getSearchCondition(SearchContext context, String expression) {
    	return context.getCondition(expression, Person.class, beanPropertiesMap);
    }
    
    public void init() {

    	try {
    		em.getTransaction().begin();
    		
    		Person mother = new Person("Lorraine", 50);
	        addPerson(mother);
	        
	        em.persist(mother);
	        
	        Person father = new Person("John", 55);
	        addPerson(father);
	
	        em.persist(father);
	        
	        Person partner = new Person("Catherine", 28);
	        addPerson(partner);
	
	        em.persist(partner);
	        
	        Person p = new Person("Fred", 30, mother, father, partner);
	        addPerson(p);
	        em.persist(p);
	        
	        //father.addChild(p);
	        mother.addChild(p);
	        
	        
	        em.getTransaction().commit();
	        
    	} catch (Exception ex) {
    		throw new RuntimeException(ex);
    	}
    }
}
