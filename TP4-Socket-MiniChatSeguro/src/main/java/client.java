import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javax.crypto.SecretKey;

public class client {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_SERVER = "\u001B[32m";
    private static final String ANSI_CLIENT = "\u001B[36m";
    private static final String ANSI_ERROR = "\u001B[31m";
    private static final String ANSI_INFO = "\u001B[33m";

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader console;

    private KeyPair rsaKeyPair;
    private PrivateKey myPriv;
    private PublicKey myPub;
    private String username;

    
    private final Map<String, String> knownPublics = new HashMap<>();

    public client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 12345;
        if (args.length >= 2) {
            host = args[0]; port = Integer.parseInt(args[1]);
        }
        client c = new client(host, port);
        c.start();
    }

    public void start() {
        try {
            console = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingresa tu nombre de usuario: ");
            username = console.readLine().trim();
            if (username.isEmpty()) username = "user" + new Random().nextInt(9999);

            rsaKeyPair = cryptoUtils.generateRSAKeyPair();
            myPriv = rsaKeyPair.getPrivate();
            myPub = rsaKeyPair.getPublic();

            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            
            String pubB64 = cryptoUtils.publicKeyToBase64(myPub);
            out.write("INIT||" + username + "||" + pubB64 + "\n");
            out.flush();
            
            new Thread(this::readerLoop).start();
           
            printInfo("Conectado. Usa /verComandos para ver comandos.");
            loopConsole();

        } catch (Exception e) {
            printError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void readerLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                handleServerLine(line);
            }
        } catch (IOException e) {
            printError("Conexión cerrada por el servidor.");
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    private void handleServerLine(String line) {
        try {
            if (line.startsWith("SYS||")) {
                String[] p = line.split("\\|\\|", 3);
                if (p.length >= 2) {
                    if (p[1].equals("LIST")) {
                        String list = (p.length >= 3) ? p[2] : "";
                        printInfo("Usuarios conectados: " + list);
                    } else if (p[1].equals("OK")) {
                        String msg = (p.length >= 3) ? p[2] : "OK";
                        printServer(msg);
                    } else if (p[1].equals("ERROR")) {
                        String msg = (p.length >= 3) ? p[2] : "ERROR";
                        printError(msg);
                    } else {
                        String msg = (p.length >= 2) ? p[1] : "";
                        printServer(msg);
                    }
                }
            } else if (line.startsWith("SEND||")) {
                
                String[] p = line.split("\\|\\|", 6);
                String from = p[1];
                String encKeyB64 = p[2];
                String ivB64 = p[3];
                String payloadB64 = p[4];
                String filename = p[5];

                
                byte[] encKey = cryptoUtils.fromBase64(encKeyB64);
                byte[] aesKeyBytes = cryptoUtils.rsaDecrypt(encKey, myPriv);
                SecretKey aesKey = cryptoUtils.aesKeyFromBytes(aesKeyBytes);

                byte[] iv = cryptoUtils.fromBase64(ivB64);
                byte[] cipherPayload = cryptoUtils.fromBase64(payloadB64);
                byte[] plain = cryptoUtils.aesDecrypt(cipherPayload, aesKey, iv);

                if (filename == null || filename.equals("NULL") || filename.equals("null")) {
                    String msg = new String(plain, "UTF-8");
                    printClient(String.format("[FROM %s] %s", from, msg));
                } else {
                    
                    Path dl = Paths.get("downloads");
                    if (!Files.exists(dl)) Files.createDirectories(dl);
                    Path filePath = dl.resolve(filename);
                    Files.write(filePath, plain);
                    printClient(String.format("[FROM %s] Archivo recibido: %s (guardado en downloads/%s)", from, filename, filename));
                }
            } else {
                printServer("Línea desconocida del servidor: " + line);
            }
        } catch (Exception e) {
            printError("Error al procesar mensaje entrante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loopConsole() {
        try {
            printInfo("/verComandos para ayuda.");
            String line;
            while ((line = console.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("/salir")) {
                    sendRaw("CMD||EXIT");
                    printInfo("Desconectando...");
                    socket.close();
                    break;
                } else if (line.equals("/listar")) {
                    sendRaw("CMD||LIST");
                } else if (line.equals("/verComandos") || line.equals("/ayuda")) {
                    showCommands();
                } else if (line.startsWith("/msg ")) {
                    
                    String[] parts = line.split(" ", 3);
                    if (parts.length < 3) {
                        printError("Uso: /msg [usuario] [mensaje]");
                        continue;
                    }
                    String to = parts[1];
                    String msg = parts[2];
                    sendEncryptedMessage(to, msg.getBytes("UTF-8"), null);
                } else if (line.startsWith("/enviarArchivo ")) {
                    
                    String[] parts = line.split(" ", 3);
                    if (parts.length < 3) {
                        printError("Uso: /enviarArchivo [usuario] [archivo]");
                        continue;
                    }
                    String to = parts[1];
                    String path = parts[2];
                    Path p = Paths.get(path);
                    if (!Files.exists(p)) {
                        printError("Archivo no encontrado: " + path);
                        continue;
                    }
                    String filename = p.getFileName().toString();
                    byte[] fileBytes = Files.readAllBytes(p);
                    sendEncryptedMessage(to, fileBytes, filename);
                    printInfo("Archivo enviado (cifrado) a " + to);
                } else {
                    
                    sendRaw("CMD||LIST");
                    
                    Thread.sleep(100); 
                    
                    printInfo("Enviando mensaje público a usuarios conocidos...");

                    byte[] msgBytes = line.getBytes("UTF-8");
                    boolean any = false;
                    
                    for (Map.Entry<String, String> e : new HashMap<>(knownPublics).entrySet()) {
                        String to = e.getKey();
                        if (to.equals(username)) continue;
                        any = true;
                        sendEncryptedMessage(to, msgBytes, null);
                    }
                    if (!any) {
                        printError("No hay claves públicas de otros usuarios en caché. Envía un /msg a un usuario una vez para intercambiar claves, o espera a que otros envíen mensajes para obtener su clave.");
                    }
                }
            }
        } catch (Exception e) {
            printError("Error en consola: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCommands() {
        printInfo("Comandos disponibles:");
        System.out.println("/salir - Cerrar sesión y desconectar.");
        System.out.println("/listar - Ver usuarios conectados.");
        System.out.println("/verComandos - Mostrar comandos.");
        System.out.println("/msg [usuario] [mensaje] - Enviar mensaje privado.");
        System.out.println("/enviarArchivo [usuario] [archivo] - Enviar archivo a usuario.");
        System.out.println("/ayuda - Mostrar ayuda.");
    }

    private synchronized void sendRaw(String s) throws IOException {
        out.write(s + "\n");
        out.flush();
    }


    private void sendEncryptedMessage(String to, byte[] content, String filename) throws Exception {
        
        String pubB64 = knownPublics.get(to);
        if (pubB64 == null) {
            printInfo("No tenemos clave pública de " + to + ". Solicitando lista al servidor y esperando...");
            sendRaw("CMD||LIST");
            Thread.sleep(150);
            
            if (!knownPublics.containsKey(to)) {
                printError("Aún no tenemos la clave pública de " + to + ". Pídele al usuario que se conecte o envíe un mensaje primero.");
                return;
            }
            pubB64 = knownPublics.get(to);
        }
        java.security.PublicKey pub = cryptoUtils.publicKeyFromBase64(pubB64);


        SecretKey aesKey = cryptoUtils.generateAESKey(256);
        cryptoUtils.AESResult ar = cryptoUtils.aesEncrypt(content, aesKey);
        
        byte[] encAesKey = cryptoUtils.rsaEncrypt(aesKey.getEncoded(), pub);
        String encKeyB64 = cryptoUtils.toBase64(encAesKey);
        String ivB64 = cryptoUtils.toBase64(ar.iv);
        String payloadB64 = cryptoUtils.toBase64(ar.cipherText);
        String fn = (filename == null) ? "NULL" : filename;
        
        String line = String.format("SEND||%s||%s||%s||%s||%s", to, encKeyB64, ivB64, payloadB64, fn);
        sendRaw(line);
    }

    private void printServer(String m) { System.out.println(ANSI_SERVER + "[SERVER] " + m + ANSI_RESET); }
    private void printClient(String m) { System.out.println(ANSI_CLIENT + "[MSG] " + m + ANSI_RESET); }
    private void printError(String m) { System.out.println(ANSI_ERROR + "[ERROR] " + m + ANSI_RESET); }
    private void printInfo(String m) { System.out.println(ANSI_INFO + "[INFO] " + m + ANSI_RESET); }
}
