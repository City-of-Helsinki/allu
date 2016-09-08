package fi.hel.allu.pdfcreator.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class JsonConverter {

  // XML file header + stylesheet reference:
  private static final String XML_HEADER_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "\n<?xml-stylesheet type='text/xsl' href=\"%s\"?>";
  // Magic key for the stylesheet to find it's base directory:
  private static final String XML_BASEDIR_KEY = "basedir";
  // The name of the root element in generated XML:
  private static final String XML_ROOT_ELEMENT = "root";
  // The name of the element under root that contains user data
  private static final String XML_DATA_ELEMENT = "data";

  /**
   * Convert the given JSON object to XML string that has the following
   * structure: <root> <basedir>stylesheetDir</basedir> <data>JSON object
   * contents</data> </root> The XML string also has a stylesheet header that
   * points to the given style sheet.
   *
   * @param dataJson
   * @param stylesheetPath
   * @param baseDir
   * @return
   * @throws JSONException
   */
  public static String jsonToXml(String dataJson, String stylesheetPath, String baseDir) throws JSONException {
    // Read in the supplied JSON object:
    JSONObject jsonIn = new JSONObject(dataJson);
    // Generate the top-level object:
    JSONObject jsonOut = new JSONObject();
    jsonOut.put(XML_BASEDIR_KEY, baseDir);
    jsonOut.put(XML_DATA_ELEMENT, jsonIn);
    // Convert to XML:
    String xml = String.format(XML_HEADER_TEMPLATE, stylesheetPath) + XML.toString(jsonOut, XML_ROOT_ELEMENT);
    return xml;
  }

}
