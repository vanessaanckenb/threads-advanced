package br.com.vanessaancken.threads.initialsketch;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTask {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("---Starting server---");
        ServerSocket serverSocket = new ServerSocket(1234);
        ExecutorService threadsPool = Executors.newCachedThreadPool();

        while(true) {
            Socket socket = serverSocket.accept();
            threadsPool.execute(new TaskDistributor(socket));
        }
    }
}

class TaskDistributor implements Runnable {

    Socket socket;

    public TaskDistributor(Socket socket){
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