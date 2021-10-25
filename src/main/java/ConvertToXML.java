import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvertToXML {
    private static final Logger logger = LoggerFactory.getLogger(ConvertToXML.class);

    public void fillXML(File file, List<Integer> values) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String firstString = "<entries>\n";
            String lastString = "</entries>";
            writer.write(firstString);
            for (Integer value : values) {
                writer.write("\t<entry>\n");
                writer.write("\t\t<field>");
                writer.write(value.toString());
                writer.write("</field>\n");
                writer.write("\t</entry>\n");
            }
            writer.write(lastString);
            logger.info("XML is created");
        } catch (IOException e) {
            logger.error("Can`t create XML file", e);
            throw new IllegalStateException("Can`t create XML file", e);
        }
    }

    public void transformXML(File source, File target, File xslt) {
        Source xmlSource = new StreamSource(source);
        Result xmlTarget = new StreamResult(target);
        Source xsltPattern = new StreamSource(xslt);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltPattern);
            transformer.transform(xmlSource, xmlTarget);
            logger.info("XML is converted");
        } catch (TransformerException e) {
            logger.error("XML converting failed", e);
            throw new IllegalStateException("XML converting failed", e);
        }
    }

    public List<Integer> extractNodeAttributes(File source, String expression, String attributeName) {
        List<Integer> result = new ArrayList<>();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(source);
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node entry = nodeList.item(i);
                int tempValue = Integer.parseInt(entry.getAttributes().getNamedItem(attributeName).getNodeValue());
                result.add(tempValue);
            }
            logger.info("Numbers converted");
        } catch (ParserConfigurationException | IOException | XPathExpressionException | SAXException e) {
            logger.error("Numbers converting failed", e);
            throw new IllegalStateException("Numbers converting failed", e);
        }
        return result;
    }
}