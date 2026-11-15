package fi.hel.allu.sap.marshaller;

import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
