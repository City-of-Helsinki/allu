package fi.hel.allu.supervision.api.service;

import java.beans.PropertyDescriptor;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.PropertyAccessorFactory;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import io.swagger.annotations.ApiModelProperty;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUpdateServiceTest {

  private static final Integer APPLICATION_ID = 1;

  @Mock
  private ApplicationServiceComposer applicationServiceComposer;
  private ApplicationUpdateService applicationUpdateService;
  private ApplicationJson application;



  @Before
  public void setup() {
    application = new ApplicationJson();
    applicationUpdateService = new ApplicationUpdateService(applicationServiceComposer);
    when(applicationServiceComposer.findApplicationById(APPLICATION_ID)).thenReturn(application);
    ApplicationStatusInfo statusInfo = new ApplicationStatusInfo();
    statusInfo.setStatus(StatusType.HANDLING);
    when(applicationServiceComposer.getApplicationStatus(APPLICATION_ID)).thenReturn(statusInfo);
  }

  @Test
  public void shouldUpdateApplicationFields() {
    Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
    kindsWithSpecifiers.put(ApplicationKind.DATA_TRANSFER, Collections.singletonList(ApplicationSpecifier.DATA_WELL));
    Map<String, Object> values = new HashMap<>();
    values.put("name", "newName");
    values.put("endTime", ZonedDateTime.now());
    values.put("kindsWithSpecifiers", kindsWithSpecifiers);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("name"), application.getName());
    assertEquals(TimeUtil.homeTime((ZonedDateTime)values.get("endTime")), TimeUtil.homeTime(application.getEndTime()));
    assertEquals(values.get("kindsWithSpecifiers"), application.getKindsWithSpecifiers());
  }

  @Test
  public void shouldUpdateAreaRentalFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("pksCard", true);
    values.put("trafficArrangementImpedimentType", TrafficArrangementImpedimentType.INSIGNIFICANT_IMPEDIMENT);
    values.put("terms", "new terms");
    AreaRentalJson extension = new AreaRentalJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("pksCard"), extension.getPksCard());
    assertEquals(values.get("trafficArrangementImpedimentType"), extension.getTrafficArrangementImpedimentType());
    assertEquals(values.get("terms"), extension.getTerms());

  }

  @Test
  public void shouldUpdateCableReportFields() {
    Map<String, Object> values = new HashMap<>();
    List<CableInfoEntryJson> infoEntries = new ArrayList<>();
    CableInfoEntryJson entry1 = new CableInfoEntryJson();
    entry1.setType(DefaultTextType.TELECOMMUNICATION);
    CableInfoEntryJson entry2 = new CableInfoEntryJson();
    entry2.setType(DefaultTextType.ELECTRICITY);
    infoEntries.add(entry1);
    infoEntries.add(entry2);
    values.put("infoEntries", infoEntries);
    values.put("workDescription", "Work description");
    CableReportJson extension = new CableReportJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(2, extension.getInfoEntries().size());
    assertTrue(extension.getInfoEntries().stream().anyMatch(e -> e.getType() == DefaultTextType.TELECOMMUNICATION));
    assertTrue(extension.getInfoEntries().stream().anyMatch(e -> e.getType() == DefaultTextType.ELECTRICITY));
    assertEquals(values.get("workDescription"), extension.getWorkDescription());
  }

  @Test
  public void shouldUpdateEventFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("entryFee", 24);
    values.put("surfaceHardness", SurfaceHardness.HARD);
    EventJson extension = new EventJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("entryFee"), extension.getEntryFee());
    assertEquals(values.get("surfaceHardness"), extension.getSurfaceHardness());
  }

  @Test
  public void shouldUpdateExcavationFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("additionalInfo", "Additional information");
    values.put("cableReports", Collections.singletonList("JS1900001"));
    ExcavationAnnouncementJson extension = new ExcavationAnnouncementJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("additionalInfo"), extension.getAdditionalInfo());
    assertTrue(extension.getCableReports().size() == 1);
    assertTrue(extension.getCableReports().contains("JS1900001"));
  }

  @Test
  public void shouldUpdateNoteFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("description", "Note description");
    NoteJson extension = new NoteJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("description"), extension.getDescription());
  }

  @Test
  public void shouldUpdatePlacementContractFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("propertyIdentificationNumber", "A123");
    PlacementContractJson extension = new PlacementContractJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("propertyIdentificationNumber"), extension.getPropertyIdentificationNumber());
  }

  @Test
  public void shouldUpdateShortTermRentalFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("commercial", Boolean.TRUE);
    ShortTermRentalJson extension = new ShortTermRentalJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("commercial"), extension.getCommercial());
  }

  @Test
  public void shouldUpdateTrafficArrangementFields() {
    Map<String, Object> values = new HashMap<>();
    values.put("trafficArrangements", "Traffic arrangements");
    TrafficArrangementJson extension = new TrafficArrangementJson();
    application.setExtension(extension);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
    assertEquals(values.get("trafficArrangements"), extension.getTrafficArrangements());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowWithInvalidField() {
    Map<String, Object> values = new HashMap<>();
    values.put("foo", "bar");
    applicationUpdateService.update(APPLICATION_ID, 1, values);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfFieldNotUpdatable() {
    Map<String, Object> values = new HashMap<>();
    values.put("type", ApplicationType.AREA_RENTAL);
    applicationUpdateService.update(APPLICATION_ID, 1, values);
  }

  @Test
  public void nonReadonlyApplicationFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new ApplicationJson());
  }

  @Test
  public void nonReadonlyAreaRentalFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new AreaRentalJson());
  }

  @Test
  public void nonReadonlyCableReportFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new CableReportJson());
  }

  @Test
  public void nonReadonlyEventFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new EventJson());
  }

  @Test
  public void nonReadonlyExcavationFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new ExcavationAnnouncementJson());
  }

  @Test
  public void nonReadonlyNoteFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new NoteJson());
  }

  @Test
  public void nonReadonlyPlacementContractFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new PlacementContractJson());
  }

  @Test
  public void nonReadonlyShortTermRentalFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new ShortTermRentalJson());
  }

  @Test
  public void nonReadonlyTrafficArrangementFieldsUpdatable() {
    nonReadOnlyFieldsUpdatable(new TrafficArrangementJson());
  }

  private <T> void nonReadOnlyFieldsUpdatable(T targetObject) {
    PropertyDescriptor[] descriptors =  PropertyAccessorFactory.forBeanPropertyAccess(targetObject).getPropertyDescriptors();
    Stream.of(descriptors).forEach(d -> nonReadOnlyFieldUpdatable(d));
  }

  private void nonReadOnlyFieldUpdatable(PropertyDescriptor d) {
    ApiModelProperty propertyAnnotation = d.getReadMethod().getAnnotation(ApiModelProperty.class);
    if (propertyAnnotation != null && !propertyAnnotation.readOnly() && !propertyAnnotation.hidden() && d.getWriteMethod() != null) {
      assertNotNull(d.getName(), d.getWriteMethod().getAnnotation(UpdatableProperty.class));
    }
  }

}
