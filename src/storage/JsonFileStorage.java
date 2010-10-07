package storage;

import java.io.File;
import java.io.IOException;

import model.Persons;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.log.Log;

public class JsonFileStorage implements Storage {
	
	private ObjectMapper _mapper = new ObjectMapper();
	private File _f;
	
	public JsonFileStorage(String path) {
		_f = new File(path);
	}

	@Override
	public boolean store(Persons ps) {
		try {
			_mapper.writeValue(_f, ps);
			return true;
		} catch (JsonGenerationException e) {
			Log.warn(e);
		} catch (JsonMappingException e) {
			Log.warn(e);
		} catch (IOException e) {
			Log.warn(e);
		}
		return false;
	}

	@Override
	public Persons fetch() {
		try {
			return _mapper.readValue(_f, Persons.class);
		} catch (JsonParseException e) {
			Log.warn(e);
		} catch (JsonMappingException e) {
			Log.warn(e);
		} catch (IOException e) {
			Log.warn(e);
		}
		return null;
	}
	
}
