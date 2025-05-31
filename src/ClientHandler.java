import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * The {@code ClientHandler} class handles the communication
 * between the server and a single client in a separate thread.
 *
 * It receives purchase requests, sends price updates, and
 * tracks when a client has finished their transactions.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket = null;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Random rand = new Random();
    private boolean isActive = true;
    private final Runnable onClientFinished;


    public ClientHandler(Socket socket, Runnable onClientFinished) {
        this.clientSocket = socket;
        this.onClientFinished = onClientFinished;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Client [" + clientSocket + "] avviato");

            while (isActive && !clientSocket.isClosed()) {
                try {
                    int salePrice = rand.nextInt(91) + 10;

                    // Check if the socket is still open before sending
                    if (!clientSocket.isClosed() && clientSocket.isConnected()) {
                        out.writeObject(new Response(salePrice));
                        out.flush();
                    } else {
                        System.out.println("Socket chiuso, interrompo la comunicazione.");
                        break;
                    }

                    Object obj = in.readObject();
                    if (!(obj instanceof Request)) {
                        System.err.println("messaggio ricevuto non valido");
                        continue;
                    }

                    Request req = (Request) obj;
                    int value = req.getValue();

                    if (value == -2) {
                        System.out.println("Client [" + clientSocket + "] ha finito di acquistare.");
                        isActive = false;
                        onClientFinished.run();
                    } else if (value == -1) {
                        System.out.println("Client [" + clientSocket + "] non ha acquistato.");
                    } else {
                        System.out.println("Client [" + clientSocket + "] ha acquistato il prodotto a: " + value + "€");
                    }

                    Thread.sleep(1000);

                } catch (SocketException e) {
                    System.err.println("Client [" + clientSocket + "] ha chiuso la connessione inaspettatamente.");
                    isActive = false;
                    break;
                }
            }

            // Socket closure control
            if (!clientSocket.isClosed()) {
                out.flush();
                clientSocket.close();
            }
            System.out.println("Connessione chiusa con il Client [" + clientSocket + "]");

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            System.err.println("Errore di comunicazione con il client: " + e.getMessage());
        }
    }
}
