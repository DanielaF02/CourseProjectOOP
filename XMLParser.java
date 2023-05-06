import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;

public class XMLParser {
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;

    public DocumentBuilder getBuilder() {
        return builder;
    }

    XMLParser() {
        try {
            this.builder = factory.newDocumentBuilder();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveToXML(Document dom, String fileName) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(dom);
            StreamResult streamResult = new StreamResult(new FileWriter(fileName));
            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}