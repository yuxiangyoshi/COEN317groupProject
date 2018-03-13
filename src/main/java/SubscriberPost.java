import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SubscriberPost extends Thread{

    private String _subName;
    private SubscriberGet _subscriberGet;

    public SubscriberPost(String subName, SubscriberGet subscriberGet) {
        this._subName = subName;
        this._subscriberGet = subscriberGet;
    }

    @Override
    public void run() {
//        System.out.println("start to run SubscriberPost " + this._subName);    // test only
        // get the accessible server
        while (true) {
//            System.out.print("subscriber " + _subName + ">");
            Scanner scan = new Scanner(System.in);
            String command = scan.nextLine();
            if ("exit".equals(command)) {
                // stop the thread
                this._subscriberGet.interrupt();
                scan.close();
                break;
            }
            try {
                Socket socket = null;
                FileReader fileReader = new FileReader("serverinfo.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] serverfields = line.split(" ");
                    String serverIP = serverfields[1];
                    int serverPort = Integer.valueOf(serverfields[2]);

                    try {
                        socket = new Socket(serverIP, serverPort);
                    } catch (Exception e) {
//                        System.err.println("Cannot connect to server: IP: " + serverIP + " Port: " + serverPort);    // test only
                        continue;
                    }

//                    System.out.println("Now post a request to server...");    // test only

                    PrintStream ps = new PrintStream(socket.getOutputStream());
                    ps.println("subscriber " + _subName + " " + command);

//                    ps.flush();
                    ps.close();
                    socket.close();
                    break;
                }


                bufferedReader.close();
                fileReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Exit Subscriber");    // test only
    }


}
