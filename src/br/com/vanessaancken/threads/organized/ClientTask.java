package br.com.vanessaancken.threads.organized;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientTask {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 1234);
        System.out.println("###Connected###");

        final var sendMessageThreads = new Thread(new SendMessageTask(socket));
        final var receiveMessageThreads = new Thread(new ReceiveMessageTask(socket));

        sendMessageThreads.start();
        receiveMessageThreads.start();

        sendMessageThreads.join();

        socket.close();
    }
}


class SendMessageTask implements Runnable {

    Socket socket;

    public SendMessageTask(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()){
                final String output = scanner.nextLine();
                if(output.isBlank()){
                    break;
                }
                printStream.println(output);
            }
            scanner.close();
            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReceiveMessageTask implements Runnable {

    Socket socket;

    public ReceiveMessageTask(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            final var scanner = new Scanner(socket.getInputStream());
            while(scanner.hasNextLine()){
                String input = scanner.nextLine();
                System.out.println(input);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}