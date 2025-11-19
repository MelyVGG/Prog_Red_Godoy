import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static final int port= 5000 ;
	private static final String Bienvenido = "Bienvenido al servidor local";
	
	@SuppressWarnings("unchecked")
	private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList());
	public static void main(String[]args) {
		System.out.println("Servidor iniciado en el puerto "+ port);
		try (ServerSocket serverSocket = new ServerSocket(port)){
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ClientHandler handler = new ClientHandler(clientSocket);
				clients.add(handler);
				handler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void broadcast(String msg) {
		synchronized(clients) {
			for (ClientHandler c: clients) {
				c.sendMessage(msg);
			}
		}
	}
	public static void removeClient(ClientHandler c) {
		clients.remove(c);
	}
		static class ClientHandler extends Thread {
			private Socket socket;
			private BufferedReader in;
			private PrintWriter out;
			
			public ClientHandler(Socket s) {
				this.socket = s;
			}
			@Override public void run() {
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					
					sendMessage(Bienvenido);
					String line;
					while((line = in.readLine())!=null) {
						if (line.equals("/hi")) {
							sendMessage(Bienvenido);
						}
						else if (line.equals("/logout")) {
							sendMessage("Desconectado");
							break;
						}
						else if (line.startsWith("/")) {
							sendMessage("Comando desconocido");
						}
						else {
							Server.broadcast(line);
						}
					}
				} catch (IOException e) {
					System.out.println("Cliente desconectado");
				} finally {
					try {
						socket.close();
					} catch (IOException ignored) {}
					Server.removeClient(this);
				}
			}
			public void sendMessage(String msg) {
				out.println(msg);
			}
		}
	}