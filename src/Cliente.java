import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import org.json.*;

import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

public class Cliente {

    public enum Filtro {
        Alfabético, Tipo
    }

    public static class Catalogo {

        public static JSONArray og;

        public static JSONArray actual;

        public static Socket cl;

        public Catalogo() {

        }

        public static JSONArray getOg() {
            return og;
        }

        public static JSONArray getActual() {
            return actual;
        }

        public static void setActual(JSONArray json) {
            actual = json;
        }

        public static void setOG(JSONArray json) {
            og = json;
        }
    }

    public static class Carrito {
        public static JSONArray carrito = new JSONArray();

        public Carrito() {

        }

        public static JSONArray getCarrito() {

            return carrito;

        }

        public static void setCarrito(JSONArray json) {

            carrito = json;

        }

        public static void addCarrito(JSONObject producto, int cantidad) {
            //TODO Arreglar que no se altere el catalogo al agregar al carrito
            for (int i = 0; i < carrito.length(); i++) {

                JSONObject prodCarrito = carrito.getJSONObject(i);
                if (prodCarrito.getInt("id") == producto.getInt("id")) {
                    int cantActual = prodCarrito.getInt("cantidad");
                    if (cantActual + cantidad > producto.getInt("cantidad")) {
                        System.err.println("No hay suficiente stock");
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {

                        }
                        return;
                    }
                    prodCarrito.put("cantidad", cantActual + cantidad);
                    return;
                }

            }
            JSONObject nuevo = producto;;
            nuevo.put("cantidad", cantidad);
            //producto.put("cantidad", cantidad);
            carrito.put(nuevo);

        }

