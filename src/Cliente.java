import java.net.*;
import java.io.*;

public class Cliente {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        try {
            int pto = 8000;
            InetAddress host = null; //Integer.MAX_VALUE
            String dir="127.0.0.1";
            host = InetAddress.getByName(dir);//UnknownHostException
            System.out.println(host);
            
            Socket cl = new Socket(host,pto);
            System.out.println("Conexion con el servidor "+dir+":"+pto+" establecida\n");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream(),"ISO-8859-1"));
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"ISO-8859-1"));//"Windows-1250"
            BufferedReader br1 = new BufferedReader(new InputStreamReader(cl.getInputStream(),"ISO-8859-1"));

            while(true){
                System.out.println("Escribe un mensaje, <ENTER> para enviar, \"salir\" para terminar");
                String mensaje = br.readLine();  //Integer.MAX_VALUE
                System.out.println("LOL");
                pw.println(mensaje);
                pw.flush();
                if(mensaje.compareToIgnoreCase("salir")==0){
                    br.close();
                    pw.close();
                    cl.close();
                    System.exit(0);
                } else{
                    String eco = br1.readLine();
                    System.out.println("Eco recibido desde "+cl.getInetAddress()+":"+cl.getPort()+" "+eco);
                }//else*/
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
