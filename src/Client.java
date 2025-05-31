import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

/**
 * The {@code Client} class connects to the server,
 * receives item prices, and sends purchase requests.
 *
 * Each client makes up to 10 purchases and then
 * terminates the connection by sending a final message.
 */
public class Client {
    private static final int SPORT = 8354;
    private static final String SHOST = "localhost";
    private static final int MAX_PURCHASES = 10;

    /**
     * Entry point of the client.
     *
     * @param args an array containing a single argument: the client ID.
     * @throws Exception if a network or I/O error occurs.
     */
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket(SHOST, SPORT);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Client" + Arrays.toString(args) + ": " + "Connesso al Server");

            int purchases = 0;
            Random random = new Random();

            while (purchases < MAX_PURCHASES) {
                Object obj = in.readObject();
                if (!(obj instanceof Response res)) {
                    System.err.println("Client " + Arrays.toString(args) + ": " + "Messaggio ricevuto invalido.");
                    continue;
                }

                int serverPrice = res.getValue();
                int maxPrice = random.nextInt(66) + 10; // 10-75

                System.out.println("Client " + Arrays.toString(args) + ": " + "Prezzo dell'articolo: " + serverPrice + "€, Prezzo massimo per acquistare: " + maxPrice + "€");

                if (serverPrice <= maxPrice) {
                    out.writeObject(new Request(serverPrice));
                    purchases++;
                    System.out.println("Client " + Arrays.toString(args) + ": " + "Acquistato! Acquistiti effettuati: " + purchases);
                } else {
                    out.writeObject(new Request(-1));
                    System.out.println("Client " + Arrays.toString(args) + ": " + "Non Acquistato.");
                }

                Thread.sleep(2000); // optional delay
            }

            // Notify server of termination
            out.writeObject(new Request(-2));
            System.out.println("Client " + Arrays.toString(args) + ": " + "Acquisti terminati, Client spento.");
        }
    }
}
