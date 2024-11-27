import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final int COREPOOL = 3; // thread minimi attivi nel pool
    private static final int MAXPOOL = 100; // thread massimi attivi nel pool
    private static final long IDLETIME = 5000; // tempo di inattività di un thread prima della rimozione

    private static final int SPORT = 4444;

    private ServerSocket socket;
    private ThreadPoolExecutor pool;

    public Server() throws IOException // solleva un'eccezione se l'operazione fallisce
    {
        this.socket = new ServerSocket(SPORT);
    }

    private void run() {
        /*
         * LinkedBlockingQueue<Runnable>
         * 
         * È una coda thread-safe che può contenere elementi di tipo Runnable
         * Il tipo generico Runnable indica che la coda è progettata per memorizzare
         * task (unità di lavoro)
         * che possono essere eseguiti da un thread.
         * 
         */
        this.pool = new ThreadPoolExecutor(COREPOOL, MAXPOOL, IDLETIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        while (true) {
            try {
                Socket s = this.socket.accept();

                this.pool.execute(new ServerThread(this, s));

            } catch (Exception e) {
                break;
            }
        }

        this.pool.shutdown();
    }

    public ThreadPoolExecutor getPool() {
        return this.pool;
    }

    public void close() {
        try {
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws IOException {
        new Server().run();
    }
}
