// Example inspired by https://www.baeldung.com/a-guide-to-java-sockets
// Simple server handling a counter 
// jst@itu.dk * 2024-04-29

package exercises11;

import java.net.*;
import java.io.*;

public class NumberServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int count = 0;

    public String readMessage(BufferedReader in) {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = readMessage(in)) != null) {
                //System.out.println(inputLine);
                if ("incr".equals(inputLine)) {
                    count = count + 1;
                    out.println(count);
                    //System.out.println("count= "+count);
                } else if ("get".equals(inputLine)) {
                    out.println(count);
                } else if ("put".equals(inputLine.substring(0, 3))) {
                    //System.out.println("i= "+inputLine.substring(4, inputLine.length()));
                    count = Integer.parseInt(inputLine.substring(4, inputLine.length()));
                    out.println(count);
                } else if ("stop".equals(inputLine)) {
                    out.println("good bye " + count);
                    stop();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("count= " + count);
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new NumberServer().start(8080);
    }
}