var switchboard = {}
switchboard.websockets = {
		
	"server" : "ws://localhost:8080/ws/",
	
	// the websocket - null untill initialized
	"_ws" : null,
	
	/** Function: initialize
	 * 
	 * Initializes the connection to the websockets provider
	 * 
	 */
	"initialize" : function() {

		// check if you're already connected
		if(switchboard.websockets._ws != null) {
			alert("already connected");
			return;
		}
	
		// connect to server - bind event handler
		var ws = new WebSocket(switchboard.websockets.server);
		ws.onopen = switchboard.websockets.onOpen;
		ws.onmessage = switchboard.websockets.onMessage;
		ws.onclose = switchboard.websockets.onClose;
		
		// remember ws
		switchboard.websockets._ws = ws;	
	},

	/**	Function: onOpen
	 * 
	 * Handle connection established
	 * 
	 */
	"onOpen" : function() {
		console.debug("connection established");
	},
	
	/** Function: onMessage
	 * 
	 * Handle message received
	 * 
	 */
	"onMessage" : function(m) {
		console.debug("message received");
		eval("var p = ("+m.data+")");
		console.debug(p.name+" "+p.email);
		// create and insert into dom
		var elm = $("#"+p.id);
		if(elm.length==0) {
			elm = $("#person").clone();
			$(elm).attr("id", p.id);
			$(elm).appendTo("#people");
		}
		$(".name", elm).html(p.name);
		$(".email", elm).html(p.email);
		$(".phonestatus", elm).html(p.phone.status);
	},
	
	/** Function: onClose
	 * 
	 * Handle connection close
	 * 
	 */
	"onClose" : function() {
		console.debug("connection closed");
	}
}

// initialize the connection
$(switchboard.websockets.initialize);