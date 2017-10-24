package fi.hel.allu.sap.marshaller;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class AlluUnmarshaller {
  private JAXBContext jc = null;
  private Unmarshaller unmarshaller = null;

  private AlluUnmarshaller createUnmarshaller() throws JAXBException {
    jc = JAXBContext.newInstance("fi.hel.allu.sap.model");
    unmarshaller = jc.createUnmarshaller();
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> T unmarshal(InputStream is) throws JAXBException {
    if (jc == null || unmarshaller == null) {
      createUnmarshaller();
    }
    return (T)unmarshaller.unmarshal(is);
  }
}
