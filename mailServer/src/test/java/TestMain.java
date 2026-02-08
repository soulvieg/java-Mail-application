import com.example.mailserver.network.Server;

public class TestMain {
    public static void main(String[] args) {
        Server server = new Server(8189);
        server.start();
    }
}
