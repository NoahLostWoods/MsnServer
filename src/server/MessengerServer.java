package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class MessengerServer {

    private static final int PORT = 12345;
    private static final Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server in ascolto sulla porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());

                // Leggi il nome utente dal client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String username = in.readLine(); // Leggi il nome utente inviato dal client
                System.out.println("Nome utente ricevuto: " + username);

                ClientHandler clientHandler = new ClientHandler(clientSocket, MessengerServer::sendMessageTo, username);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageTo(String message, String recipientUsername) {
        for (ClientHandler client : clientHandlers) {
            if (client.getUsername().equals(recipientUsername)) {
                client.sendMessage(message);
                return;
            }
        }
        System.out.println("Utente " + recipientUsername + " non trovato.");
    }
}
