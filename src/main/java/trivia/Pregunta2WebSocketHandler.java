package trivia;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class Pregunta2WebSocketHandler {

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		App.removeUser(user);
	}

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        App.manageMessage(user, message);
    }
}