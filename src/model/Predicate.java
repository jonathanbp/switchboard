package model;

import model.entities.Person;

public interface Predicate {
	boolean match(Person p);
}
