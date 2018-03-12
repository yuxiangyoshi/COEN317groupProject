import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class MainServer {
    public static void main(String[] args) throws IOException{
        if (args == null || args.length == 0) {
            System.err.println("Usage: java MainServer <server_name>");
            System.exit(-1);
        }
        MainServer ms = new MainServer(args[0]);
        ms.start();
    }

    private String _serverName;
    private int _port;

    public MainServer(String serverName) throws IOException{
        this._serverName = serverName;
        String ip = InetAddress.getLocalHost().getHostAddress();
        _port = ToolKit.findAvailablePort();
        System.out.println("Main Server ip:" + ip + " port:" + _port + " start..");

        // write the ip address to serverinfo.txt for publishers and users
        System.out.println("read file serverinfo.txt...");    // test only
        File file = new File("serverinfo.txt");
        boolean exist = false;
        boolean fileEmpty = true;

        if (file.exists()) {
            FileReader fr = new FileReader("serverinfo.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                fileEmpty = false;
                String[] infoFields = line.split(" ");
                if (infoFields[1].equals(ip) && infoFields[2].equals(Integer.toString(this._port))) {
                    exist = true;
                    break;
                }
            }
            br.close();
            fr.close();
        }

        System.out.println("write the server info to serverinfo.txt...");    // test only
        if (!exist) {
            BufferedWriter bw = new BufferedWriter(new FileWriter("serverinfo.txt", true));
            if (!fileEmpty) {
                bw.newLine();
            }
            bw.write(this._serverName + " " + ip + " " + this._port);
            bw.close();
        }
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(this._port);

            while (true) {
                System.out.println("waiting for a new client socket...");    // test only
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                System.out.println("request is from ip: " + ip + " port: " + port);
                Scanner scanner = new Scanner(socket.getInputStream());
                while (scanner.hasNextLine()) {
                    String command = scanner.nextLine();
                    System.out.println(command);    // test only

                    executeCommand(command);
                }
                scanner.close();
                socket.close();
                System.out.println("complete current request...");    // test only
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void executeCommand(String command) {
        if (command.startsWith("publisher")) {
            int endIndex = command.indexOf(" ", "publisher".length() + 1);
            String subName = command.substring("publisher".length() + 1, endIndex).trim();    // subscriber Name
            String message = command.substring(endIndex + 1).trim();
            List<String> subscriberList = new ArrayList<>();
            File relationFile = new File("relation.txt");
            if (relationFile.exists()) {
                try {
                    FileReader fileReader = new FileReader(relationFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] relfields = line.split(" ");
                        if (relfields.equals(subName)) {
                            subscriberList.add(relfields[1]);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            Iterator<String> iterator = subscriberList.iterator();
            while (iterator.hasNext()) {
                String subscriber = iterator.next();
                try {
                    FileReader fileReader = new FileReader(_serverName + File.separator + "subscriberinfo.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] subInfoFields = line.split(" ");
                        if (subInfoFields[0].equals(subscriber)) {
                            Socket socket = null;
                            String subsIP = subInfoFields[1];
                            int subsPort = Integer.valueOf(subInfoFields[2]);
                            try {
                                socket = new Socket(subsIP, subsPort);
                            } catch (Exception ex) {
                                System.err.println("Unable to connect to subscriber " + subscriber + " IP: " + subsIP + " Port:" + subsPort);
                                // Write to pending message file
                                try {
                                    FileWriter fileWriter = new FileWriter(this._serverName + File.separator + "");
                                    PrintWriter printWriter = new PrintWriter(fileWriter);
                                    printWriter.println(subscriber + " " + );

                                    printWriter.close();
                                    fileWriter.close();
                                } catch (IOException ioe) {

                                }

                                continue;
                            }

                            PrintStream printStream = new PrintStream(socket.getOutputStream());
                            printStream.println("message" + message);
                            if (socket != null) {
                                socket.close();
                            }
                        }
                    }
                    bufferedReader.close();
                    fileReader.close();
                } catch (IOException ex) {
                    System.err.println("Error occur in reading file subscriberinfo.txt");
                }

            }

        } else if (command.startsWith("subscriber")) {    // handle request from subscriber
            int startIndex = "subscriber".length() + 1;
            int endIndex = command.indexOf(" ", startIndex);
            String subscriber = command.substring(startIndex, endIndex);

            if (command.startsWith("online", endIndex + 1)) {
                // a subscriber comes to line, update the subscriber information
                List<SubscriberInfo> subscriberInfoList = new ArrayList<>();
                startIndex = endIndex + "online".length() + 1;
                String[] values = command.split(" ");
                String subsNewIP = values[4];
                String subsNewPort = values[5];

                try {
                    File subsInfoFile = new File(_serverName + File.separator + "subscriberinfo.txt");
                    FileReader fileReader = new FileReader(subsInfoFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] subsfields = line.split(" ");
                        if (subsfields[0].equals(subscriber)) {
                            subscriberInfoList.add(new SubscriberInfo(subscriber, subsNewIP, subsNewPort));
                        } else {
                            subscriberInfoList.add(new SubscriberInfo(subsfields[0], subsfields[1], subsfields[2]));
                        }
                    }
                    bufferedReader.close();
                    fileReader.close();

                    // write to file subscriberinfo.txt (replace the old information)
                    FileWriter fileWriter = new FileWriter(subsInfoFile, false);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    Iterator<SubscriberInfo> iterator = subscriberInfoList.iterator();
                    String prefix = "";
                    while (iterator.hasNext()) {
                        SubscriberInfo subsInfo = iterator.next();
                        String content = prefix + subsInfo._subscriber + " " + subsInfo._ip + " " + subsInfo._port;
                        bufferedWriter.write(content);
                    }

                    bufferedWriter.close();
                    fileWriter.close();
                } catch (IOException ex) {

                }

                // send back the unread message to that
                try {
                    // the unsent message will be rewritten to the file
                    List<String> unsentMsgList = new ArrayList<>();
                    List<String> sentMsgList = new ArrayList<>();

                    FileReader fileReader = new FileReader(_serverName + File.separator + "unreadmsg.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        int offset = line.indexOf(" ");
                        String subName = line.substring(0, offset);
                        if (subscriber.equals(subName)) {
                            sentMsgList.add(line.substring(offset + 1));
                        } else {
                            unsentMsgList.add(line);
                        }
                    }
                    bufferedReader.close();
                    fileReader.close();

                    Socket socket = new Socket(subsNewIP, Integer.valueOf(subsNewPort));
                    PrintStream ps = new PrintStream(socket.getOutputStream());
                    for (String msg : sentMsgList) {
                        ps.println(msg);
                    }
                    ps.flush();
                    ps.close();
                    socket.close();

                    FileWriter fileWriter = new FileWriter(_serverName + File.separator + "unreadmsg.txt", false);
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    for (String msg : unsentMsgList) {
                        printWriter.println(msg);
                    }

                    printWriter.close();
                    fileWriter.close();

                } catch (IOException ex) {

                }


                // notify other servers
                try {
                    FileReader fileReader = new FileReader("serverinfo.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] serverfields = line.split(" ");
                        if (!this._serverName.equals(serverfields[0])) {
                            Socket socket = null;
                            try {
                                socket = new Socket(serverfields[1], Integer.valueOf(serverfields[2]));
                                PrintStream printStream = new PrintStream(socket.getOutputStream());
                                printStream.println("server online " + subscriber + " " + subsNewIP + " " + subsNewPort);
                                printStream.println("server sendmsg " + subscriber);
                                printStream.close();
                                socket.close();
                            } catch (IOException ex) {
                                System.err.println("Error: Unable to connect to server IP:" + serverfields[1] + " port: " + serverfields[2]);
                                continue;
                            }
                        }
                    }

                    bufferedReader.close();
                    fileReader.close();

                } catch (IOException ex) {

                }


            } else if (command.startsWith("subscribe", endIndex + 1)) {
                // update local relation.txt
                int offset = command.lastIndexOf(" ") + 1;
                String publisher = command.substring(offset);
                try {
                    FileWriter fileWriter = new FileWriter("relation.txt", true);
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    printWriter.println(publisher + " " + subscriber);
                    printWriter.close();
                    fileWriter.close();
                }catch (IOException ex) {

                }

                // notify other servers

                try {
                    FileReader fileReader = new FileReader("serverinfo.txt");
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] serverfields = line.split(" ");
                        if (!this._serverName.equals(serverfields[0])) {
                            Socket socket = null;
                            try {
                                socket = new Socket(serverfields[1], Integer.valueOf(serverfields[2]));
                                PrintStream ps = new PrintStream(socket.getOutputStream());
                                ps.println("server subscribe " + publisher + " " + subscriber);
                                ps.close();
                                socket.close();
                            } catch (Exception ex) {
                                System.err.println("Fail to connect to server IP: " + serverfields[1] + " port: " + serverfields[2]);
                                continue;
                            }

                        }
                    }

                    bufferedReader.close();
                    fileReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();    // test only
                }

            }

        } else if (command.startsWith("server")) {



        }
    }

    private class SubscriberInfo{
        public String _subscriber;
        public String _ip;
        public String _port;

        public SubscriberInfo(String subscriber, String ip, String port) {
            this._subscriber = subscriber;
            this._ip = ip;
            this._port = port;
        }
    }

}
