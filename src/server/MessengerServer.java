package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class MessengerServer {

    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server in ascolto sulla porta " + PORT);

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, MessengerServer::broadcast);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler excludeUser){
        for(ClientHandler client : clientHandlers){
            if(client != excludeUser){
                client.sendMessage(message);
            }
        }
    }
}
