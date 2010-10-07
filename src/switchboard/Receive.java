package switchboard;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.log.Log;

public class Receive {
	
	private static ObjectMapper _mapper = new ObjectMapper();
	
	public static Object fromJSON(String json, Class clazz) {
		try {
			return _mapper.readValue(json, clazz);
		} 
		catch (Exception e) {
			Log.warn(e);
		}
		return null;
	}
}
