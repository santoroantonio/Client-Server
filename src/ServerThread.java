import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Random;

public class ServerThread implements Runnable {

    private static final int MAX = 100, MIN = 10;

    // Il tempo di attesa simulato tra la ricezione e l'invio della risposta
    private static final long SLEEPTIME = 200;

    private Server server;
    private Socket socket;

    public ServerThread(final Server s, final Socket c) {
        this.server = s;
        this.socket = c;
    }

    /////////////////////////// RUN \\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void run() {
        ObjectInputStream is = null;
        ObjectOutputStream os = null;

        try {
            is = new ObjectInputStream(new BufferedInputStream(
                    this.socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }

        String id = String.valueOf(this.hashCode());

        Random r = new Random();

        int count = 0;

        while (true) {
            try {

                if (os == null) {
                    os = new ObjectOutputStream(new BufferedOutputStream(
                            this.socket.getOutputStream()));
                }

                Response rs = new Response(r.nextInt(MIN, MAX + 1));

                System.out.format("thread %s invia: %s $ al suo client %n",
                        id, rs.getValue());
                os.writeObject(rs);

                /*
                 * os.flush()
                 *
                 * Forza lo svuotamento del buffer dello stream di output
                 * evita ritardi dovuti al buffering
                 * 
                 */
                os.flush();

                Thread.sleep(SLEEPTIME);

                Object i = is.readObject();

                if (i instanceof Request) {

                    // converte "i" in un tipo Request
                    Request rq = (Request) i;

                    if (rq.getValue() == 0) {
                        System.out.format("thread %s riceve: il client non  ha acquistato il prodotto %n",
                                id);
                        count++;
                    } else {
                        System.out.format("thread %s riceve: il client ha acquistato il prodotto %n",
                                id);
                    }

                    if (count == 10) {
                        if (this.server.getPool().getActiveCount() == 1) {

                            /*
                             * this.server è una referenza al server principale.
                             * getPool() restituisce il thread pool del server, che gestisce tutti i thread
                             * attivi (uno per ogni client connesso).
                             * getActiveCount() restituisce il numero di thread attivi nel pool.
                             * 
                             * Se il thread corrente è l'ultimo attivo, il server viene chiuso.
                             */

                            this.server.close();
                        }

                        this.socket.close();

                        return;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

}
