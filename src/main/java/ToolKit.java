import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToolKit {

    public static int findAvailablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        }
    }

    public static String retrieveCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));    // test only
        return dateFormat.format(date);
    }
}
