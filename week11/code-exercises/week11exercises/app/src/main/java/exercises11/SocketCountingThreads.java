/* Timing a simple Socket connection from Java
Code can be used to demonstrate 
   1. race-condition if server counted is not locked
   2. measure run-time for different types of locking (see run method)
   3. measure run-time differences for different placements of server localhost or local network
*/

// jst@itu.dk * 2024-07-11

package exercises11;

import java.io.IOException;
import java.lang.InterruptedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import benchmarking.Benchmark;  // Mark7 modified to show seconds in stead of nS
import benchmarking.Benchmarkable;

public class SocketCountingThreads {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    Monitor clientLock = new Monitor();
    private static int iterations = 10;
    private static int noofThreads = 10;
    private final static String URL =
            "127.0.0.1";     // this PC
    //"localhost";     // this PC

    public SocketCountingThreads() {
        startConnection(URL, 8080);
        Benchmark.Mark7("Socket com:", i -> Transaction());
        sendMessage("stop");
        stopConnection();
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String sendMessage(String msg) {
        try {
            out.println(msg);
            return in.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public class Monitor {
        public synchronized void incr() {
            int c = Integer.parseInt(sendMessage("get"));
            c = c + 1;
            sendMessage("put&" + c);
        }
    }

    public class TransactionThread extends Thread {

        private void noLocking() {
            // Fetching, incrementing and storing not atomic
            //uses GET on the server.
            //Then increments in client
            //Then uses PUT on the server
            int c;
            for (int k = 0; k < iterations; k = k + 1) {
                c = Integer.parseInt(sendMessage("get"));
                c = c + 1;
                sendMessage("put&" + c);
            }
        }

        private void serverLocking() {
            //Uses INCR, that on server increments and returns the new value
            for (int k = 0; k < iterations; k = k + 1) {
                sendMessage("incr");
            }
        }

        private void clientLocking() {
            //uses GET on the server, which returns the value
            //Increment the value on client with lock
            //Then uses PUT on the server
            for (int k = 0; k < iterations; k = k + 1) {
                clientLock.incr();
            }
        }

        public void run() {
            noLocking();
            //serverLocking();
            //clientLocking();

        }
    }

    private int Transaction() {
        TransactionThread[] t = new TransactionThread[noofThreads];
        sendMessage("put&" + 0); // reset counter on server to 0
        for (int j = 0; j < noofThreads; j = j + 1) {
            t[j] = new TransactionThread();
            t[j].start();
        }
        ;
        try {
            for (int j = 0; j < noofThreads; j = j + 1)
                t[j].join();
        } catch (InterruptedException ioe) {
        }
        ;
        return 0;
    }

    public static void main(String[] args) {
        new SocketCountingThreads();
    }
}
