import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The {@code Server} class listens for incoming client connections
 * and delegates each connection to a separate {@code ClientHandler}.
 *
 * It supports concurrent clients using a fixed thread pool.
 */
public class Server {

    private static final int COREPOOL = 10; // thread minimi attivi nel pool
    private static final int SPORT = 8354;

    /**
     * Starts the server and handles incoming connections.
     *
     * @param args not used.
     */
    public static void main(final String[] args){
        ExecutorService pool = Executors.newFixedThreadPool(COREPOOL);
        AtomicInteger finishedClients = new AtomicInteger(0);
        int numClients;

        try {
            numClients = waitLauncher();

            System.out.println("Attendi server...");
            ServerSocket server = new ServerSocket(SPORT); // I initialize the service
            System.out.println("Server pronto, in ascolto sulla porta: " + SPORT);

            while (finishedClients.get() < numClients){
                Socket clientSocket = server.accept();
                System.out.println("Client connesso: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, () -> {
                    int count = finishedClients.incrementAndGet();
                    System.out.println("Client chiuso: Totale di client terminati: " + count + "/" + numClients);

                    if (count == numClients) {
                        System.out.println("Tutti i client sono terminati, server spento");
                        pool.shutdown();
                        try {
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                pool.execute(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            pool.shutdown();
            System.out.println("Il server è spento.");
        }


    }

    public static int waitLauncher() throws IOException {
        ServerSocket controlSocket = new ServerSocket(SPORT);
        System.out.println("Attendo LauncherClients sulla porta: " + SPORT);

        Socket controlConnection = controlSocket.accept();
        DataInputStream controlInput = new DataInputStream(controlConnection.getInputStream());
        int expectedClients = controlInput.readInt();
        controlSocket.close();

        return expectedClients;
    }

}