        public static void emptyCarrito() {

            if (carrito.length() == 0) {
                // TODO Check This
                System.out.println("El carrito está vacio");
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } else {
                imprScrn(1, flag);
                mostrarCarrito(carrito);
                BufferedReader buff = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));// "Windows-1250"
                try {
                    String opt = buff.readLine();

                    menuCarrito(opt);

                } catch (Exception e) {
                    // TODO: handle exception

                    System.out.println(e.getMessage());
                }
            }

        }
    }

    private static Filtro flag = Filtro.Alfabético;

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        try {
            int pto = 8000;
            InetAddress host = null; // Integer.MAX_VALUE
            String dir = "127.0.0.1";
            host = InetAddress.getByName(dir);// UnknownHostException
            System.out.println(host);
            Socket cli = new Socket(host, pto);
            Catalogo.cl = cli;
            BufferedReader br1 = new BufferedReader(new InputStreamReader(Catalogo.cl.getInputStream(), "ISO-8859-1"));

            System.out.println("Conexion con el servidor " + dir + ":" + pto + " establecida\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "ISO-8859-1"));// "Windows-1250"
            // while(true){
            String ec = br1.readLine();
            br1.close();
            System.out.println(ec);
            // }
            JSONObject json = new JSONObject(ec);
            JSONArray articulos = json.getJSONArray("articulos");
            articulos = sortAlfa(articulos);
            Catalogo.setOG(articulos);
            Catalogo.setActual(articulos);

            while (true) {

                imprScrn(0, flag);
                mostrarListadoArticulos(Catalogo.getActual());
                String opt = br.readLine(); // Integer.MAX_VALUE
                menu(opt, articulos);

                // String eco2 = br1.readLine();
                // System.out.println("Eco recibido desde " + cl.getInetAddress() + ":" +
                // cl.getPort()
                // + " " + eco2);
            }

            // cl.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("xd");
            System.out.println(e.getMessage());
        }
    }

    public static void imprScrn(int key, Filtro filtro) {

        System.out.print("\033c");
        System.out.flush();
        System.out.println("███████╗ █████╗ ██████╗ ███╗   ███╗ █████╗  ██████╗██╗ █████╗ \n" +
                "██╔════╝██╔══██╗██╔══██╗████╗ ████║██╔══██╗██╔════╝██║██╔══██╗\n" +
                "█████╗  ███████║██████╔╝██╔████╔██║███████║██║     ██║███████║\n" +
                "██╔══╝  ██╔══██║██╔══██╗██║╚██╔╝██║██╔══██║██║     ██║██╔══██║\n" +
                "██║     ██║  ██║██║  ██║██║ ╚═╝ ██║██║  ██║╚██████╗██║██║  ██║\n" +
                "╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝╚═╝  ╚═╝\n");

        switch (key) {
            case 0:
                System.out.println("\n[B]Buscar artículos [F] Filtrar (Actual:" + filtro.toString()
                        + ") [V]Ver Carrito [S]Salir\n" +
                        "Para seleccionar un artículo, ingrese el NÚMERO del listado");

                break;

            case 1:
                System.out.println("\n[F] Filtrar (Actual:" + filtro.toString() + ") [C]Comprar Carrito [R]Regresar\n" +
                        "Para seleccionar un artículo, ingrese el NÚMERO del listado");

            default:
                break;
        }
    }

    public static void menu(String opt, JSONArray json) {

        boolean isInt;

        try {
            Integer.parseInt(opt);
            isInt = true;

        } catch (NumberFormatException e) {
            isInt = false;
        }
        if (!isInt) {
            switch (opt.toUpperCase()) {
                case "B":

                    System.out.println(
                            "Ingresa el normbre o marca de lo que buscas:\n(Deja en blanco para mostrar todos los articulos)");
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));// "Windows-1250"
                    try {
                        String search = br.readLine();
                        Catalogo.setActual(buscar(search, json));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                    break;

                case "F":

                    if (flag.toString().equals("Tipo")) {

                        Catalogo.setActual(sortAlfa(Catalogo.getActual()));
                        flag = Filtro.Alfabético;

                    } else {
                        Catalogo.setActual(sortTipo(Catalogo.getActual()));
                        flag = Filtro.Tipo;
                    }

                    break;

                case "V":

                    try {

                        Carrito.emptyCarrito();

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;

                case "S":

                    try {
                        PrintWriter pw = new PrintWriter(
                                new OutputStreamWriter(Catalogo.cl.getOutputStream(), "ISO-8859-1"));
                        pw.println("salir");
                        pw.flush();
                        pw.close();

                        Catalogo.cl.close();
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(e.getMessage());
                    }
                    System.exit(0);
                    break;

                default:
                    break;
            }
        } else {

            int art = Integer.parseInt(opt) - 1;
            JSONObject prod = Catalogo.actual.getJSONObject(art);
            int catcant = prod.getInt("cantidad");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));// "Windows-1250"
            int cant;
            while (true) {
                System.out.println("\r\033[2K¿Cuantos deseas agregar?");

                try {
                    String cantStr = br.readLine();

                    try {
                        cant = Integer.parseInt(cantStr);
                        if (cant > catcant) {
                            System.err.println("\r\033[2KNo hay suficiente stock");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException in) {
                                in.printStackTrace();
                            }
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.err.println("Cantidad inválida");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException in) {
                            in.printStackTrace();
                        }

                    }

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (prod.length() != 0 && cant > 0) {

                Carrito.addCarrito(prod, cant);

            }

        }
    }

    public static void menuCarrito(String opt) {

        Carrito.setCarrito(sortAlfa(Carrito.getCarrito()));
        flag = Filtro.Alfabético;

        boolean isInt;
        try {
            Integer.parseInt(opt);
            isInt = true;
        } catch (NumberFormatException e) {
            isInt = false;
        }
        if (!isInt) {
            switch (opt) {

                case "F":

                    if (flag.toString().equals("Tipo")) {

                        Carrito.setCarrito(sortAlfa(Carrito.getCarrito()));
                        flag = Filtro.Alfabético;

                    } else {
                        Carrito.setCarrito(sortTipo(Carrito.getCarrito()));
                        flag = Filtro.Tipo;
                    }

                    break;

                case "C":

                    try {

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;

                case "R":

                    break;

                default:
                    break;
            }
        } else {

            JSONObject prod = Carrito.getCarrito().getJSONObject(0);
            System.out.println("¿Cuantos deseas agregar?");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));// "Windows-1250"
            int cant;
            try {
                String cantStr = br.readLine();

                while (true) {
                    try {
                        cant = Integer.parseInt(cantStr);
                        break;
                    } catch (NumberFormatException e) {
                        System.err.println("Cantidad inválida");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException in) {
                            in.printStackTrace();
                        }

                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }

            if (prod.length() != 0 && cant > 0) {

                Carrito.addCarrito(prod, cant);

            }

        }
    }

    public static JSONArray buscar(String search, JSONArray json) {

        if (search.equals("")) {
            return json;
        }

        JSONArray busqueda = new JSONArray();
        Pattern patron = Pattern.compile("([A-Z]|[a-z])*(" + search + ")+([A-Z]|[a-z])*");

        for (int i = 0; i < json.length(); i++) {

            JSONObject articulo = json.getJSONObject(i);

            String nombre = articulo.getString("nombre");
            String marca = articulo.getString("marca");

            Matcher matchNom = patron.matcher(nombre);
            Matcher matchMarca = patron.matcher(marca);

            boolean flagNom = matchNom.find();
            boolean flagMarca = matchMarca.find();

            if (flagNom || flagMarca) {
                System.out.println("Coincidencia");
                busqueda.put(articulo);
            } else {
                System.out.println("Sin coincidencia");
            }

        }

        return busqueda;

    }

    public static void mostrarCarrito(JSONArray articulos) {
        try {

            System.out.println(
                    "╔══════════════════════════════════════════════════════════════════════════════════════╗");
            System.out
                    .println("║                                CARRITO DE ARTÍCULOS                                 ║");
            System.out
                    .println("╠═════╦══════════════════════════════╦══════════╦══════════╦════════════╦═════════════╣");
            System.out
                    .println("║  #  ║          NOMBRE              ║   MARCA  ║ CANTIDAD ║    TIPO    ║   PRECIO    ║");
            System.out
                    .println("╠═════╬══════════════════════════════╬══════════╬══════════╬════════════╬═════════════╣");

            for (int i = 0; i < articulos.length(); i++) {
                JSONObject articulo = articulos.getJSONObject(i);
                int cantidad = articulo.getInt("cantidad");

                if (cantidad > 0) {
                    int numeroListado = i + 1; // Número del listado (no el ID del JSON)
                    String nombre = articulo.getString("nombre");
                    String marca = articulo.getString("marca");

                    String tipo = articulo.getString("tipo");
                    double precio = articulo.getDouble("precio");

                    // Formatear para que quede alineado
                    String nombreFormateado = String.format("%-28s",
                            nombre.length() > 28 ? nombre.substring(0, 25) + "..." : nombre);
                    String marcaFormateada = String.format("%-10s", marca);
                    String cantidadFormateada = String.format("%-10d", cantidad);
                    String tipoFormateado = String.format("%-12s", tipo);
                    String precioFormateado = String.format("$%-10.2f", precio);

                    System.out.printf("║ [%d] ║ %s ║ %s ║ %s ║ %s ║ %s ║%n",
                            numeroListado, nombreFormateado, marcaFormateada,
                            cantidadFormateada, tipoFormateado, precioFormateado);

                }
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public static void mostrarListadoArticulos(JSONArray articulos) {
        try {

            System.out.println(
                    "╔══════════════════════════════════════════════════════════════════════════════════════╗");
            System.out
                    .println("║                                LISTADO DE ARTÍCULOS                                 ║");
            System.out
                    .println("╠═════╦══════════════════════════════╦══════════╦══════════╦════════════╦═════════════╣");
            System.out
                    .println("║  #  ║          NOMBRE              ║   MARCA  ║ CANTIDAD ║    TIPO    ║   PRECIO    ║");
            System.out
                    .println("╠═════╬══════════════════════════════╬══════════╬══════════╬════════════╬═════════════╣");

            for (int i = 0; i < articulos.length(); i++) {
                JSONObject articulo = articulos.getJSONObject(i);
                int cantidad = articulo.getInt("cantidad");

                if (cantidad > 0) {
                    int numeroListado = i + 1; // Número del listado (no el ID del JSON)
                    String nombre = articulo.getString("nombre");
                    String marca = articulo.getString("marca");

                    String tipo = articulo.getString("tipo");
                    double precio = articulo.getDouble("precio");

                    // Formatear para que quede alineado
                    String nombreFormateado = String.format("%-28s",
                            nombre.length() > 28 ? nombre.substring(0, 25) + "..." : nombre);
                    String marcaFormateada = String.format("%-10s", marca);
                    String cantidadFormateada = String.format("%-10d", cantidad);
                    String tipoFormateado = String.format("%-12s", tipo);
                    String precioFormateado = String.format("$%-10.2f", precio);

                    System.out.printf("║ [%d] ║ %s ║ %s ║ %s ║ %s ║ %s ║%n",
                            numeroListado, nombreFormateado, marcaFormateada,
                            cantidadFormateada, tipoFormateado, precioFormateado);

                }
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public static JSONArray sortAlfa(JSONArray jsonArray) {
        // Convertir a lista, ordenar y reemplazar el array original
        List<JSONObject> listaArticulos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            listaArticulos.add(jsonArray.getJSONObject(i));
        }

        Collections.sort(listaArticulos, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                String nombre1 = o1.getString("nombre");
                String nombre2 = o2.getString("nombre");
                return nombre1.compareTo(nombre2);
            }
        });

        JSONArray orden = new JSONArray(listaArticulos);
        return orden;

    }

    public static JSONArray sortTipo(JSONArray jsonArray) {
        // Convertir a lista, ordenar y reemplazar el array original
        List<JSONObject> listaArticulos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            listaArticulos.add(jsonArray.getJSONObject(i));
        }

        Collections.sort(listaArticulos, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                String tipo1 = o1.getString("tipo");
                String tipo2 = o2.getString("tipo");
                return tipo1.compareTo(tipo2);
            }
        });

        JSONArray orden = new JSONArray(listaArticulos);
        return orden;

    }

    public static void checkCarrito() {

        for (int i = 0; i < Carrito.carrito.length(); i++) {
            JSONObject articulo = Carrito.carrito.getJSONObject(i);
            int cantidad = articulo.getInt("cantidad");

            if (cantidad > 0) {
                System.out.println("El carrito tiene artículos");
                return;
            }
        }
    }
}
