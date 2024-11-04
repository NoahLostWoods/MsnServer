package server;

import com.sun.security.ntlm.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.BiConsumer;

public class ClientHandler implements Runnable{

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BiConsumer<String, ClientHandler> broadcast;

    public ClientHandler(Socket socket, BiConsumer<String, ClientHandler> broadcast){
        this.socket = socket;
        this.broadcast = broadcast;

        try{
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        System.out.println(message);
    }

    @Override
    public void run() {
        String message;
        try{
            while ((message = in.readLine()) != null){
                System.out.println("Messaggio ricevuto: " + message);
                broadcast.accept(message, this);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println("Client disconnesso.");
        }
    }
}
