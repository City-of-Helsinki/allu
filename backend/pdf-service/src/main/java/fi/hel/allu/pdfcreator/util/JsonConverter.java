package fi.hel.allu.pdfcreator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;

@Controller
public class JsonConverter {

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
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  public String jsonToXml(String dataJson, String baseDir)
      throws JsonParseException, JsonMappingException, IOException {
    // Read in the supplied JSON object:
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> userData = mapper.readValue(dataJson, new TypeReference<Map<String, Object>>() {
    });
    // Generate the top-level object:
    Map<String, Object> jsonOut = new HashMap<>();
    jsonOut.put(XML_BASEDIR_KEY, baseDir);
    jsonOut.put(XML_DATA_ELEMENT, userData);
    // Convert to XML:
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.enable(Feature.WRITE_XML_1_1).enable(SerializationFeature.INDENT_OUTPUT);
    String xml = xmlMapper.writer().withRootName(XML_ROOT_ELEMENT)
        .writeValueAsString(jsonOut);
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
  public String applyStylesheet(String xml, InputStream stylesheetStream)
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
