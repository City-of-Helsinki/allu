package fi.hel.allu.model.testUtils;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.LocationService;

import org.geolatte.geom.Geometry;
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

  public void deleteAllData() throws SQLException {
    sqlRunner.runSql(DELETE_ALL_DATA);
  }

  private Application dummyBasicApplication(String name, String handler) {
    Customer person = insertPerson();
    Integer projectId = insertProject();
    User user = insertUser(handler);
    Application app = new Application();
    app.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, person, Collections.emptyList())));
    app.setProjectId(projectId);
    app.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    app.setStartTime(ZonedDateTime.parse("2015-01-03T10:15:30+02:00"));
    app.setEndTime(ZonedDateTime.parse("2015-02-03T10:15:30+02:00"));
    app.setRecurringEndTime(app.getEndTime());
    app.setMetadataVersion(1);
    app.setDecisionTime(ZonedDateTime.now());
    app.setName(name);
    app.setHandler(user.getId());
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
   * @param handler
   *          application handler's name
   * @return prepared application
   */
  public Application dummyOutdoorApplication(String name, String handler) {
    Application app = dummyBasicApplication(name, handler);
    app.setType(ApplicationType.EVENT);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OUTDOOREVENT, Collections.emptyList()));
    app.setApplicationId("TP1600001");
    app.setExtension(dummyOutdoorEvent());
    return app;
  }

  public Application dummyBridgeBannerApplication(String name, String handler) {
    Application app = dummyBasicApplication(name, handler);
    app.setType(ApplicationType.SHORT_TERM_RENTAL);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.BRIDGE_BANNER, Collections.emptyList()));
    app.setApplicationId("VL1600001");
    app.setExtension(dummyBridgeBannerEvent());
    return app;
  }

  public Application dummyAreaRentalApplication(String name, String handler) {
    Application app = dummyBasicApplication(name, handler);
    app.setType(ApplicationType.AREA_RENTAL);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OTHER, Collections.emptyList()));
    app.setApplicationId("AL1700001");
    app.setExtension(dummyAreaRentalEvent());
    return app;
  }

  public Application dummyNoteApplication(String name, String handler) {
    Application app = dummyBasicApplication(name, handler);
    app.setType(ApplicationType.NOTE);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.STATEMENT, Collections.emptyList()));
    app.setApplicationId("MP1700001");
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

  /**
   * Create and insert a dummy application into database.
   *
   * @param name
   *          application's name
   * @param handler
   *          application's handler's name
   * @return application's ID.
   */
  public Integer insertApplication(String name, String handler) {
    Application appl = dummyOutdoorApplication(name, handler);
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
    Customer insertedPerson = customerDao.insert(personCustomer);
    return insertedPerson;
  }

  /**
   * Insert a dummy project into database.
   *
   * @return Inserted project's ID
   * @throws Exception
   */
  public Integer insertProject() {
    Project project = new Project();
    project.setName("Viemärityö");
    project.setOwnerName("hankkeen omistaja");
    project.setContactName("hankkeen kontakti");
    project.setStartTime(ZonedDateTime.now());
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
    user.setUserName(userName);
    return userDao.insert(user);
  }

  private static final String[] DELETE_ALL_DATA = new String[] {
      "delete from allu.decision",
      "delete from allu.contact",
      "delete from allu.default_attachment_application_type",
      "delete from allu.default_attachment",
      "delete from allu.application_attachment",
      "delete from allu.attachment",
      "delete from allu.invoice_row",
      "delete from allu.application_tag",
      "delete from allu.location_flids",
      "delete from allu.location_geometry",
      "delete from allu.location",
      "delete from allu.field_change",
      "delete from allu.change_history",
      "delete from allu.distribution_entry",
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
      "delete from allu.default_recipient",
   };
}
