import java.net.*;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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
                String catalogo=leerJSON();
                System.out.println("Enviando catalogo a "+cl.getInetAddress()+":"+cl.getPort());
                pw.println(catalogo);
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
                        msj=leerJSON();
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
    public static String leerJSON() {

        try {
            File file = new File("catalogo.json");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder jsonContent = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                jsonContent.append(linea);
                
            }
            
            reader.close();
            
            String json = jsonContent.toString();
            return json;


        } catch (Exception e) {
            // TODO: handle exception
            return e.getMessage();
        }



    }
}
