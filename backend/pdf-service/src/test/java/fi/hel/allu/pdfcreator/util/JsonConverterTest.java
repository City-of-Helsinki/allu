package fi.hel.allu.pdfcreator.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class JsonConverterTest {

  @Test
  public void testJsonToXml()
      throws ParserConfigurationException, IOException, XPathExpressionException, JSONException, TransformerException {
    // Simple test: convert short JSON to XML and check that the result parses
    // and has the proper structure.

    final String json = "{\"key1\":\"value1\", \"key2\":\"value2\", \"anArray\":[\"one\",\"two\",\"three\"]}";
    final String baseDir = "/f/g/h/i/";
    final String xml = JsonConverter.jsonToXml(json, baseDir);

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
  }

  final static String testXml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
      "<catalog><cd><artist>Bob Dylan</artist><company>Columbia</company><country>USA</country>" +
      "<price>10.9</price><title>Empire Burlesque</title><year>1985</year></cd>" +
      "<cd><artist>Bonnie Tyler</artist><company>CBS Records</company><country>UK</country>" +
      "<price>9.9</price><title>Hide your heart</title><year>1988</year></cd></catalog>";

  final static String testXslt =
      "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
      "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">" +
      "<xsl:output method=\"text\"/><xsl:template match=\"/\">" +
          "<xsl:for-each select=\"catalog/cd\">A CD by <xsl:value-of select=\"artist\"/> "
          +
      "called <xsl:value-of select=\"title\"/> costs <xsl:value-of select=\"price\"/> EUR.\n" +
      "</xsl:for-each></xsl:template></xsl:stylesheet>";

  final static String expectedText =
      "A CD by Bob Dylan called Empire Burlesque costs 10.9 EUR.\n"+
      "A CD by Bonnie Tyler called Hide your heart costs 9.9 EUR.\n";

  @Test
  public void testStylesheet() throws TransformerException
  {
    InputStream stylesheetStream = new ByteArrayInputStream(testXslt.getBytes());
    String text = JsonConverter.applyStylesheet(testXml, stylesheetStream);
    assertEquals(expectedText, text);
  }
}
