import java.io.*;
import java.net.*;

public class Client {
	public static void main (String[]args) {
		
		// Código Unico = 1BDE006A
		// Code = BACB
		
		String ip = "130.10.1.54";
		int port = 5000;
		
		System.out.println("Coenctado a "+ ip + ":" + port + "...");
		
		try {
			Socket socket = new Socket(ip,port);
			System.out.println("Conectado al servidor del profesor");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			Thread listener = new Thread(() ->{ 
				String msg;
				try {
					while ((msg = in.readLine())!=null) {
						System.out.println("[Servidor]" + msg);
					}
				} catch (IOException e) {
					System.out.println("Conexion cerrada");
				}
			});
			listener.start();
			
			Thread sender = new Thread(() ->{
				BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
				while(true) {
					try {
						String text = console.readLine();
						out.println(text);
						
						if(text.equals("/logout")) {
							socket.close();
							break;
						}
					} catch (IOException e) {
						break;
					}
				}
			});
			sender.start();
			try {
				sender.join();
				listener.join();
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
			try {
				if(!socket.isClosed()) socket.close();
			} catch (IOException ignored) {}
	}catch (Exception e) {
		System.out.println("No se pudo conectar al servidor del profesor");
	}
}
}
