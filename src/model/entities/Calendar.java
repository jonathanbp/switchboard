package model.entities;

import java.util.Observable;

public class Calendar extends Observable {
	private String currentActivity;
	
	public Calendar() {}

	public Calendar(String currentActivity) {
		super();
		this.currentActivity = currentActivity;
	}

	public void setCurrentActivity(String currentActivity) {
		this.currentActivity = currentActivity;
		notifyObservers();
	}

	public String getCurrentActivity() {
		return currentActivity;
	}
}
