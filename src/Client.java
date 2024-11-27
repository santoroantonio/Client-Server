import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Client {
    private static final int SPORT = 8354;
    private static final String SHOST = "localhost";
    private int price;
    private final int MIN = 10, MAX = 75;

    public void send() {
        try {
            System.out.println("Client started");
            Socket soc = new Socket(SHOST, SPORT);

            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(soc.getInputStream()));
            ObjectOutputStream os = new ObjectOutputStream(soc.getOutputStream());

            Random r = new Random();

            while (true) {

                price = r.nextInt(MIN, MAX + 1);

                Object i = is.readObject();

                if (i instanceof Request) {
                    Request rq = (Request) i;

                    Response rs;

                    if (rq.getValue() < price) {
                        rs = new Response(1);
                    } else {
                        rs = new Response(0);
                    }

                    os.writeObject(rs);
                    os.flush();

                }
            }
            // soc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client().send();
    }
}
