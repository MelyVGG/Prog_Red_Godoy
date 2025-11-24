package TP3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class fileServer {
    private static final int PORT = 5000;
    private static final int BUFFER_SIZE = 4096;
    private static final Path RECEIVED_DIR = Path.of("received");

    public static void main(String[] args) {
        try {
            if (!Files.exists(RECEIVED_DIR)) {
                Files.createDirectories(RECEIVED_DIR);
            }
        } catch (IOException e) {
            System.err.println(colors.red + "ERROR creando carpeta de recepción: " + e.getMessage() + colors.reset);
            return;
        }

        System.out.println(colors.blue + "Servidor iniciado. Escuchando en puerto " + PORT + "..." + colors.reset);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(colors.blue + "Cliente conectado desde " + clientSocket.getRemoteSocketAddress() + colors.reset);

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println(colors.red + "ERROR en servidor: " + e.getMessage() + colors.reset);
        }
    }

    private static void handleClient(Socket socket) {
        try (Socket s = socket;
             DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()))) {

            while (true) {
                int nameLen;
                try {
                    nameLen = dis.readInt();
                } catch (EOFException eof) {
                    System.out.println(colors.blue + "Cliente desconectado correctamente." + colors.reset);
                    break;
                }

                if (nameLen == -1) {
                    System.out.println(colors.blue + "Cliente indicó fin de transmisión." + colors.reset);
                    break;
                }

                if (nameLen <= 0) {
                    System.out.println(colors.red + "Nombre de archivo inválido recibido: " + nameLen + colors.reset);
                    break;
                }

                byte[] nameBytes = new byte[nameLen];
                dis.readFully(nameBytes);
                String fileName = new String(nameBytes, "UTF-8");

                long fileSize = dis.readLong();

                System.out.println(colors.blue + "Iniciando recepción de archivo: " + fileName + " (" + fileSize + " bytes)" + colors.reset);

                Path target = RECEIVED_DIR.resolve(fileName);
                if (Files.exists(target)) {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    target = RECEIVED_DIR.resolve(timestamp + "_" + fileName);
                }

                try (OutputStream fos = new BufferedOutputStream(Files.newOutputStream(target))) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    long remaining = fileSize;
                    int read;
                    while (remaining > 0 && (read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, read);
                        remaining -= read;
                    }
                    fos.flush();
                } catch (IOException ioe) {
                    System.err.println(colors.red + "ERROR guardando archivo " + fileName + ": " + ioe.getMessage() + colors.reset);
                    continue;
                }

                System.out.println(colors.green + "Archivo recibido y guardado correctamente: " + target.toString() + colors.reset);
            }
        } catch (IOException e) {
            System.err.println(colors.red + "ERROR en la conexión con el cliente: " + e.getMessage() + colors.reset);
        }
    }
}