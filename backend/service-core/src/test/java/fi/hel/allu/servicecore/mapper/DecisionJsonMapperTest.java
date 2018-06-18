package fi.hel.allu.servicecore.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fi.hel.allu.servicecore.domain.PostalAddressJson;

public class DecisionJsonMapperTest {

  private DecisionJsonMapper decisionMapper;

  @Before
  public void setup() {
    decisionMapper = new DecisionJsonMapper(null, null, null, null, null, null);
  }

  @Test
  public void testPostalAddress() {
    PostalAddressJson postalAddressJson = new PostalAddressJson("Aapakatu 12", "123456", "Aapala");
    Assert.assertEquals("Aapakatu 12, 123456 Aapala", decisionMapper.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson("", null, "Apaa");
    Assert.assertEquals("Apaa", decisionMapper.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson("Syrjäpolku 3", null, null);
    Assert.assertEquals("Syrjäpolku 3", decisionMapper.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson("Yypöntie 1", null, "Ypäjä");
    Assert.assertEquals("Yypöntie 1, Ypäjä", decisionMapper.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson(null, null, null);
    Assert.assertEquals("", decisionMapper.postalAddress(postalAddressJson));
  }

}
