package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.BiConsumer;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BiConsumer<String, String> sendMessageTo;
    private String username;

    public ClientHandler(Socket socket, BiConsumer<String, String> sendMessageTo, String username) {
        this.socket = socket;
        this.sendMessageTo = sendMessageTo;
        this.username = username;

        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message); // Invia il messaggio al client
    }

    public String getUsername() {
        return username; // Metodo per ottenere il nome utente
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                System.out.println("Messaggio ricevuto: " + message);

                // Se il messaggio contiene il nome del destinatario, invia a quel client
                if (message.contains(":")) {
                    String[] parts = message.split(":", 2);
                    String recipient = parts[0].trim();
                    String msgToSend = parts[1].trim();
                    sendMessageTo.accept(username + ": " + msgToSend, recipient);
                } else {
                    // Altrimenti, gestisci come desiderato (ad esempio, un messaggio di errore)
                    System.out.println("Formato messaggio non valido.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client disconnesso.");
        }
    }
}
