import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Client {
    private static final int SPORT = 8354;
    private static final String SHOST = "localhost";
    private int price, offer;
    private final int MIN = 10, MAX = 75;

    public void send() {
        try {
            System.out.println("Client started");
            Socket soc = new Socket(SHOST, SPORT);

            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            DataOutputStream out = new DataOutputStream(soc.getOutputStream());

            Random r = new Random();

            while (true) {
                Request rq = new Request(r.nextInt(MAX));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client().send();
    }
}
