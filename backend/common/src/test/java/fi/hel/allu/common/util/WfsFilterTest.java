package fi.hel.allu.common.util;

import fi.hel.allu.common.wfs.WfsFilter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class WfsFilterTest {

  private static final String STRING_PROPERTY = "string";
  private static final String STRING_VALUE = "string value";
  private static final String INTEGER_PROPERTY = "integer";
  private static final Integer INTEGER_VALUE = 1;

  @Test
  public void shouldBuildBasicFilter() {
    WfsFilter wfsFilter = new WfsFilter(STRING_PROPERTY, STRING_VALUE);
    String filterString = wfsFilter.build();
    String expected = String.format("(%s='%s')", STRING_PROPERTY, STRING_VALUE);
    Assert.assertEquals(expected, filterString);
  }

  @Test
  public void shouldBuildBasicFilterIntegerValue() {
    WfsFilter wfsFilter = new WfsFilter(INTEGER_PROPERTY, INTEGER_VALUE);
    String filterString = wfsFilter.build();
    String expected = String.format("(%s='%d')", INTEGER_PROPERTY, INTEGER_VALUE);
    Assert.assertEquals(expected, filterString);
  }

  @Test
  public void shouldBuildFilterWithAnd() {
    WfsFilter stringFilter = new WfsFilter(STRING_PROPERTY, STRING_VALUE);
    WfsFilter andFilter = stringFilter.and(INTEGER_PROPERTY, Optional.of(INTEGER_VALUE));
    String filterString = andFilter.build();
    String expectedString = String.format("%s='%s'", STRING_PROPERTY, STRING_VALUE);
    String expectedInt = String.format("%s='%d'", INTEGER_PROPERTY, INTEGER_VALUE);
    String expected = String.format("(%s AND %s)", expectedString, expectedInt);
    Assert.assertEquals(expected, filterString);
  }

  @Test
  public void shouldBuildFilterWithOr() {
    WfsFilter stringFilter = new WfsFilter(STRING_PROPERTY, STRING_VALUE);
    WfsFilter andFilter = stringFilter.or(INTEGER_PROPERTY, Optional.of(INTEGER_VALUE));
    String filterString = andFilter.build();
    String expectedString = String.format("%s='%s'", STRING_PROPERTY, STRING_VALUE);
    String expectedInt = String.format("%s='%d'", INTEGER_PROPERTY, INTEGER_VALUE);
    String expected = String.format("(%s OR %s)", expectedString, expectedInt);
    Assert.assertEquals(expected, filterString);
  }

  @Test
  public void shouldBuildWithAndEmpty() {
    WfsFilter stringFilter = new WfsFilter(STRING_PROPERTY, STRING_VALUE);
    WfsFilter andFilter = stringFilter.and(INTEGER_PROPERTY, Optional.empty());
    String filterString = andFilter.build();
    String expected = String.format("(%s='%s')", STRING_PROPERTY, STRING_VALUE);
    Assert.assertEquals(expected, filterString);
  }
}
