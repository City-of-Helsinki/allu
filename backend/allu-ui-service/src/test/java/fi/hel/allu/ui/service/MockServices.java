package fi.hel.allu.ui.service;


import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.OutdoorEventES;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.geolatte.geom.builder.DSL.*;

public abstract class MockServices {
  @Mock
  protected ApplicationProperties props;
  @Mock
  protected RestTemplate restTemplate;

  public void initSaveMocks() {
    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Application.class)))
        .thenAnswer((Answer<Application>) invocation -> createMockApplicationModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Person.class)))
        .thenAnswer((Answer<Person>) invocation -> createMockPersonModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Organization.class)))
        .thenAnswer((Answer<Organization>) invocation -> createMockOrganizationModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Customer.class)))
        .thenAnswer((Answer<Customer>) invocation -> createMockCustomerModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Project.class)))
        .thenAnswer((Answer<Project>) invocation -> createMockProjectModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Applicant.class)))
        .thenAnswer((Answer<Applicant>) invocation -> createMockApplicantModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Location.class)))
        .thenAnswer((Answer<Location>) invocation -> createMockLocationModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(OutdoorEvent.class)))
        .thenAnswer((Answer<OutdoorEvent>) invocation -> createMockOutdoorEventModel());

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(ApplicationES.class)))
        .thenAnswer((Answer<ApplicationES>) invocation -> createMockApplicationES());
  }

  public void initSearchMocks() {
    Mockito.when(restTemplate.getForObject(Mockito.any(String.class), Mockito.eq(Application.class), Mockito.any
        (String.class)))
        .thenAnswer((Answer<Application>) invocation -> createMockApplicationModel());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Person.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Person>>) invocation -> createMockPersonResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Customer.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Customer>>) invocation -> createMockCustomerResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Project.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Project>>) invocation -> createMockProjectResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Applicant.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Applicant>>) invocation -> createMockApplicantResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Organization.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Organization>>) invocation -> createMockOrganizationResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Location.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<Location>>) invocation -> createMockLocationResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(OutdoorEvent.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<OutdoorEvent>>) invocation -> createMockOutdoorEventResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Application[].class), Mockito.any
        (String.class)))
        .thenAnswer((Answer<ResponseEntity<Application[]>>) invocation ->
            createMockApplicationListResponse());

    Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(StructureMeta.class), Mockito.any(ApplicationType.class)))
        .thenAnswer((Answer<ResponseEntity<StructureMeta>>) invocation -> createMockStructureMetaResponse());

    Mockito.when(restTemplate.postForEntity(Mockito.any(String.class), Mockito.anyObject(),
        Mockito.eq(Application[].class)))
        .thenAnswer((Answer<ResponseEntity<Application[]>>) invocation ->
            createMockApplicationListResponse());

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
    return locationJson;
  }

  public PersonJson createPersonJson(Integer id) {
    PersonJson personJson = new PersonJson();
    personJson.setId(id);
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setCity("Person city, Json");
    postalAddressJson.setPostalCode("postalcode, Json");
    postalAddressJson.setStreetAddress("street address 2, Json");
    personJson.setPostalAddress(postalAddressJson);
    personJson.setSsn("343232, Json");
    personJson.setPhone("43244323, Json");
    personJson.setName("Mock person, Json");
    personJson.setEmail("Mock email, Json");
    return personJson;
  }

  public CustomerJson createCustomerJson(Integer customerId, Integer typeId) {
    CustomerJson customer = new CustomerJson();
    customer.setId(customerId);
    customer.setPerson(createPersonJson(typeId));
    customer.setSapId("444-1, Json");
    customer.setType(CustomerType.Person);
    return customer;
  }

  public OrganizationJson createOrganizationJson(Integer id) {
    OrganizationJson organizationJson = new OrganizationJson();
    organizationJson.setId(id);
    organizationJson.setBusinessId("444444, Json");
    PostalAddressJson postalAddressJsonOrgnization = new PostalAddressJson();
    postalAddressJsonOrgnization.setCity("Kaupunki2, Json");
    postalAddressJsonOrgnization.setStreetAddress("Osoite 213, Json");
    postalAddressJsonOrgnization.setPostalCode("002113, Json");
    organizationJson.setPostalAddress(postalAddressJsonOrgnization);
    organizationJson.setPhone("323423421, Json");
    organizationJson.setName("Organisaatio 2, Json");
    ;
    organizationJson.setEmail("organization2 email, Json");
    return organizationJson;
  }

  public ApplicantJson createApplicantJson(Integer id, Integer typeId) {
    ApplicantJson applicantJson = new ApplicantJson();
    applicantJson.setId(id);
    applicantJson.setType(CustomerType.Company);
    applicantJson.setOrganization(createOrganizationJson(typeId));
    return applicantJson;
  }

  public ProjectJson createProjectJson(Integer id) {
    ProjectJson project = new ProjectJson();
    project.setId(id);
    project.setName("Hanke1, Json");
    return project;
  }

  public OutdoorEventJson createOutdoorEventJson() {
    OutdoorEventJson outdoorEventJson = new OutdoorEventJson();
    outdoorEventJson.setDescription("Outdoor event description, Json");
    outdoorEventJson.setAttendees(1000);
    outdoorEventJson.setStartTime(ZonedDateTime.now());
    outdoorEventJson.setNature("Outdoor event nature, Json");
    outdoorEventJson.setUrl("Outdoor event url, Json");
    outdoorEventJson.setEcoCompass(true);
    outdoorEventJson.setTimeExceptions("Mock exceptions");
    outdoorEventJson.setSalesActivity(true);
    outdoorEventJson.setStructureArea(100);
    outdoorEventJson.setPricing("Mock pricing");
    outdoorEventJson.setEntryFee(1233);
    outdoorEventJson.setFoodProviders("Mock foodProviders");
    outdoorEventJson.setStructureDescription("Structure description");
    ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    ZonedDateTime zonedDateTime2 = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    outdoorEventJson.setStartTime(zonedDateTime);
    outdoorEventJson.setEndTime(zonedDateTime2);
    return outdoorEventJson;
  }

  public StructureMetaJson createMockStructureMetadataJson() {
    AttributeMetaJson attributeMetaJson = new AttributeMetaJson();
    attributeMetaJson.setName("test_attribute");
    attributeMetaJson.setUiName("test ui name");
    attributeMetaJson.setDataType(AttributeDataType.STRING);
    StructureMetaJson structureMetaJson = new StructureMetaJson();
    structureMetaJson.setVersion(1);
    structureMetaJson.setApplicationType(ApplicationType.OutdoorEvent.toString());
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
    applicationJson.setType(ApplicationType.OutdoorEvent);
    applicationJson.setMetadata(createMockStructureMetadataJson());
    applicationJson.setCreationTime(ZonedDateTime.now());
    applicationJson.setStatus("Vireillä, Json");
    applicationJson.setHandler("Kalle käsittelijä, Json");
    applicationJson.setCustomer(createCustomerJson(null, null));
    applicationJson.setApplicant(createApplicantJson(null, null));
    applicationJson.setLocation(createLocationJson(null));
    applicationJson.setProject(createProjectJson(null));
    applicationJson.setEvent(createOutdoorEventJson());
    return applicationJson;
  }

  public Application createMockApplicationModel() {
    Application application = new Application();
    application.setId(1);
    application.setName("Mock name, Model");
    application.setProjectId(100);
    application.setCreationTime(ZonedDateTime.now());
    application.setCustomerId(101);
    application.setHandler("Mock handler, Model");
    application.setType(ApplicationType.OutdoorEvent);
    application.setLocationId(102);
    application.setApplicantId(103);
    application.setStatus("Vireillä");
    application.setEvent(createMockOutdoorEventModel());
    return application;
  }


  public Person createMockPersonModel() {
    Person person = new Person();
    person.setCity("Person city, Model");
    person.setPostalCode("postalcode, Model");
    person.setStreetAddress("street address 2, Model");
    person.setSsn("343232, Model");
    person.setPhone("43244323, Model");
    person.setName("Mock person, Model");
    person.setId(200);
    person.setEmail("Mock email, Model");
    return person;
  }

  public Organization createMockOrganizationModel() {
    Organization organization = new Organization();
    organization.setBusinessId("3333333, Model");
    organization.setCity("Kaupunki, Model");
    organization.setStreetAddress("Osoite 21, Model");
    organization.setPostalCode("00211, Model");
    organization.setPhone("32342342, Model");
    organization.setName("Organisaatio 1, Model");
    organization.setId(201);
    organization.setEmail("organization email, Model");
    return organization;
  }

  public Customer createMockCustomerModel() {
    Customer customer = new Customer();
    customer.setId(101);
    customer.setPersonId(200);
    customer.setSapId("444-1, Model");
    customer.setType(CustomerType.Person);
    return customer;
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
    applicant.setType(CustomerType.Company);
    applicant.setOrganizationId(201);
    return applicant;
  }

  public Location createMockLocationModel() {
    Location location = new Location();
    location.setCity("City1, Model");
    location.setPostalCode("33333, Model");
    location.setStreetAddress("Street 1, Model");
    location.setId(102);
    location.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
    return location;
  }

  public OutdoorEvent createMockOutdoorEventModel() {
    OutdoorEvent outdoorEvent = new OutdoorEvent();
    outdoorEvent.setUrl("url, Model");
    outdoorEvent.setNature("outdoor event nature, Model");
    outdoorEvent.setStartTime(ZonedDateTime.now());
    outdoorEvent.setAttendees(1050);
    outdoorEvent.setDescription("Outdoor event description, Model");
    outdoorEvent.setEndTime(ZonedDateTime.now());
    outdoorEvent.setEcoCompass(true);
    outdoorEvent.setSalesActivity(true);
    outdoorEvent.setEntryFee(1234);
    ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    ZonedDateTime zonedDateTime2 = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    outdoorEvent.setStartTime(zonedDateTime);
    outdoorEvent.setEndTime(zonedDateTime2);
    return outdoorEvent;
  }

  public ApplicationES createMockApplicationES() {
    ApplicationES applicationES = new ApplicationES();
    ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, zoneId);
    applicationES.setCreationTime(zonedDateTime);
    applicationES.setName("Mock name, ES");
    applicationES.setStatus("Vireillä, ES");
    applicationES.setHandler("Handler, ES");
    applicationES.setId(1);
    applicationES.setType(ApplicationType.OutdoorEvent);
    applicationES.setApplicationTypeData(createApplicationTypeDataES());
    return applicationES;
  }

  public OutdoorEventES createApplicationTypeDataES() {
    OutdoorEventES outdoorEventJson = new OutdoorEventES();
    outdoorEventJson.setDescription("Outdoor event description, ES");
    outdoorEventJson.setAttendees(1000);
    outdoorEventJson.setStartTime(ZonedDateTime.now());
    outdoorEventJson.setNature("Outdoor event nature, ES");
    outdoorEventJson.setUrl("Outdoor event url, ES");
    outdoorEventJson.setEcoCompass("Ekokompassi, ES");
    outdoorEventJson.setTimeExceptions("Mock exceptions, ES");
    outdoorEventJson.setSalesActivity("Myyntiä, ES");
    outdoorEventJson.setStructureArea(100);
    outdoorEventJson.setPricing("Mock pricing, ES");
    outdoorEventJson.setEntryFee(1233);
    outdoorEventJson.setFoodProviders("Mock foodProviders, ES");
    outdoorEventJson.setStructureDescription("Structure description, ES");
    ZoneId zoneId = ZoneId.of("Europe/Helsinki");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2016, 11, 30, 23, 45, 59, 1234, zoneId);
    ZonedDateTime zonedDateTime2 = ZonedDateTime.of(2016, 11, 30, 23, 45, 59, 1234, zoneId);
    outdoorEventJson.setStartTime(zonedDateTime);
    outdoorEventJson.setEndTime(zonedDateTime2);
    return outdoorEventJson;
  }

  public ResponseEntity<Person> createMockPersonResponse() {
    return new ResponseEntity<>(createMockPersonModel(), HttpStatus.OK);
  }

  public ResponseEntity<Project> createMockProjectResponse() {
    return new ResponseEntity<>(createMockProjectModel(), HttpStatus.OK);
  }

  public ResponseEntity<Customer> createMockCustomerResponse() {
    return new ResponseEntity<>(createMockCustomerModel(), HttpStatus.OK);
  }

  public ResponseEntity<Applicant> createMockApplicantResponse() {
    return new ResponseEntity<>(createMockApplicantModel(), HttpStatus.OK);
  }

  public ResponseEntity<Organization> createMockOrganizationResponse() {
    return new ResponseEntity<>(createMockOrganizationModel(), HttpStatus.OK);
  }

  public ResponseEntity<Location> createMockLocationResponse() {
    return new ResponseEntity<>(createMockLocationModel(), HttpStatus.OK);
  }

  public ResponseEntity<OutdoorEvent> createMockOutdoorEventResponse() {
    return new ResponseEntity<>(createMockOutdoorEventModel(), HttpStatus.OK);
  }

  private ResponseEntity<Application[]> createMockApplicationListResponse() {
    Application applicationModelArray[] = new Application[2];
    applicationModelArray[0] = createMockApplicationModel();

    Application applicationModel = new Application();
    applicationModel.setId(1234);
    applicationModel.setType(ApplicationType.OutdoorEvent);
    applicationModel.setHandler("MockHandler2");
    applicationModel.setStatus("MockStatus2");
    applicationModel.setProjectId(4321);
    applicationModel.setName("MockName2");
    applicationModel.setCustomerId(3456);
    applicationModel.setApplicantId(655);
    applicationModel.setLocationId(345);
    applicationModel.setEvent(createMockOutdoorEventModel());
    applicationModelArray[1] = applicationModel;

    return new ResponseEntity<>(applicationModelArray, HttpStatus.OK);
  }

  private ResponseEntity<StructureMeta> createMockStructureMetaResponse() {
    AttributeMeta attributeMeta = new AttributeMeta();
    attributeMeta.setName("test_attribute");
    StructureMeta structureMeta = new StructureMeta();
    structureMeta.setApplicationType("OutdoorEvent");
    structureMeta.setVersion(1);
    structureMeta.setAttributes(Collections.singletonList(attributeMeta));
    return new ResponseEntity<StructureMeta>(structureMeta, HttpStatus.OK);
  }
}
