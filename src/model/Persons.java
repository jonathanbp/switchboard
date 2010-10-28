package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import model.entities.Person;

public class Persons extends Observable implements Observer {
	
	private static Persons _instance;
	
	private List<Person> _everyone = new CopyOnWriteArrayList<Person>();
	
	public static synchronized Persons getInstance() {
		if(_instance == null) _instance = new Persons();
		return _instance;
	}


	public Persons() {
		
		/*everyone.addAll(Arrays.asList(
			new Person("Name 1", "one@cetrea.com", new Phone("11111","11111",PhoneStatus.OnHook), new Calendar("Nothing")),
			new Person("Name 2", "two@cetrea.com", new Phone("22222","22222",PhoneStatus.OnHook), new Calendar("Nothing"))
			));
		for (Person p : everyone) {
			p.addObserver(this);
		}*/
	}
	
	public List<Person> getEveryone() { 
		return _everyone;
	}
	
	public void setEveryone(List<Person> everyone) {
		_everyone = everyone;
	}
	
	public List<Person> find(Predicate p) {
		List<Person> matches = new ArrayList<Person>();
		for(Person person : _everyone) {
			if(p.match(person)) matches.add(person);
		}
		return matches;
	}


	@Override
	public void update(Observable o, Object arg) {
		
		setChanged();
		notifyObservers(o);
	}


	public void add(Person p) {
		p.addObserver(this);
		_everyone.add(p);
		
		setChanged();
		notifyObservers(p);
	}


	public static Predicate withEmail(final String email) {
		return new Predicate() {
			
			@Override
			public boolean match(Person p) {
				return p.getEmail() != null && p.getEmail().equals(email);
			}
		};
	}


	public static Predicate withPhone(final String ip) {
		return new Predicate() {
			
			@Override
			public boolean match(Person p) {
				return p.getPhone() != null && p.getPhone().getIp() != null && p.getPhone().getIp().equals(ip);
			}
		};
	}
	
	
}
