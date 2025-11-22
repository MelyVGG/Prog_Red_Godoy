import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 Protocolo simple basado en líneas:
 - INIT||username||publicKeyBase64
 - CMD||LIST
 - CMD||EXIT
 - SEND||toUsername||encryptedAesKeyBase64||ivBase64||payloadBase64||filenameOrNULL
 - SYS||message  (servidor -> cliente)
*/

public class server {
    private final int port;
    // username -> ClientHandler
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    // store public keys (Base64 string) for quick listing
    private final Map<String, String> publicKeys = new ConcurrentHashMap<>();

    public server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 12345;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        server server = new server(port);
        server.start();
    }

    public void start() {
        System.out.println("[SERVER] Iniciando en puerto " + port);
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> {
                    try {
                        handleNewConnection(s);
                    } catch (Exception e) {
                        System.err.println("[SERVER] Error al aceptar conexión: " + e.getMessage());
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNewConnection(Socket s) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            // esperar INIT
            String init = in.readLine();
            if (init == null || !init.startsWith("INIT||")) {
                out.write("SYS||Error inicial. Debes enviar INIT||username||publicKeyBase64\n");
                out.flush();
                s.close();
                return;
            }
            String[] parts = init.split("\\|\\|", 3);
            String username = parts[1];
            String pubKey = parts[2];

            if (clients.containsKey(username)) {
                out.write("SYS||ERROR||Usuario ya conectado\n");
                out.flush();
                s.close();
                return;
            }

            ClientHandler ch = new ClientHandler(username, s, in, out);
            clients.put(username, ch);
            publicKeys.put(username, pubKey);

            broadcastSystem(String.format("Usuario %s se ha conectado.", username), username);

            new Thread(ch).start();
            System.out.println("[SERVER] Usuario conectado: " + username);
            out.write("SYS||OK||Conexión exitosa al servidor.\n");
            out.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void broadcastSystem(String msg, String exceptUsername) {
        for (ClientHandler ch : clients.values()) {
            if (ch.username.equals(exceptUsername)) continue;
            ch.sendRaw("SYS||" + msg);
        }
    }

    private void removeClient(String username) {
        clients.remove(username);
        publicKeys.remove(username);
        broadcastSystem("Usuario " + username + " se ha desconectado.", username);
        System.out.println("[SERVER] Usuario desconectado: " + username);
    }

    class ClientHandler implements Runnable {
        String username;
        Socket socket;
        BufferedReader in;
        BufferedWriter out;
        volatile boolean running = true;

        ClientHandler(String username, Socket socket, BufferedReader in, BufferedWriter out) {
            this.username = username;
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        void sendRaw(String line) {
            try {
                out.write(line + "\n");
                out.flush();
            } catch (IOException e) {
                // cliente muerto -> cleanup
                running = false;
                try { socket.close(); } catch (Exception ignored) {}
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while (running && (line = in.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    handleLine(line);
                }
            } catch (IOException e) {
                // ignore
            } finally {
                running = false;
                try { socket.close(); } catch (Exception ignored) {}
                removeClient(username);
            }
        }

        private void handleLine(String line) {
            try {
                if (line.startsWith("CMD||")) {
                    String cmd = line.substring(5);
                    if (cmd.equals("LIST")) {
                        StringBuilder sb = new StringBuilder();
                        for (String u : clients.keySet()) sb.append(u).append(",");
                        sendRaw("SYS||LIST||" + sb.toString());
                    } else if (cmd.equals("EXIT")) {
                        sendRaw("SYS||OK||Desconectando...");
                        running = false;
                        try { socket.close(); } catch (Exception ignored) {}
                    }
                    return;
                } else if (line.startsWith("SEND||")) {
                    // formato SEND||to||encAesKeyBase64||ivBase64||payloadBase64||filenameOrNULL
                    String[] p = line.split("\\|\\|", 6);
                    if (p.length < 6) {
                        sendRaw("SYS||ERROR||Formato SEND inválido");
                        return;
                    }
                    String to = p[1];
                    String encKey = p[2];
                    String iv = p[3];
                    String payload = p[4];
                    String filename = p[5];

                    if (to.equals("ALL")) {
                        // Reenvío a todos excepto remitente
                        for (ClientHandler ch : clients.values()) {
                            if (ch.username.equals(username)) continue;
                            ch.sendRaw("SEND||" + username + "||" + encKey + "||" + iv + "||" + payload + "||" + filename);
                        }
                    } else {
                        ClientHandler dest = clients.get(to);
                        if (dest == null) {
                            sendRaw("SYS||ERROR||Usuario no encontrado para el mensaje privado.");
                        } else {
                            dest.sendRaw("SEND||" + username + "||" + encKey + "||" + iv + "||" + payload + "||" + filename);
                            sendRaw("SYS||OK||Mensaje reenviado a " + to);
                        }
                    }
                } else {
                    sendRaw("SYS||ERROR||Comando no reconocido.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
