package switchboard;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.log.Log;

public class Receive {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static Object fromJSON(String json, Class clazz) {
		try {
			return mapper.readValue(json, clazz);
		} 
		catch (Exception e) {
			Log.warn(e);
		}
		return null;
	}
}
