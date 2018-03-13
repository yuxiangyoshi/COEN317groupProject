import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class SubscriberHandler {
    public static void main(String[] args) throws IOException{
        if (args == null || args.length == 0) {
            System.err.println("Usage java SubscriberHandler <subscriber-name>");
            System.exit(-1);
        }

        SubscriberHandler subscriberHandler = new SubscriberHandler(args[0]);
    }


    private String _subName;
    private int _port;

    public SubscriberHandler(String name) throws IOException{
        this._subName = name;
        String ip = InetAddress.getLocalHost().getHostAddress();
        this._port = ToolKit.findAvailablePort();
//        System.out.println(this._subName + " IP: " + InetAddress.getLocalHost().getHostAddress() + " port: " + this._port);    // test only

        // access to serverinfo.txt to the server information.
        Socket socket = null;
        File serverinfo = new File("serverinfo.txt");
        FileReader fileReader = new FileReader(serverinfo);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            boolean isconnected = false;
            String[] serverfields = line.split(" ");
            String serverIP = serverfields[1];
            int serverPort = Integer.valueOf(serverfields[2]);
            try {
                socket = new Socket(serverIP, serverPort);
            } catch (Exception ex) {
//                System.err.println("Fail to connect server: " + serverIP + " port: " + serverPort);    // test only
                continue;
            }
            isconnected = true;
            if (isconnected) {
                break;
            }

        }

        // Tell the server that the subscriber is online
        PrintStream ps = new PrintStream(socket.getOutputStream());
        Scanner scanner = new Scanner(socket.getInputStream());
        ps.println("subscriber " + _subName + " online " + ip + " " + this._port);

//        System.out.println("start to load unread message...");    // test only

        // read the unread messages
        String message = null;
        while (scanner.hasNextLine()) {
            message = scanner.nextLine();
            System.out.println("unread message:" + message);
        }

        System.out.println("finish loading unread message...");    // test only

        // start Subscribe Post (Write to Server) and Get (Read from Server)
//        System.out.println("start GET and POST for subscriber " + this._subName);
        SubscriberGet subscriberGet = new SubscriberGet(this._subName, this._port);
        subscriberGet.start();

        SubscriberPost subscriberPost = new SubscriberPost(this._subName, subscriberGet);
        subscriberPost.start();

    }




}
