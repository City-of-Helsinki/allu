package fi.hel.allu.ui.geocode;

import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WfsFeatureCollectionTest {
  @Test
  public void testUnmarshal() throws Exception {
    JAXBContext jc = JAXBContext.newInstance(WfsFeatureCollection.class);

    Unmarshaller unmarshaller = jc.createUnmarshaller();
    File xml = new File("src/test/resources/fi/hel/allu/ui/geocode/geocode_viipurink_wfs.xml");
    WfsFeatureCollection wfsFeatureCollection = (WfsFeatureCollection) unmarshaller.unmarshal(xml);

    List<Double> xValues = Arrays.asList(new Double[] {25496886D, 25497057D, 25496816D, 25496862D});
    List<Double> yValues = Arrays.asList(new Double[] {6675339D, 6675368D, 6675394D, 6675332D});
    List<Integer> streetNumber = Arrays.asList(new Integer[] {10,1,11,12});

    for (int i = 0; i < xValues.size(); ++i) {
      assertEquals((double) xValues.get(i), wfsFeatureCollection.featureMember.get(i).geocodedAddress.x, 0);
      assertEquals((double) yValues.get(i), wfsFeatureCollection.featureMember.get(i).geocodedAddress.y, 0);
      assertEquals("Viipurinkatu", wfsFeatureCollection.featureMember.get(i).geocodedAddress.streetName);
      assertEquals((int) streetNumber.get(i), wfsFeatureCollection.featureMember.get(i).geocodedAddress.streetNumber);
    }
  }

}
