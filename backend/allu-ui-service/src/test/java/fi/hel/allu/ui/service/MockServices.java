package fi.hel.allu.ui.service;


import fi.hel.allu.common.types.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.UserMapper;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.ring;

public abstract class MockServices {
  @Mock
  protected ApplicationProperties props;
  @Mock
  protected RestTemplate restTemplate;

  private static final int METADATA_VERSION = 1;

  public void initSaveMocks() {
    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Application.class)))
        .thenAnswer((Answer<Application>) invocation -> createMockApplicationModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Applicant.class)))
        .thenAnswer((Answer<Applicant>) invocation -> createMockPersonModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Project.class)))
        .thenAnswer((Answer<Project>) invocation -> createMockProjectModel());

    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class), Mockito.eq(Project.class), Mockito.anyInt()))
      .thenAnswer((Answer<ResponseEntity<Project>>) invocation -> new ResponseEntity<>(createMockProjectModel(), HttpStatus.CREATED));
    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Applicant.class)))
        .thenAnswer((Answer<Applicant>) invocation -> createMockApplicantModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Location.class)))
        .thenAnswer(
            (Answer<Location>) invocation -> createMockLocationModel(invocation.getArgumentAt(1, Location.class)));

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Event.class)))
        .thenAnswer((Answer<Event>) invocation -> createMockOutdoorEventModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(ApplicationES.class)))
        .thenAnswer((Answer<ApplicationES>) invocation -> createMockApplicationES());
  }

  public void initSearchMocks() {
    Mockito.when(restTemplate.getForObject(Mockito.any(String.class), Mockito.eq(Application.class), Mockito.any
        (String.class)))
        .thenAnswer((Answer<Application>) invocation -> createMockApplicationModel());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(User.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<User>>) invocation -> createMockUserResponse());

    Mockito
        .when(
            restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(AttachmentInfo[].class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<AttachmentInfo[]>>) invocation -> createMockAttachmentInfoListResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Applicant.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Applicant>>) invocation -> createMockPersonResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Applicant.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Applicant>>) invocation -> createMockApplicantResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Location.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Location>>) invocation -> createMockLocationResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Event.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Event>>) invocation -> createMockOutdoorEventResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Application[].class), Mockito.any
        (String.class)))
        .thenAnswer((Answer<ResponseEntity<Application[]>>) invocation ->
            createMockApplicationListResponse());

    Mockito.when(restTemplate.postForEntity(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Application[].class)))
        .thenAnswer((Answer<ResponseEntity<Application[]>>) invocation ->
            createMockApplicationListResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(FixedLocation[].class)))
        .then(invocation -> createMockFixedLocationList());

    Mockito.when(props.getModelServiceUrl(Mockito.any(String.class))).thenAnswer((Answer<String>) invocationOnMock -> "http://localhost:85/testing");

  }

  public LocationJson createLocationJson(Integer id) {
    LocationJson locationJson = new LocationJson();
    locationJson.setId(id);
    PostalAddressJson postalAddressJsonLocation = new PostalAddressJson();
    postalAddressJsonLocation.setStreetAddress("address, Json");
    postalAddressJsonLocation.setPostalCode("33333, Json");
    postalAddressJsonLocation.setCity("city, Json");
    locationJson.setPostalAddress(postalAddressJsonLocation);
    locationJson.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
    locationJson.setFixedLocationIds(Arrays.asList(12345, 5432));
    locationJson.setStartTime(ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
    locationJson.setEndTime(ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
    return locationJson;
  }

  public ApplicantJson createApplicantJson(Integer id, Integer typeId) {
    ApplicantJson applicantJson = new ApplicantJson();
    applicantJson.setId(id);
    applicantJson.setType(ApplicantType.COMPANY);
    applicantJson.setName("noname");
    applicantJson.setRegistryKey("444444");
    return applicantJson;
  }

  public ProjectJson createProjectJson(Integer id) {
    ProjectJson project = new ProjectJson();
    project.setId(id);
    project.setName("Hanke1, Json");
    return project;
  }

  public EventJson createOutdoorEventJson() {
    EventJson eventJson = new EventJson();
    eventJson.setDescription("Outdoor event description, Json");
    eventJson.setAttendees(1000);
    eventJson.setEventStartTime(ZonedDateTime.now());
    eventJson.setNature(EventNature.CLOSED);
    eventJson.setUrl("Outdoor event url, Json");
    eventJson.setEcoCompass(true);
    eventJson.setTimeExceptions("Mock exceptions");
    eventJson.setFoodSales(true);
    eventJson.setStructureArea(100);
    eventJson.setNoPriceReason("Mock pricing");
    eventJson.setEntryFee(1233);
    eventJson.setFoodProviders("Mock foodProviders");
    eventJson.setStructureDescription("Structure description");
    ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    ZonedDateTime zonedDateTime2 = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    eventJson.setEventStartTime(zonedDateTime);
    eventJson.setEventEndTime(zonedDateTime2);
    return eventJson;
  }

  protected StructureMetaJson createMockStructureMetadataJson() {
    AttributeMetaJson attributeMetaJson = new AttributeMetaJson();
    attributeMetaJson.setName("test_attribute");
    attributeMetaJson.setUiName("test ui name");
    attributeMetaJson.setDataType(AttributeDataType.STRING);
    StructureMetaJson structureMetaJson = new StructureMetaJson();
    structureMetaJson.setVersion(1);
    structureMetaJson.setTypeName(ApplicationKind.OUTDOOREVENT.toString());
    structureMetaJson.setAttributes(Collections.singletonList(attributeMetaJson));
    return structureMetaJson;
  }

  public List<ContactJson> createContactList() {
    List<ContactJson> result = new ArrayList<>();
    for (int i = 0; i < 5; ++i) {
      ContactJson contact = new ContactJson();
      contact.setName(String.format("Contact Name %d", i));
      contact.setId(i + 10);
      result.add(contact);
    }
    return result;
  }
  public ApplicationJson createMockApplicationJson(Integer id) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setId(id);
    applicationJson.setName("Tapahtuma 1, Json");
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setKind(ApplicationKind.OUTDOOREVENT);
    applicationJson.setMetadataVersion(METADATA_VERSION);
    applicationJson.setCreationTime(ZonedDateTime.now());
    applicationJson.setStartTime(ZonedDateTime.now());
    applicationJson.setEndTime(ZonedDateTime.now().plusDays(1));
    applicationJson.setDecisionTime(ZonedDateTime.now());
    applicationJson.setStatus(StatusType.PENDING);
    applicationJson.setHandler(UserMapper.mapToUserJson(createMockUser()));
    applicationJson.setApplicant(createApplicantJson(null, null));
    applicationJson.setLocations(Collections.singletonList(createLocationJson(null)));
    applicationJson.setProject(createProjectJson(null));
    applicationJson.setExtension(createOutdoorEventJson());
    return applicationJson;
  }

  public Application createMockApplicationModel() {
    Application application = new Application();
    application.setId(1);
    application.setName("Mock name, Model");
    application.setProjectId(100);
    application.setCreationTime(ZonedDateTime.now());
    application.setDecisionTime(ZonedDateTime.now());
    application.setHandler(createMockUser().getId());
    application.setType(ApplicationType.EVENT);
    application.setKind(ApplicationKind.OUTDOOREVENT);
    application.setApplicantId(103);
    application.setStatus(StatusType.PENDING);
    application.setExtension(createMockOutdoorEventModel());
    application.setMetadataVersion(1);
    return application;
  }


  public Applicant createMockPersonModel() {
    Applicant person = new Applicant();
    person.setCity("Person city, Model");
    person.setPostalCode("postalcode, Model");
    person.setStreetAddress("street address 2, Model");
    person.setRegistryKey("343232, Model");
    person.setPhone("43244323, Model");
    person.setName("Mock person, Model");
    person.setId(200);
    person.setEmail("Mock email, Model");
    return person;
  }

  public Project createMockProjectModel() {
    Project project = new Project();
    project.setId(100);
    project.setName("Hanke1, Model");
    return project;
  }

  public Applicant createMockApplicantModel() {
    Applicant applicant = new Applicant();
    applicant.setId(103);
    applicant.setType(ApplicantType.PERSON);
    return applicant;
  }

  public Location createMockLocationModel(Location input) {
    if (input != null && input.getId() != null) {
      return input;
    }
    Location location = new Location();
    location.setCity("City1, Model");
    location.setPostalCode("33333, Model");
    location.setStreetAddress("Street 1, Model");
    location.setFixedLocationIds(Arrays.asList(23456, 7656));
    location.setId(102);
    location.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
    return location;
  }

  public Event createMockOutdoorEventModel() {
    Event event = new Event();
    event.setUrl("url, Model");
    event.setNature(EventNature.PUBLIC_NONFREE);
    event.setAttendees(1050);
    event.setDescription("Outdoor event description, Model");
    event.setEcoCompass(true);
    event.setFoodSales(true);
    event.setEntryFee(1234);
    event.setBuildSeconds(60 * 60 * 24);
    event.setTeardownSeconds(60 * 60 * 24 * 2);
    return event;
  }

  public ApplicationES createMockApplicationES() {
    ApplicationES applicationES = new ApplicationES();
    ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    applicationES.setCreationTime(zonedDateTime);
    ZonedDateTime zonedDateTime2 = ZonedDateTime.of(2016, 10, 30, 23, 45, 59, 1234, zoneId);
    applicationES.setDecisionTime(zonedDateTime2);
    applicationES.setName("Mock name, ES");
    applicationES.setStatus(new StatusTypeES(StatusType.PENDING));
    User user = createMockUser();
    applicationES.setHandler(new UserES(user.getUserName(), user.getRealName()));
    applicationES.setId(1);
    applicationES.setType(new ApplicationTypeES(ApplicationType.EVENT));
    applicationES.setApplicationTypeData(createApplicationTypeDataES());
    return applicationES;
  }

  public List<ESFlatValue> createApplicationTypeDataES() {

    List<ESFlatValue> esFlatValues = new ArrayList<>();
    ZonedDateTime zonedDateTimeStart = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    ZonedDateTime zonedDateTimeEnd = ZonedDateTime.parse("2016-07-06T06:23:04.000Z");

    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "startTime", zonedDateTimeStart.toString()));
    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "endTime", zonedDateTimeEnd.toString()));
    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "attendees", 1000L));
    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "description", "Ulkoilmatapahtuman selitettä tässä."));
    return esFlatValues;
  }

  public ResponseEntity<Applicant> createMockPersonResponse() {
    return new ResponseEntity<>(createMockPersonModel(), HttpStatus.OK);
  }

  public ResponseEntity<Project> createMockProjectResponse() {
    return new ResponseEntity<>(createMockProjectModel(), HttpStatus.OK);
  }

  public ResponseEntity<Applicant> createMockApplicantResponse() {
    return new ResponseEntity<>(createMockApplicantModel(), HttpStatus.OK);
  }

  public ResponseEntity<Location> createMockLocationResponse() {
    return new ResponseEntity<>(createMockLocationModel(null), HttpStatus.OK);
  }

  public ResponseEntity<Event> createMockOutdoorEventResponse() {
    return new ResponseEntity<>(createMockOutdoorEventModel(), HttpStatus.OK);
  }

  public User createMockUser() {
    User user = new User();
    user.setId(1);
    user.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_VIEW));
    user.setIsActive(true);
    user.setAllowedApplicationTypes(Arrays.asList(ApplicationType.EVENT));
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName("username");
    return user;
  }

  public ResponseEntity<User> createMockUserResponse() {
    return new ResponseEntity<>(createMockUser(), HttpStatus.OK);
  }

  private ResponseEntity<Application[]> createMockApplicationListResponse() {
    Application applicationModelArray[] = new Application[2];
    applicationModelArray[0] = createMockApplicationModel();

    Application applicationModel = new Application();
    applicationModel.setId(1234);
    applicationModel.setType(ApplicationType.EVENT);
    applicationModel.setKind(ApplicationKind.OUTDOOREVENT);
    applicationModel.setHandler(createMockUser().getId());
    applicationModel.setStatus(StatusType.HANDLING);
    applicationModel.setProjectId(4321);
    applicationModel.setName("MockName2");
    applicationModel.setApplicantId(655);
    applicationModel.setExtension(createMockOutdoorEventModel());
    applicationModel.setMetadataVersion(1);
    applicationModelArray[1] = applicationModel;

    return new ResponseEntity<>(applicationModelArray, HttpStatus.OK);
  }

  private ResponseEntity<AttachmentInfo[]> createMockAttachmentInfoListResponse() {
    AttachmentInfo attachmentInfoArray[] = new AttachmentInfo[3];
    for (int i = 0; i < attachmentInfoArray.length; ++i) {
      AttachmentInfo info = new AttachmentInfo();
      info.setName(String.format("Attachment_%d.txt", i));
      info.setId(123 + i);
      info.setCreationTime(ZonedDateTime.now());
      info.setDescription(String.format("Attachment %d", i));
      info.setSize(123L * i);
      attachmentInfoArray[i] = info;
    }
    return new ResponseEntity<>(attachmentInfoArray, HttpStatus.OK);
  }

  protected ResponseEntity<StructureMeta> createMockStructureMetaResponse() {
    AttributeMeta attributeMeta = new AttributeMeta();
    attributeMeta.setName("test_attribute");
    StructureMeta structureMeta = new StructureMeta();
    structureMeta.setTypeName("Event");
    structureMeta.setVersion(1);
    structureMeta.setAttributes(Collections.singletonList(attributeMeta));
    return new ResponseEntity<>(structureMeta, HttpStatus.OK);
  }

  private ResponseEntity<FixedLocation[]> createMockFixedLocationList() {
    FixedLocation[] fixedLocations = new FixedLocation[2];
    for (int i = 0; i < fixedLocations.length; ++i) {
      FixedLocation fixedLocation = new FixedLocation();
      fixedLocation.setId(911 + i);
      fixedLocation.setArea("FixedLocation " + i);
      fixedLocation.setSection("Section " + i);
      fixedLocations[i] = fixedLocation;
    }
    return new ResponseEntity<>(fixedLocations, HttpStatus.OK);
  }

}
