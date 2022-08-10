package br.com.vanessaancken.threads.organized;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTask {

    private ServerSocket serverSocket;
    private ExecutorService threadsPool;
    private boolean isRunning;

    public ServerTask() throws IOException {
        System.out.println("---Starting server---");
        this.serverSocket = new ServerSocket(1234);
        this.threadsPool = Executors.newCachedThreadPool();
        this.isRunning = Boolean.TRUE;
    }

    public void run() throws IOException {
        while(isRunning) {
            Socket socket = serverSocket.accept();
            threadsPool.execute(new TaskDistributor(this, socket));
        }
    }

    public void close() throws IOException {
        this.isRunning = Boolean.FALSE;
        serverSocket.close();
        threadsPool.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final var server = new ServerTask();
        server.run();
        server.close();
    }
}

class TaskDistributor implements Runnable {

    ServerTask server;
    Socket socket;

    public TaskDistributor(ServerTask server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("#Acceptiong client on port " + socket);
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNextLine()){
                final String input = scanner.nextLine();
                System.out.println(input);
                if(input.equalsIgnoreCase("end")){
                    server.close();
                }
                printStream.println("Ok");
            }
            scanner.close();
            printStream.close();
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}