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
import model.entities.PhoneStatus;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import storage.Storage;
import switchboard.util.ListUtil;



public class SwitchBoard extends Server implements Observer {

	List<SwitchBoardWebSocket> _webSockets = new CopyOnWriteArrayList<SwitchBoardWebSocket>();
	private SelectChannelConnector _connector;
	public Persons _people;
	
	public Storage _storage;

	public SwitchBoard() {
	
		// fetch people
    	_people = _storage.fetch();
    	if(_people == null) _people = Persons.getInstance();
    	
    	_people.addObserver(this);
    	
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
			
        	private final int IP = 0;
        	private final int ACTION = 1;
        	
			@Override
			public void handle(String path, org.eclipse.jetty.server.Request arg1,
					HttpServletRequest arg2, HttpServletResponse arg3)
					throws IOException, ServletException {
				
				Log.warn(path);
				
				// path is <ip>/<action>
				String[] request = path.split("/");
				if(request.length!=2) {
					Log.warn("SNOM request was malformed - "+path);
					return;
				}
				
				// find person with phone with ip and act according to action
				Person p = ListUtil.single(_people.find(Persons.withPhone(request[IP])));				
				p.getPhone().setStatus(PhoneStatus.fromString(request[ACTION]));
				
				/*for(Person p : people.find(Persons.withEmail("one@cetrea.com"))) {
					PhoneStatus s = (p.getPhone().getStatus()==PhoneStatus.OffHook) ? PhoneStatus.OnHook : PhoneStatus.OffHook;
					p.getPhone().setStatus(s);
				}*/
				
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
			
			action.PersonAction r = (action.PersonAction) Receive.fromJSON(data, action.PersonAction.class);
			
			if(r != null) {
				switch(r.getAction()) {
					case addPerson:
						_people.add(r.getPerson());
						_storage.store(_people);
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
