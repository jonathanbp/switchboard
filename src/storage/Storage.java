package storage;

import model.Persons;

public interface Storage {
	public boolean store(Persons ps);
	public Persons fetch();
}
