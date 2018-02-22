package fi.hel.allu.sap.mashaller;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import fi.hel.allu.sap.marshaller.AlluUnmarshaller;
import fi.hel.allu.sap.model.DEBMAS06;

public class AlluUnmarshallerTest {

  private static final String TEST_FILE = "sap_customer.xml";

  @Test
  public void shouldUnmarshallInputStream() throws JAXBException {
    AlluUnmarshaller unmarshaller = new AlluUnmarshaller();
    DEBMAS06 debmas06 = unmarshaller.unmarshal(getInputStream());
    assertNotNull(debmas06);
    assertNotNull(debmas06.getiDoc());
    assertNotNull(debmas06.getiDoc().getE1kna1m());
    assertNotNull(debmas06.getiDoc().getE1kna1m().getE1knvvm());
    assertNotNull(debmas06.getiDoc().getE1kna1m().getE1knvvm().get(0).getEikto());
  }

  private InputStream getInputStream() {
    return this.getClass().getResourceAsStream(TEST_FILE);
  }
}
