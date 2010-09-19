package action;

import model.entities.Person;

public class Request {
	
	public enum Action {
		addPerson,
		removePerson
	}
	
	private String sender;
	private Action action;
	private Person person;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

}
