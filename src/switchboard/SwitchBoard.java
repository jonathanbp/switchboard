package switchboard;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Persons;
import model.entities.Person;
import model.entities.Phone;
import model.entities.PhoneStatus;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;



public class SwitchBoard extends Server implements Observer {

	List<SwitchBoardWebSocket> _webSockets = new CopyOnWriteArrayList<SwitchBoardWebSocket>();
	private SelectChannelConnector _connector;
	public Persons people;

	public SwitchBoard() {
    	
    	people = Persons.getInstance();
    	people.addObserver(this);
    	
    	// connector
        _connector = new SelectChannelConnector();
        _connector.setPort(8080);
       

        addConnector(_connector);
        
        // handler for websockets
        Handler wsHandler = new WebSocketHandler()
        {
        	
            @Override
            protected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol)
            {
                return new SwitchBoardWebSocket();
            }
        };
        
        // handler for phone callbacks
        Handler phoneCallbackHandler = new AbstractHandler() {
			
			@Override
			public void handle(String arg0, org.eclipse.jetty.server.Request arg1,
					HttpServletRequest arg2, HttpServletResponse arg3)
					throws IOException, ServletException {
				
				for(Person p : people.find(Persons.withEmail("one@cetrea.com"))) {
					PhoneStatus s = (p.getPhone().getStatus()==PhoneStatus.OffHook) ? PhoneStatus.OnHook : PhoneStatus.OffHook;
					p.getPhone().setStatus(s);
				}
				
			}
		};
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
		resourceHandler.setResourceBase("./WebContent/WEB-INF");
		
		
		ContextHandler wsContext = new ContextHandler();
		wsContext.setContextPath("/ws");
		wsContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		wsContext.setHandler(wsHandler);
		
		
		ContextHandler resourceContext = new ContextHandler();
		resourceContext.setContextPath("/");;
		resourceContext.setHandler(resourceHandler);
		
		ContextHandler phoneCallbackContext = new ContextHandler();
		phoneCallbackContext.setContextPath("/snom");
		phoneCallbackContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		phoneCallbackContext.setHandler(phoneCallbackHandler);
        
		// handlerlist will iterate handlers for each request
		ContextHandlerCollection contexts = new ContextHandlerCollection();		
		contexts.setHandlers(new Handler[] { wsContext, phoneCallbackContext, resourceContext });
        
        // list is main handler
        setHandler(contexts);
    }
    
	@Override
	public void update(Observable o, Object arg) {
		for (SwitchBoardWebSocket ws : _webSockets) {
			try {
				Transmit.asJSON(arg, ws._outbound);
				Log.info("Transmitted update");
			} catch (IOException e) {
				Log.warn(e);
			}
		}
	}
    
    public static void main(String[] args)
    {
        try
        {
            SwitchBoard server = new SwitchBoard();
            server.start();
            server.join();
        }
        catch (Exception e)
        {
            Log.warn(e);
        }
    }
	
	private class SwitchBoardWebSocket implements WebSocket {

		private Outbound _outbound;

		@Override
		public void onConnect(Outbound out) {
			Log.info("Socket established - "+out.toString());
			_outbound = out;
			_webSockets.add(this);
		}

		@Override
		public void onDisconnect() {
			_webSockets.remove(this);
		}

		@Override
		public void onMessage(byte frame, String data) {
			Log.info("Message received - "+data);
			
			action.Request r = (action.Request) Receive.fromJSON(data, action.Request.class);
			
			if(r != null) {
				switch(r.getAction()) {
					case addPerson:
						people.add(r.getPerson());
						break;
				}
			}
			
			/*for (SwitchBoardWebSocket socket : _webSockets)
            {
                try
                {
                    socket._outbound.sendMessage(frame,data);
        			//Transmit.asJSON(Persons.getInstance().all(), socket._outbound);
                }
                catch(IOException e) 
                { 
                	Log.warn(e); 
                }
            }*/
		}

		@Override
		public void onMessage(byte frame, byte[] data, int offset, int length) {
			// nop
		}

	}
}
