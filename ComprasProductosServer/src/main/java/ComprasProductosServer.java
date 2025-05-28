import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.net.URLDecoder;

public class ComprasProductosServer {

    // Productos ejemplo
    static class Producto {
        String nombre;
        double precio;

        Producto(String n, double p) {
            nombre = n;
            precio = p;
        }
    }

    static Producto[] productos = {
        new Producto("Manzanas", 2.5),
        new Producto("Naranjas", 1.8),
        new Producto("Peras", 3.0)
    };

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new FormHandler());
        server.createContext("/procesar", new ProcesarHandler());

        server.setExecutor(null);
        System.out.println("Servidor iniciado en http://localhost:8080");
        server.start();
    }

    static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder();
            response.append("<html><head><title>Compras de productos</title></head><body>");
            response.append("<h2>Compras de productos</h2>");
            response.append("<form method='POST' action='/procesar'>");

            for (int i = 0; i < productos.length; i++) {
                Producto p = productos[i];
                response.append("<p>")
                    .append(p.nombre)
                    .append(" - Precio: $")
                    .append(String.format("%.2f", p.precio))
                    .append("<br>Cantidad: <input type='number' name='cantidad")
                    .append(i)
                    .append("' value='0' min='0'>")
                    .append("</p>");
            }

            response.append("<input type='submit' value='Calcular total'>");
            response.append("</form></body></html>");

            byte[] bytes = response.toString().getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class ProcesarHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Método no permitido
                return;
            }

            // Leer los datos del formulario
            InputStream is = exchange.getRequestBody();
            String formData = new String(is.readAllBytes(), "UTF-8");

            Map<String, String> params = parseFormData(formData);

            // Validar cantidades
            for (int i = 0; i < productos.length; i++) {
                String key = "cantidad" + i;
                String val = params.getOrDefault(key, "0");
                int cantidad = 0;
                try {
                    cantidad = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    cantidad = -1;
                }
                if (cantidad < 0) {
                    mostrarError(exchange, "Lo siento, ingrese una cantidad positiva.");
                    return;
                }
            }

            // Calcular total
            double total = 0.0;
            for (int i = 0; i < productos.length; i++) {
                int cantidad = Integer.parseInt(params.get("cantidad" + i));
                total += cantidad * productos[i].precio;
            }

            mostrarTotal(exchange, total);
        }

        private void mostrarError(HttpExchange exchange, String mensaje) throws IOException {
            String html = "<html><head><title>Error</title></head><body>" +
                    "<h3 style='color:red;'>" + mensaje + "</h3>" +
                    "<a href='/'>Volver al formulario</a>" +
                    "</body></html>";

            byte[] bytes = html.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private void mostrarTotal(HttpExchange exchange, double total) throws IOException {
            String html = "<html><head><title>Total a pagar</title></head><body>" +
                    "<h2>El total a pagar es: $" + String.format("%.2f", total) + "</h2>" +
                    "<a href='/'>Volver al formulario</a>" +
                    "</body></html>";

            byte[] bytes = html.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        // Método para parsear form data "application/x-www-form-urlencoded"
        private Map<String, String> parseFormData(String formData) throws IOException {
            return java.util.Arrays.stream(formData.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                    a -> urlDecode(a[0]),
                    a -> a.length > 1 ? urlDecode(a[1]) : ""
                ));
        }

        private String urlDecode(String s) {
            try {
                return URLDecoder.decode(s, "UTF-8");
            } catch (Exception e) {
                return "";
            }
        }
    }
}