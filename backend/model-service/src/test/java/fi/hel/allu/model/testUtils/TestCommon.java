package fi.hel.allu.model.testUtils;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CodeSetDao;
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.LocationService;

import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.Geometry;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Helper class for routines shared between all tests
 *
 *
 */
@Component
public class TestCommon {
  private static final Logger logger = LoggerFactory.getLogger(TestCommon.class);

  private static int projectNbr = 0;

  @Autowired
  private SqlRunner sqlRunner;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private LocationService locationService;
  @Autowired
  private CustomerDao customerDao;
  @Autowired
  private ProjectDao projectDao;
  @Autowired
  private UserDao userDao;
  @Autowired
  private CodeSetDao codeSetDao;
  @Autowired
  private ContactDao contactDao;

  public void deleteAllData() throws SQLException {
    sqlRunner.runSql(DELETE_ALL_DATA);
  }

  private Application dummyBasicApplication(String name, String owner) {
    Customer person = insertPerson();
    Integer projectId = insertProject("dummyProject" + (projectNbr++));
    User user = insertUser(owner);
    Application app = new Application();
    app.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, person, Collections.emptyList())));
    app.setProjectId(projectId);
    app.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    app.setStartTime(ZonedDateTime.parse("2015-01-03T10:15:30+02:00"));
    app.setEndTime(ZonedDateTime.parse("2015-02-03T10:15:30+02:00"));
    app.setInvoicingDate(ZonedDateTime.parse("2015-01-18T10:15:30+02:00"));
    app.setRecurringEndTime(app.getEndTime());
    app.setMetadataVersion(1);
    app.setDecisionTime(ZonedDateTime.now());
    app.setName(name);
    app.setOwner(user.getId());
    app.setNotBillable(false);
    return app;
  }

  /**
   * Create a dummy application for insertion into database.
   *
   * Creates dummy project, person, and customer in DB and prepares an
   * Application that uses them.
   *
   * @param name
   *          application name
   * @param owner
   *          application owner's name
   * @return prepared application
   */
  public Application dummyOutdoorApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.EVENT);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OUTDOOREVENT, Collections.emptyList()));
    app.setExtension(dummyOutdoorEvent());
    return app;
  }

  public Application dummyBridgeBannerApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.SHORT_TERM_RENTAL);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.BRIDGE_BANNER, Collections.emptyList()));
    app.setExtension(dummyBridgeBannerEvent());
    return app;
  }

  public Application dummyAreaRentalApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.AREA_RENTAL);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OTHER, Collections.emptyList()));
    app.setExtension(dummyAreaRentalEvent());
    return app;
  }

  public Application dummyNoteApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.NOTE);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.STATEMENT, Collections.emptyList()));
    app.setExtension(dummyNote());
    return app;
  }
  /**
   * Create a dummy outdoor applicationExtension.
   *
   * @return the applicationExtension
   */
  public ApplicationExtension dummyOutdoorEvent() {
    Event event = new Event();
    event.setDescription("desc");
    event.setAttendees(10);
    event.setNature(EventNature.PUBLIC_FREE);
    return event;
  }

  public ApplicationExtension dummyBridgeBannerEvent() {
    ShortTermRental shortTermRental = new ShortTermRental();
    shortTermRental.setDescription("desc");
    shortTermRental.setCommercial(true);
    return shortTermRental;
  }

  public ApplicationExtension dummyAreaRentalEvent() {
    AreaRental areaRental = new AreaRental();
    areaRental.setAdditionalInfo("foobar additional info");
    return areaRental;
  }

  public ApplicationExtension dummyNote() {
    Note note = new Note();
    note.setDescription("Dummy note");
    return note;
  }

  public ApplicationTag dummyTag(ApplicationTagType tagType) {
    ApplicationTag tag = new ApplicationTag();
    tag.setType(tagType);
    tag.setCreationTime(ZonedDateTime.now());
    return tag;
  }

  /**
   * Create and insert a dummy application into database.
   *
   * @param name
   *          application's name
   * @param owner
   *          application's owner's name
   * @return application's ID.
   */
  public Integer insertApplication(String name, String owner) {
    Application appl = dummyOutdoorApplication(name, owner);
    return applicationDao.insert(appl).getId();
  }

  /**
   * Insert a location with given street address and geometry.
   *
   * @param streetAddress
   * @param geometry
   * @return inserted location's ID
   */
  public Integer insertLocation(
      String streetAddress,
      Geometry geometry,
      int applicationId,
      ZonedDateTime startTime,
      ZonedDateTime endTime) {
    Location location = new Location();
    location.setGeometry(geometry);
    location.setPostalAddress(new PostalAddress(streetAddress, null, null));
    location.setApplicationId(applicationId);
    location.setUnderpass(false);
    location.setStartTime(startTime);
    location.setEndTime(endTime);
    return locationService.insert(Collections.singletonList(location)).get(0).getId();
  }

  /**
   * Insert a dummy person into database.
   *
   * @return the person's ID
   */
  public Customer insertPerson() {
    Customer personCustomer = new Customer();
    personCustomer.setName("Pentti");
    personCustomer.setType(CustomerType.PERSON);
    personCustomer.setRegistryKey("121212-xxxx");
    personCustomer.setEmail("pena@dev.null");
    personCustomer.setCountryId(getCountryIdOfFinland());
    Customer insertedPerson = customerDao.insert(personCustomer);
    return insertedPerson;
  }

  public Contact insertContact(Integer customerId) {
    Contact contact = new Contact();
    contact.setName("Kontti Kontakti");
    contact.setCustomerId(customerId);
    Contact insertedContact = contactDao.insert(Arrays.asList(contact)).get(0);
    return insertedContact;
  }
  /**
   * Insert a dummy project into database.
   *
   * @return Inserted project's ID
   * @throws Exception
   */
  public Integer insertProject(String identifier) {
    Project project = new Project();
    project.setName("Viemärityö");
    project.setCustomerId(insertPerson().getId());
    project.setContactId(insertContact(project.getCustomerId()).getId());
    project.setStartTime(ZonedDateTime.now());
    project.setIdentifier(identifier);
    Project insertedProject = projectDao.insert(project);
    return insertedProject.getId();
  }

  public User insertUser(String userName) {
    User user = new User();
    user.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_VIEW));
    user.setIsActive(true);
    user.setAllowedApplicationTypes(Arrays.asList(ApplicationType.EVENT));
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName(StringUtils.lowerCase(userName));
    return userDao.insert(user);
  }

  public Integer getCountryIdOfFinland() {
    CodeSet codeSet = codeSetDao.findByTypeAndCode(CodeSetType.Country, "FI").get();
    return codeSet.getId();
  }

  private static final String[] DELETE_ALL_DATA = new String[] {
      "delete from allu.decision",
      "delete from allu.application_customer_contact",
      "delete from allu.contact",
      "delete from allu.default_attachment_application_type",
      "delete from allu.default_attachment",
      "delete from allu.application_attachment",
      "delete from allu.attachment",
      "delete from allu.attachment_data",
      "delete from allu.charge_basis",
      "delete from allu.application_tag",
      "delete from allu.location_flids",
      "delete from allu.location_geometry",
      "delete from allu.location",
      "delete from allu.field_change",
      "delete from allu.change_history",
      "delete from allu.distribution_entry",
      "delete from allu.supervision_task",
      "delete from allu.application",
      "delete from allu.project",
      "delete from allu.external_user_customer",
      "delete from allu.external_user_role",
      "delete from allu.external_user",
      "delete from allu.customer",
      "delete from allu.outdoor_pricing",
      "delete from allu.fixed_location",
      "delete from allu.user_application_type",
      "delete from allu.user_role",
      "delete from allu.user_city_district",
      "delete from allu.user",
      "delete from allu.default_text",
      "delete from allu.default_recipient"
   };
}
