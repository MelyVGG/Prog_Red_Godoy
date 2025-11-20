package tpFinalDeA2;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

@SuppressWarnings({ })
public class GameRoom implements Runnable {
    private final ClientHandler[] handlers = new ClientHandler[2];
    private final Socket[] sockets = new Socket[2];
    private final tablero[] tableros = new tablero[2];
    private int turno = 0;
    private boolean[] ready = new boolean[]{false, false};

    public GameRoom(Socket s1, Socket s2) throws IOException {
        sockets[0] = s1;
        sockets[1] = s2;
        handlers[0] = new ClientHandler(s1, this);
        handlers[1] = new ClientHandler(s2, this);

        // set room + playerId en handlers
        handlers[0].setRoom(this, 0);
        handlers[1].setRoom(this, 1);

        tableros[0] = new tablero();
        tableros[1] = new tablero();
    }

    @Override
    public void run() {
        
        new Thread(handlers[0]).start();
        new Thread(handlers[1]).start();

        
        sendToBoth(new message("START", null, Map.of("message", "Partida iniciada. Coloca tus barcos")));
    }

    
    public synchronized void handleMessage(ClientHandler from, message msg) {
        try {
            int pid = from.playerId;
            switch (msg.type) {
                case "PLACE_SHIP":
                    
                    Map<String, Object> pl = msg.payload;
                    String name = (String) pl.get("name");
                    Double f = (Double) pl.get("row");
                    Double c = (Double) pl.get("col");
                    Double len = (Double) pl.get("length");
                    Boolean hor = (Boolean) pl.get("horizontal");
                    boolean ok = tableros[pid].colocarBarco(name, f.intValue(), c.intValue(), len.intValue(), hor);
                    from.send(new message("PLACE_RESULT", pid, Map.of("ok", ok)));
                    break;
                case "READY":
                    ready[pid] = true;
                    
                    if (ready[0] && ready[1]) {
                        
                        turno = 0;
                        handlers[turno].send(new message("TURN", turno, Map.of("yourTurn", true)));
                        handlers[1 - turno].send(new message("TURN", 1 - turno, Map.of("yourTurn", false)));
                    }
                    break;
                case "SHOOT":
                    if (pid != turno) {
                        from.send(new message("ERROR", pid, Map.of("message", "No es tu turno")));
                        break;
                    }
                    Map<String, Object> payload = msg.payload;
                    Double rf = (Double) payload.get("row");
                    Double cf = (Double) payload.get("col");
                    int rfInt = rf.intValue(), cfInt = cf.intValue();
                    int enemigo = 1 - pid;
                    String result = tableros[enemigo].disparar(rfInt, cfInt);
                    
                    handlers[pid].send(new message("RESULT", pid, Map.of("result", result, "row", rfInt, "col", cfInt)));
                    handlers[enemigo].send(new message("INCOMING", enemigo, Map.of("result", result, "row", rfInt, "col", cfInt)));

                    
                    if (tableros[enemigo].todosLosBarcosCaidos()) {
                        handlers[pid].send(new message("GAME_OVER", pid, Map.of("winner", pid)));
                        handlers[enemigo].send(new message("GAME_OVER", enemigo, Map.of("winner", pid)));
                    } else {
                        
                        turno = enemigo;
                        handlers[turno].send(new message("TURN", turno, Map.of("yourTurn", true)));
                        handlers[1 - turno].send(new message("TURN", 1 - turno, Map.of("yourTurn", false)));
                    }
                    break;
                case "CHAT":
                    
                    handlers[1 - pid].send(new message("CHAT", pid, msg.payload));
                    break;
                default:
                    from.send(new message("ERROR", pid, Map.of("message", "Tipo de mensaje desconocido")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                from.send(new message("ERROR", from.playerId, Map.of("message", "Excepción en servidor")));
            } catch (Exception ignored) {}
        }
    }

    private void sendToBoth(message msg) {
        handlers[0].send(msg);
        handlers[1].send(msg);
    }

    public void clientDisconnected(int playerId) {
        int other = 1 - playerId;
        if (handlers[other] != null) {
            handlers[other].send(new message("OPPONENT_DISCONNECTED", other, Map.of("message", "Rival desconectado")));
        }
    }
}