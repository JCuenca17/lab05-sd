import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ValidarBOE {
    public static void main(String[] args) {
        try {
            // Ruta del archivo XML
            File archivo = new File("boe.xml");

            // Crear el parser DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Recomendado
            factory.setValidating(false); // Solo bien formado (por ahora)

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler());

            Document doc = builder.parse(archivo);

            System.out.println("El archivo boe.xml está BIEN FORMADO.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
