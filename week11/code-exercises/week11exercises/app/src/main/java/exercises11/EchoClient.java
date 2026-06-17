// Example from https://www.baeldung.com/a-guide-to-java-sockets
// jst@itu.dk * 2024-05-03
package exercises11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
      try {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      } catch (IOException e) {  System.out.println(e.getMessage());   }
    }

    public String sendMessage(String msg) {
      try {
        out.println(msg);
        return in.readLine();
      } catch (Exception e) {  return null;   }
    }

    public void stopConnection() {
      try {
        in.close();
        out.close();
        clientSocket.close();
      } catch (IOException e) {  System.out.println(e.getMessage());   }
    }

    public EchoClient() {
      startConnection("127.0.0.1", 8080);
      String resp1= sendMessage("hello");
      String resp2= sendMessage("world");
      String resp3= sendMessage("!");
      String resp4= sendMessage("."); // . stops the server

      System.out.println(resp1+" "+resp2+" "+resp3+" "+resp4);
    }

    public static void main(String[] args) {
      new EchoClient();
    }
}