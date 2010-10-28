package test;

import junit.framework.Assert;

import org.junit.Test;

import model.Persons;
import model.entities.Person;
import model.entities.Phone;
import model.entities.PhoneStatus;
import storage.JsonFileStorage;
import storage.Storage;


public class StorageTest {

	@Test
	public void testStore() {
		
		Storage s = new JsonFileStorage("jsf-test");
		
		Persons ps = Persons.getInstance();
		ps.add(new Person("TestName","TestEmail", new Phone("e49873", "9802938", PhoneStatus.OnHook), null));
		ps.add(new Person("TestName","TestEmail", new Phone("e49873", "9802938", PhoneStatus.OnHook), null));
		
		s.store(ps);
		
		Persons ps_fromstore = s.fetch();
		
		Assert.assertEquals(ps.getEveryone().size(), ps_fromstore.getEveryone().size());
		
	}
}
