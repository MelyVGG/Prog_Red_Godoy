package TP3;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class fileClient {
    private static final String SERVER_HOST = "localhost"; // cambia si el server está en otra máquina
    private static final int SERVER_PORT = 5000;
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new fileClient().start();
            } catch (Exception e) {
                System.err.println(colors.red + "ERROR iniciando cliente: " + e.getMessage() + colors.reset);
            }
        });
    }

    private void start() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream din = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
            // Mensaje de conexión
            System.out.println(colors.green + "Conexión establecida con el servidor " + socket.getRemoteSocketAddress() + colors.reset);

            boolean enviarMas = true;
            while (enviarMas) {
                File file = chooseFile();
                if (file == null) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "No se eligió archivo. Desea cerrar la conexión con el servidor?",
                            "Cerrar",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        break;
                    } else {
                        continue;
                    }
                }

                sendFile(file.toPath(), dos);

                // Preguntar si desea enviar otro
                int resp = JOptionPane.showConfirmDialog(null,
                        "¿Desea enviar otro archivo?",
                        "Enviar otro?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                enviarMas = (resp == JOptionPane.YES_OPTION);
            }

            // Enviar señal de terminación (-1)
            dos.writeInt(-1);
            dos.flush();

            System.out.println(colors.blue + "Terminando conexión con servidor." + colors.reset);
        } catch (IOException e) {
            System.err.println(colors.red + "ERROR de conexión/transmisión: " + e.getMessage() + colors.reset);
        }
    }

    private File chooseFile() {
        // Usamos JFileChooser para seleccionar archivos. Puede integrarse en JOptionPane si quieres.
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo para enviar");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // opcional: añadir filtros si quieres
        // chooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg"));

        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private void sendFile(Path path, DataOutputStream dos) {
        File file = path.toFile();
        String filename = file.getName();
        long fileSize = file.length();

        try (InputStream fis = new BufferedInputStream(new FileInputStream(file))) {
            // Enviar longitud del nombre y nombre (UTF-8)
            byte[] nameBytes = filename.getBytes("UTF-8");
            dos.writeInt(nameBytes.length);
            dos.write(nameBytes);

            // Enviar tamaño del archivo
            dos.writeLong(fileSize);

            System.out.println(colors.blue + "Enviando archivo: " + filename + " (" + fileSize + " bytes)..." + colors.reset);

            // Enviar contenido en bloques
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            long totalSent = 0;
            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
                totalSent += read;
            }
            dos.flush();

            if (totalSent == fileSize) {
                System.out.println(colors.green + "Envío exitoso: " + filename + colors.reset);
                JOptionPane.showMessageDialog(null, "Envío exitoso: " + filename, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.err.println(colors.red + "Error: bytes enviados (" + totalSent + ") != tamaño del archivo (" + fileSize + ")" + colors.reset);
                JOptionPane.showMessageDialog(null, "Error: bytes enviados != tamaño del archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            System.err.println(colors.red + "ERROR enviando archivo " + filename + ": " + e.getMessage() + colors.reset);
            JOptionPane.showMessageDialog(null, "ERROR enviando archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

