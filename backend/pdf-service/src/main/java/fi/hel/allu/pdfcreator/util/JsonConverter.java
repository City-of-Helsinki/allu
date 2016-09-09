package fi.hel.allu.pdfcreator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class JsonConverter {

  // XML file header + stylesheet reference:
  private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
  // Magic key for the stylesheet to find it's base directory:
  private static final String XML_BASEDIR_KEY = "basedir";
  // The name of the root element in generated XML:
  private static final String XML_ROOT_ELEMENT = "root";
  // The name of the element under root that contains user data
  private static final String XML_DATA_ELEMENT = "data";

  /**
   * Convert the given JSON object to XML string that has the following
   * structure: <root> <basedir>stylesheetDir</basedir> <data>JSON object
   * contents</data> </root>
   *
   * @param dataJson
   * @param baseDir
   * @return
   * @throws JSONException
   */
  public static String jsonToXml(String dataJson, String baseDir) throws JSONException {
    // Read in the supplied JSON object:
    JSONObject jsonIn = new JSONObject(dataJson);
    // Generate the top-level object:
    JSONObject jsonOut = new JSONObject();
    jsonOut.put(XML_BASEDIR_KEY, baseDir);
    jsonOut.put(XML_DATA_ELEMENT, jsonIn);
    // Convert to XML:
    String xml = XML_HEADER + XML.toString(jsonOut, XML_ROOT_ELEMENT);
    return xml;
  }

  /**
   * Apply a XSLT stylesheet to XML data
   *
   * @param xml
   *          The XML data to transform
   * @param stylesheetPath
   *          Path to the XSLT stylesheet
   * @return Results of the transformation
   * @throws IOException
   * @throws TransformerException
   */
  public static String applyStylesheet(String xml, InputStream stylesheetStream)
      throws TransformerException {
    TransformerFactory factory = TransformerFactory.newInstance();
    Source xslt = new StreamSource(stylesheetStream);
    Transformer transformer = factory.newTransformer(xslt);

    Source xmlSource = new StreamSource(new StringReader(xml));
    StringWriter resultWriter = new StringWriter();
    transformer.transform(xmlSource, new StreamResult(resultWriter));
    return resultWriter.toString();
  }
}
