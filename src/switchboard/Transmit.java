package switchboard;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.WebSocket.Outbound;

public class Transmit {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static void asJSON(Object o, Outbound out) throws IOException {
		String value = mapper.writeValueAsString(o);
		out.sendMessage((byte)0,value);
	}
}

