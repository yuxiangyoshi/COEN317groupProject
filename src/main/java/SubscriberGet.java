import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SubscriberGet extends Thread{

    private String _subName;
    private int _port;

    public SubscriberGet(String name, int port) {
        this._subName = name;
        this._port = port;
    }


    @Override
    public void run() {
//        System.out.println("start to run SubscriberGet " + this._subName);    // test only
        try {
            ServerSocket serverSocket = new ServerSocket(this._port);
            while (true) {
                Socket socket = serverSocket.accept();
                Scanner scanner = new Scanner(socket.getInputStream());

                while (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    System.out.println("receive message: " + message);
                }

                scanner.close();
                socket.close();
            }

        } catch (IOException ex) {

        }
    }
}
