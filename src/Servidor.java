import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Servidor {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        while (true) {
            try {
                int pto = 8000;

                ServerSocketChannel s = ServerSocketChannel.open();
                s.configureBlocking(false);
                s.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                s.socket().bind(new InetSocketAddress(pto));

                Selector sel = Selector.open();
                s.register(sel, SelectionKey.OP_ACCEPT);

                System.out.println("Servidor iniciado en el puerto " + pto + " ... Esperando clientes...");
                String catalogo = leerJSON();
                System.out.println("Catalogo cargado:");
                System.out.println(catalogo);

                while (true) {

                    sel.select();
                    Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey k = (SelectionKey) it.next();
                        it.remove();
                        if (k.isAcceptable()) {
                            SocketChannel cl = s.accept();
                            System.out
                                    .println("Cliente conectado desde->" + cl.socket().getInetAddress().getHostAddress()
                                            + ":" + cl.socket().getPort());
                            cl.configureBlocking(false);
                            cl.register(sel, SelectionKey.OP_READ);
                            System.out.println("Enviando catálogo...");
                            enviarCatalogo(cl, catalogo);
                            continue;
                            // break;
                        } // if
                          // while
                        if (k.isReadable()) {
                            SocketChannel ch = (SocketChannel) k.channel();
                            ByteBuffer b = ByteBuffer.allocate(2000);
                            b.clear();
                            int n = ch.read(b);
                            b.flip();
                            String msj = new String(b.array(), 0, n);
                            b.clear();
                            if (msj.equalsIgnoreCase("SALIR")) {
                                System.out.println("Mensaje recibido: " + msj + "\nCliente se va..");
                                ch.close();
                                continue;
                            } else {
                                System.out.println("Mensaje recibido: " + msj + "\nDevolviendo eco..");
                                String eco = "ECO_" + msj;
                                ByteBuffer b2 = ByteBuffer.wrap(eco.getBytes());
                                ch.write(b2);
                                continue;
                            } // else
                        } // if_readable
                    }
                }

                /*
                 * for(;;){
                 * sel.select();
                 * System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.
                 * getPort()+" Enviando catálogo...");
                 * PrintWriter pw = new PrintWriter(new
                 * OutputStreamWriter(cl.getOutputStream(),"ISO-8859-1"));
                 * BufferedReader br = new BufferedReader(new
                 * InputStreamReader(cl.getInputStream(),"ISO-8859-1"));
                 * pw.println(catalogo);
                 * pw.flush();
                 * System.out.println("Catalogo Enviado a"+cl.getInetAddress()+":"+cl.getPort())
                 * ;
                 * while(true){
                 * 
                 * 
                 * 
                 * 
                 * String msj = br.readLine(); // \n\r (10)(13)
                 * if(msj.compareToIgnoreCase("salir")==0){
                 * System.out.println("Cliente cierra conexion");
                 * br.close();
                 * pw.close();
                 * cl.close();
                 * break;
                 * } else{
                 * System.out.println("Mensaje recibido: "+msj+" devolviendo eco");
                 * msj=leerJSON();
                 * pw.println(msj);
                 * pw.flush();
                 * 
                 * }//else
                 * }//while
                 * }
                 */
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("xd");
                // System.out.println(e.getMessage());
            }
        }
    }

    public static void processMsg(String msg) {

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

    public static void enviarCatalogo(SocketChannel cl, String catalogo) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(catalogo.getBytes());
        cl.write(buffer);
    }

}
