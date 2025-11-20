package tpFinalDeA2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class batallaNavalServer {
    private final int port;
    private final ExecutorService pool;
    private final ConcurrentLinkedQueue<Socket> waitingQueue;
    @SuppressWarnings({ "unused" })
	private final Gson gson;

    public batallaNavalServer(int port) {
        this.port = port;
        this.pool = Executors.newCachedThreadPool();
        this.waitingQueue = new ConcurrentLinkedQueue<>();
        this.gson = new GsonBuilder().create();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Battleship escuchando en puerto " + port);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Conexion entrante: " + client.getRemoteSocketAddress());
                waitingQueue.add(client);
                
                if (waitingQueue.size() >= 2) {
                    Socket s1 = waitingQueue.poll();
                    Socket s2 = waitingQueue.poll();
                    if (s1 != null && s2 != null) {
                        GameRoom room = new GameRoom(s1, s2);
                        pool.submit(room);
                    } else {
                        
                        if (s1 != null) waitingQueue.add(s1);
                        if (s2 != null) waitingQueue.add(s2);
                    }
                } else {
                    
                    pool.submit(new ClientHandler(client, null));
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    public static void main(String[] args) {
        int port = 55555;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        batallaNavalServer server = new batallaNavalServer(port);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}