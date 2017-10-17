package fi.hel.allu.sap.marshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.OutputStream;

/**
 * Convenience class for marshalling allu SAP classes
 */
public class AlluMarshaller {
  private JAXBContext jc = null;
  private Marshaller m = null;

  public AlluMarshaller createMarshaller() throws JAXBException {
    jc = JAXBContext.newInstance("fi.hel.allu.sap.model");
    m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    return this;
  }

  public <T> AlluMarshaller marshal(T object, OutputStream os) throws JAXBException {
    if (jc == null || m == null) {
      createMarshaller();
    }
    m.marshal(object, os);
    return this;
  }
}
