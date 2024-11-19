import java.io.BufferedReader;
import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {

        try{
            System.out.println("Client started");
            Socket soc = new Socket("localhost",8354);
    
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

            System.out.print("Messaggio da inviare al server: ");
            
            //legge la stringa
            String stringa = in.readLine();

            out.println(stringa);
            
            soc.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
}
