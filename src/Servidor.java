import java.net.*;
import java.io.*;

public class Servidor {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        try {
            int pto = 8000;
            ServerSocket s = new ServerSocket(pto);
            System.out.println("Servidor iniciado en el puerto "+pto+" .. esperando cliente..");
            for(;;){
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream(),"ISO-8859-1"));
                BufferedReader br = new BufferedReader(new InputStreamReader(cl.getInputStream(),"ISO-8859-1"));
                while(true){
                    String msj = br.readLine(); //  \n\r (10)(13)
                    if(msj.compareToIgnoreCase("salir")==0){
                        System.out.println("Cliente cierra conexion");
                        br.close();
                        pw.close();
                        cl.close();
                        break;
                    } else{
                        System.out.println("Mensaje recibido: "+msj+" devolviendo eco");
                        pw.println(msj);
                        pw.flush();
                    }//else
                }//while
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("xd");
        }
    }
}
