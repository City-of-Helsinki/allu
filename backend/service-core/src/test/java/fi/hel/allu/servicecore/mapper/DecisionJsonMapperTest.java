package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DecisionJsonMapperTest {

  private DecisionJsonMapper decisionMapper;

  @Before
  public void setup() {
    decisionMapper = new DecisionJsonMapper(null, null, null, null, null);
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

  @Test
  public void nonBreakingSpaces() {
    final DecisionJson decision = new DecisionJson();
    decision.setEventDescription("Event\u00A0Description");
    final List<String> additionalConditions = Arrays.asList("Condition\u00A01", "Condition\u00A02", null);
    decision.setAdditionalConditions(additionalConditions);
    decisionMapper.convertNonBreakingSpacesToSpaces(decision);
    Assert.assertEquals("Event Description", decision.getEventDescription());
    final String[] expected = {"Condition 1", "Condition 2", null};
    Assert.assertArrayEquals(expected, decision.getAdditionalConditions().toArray());
  }
}
