package fi.hel.allu.pdfcreator.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class JsonConverterTest {

  @Test
  public void test() throws ParserConfigurationException, IOException, XPathExpressionException {
    // Simple test: convert short JSON to XML and check that the result parses
    // and has the proper structure.

    final String json = "{\"key1\":\"value1\", \"key2\":\"value2\", \"anArray\":[\"one\",\"two\",\"three\"]}";
    final String stylesheetPath = "/a/b/c/d/e.xsl";
    final String baseDir = "/f/g/h/i/";
    final String xml = JsonConverter.jsonToXml(json, stylesheetPath, baseDir);

    // Now some tests to validate the XML contents..
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = null;
    try {
      doc = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
    } catch (SAXException e) {
      fail("XML Parsing failed: " + e.getMessage());
    }

    // Verify that top-level node has correct name:
    assertEquals("root", doc.getDocumentElement().getTagName());

    // Verify that the exactly one basedir element exists and it matches
    // baseDir:
    XPath xPath = XPathFactory.newInstance().newXPath();
    NodeList nodeList = (NodeList) xPath.compile("/root/basedir").evaluate(doc, XPathConstants.NODESET);
    assertEquals(1, nodeList.getLength());
    assertEquals(baseDir, nodeList.item(0).getTextContent());

    // Verify that exactly one data element exist:
    nodeList = (NodeList) xPath.compile("/root/data").evaluate(doc, XPathConstants.NODESET);
    assertEquals(1, nodeList.getLength());

    // Make sure the JSON keys are there:
    Element elem = (Element) nodeList.item(0);
    assertEquals("value1", elem.getElementsByTagName("key1").item(0).getTextContent());
    assertEquals("value2", elem.getElementsByTagName("key2").item(0).getTextContent());

    // Check that the array is in place, too
    nodeList = (NodeList) xPath.compile("/root/data/anArray").evaluate(doc, XPathConstants.NODESET);
    assertEquals(3, nodeList.getLength());
    assertEquals("one", nodeList.item(0).getTextContent());
    assertEquals("two", nodeList.item(1).getTextContent());
    assertEquals("three", nodeList.item(2).getTextContent());

    // Make sure there's exactly one stylesheet processing instruction and it
    // contains styleSheetPath:
    int numStylesheetTags = 0;
    nodeList = doc.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && node.getNodeName() == "xml-stylesheet") {
        ProcessingInstruction pi = (ProcessingInstruction) node;
        String regex = String.format("(.* )?href=['\"]%s['\"]", stylesheetPath);
        assertTrue("style sheet doesn't match", Pattern.matches(regex, pi.getData()));
        numStylesheetTags++;
      }
    }
    assertEquals(1, numStylesheetTags);
  }

}
