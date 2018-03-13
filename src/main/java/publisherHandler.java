import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class PublisherHandler {

    public static void main(String[] args) throws IOException{
        if (args == null || args.length == 0) {
            System.err.println("Usage: java PublisherHandler <publisher_name>");
            System.exit(-1);
        }

        PublisherHandler publisher = new PublisherHandler(args[0]);

        publisher.start();
    }

    private String _pubName = "";
    private String _ip = "localhost";
    int _port = 8888;

    public PublisherHandler(String pubName) throws IOException {
        this._pubName =pubName;
        this._ip = InetAddress.getLocalHost().getHostAddress();
        this._port = ToolKit.findAvailablePort();
//        System.out.println("publisher " + _pubName + " use " + this._ip + ":" + this._port);    // test only
    }

    public void start() {

        while (true) {    // read the publishing messages
            System.out.print(this._pubName + ">");
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
//            ServerInfo actServer = getActiveServer();
//            System.out.println("The active server is " + actServer._ip + ":" + actServer._port);    // test only

            try {
//                Socket socket = new Socket("localhost", )
                Socket socket = null;
                FileReader fr = new FileReader("serverinfo.txt");
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    String [] values = line.split(" ");
                    String ip = values[1];
                    int port = Integer.valueOf(values[2]);
                    try {
                        socket = new Socket(ip, port);
                    } catch (Exception e){
                        System.err.println("Cannot locate the server " + ip + ":" + port);
                        continue;
                    }
                }
                if (socket == null) {
                    System.err.println("Cannot locate the server...");
                    continue;
                }

                PrintStream printStream = new PrintStream(socket.getOutputStream());
                String timeStampString = ToolKit.retrieveCurrentTime();
                printStream.println("publisher " + this._pubName + " (from " + this._pubName + " at " + timeStampString + "): " + message);

                printStream.flush();
                printStream.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }



    private class ServerInfo {
        String _ip = "";
        int _port;

        public ServerInfo(String ip, int port) {
            this._ip = ip;
            this._port = port;
        }
    }

}


