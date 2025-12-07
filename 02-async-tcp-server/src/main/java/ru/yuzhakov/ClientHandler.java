package ru.yuzhakov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("Ошибка обработки клиента: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }
}
