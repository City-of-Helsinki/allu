package fi.hel.allu.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.util.ObjectComparer.Difference;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObjectComparerTest {


  private ObjectComparer comparer;

  @Before
  public void setup() {
    comparer = new ObjectComparer();
  }

  @Test
  public void testSimple() {
    SimpleClass simple1 = new SimpleClass();
    simple1.setBooleanField(true);
    simple1.setStringField("Jouko Turkko");
    simple1.setListField(Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.CABLE_REPORT));
    simple1.setOtherListField(Arrays.asList(RoleType.ROLE_DECISION, RoleType.ROLE_INVOICING));
    simple1.setOtherStringField("jouko@turkko.xx");
    simple1.setThirdStringField("Manager of managing");
    SimpleClass simple2 = new SimpleClass();
    simple2.setBooleanField(false);
    simple2.setStringField("Jouko Turkko");
    simple2.setListField(Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.CABLE_REPORT));
    simple2.setOtherListField(Arrays.asList(RoleType.ROLE_DECISION, RoleType.ROLE_SUPERVISE));
    simple2.setThirdStringField("Manager of managing change");
    List<Difference> diff = comparer.compare(simple1, simple2);
    assertEquals(4, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/booleanField")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/thirdStringField")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/otherStringField")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/otherListField/1")).count());
  }

  @Test
  public void testSimpleWithFieldIgnore() {
    SimpleClass simple1 = new SimpleClass();
    simple1.setBooleanField(true);
    simple1.setStringField("Jouko Turkko");
    simple1.setListField(Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.CABLE_REPORT));
    simple1.setOtherListField(Arrays.asList(RoleType.ROLE_DECISION, RoleType.ROLE_INVOICING));
    simple1.setOtherStringField("jouko@turkko.xx");
    simple1.setThirdStringField("Manager of managing");
    SimpleClass simple2 = new SimpleClass();
    simple2.setBooleanField(false);
    simple2.setStringField("Jouko Turkko");
    simple2.setListField(Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.CABLE_REPORT));
    simple2.setOtherListField(Arrays.asList(RoleType.ROLE_DECISION, RoleType.ROLE_SUPERVISE));
    simple2.setThirdStringField("Manager of managing change");
    comparer.addMixin(SimpleClass.class, SimpleClassCompare.class);
    List<Difference> diff = comparer.compare(simple1, simple2);
    assertEquals(1, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/booleanField")).count());
  }

  @Test
  public void testComplex() {
    ComplexClass complex1 = new ComplexClass();
    complex1.setDateTimeField(ZonedDateTime.parse("2017-02-03T10:15:30+02:00[Europe/Helsinki]"));
    SimpleWithId simpleWithId = new SimpleWithId();
    simpleWithId.setStringField("hakija@jossain.org");
    simpleWithId.setSimpleField(new SimpleClass());
    simpleWithId.getSimpleField().setOtherStringField("Siti");
    simpleWithId.setId(99);
    complex1.setSimplesWithId(Collections.singletonList(simpleWithId));

    ComplexClass complex2 = new ComplexClass();
    complex2.setDateTimeField(ZonedDateTime.parse("2017-02-03T10:25:30+02:00[Europe/Helsinki]"));
    SimpleWithId simpleWithId2 = new SimpleWithId();
    simpleWithId2.setStringField("jokumuu@jossainmuualla.org");
    simpleWithId2.setSimpleField(new SimpleClass());
    simpleWithId2.getSimpleField().setOtherStringField("Villits");
    simpleWithId2.setId(99);
    complex2.setSimplesWithId(Collections.singletonList(simpleWithId2));

    List<Difference> diff = comparer.compare(complex1, complex2);
    assertEquals(3, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/simplesWithId/99/stringField")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/dateTimeField")).count());
    Difference cityDiff = diff.stream()
        .filter(d -> d.keyName.equals("/simplesWithId/99/simpleField/otherStringField")).findFirst().orElse(null);
    assertNotNull(cityDiff);
    assertEquals("Siti", cityDiff.oldValue);
    assertEquals("Villits", cityDiff.newValue);
  }

  @Test
  public void testEmptyListEqualsNull() {
    SimpleClass simple1 = new SimpleClass();
    simple1.setListField(null);
    simple1.setOtherListField(Collections.emptyList());
    SimpleClass simple2 = new SimpleClass();
    simple2.setListField(Collections.emptyList());
    simple2.setOtherListField(null);
    List<Difference> diff = comparer.compare(simple1, simple2);
    assertEquals(0, diff.size());
  }

  @Test
  public void testEmptyStringEqualsNull() {
    SimpleClass simple1 = new SimpleClass();
    simple1.setStringField("");
    simple1.setOtherStringField(null);
    SimpleClass simple2 = new SimpleClass();
    simple2.setStringField(null);
    simple2.setOtherStringField("");
    List<Difference> diff = comparer.compare(simple1, simple2);
    assertEquals(0, diff.size());
  }

  @Test
  public void testDetectArrayAddWithId() {
    ComplexClass complex1 = new ComplexClass();
    complex1.setSimplesWithId(createSimplesWithId(2));
    ComplexClass complex2 = new ComplexClass();
    complex2.setSimplesWithId(createSimplesWithId(4));
    List<Difference> diff = comparer.compare(complex1, complex2);
    // 2 new elements with id and description -> 4 changes
    assertEquals(4, diff.size());
    assertEquals(2, diff.stream().map(d -> d.keyName).filter(k -> k.matches("/simplesWithId/\\d*/id")).count());
    assertEquals(2,
        diff.stream().map(d -> d.keyName).filter(k -> k.matches("/simplesWithId/\\d*/stringField")).count());
  }

  @Test
  public void testDetectArrayAddWithoutId() {
    ComplexClass complex1 = new ComplexClass();
    complex1.setSimples(createSimples(2));
    ComplexClass complex2 = new ComplexClass();
    complex2.setSimples(createSimples(4));
    List<Difference> diff = comparer.compare(complex1, complex2);
    // 2 new elements with type -> 2 changes
    assertEquals(2, diff.stream().map(d -> d.keyName).filter(k -> k.matches("/simples/\\d*/stringField")).count());
  }

  private List<SimpleWithId> createSimplesWithId(int numSimples) {
    List<SimpleWithId> result = new ArrayList<>();
    for (int i = 1; i <= numSimples; ++i) {
      SimpleWithId swi = new SimpleWithId();
      swi.setId(420 + i);
      swi.setStringField("Attachment " + i);
      result.add(swi);
    }
    return result;
  }

  private List<SimpleClass> createSimples(int numSimples) {
    List<SimpleClass> result = new ArrayList<>();
    for (int i = 1; i <= numSimples; ++i) {
      SimpleClass sc = new SimpleClass();
      sc.setStringField("Attachment " + i);
      result.add(sc);
    }
    return result;
  }
  /* Data types for the test cases: */

  class SimpleClass {
    private boolean booleanField;
    private String stringField;
    private String otherStringField;
    private String thirdStringField;
    private List<ApplicationType> listField;
    private List<RoleType> otherListField;

    public boolean isBooleanField() {
      return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
      this.booleanField = booleanField;
    }

    public String getStringField() {
      return stringField;
    }

    public void setStringField(String stringField) {
      this.stringField = stringField;
    }

    public String getOtherStringField() {
      return otherStringField;
    }

    public void setOtherStringField(String otherStringField) {
      this.otherStringField = otherStringField;
    }

    public String getThirdStringField() {
      return thirdStringField;
    }

    public void setThirdStringField(String thirdStringField) {
      this.thirdStringField = thirdStringField;
    }

    public List<ApplicationType> getListField() {
      return listField;
    }

    public void setListField(List<ApplicationType> listField) {
      this.listField = listField;
    }

    public List<RoleType> getOtherListField() {
      return otherListField;
    }

    public void setOtherListField(List<RoleType> otherListField) {
      this.otherListField = otherListField;
    }
  }

  class SimpleWithId {
    private SimpleClass simpleField;
    private int id;
    private String stringField;

    public SimpleClass getSimpleField() {
      return simpleField;
    }

    public void setSimpleField(SimpleClass simpleField) {
      this.simpleField = simpleField;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getStringField() {
      return stringField;
    }

    public void setStringField(String stringField) {
      this.stringField = stringField;
    }

  }

  abstract class SimpleClassCompare {
    // Boolean field is not ignored
    @JsonIgnore public abstract String getStringField();
    @JsonIgnore public abstract String getOtherStringField();
    @JsonIgnore public abstract String getThirdStringField();
    @JsonIgnore public abstract List<ApplicationType> getListField();
    @JsonIgnore public abstract List<RoleType> getOtherListField();
  }

  class ComplexClass {
    private List<SimpleWithId> simplesWithId;
    private List<SimpleClass> simples;
    private ZonedDateTime dateTimeField;

    public List<SimpleWithId> getSimplesWithId() {
      return simplesWithId;
    }

    public void setSimplesWithId(List<SimpleWithId> simplesWithId) {
      this.simplesWithId = simplesWithId;
    }

    public List<SimpleClass> getSimples() {
      return simples;
    }

    public void setSimples(List<SimpleClass> simples) {
      this.simples = simples;
    }

    public ZonedDateTime getDateTimeField() {
      return dateTimeField;
    }

    public void setDateTimeField(ZonedDateTime dateTimeField) {
      this.dateTimeField = dateTimeField;
    }
  }
}
