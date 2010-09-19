package model.entities;

import java.util.Observable;

public class Phone extends Observable {
	
	private String number;
	private String ip;
	private PhoneStatus status;
	
	public Phone() {}
	
	public Phone(String number, String ip, PhoneStatus status) {
		super();
		this.number = number;
		this.ip = ip;
		this.status = status;
	}
	
	public void setStatus(PhoneStatus status) {
		this.status = status;
		setChanged();
		notifyObservers();
	}
	public PhoneStatus getStatus() {
		return status;
	}
	public void setIp(String ip) {
		this.ip = ip;
		setChanged();
		notifyObservers();
	}
	public String getIp() {
		return ip;
	}
	public void setNumber(String number) {
		this.number = number;
		setChanged();
		notifyObservers();
	}
	public String getNumber() {
		return number;
	}
}
