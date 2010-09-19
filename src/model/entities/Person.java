package model.entities;

import java.util.Observable;
import java.util.Observer;

public class Person extends Observable implements Observer {
	
	private String name;
	private String id;
	private String email;
	private Phone phone;
	private Calendar calendar;
	
	public Person() {}
	
	public Person(String name, String email, Phone phone, Calendar calendar) {
		super();
		setName(name);
		setEmail(email);
		setPhone(phone);
		setCalendar(calendar);
	}

	public String getId() {
		return id;
	}
	
	public void setCalendar(Calendar calendar) {
		if(this.calendar!=null) this.calendar.deleteObserver(this);
		this.calendar = calendar;
		this.calendar.addObserver(this);
		setChanged();
		notifyObservers();
	}
	
	public Calendar getCalendar() {
		return calendar;
	}
	public void setPhone(Phone phone) {
		if(this.phone!=null) this.phone.deleteObserver(this);
		this.phone = phone;
		this.phone.addObserver(this);
		setChanged();
		notifyObservers();
	}
	public Phone getPhone() {
		return phone;
	}
	public void setName(String name) {
		this.name = name;
		setChanged();
		notifyObservers();
	}
	public String getName() {
		return name;
	}
	public void setEmail(String email) {
		this.email = email;
		this.id = email.replace("@", "").replace(".", "");
		
		setChanged();
		notifyObservers();
	}
	public String getEmail() {
		return email;
	}

	@Override
	public void update(Observable o, Object arg) {
		// arg is not important
		setChanged();
		notifyObservers();
	}
}
