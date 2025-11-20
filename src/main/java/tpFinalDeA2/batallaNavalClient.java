package tpFinalDeA2;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class batallaNavalClient {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Gson gson = new Gson();
    private final Scanner scanner = new Scanner(System.in);

    private final tablero myBoard = new tablero();
    private final tablero enemyBoard = new tablero(); // usado solo para visualizar impactos/fallos

    public batallaNavalClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        
        new Thread(this::listen).start();

        
        sendMessage(new message("CONNECT", null, Map.of("name", "Jugador")));

        System.out.println("Conectado al servidor. Esperando emparejamiento...");
        
    }

    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                message msg = gson.fromJson(line, message.class);
                handleServerMessage(msg);
            }
        } catch (IOException e) {
            System.out.println("Conexión cerrada por servidor.");
        }
    }

    private void handleServerMessage(message msg) {
        switch (msg.type) {
            case "WAITING":
            case "ACK_WAIT":
                System.out.println(msg.payload.get("message"));
                break;
            case "START":
                System.out.println("Partida iniciada. Coloca tus barcos.");
                placeAllShips();
                sendMessage(new message("READY", null, Map.of()));
                break;
            case "PLACE_RESULT":
                System.out.println("Resultado colocación: " + msg.payload.get("ok"));
                break;
            case "TURN":
                boolean yourTurn = Boolean.TRUE.equals(msg.payload.get("yourTurn"));
                if (yourTurn) {
                    System.out.println("Es tu turno. Tablero enemigo:");
                    enemyBoard.mostrarTablero(true);
                    shootTurn();
                } else {
                    System.out.println("Turno del rival. Espera...");
                }
                break;
            case "RESULT":
                String res = (String) msg.payload.get("result");
                int rr = ((Double) msg.payload.get("row")).intValue();
                int rc = ((Double) msg.payload.get("col")).intValue();
                applyToEnemyBoard(rr, rc, res);
                System.out.println("Resultado de tu disparo: " + res + " en ("+rr+","+rc+")");
                break;
            case "INCOMING":
                String r2 = (String) msg.payload.get("result");
                int ir = ((Double) msg.payload.get("row")).intValue();
                int ic = ((Double) msg.payload.get("col")).intValue();
                applyToMyBoard(ir, ic, r2);
                System.out.println("El rival disparó en ("+ir+","+ic+"): " + r2);
                break;
            case "GAME_OVER":
                int winner = ((Double) msg.payload.get("winner")).intValue();
                System.out.println("FIN DE PARTIDA. Ganador: Jugador " + winner);
                close();
                break;
            case "OPPONENT_DISCONNECTED":
                System.out.println("Rival desconectado.");
                close();
                break;
            case "CHAT":
                System.out.println("Mensaje rival: " + msg.payload.get("message"));
                break;
            default:
                System.out.println("Mensaje servidor: " + msg.type + " " + msg.payload);
        }
    }

    private void placeAllShips() {
        
        List<Map<String,Object>> ships = new ArrayList<>();
        ships.add(Map.of("name","Portaaviones","length",5));
        ships.add(Map.of("name","Acorazado","length",4));
        ships.add(Map.of("name","Crucero","length",3));
        ships.add(Map.of("name","Submarino","length",3));
        ships.add(Map.of("name","Destructor","length",2));

        for (Map<String,Object> s : ships) {
            boolean placed = false;
            while (!placed) {
                myBoard.mostrarTablero(false);
                System.out.printf("Colocar %s (len %d). Ingresa fila,col,horizontal(true/false):%n",
                        s.get("name"), ((Integer)s.get("length")).intValue());
                String line = scanner.nextLine().trim();
                try {
                    String[] parts = line.split(",");
                    int row = Integer.parseInt(parts[0].trim());
                    int col = Integer.parseInt(parts[1].trim());
                    boolean hor = Boolean.parseBoolean(parts[2].trim());
                    placed = myBoard.colocarBarco((String)s.get("name"), row, col, ((Integer)s.get("length")).intValue(), hor);
                    if (!placed) System.out.println("No se pudo colocar (colisión o fuera de tablero). Intenta de nuevo.");
                    else {
                        System.out.println("Barco colocado localmente.");
                        
                        sendMessage(new message("PLACE_SHIP", null, Map.of(
                                "name", s.get("name"),
                                "row", row,
                                "col", col,
                                "length", ((Integer)s.get("length")).intValue(),
                                "horizontal", hor
                        )));
                    }
                } catch (Exception e) {
                    System.out.println("Entrada inválida. Formato: fila,col,horizontal (ej: 2,3,true)");
                }
            }
        }
    }

    private void shootTurn() {
        boolean valid = false;
        while (!valid) {
            System.out.println("Ingresa coordenadas de disparo fila,col (ej: 3,4):");
            String line = scanner.nextLine().trim();
            try {
                String[] parts = line.split(",");
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());
                sendMessage(new message("SHOOT", null, Map.of("row", row, "col", col)));
                valid = true;
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
            }
        }
    }

    private void applyToEnemyBoard(int row, int col, String result) {
        
        if (result.startsWith("HUNDIDO") || "IMPACTO".equals(result)) {
            enemyBoard.getTablero().get(row).get(col).setTipo(tpFinalDeA2.tipoCelda.impacto);
            enemyBoard.getTablero().get(row).get(col).setDisparada(true);
        } else if ("AGUA".equals(result)) {
            enemyBoard.getTablero().get(row).get(col).setTipo(tpFinalDeA2.tipoCelda.fallo);
            enemyBoard.getTablero().get(row).get(col).setDisparada(true);
        }
    }

    private void applyToMyBoard(int row, int col, String result) {
        if (result.startsWith("HUNDIDO") || "IMPACTO".equals(result)) {
            myBoard.getTablero().get(row).get(col).setTipo(tpFinalDeA2.tipoCelda.impacto);
            myBoard.getTablero().get(row).get(col).setDisparada(true);
        } else if ("AGUA".equals(result)) {
            myBoard.getTablero().get(row).get(col).setTipo(tpFinalDeA2.tipoCelda.fallo);
            myBoard.getTablero().get(row).get(col).setDisparada(true);
        }
    }

    private void sendMessage(message m) {
        String s = gson.toJson(m);
        out.println(s);
        out.flush();
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 55555;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);
        batallaNavalClient client = new batallaNavalClient(host, port);
        client.start();
    }
}