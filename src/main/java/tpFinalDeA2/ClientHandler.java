package tpFinalDeA2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private GameRoom room; 
    private final Gson gson;
    private BufferedReader in;
    private PrintWriter out;
    int playerId = -1;

    public ClientHandler(Socket socket, GameRoom room) {
        this.socket = socket;
        this.room = room;
        this.gson = new Gson();
    }

    public void setRoom(GameRoom room, int playerId) {
        this.room = room;
        this.playerId = playerId;
    }

    public void send(message msg) {
        String s = gson.toJson(msg);
        out.println(s);
        out.flush();
    }

    @Override
    public void run() {
        try (
            Socket s = socket;
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os), true)
        ) {
            this.in = br;
            this.out = pw;

            
            if (room == null) {
                message waiting = new message("WAITING", null, Map.of("message", "Esperando otro jugador..."));
                send(waiting);
            }

            String line;
            @SuppressWarnings("unused")
			Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            while ((line = in.readLine()) != null) {
                message msg = gson.fromJson(line, message.class);
                if (msg == null) continue;

                
                if (room != null) {
                    room.handleMessage(this, msg);
                } else {
                    
                    if ("CONNECT".equals(msg.type)) {
                        message ack = new message("ACK_WAIT", null, Map.of("message", "En cola de emparejamiento"));
                        send(ack);
                    }
                    
                }
            }
        } catch (IOException e) {
            System.out.println("Conexión cerrada por cliente: " + e.getMessage());
            if (room != null) room.clientDisconnected(playerId);
        }
    }
}