package fi.hel.allu.model.deployment.citydistrict;

import fi.hel.allu.common.wfs.WfsUtil;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class CityDistrictParserTest {

  private static final String HAAGA_POLYGON =
      "25492612.2673542,6680268.15943088 25492612.4765281,6680263.31573412 25492612.262584,6680251.71612244";

  @Test
  public void testParse() throws Exception {
    byte[] encoded = Files.readAllBytes(Paths.get("src/test/java/fi/hel/allu/model/deployment/kaupunginosat_pretty.xml"));
    String wfsXml = new String(encoded, "UTF-8");
    CityDistrictXml cityDistrictXml = WfsUtil.unmarshalWfs(wfsXml, CityDistrictXml.class);
    Assert.assertEquals(60, cityDistrictXml.featureMember.size());
    Optional<CityDistrictXml.FeatureMember> haagaOpt =
        cityDistrictXml.featureMember.stream().filter(fm -> fm.cityDistrict.districtName.trim().equals("HAAGA")).findFirst();
    Assert.assertTrue(haagaOpt.isPresent());
    CityDistrictXml.FeatureMember haaga = haagaOpt.get();
    Assert.assertTrue(haaga.cityDistrict.geometry.polygon.outerBoundary.linearRing.coordinates.startsWith(HAAGA_POLYGON));
    Assert.assertEquals(29, haaga.cityDistrict.districtId);
  }
}
