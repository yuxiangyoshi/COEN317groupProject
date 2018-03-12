import java.io.IOException;
import java.net.ServerSocket;

public class ToolKit {

    public static int findAvailablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        }
    }
}
