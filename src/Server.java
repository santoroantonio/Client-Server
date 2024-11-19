import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server {
    public static void main(String[] args) {
        try {
            System.out.println("Waiting for client...");
            ServerSocket ss = new ServerSocket(8354);

            Socket soc = ss.accept();
            System.out.println("Connessione Stabilita!");

            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String str = in.readLine();

            System.out.println("Risposta server: " + str);

            soc.close();
            ss.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
