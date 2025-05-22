import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class ValidatorXerces_J_SAX {
    public static void main(String[] args) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(false);

            SAXParser parser = factory.newSAXParser();

            parser.parse("Ej01.xml", new DefaultHandler() {
                public void error(SAXParseException e) throws SAXException {
                    System.out.println("Error: " + e.getMessage());
                }

                public void fatalError(SAXParseException e) throws SAXException {
                    System.out.println("Fatal error: " + e.getMessage());
                }

                public void warning(SAXParseException e) throws SAXException {
                    System.out.println("Warning: " + e.getMessage());
                }
            });

            System.out.println("XML validado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}