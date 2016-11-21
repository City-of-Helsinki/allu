package fi.hel.allu.model.testUtils;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Arrays;

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
  private LocationDao locationDao;
  @Autowired
  private ApplicantDao applicantDao;
  @Autowired
  private ProjectDao projectDao;
  @Autowired
  private UserDao userDao;

  public void deleteAllData() throws SQLException {
    sqlRunner.runSql(DELETE_ALL_DATA);
  }

  private Application dummyBasicApplication(String name, String handler) {
    Integer personId = insertPerson();
    Integer projectId = insertProject();
    User user = insertUser(handler);
    Application app = new Application();
    app.setApplicantId(personId);
    app.setProjectId(projectId);
    app.setCreationTime(ZonedDateTime.now());
    app.setMetadataVersion(1);
    app.setDecisionTime(ZonedDateTime.now());
    app.setName(name);
    app.setHandler(user.getId());
    return app;
  }

  /**
   * Create a dummy application for insertion into database.
   *
   * Creates dummy project, person, and applicant in DB and prepares an
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
    app.setType(ApplicationType.OUTDOOREVENT);
    app.setApplicationId("TP1600001");
    app.setEvent(dummyOutdoorEvent());
    return app;
  }

  public Application dummyBridgeBannerApplication(String name, String handler) {
    Application app = dummyBasicApplication(name, handler);
    app.setType(ApplicationType.BRIDGE_BANNER);
    app.setApplicationId("VL1600001");
    app.setEvent(dummyBridgeBannerEvent());
    return app;
  }

  /**
   * Create a dummy outdoor event.
   *
   * @return the event
   */
  public Event dummyOutdoorEvent() {
    OutdoorEvent event = new OutdoorEvent();
    event.setDescription("desc");
    event.setAttendees(10);
    event.setNature(OutdoorEventNature.PUBLIC_FREE);
    return event;
  }

  public Event dummyBridgeBannerEvent() {
    ShortTermRental shortTermRental = new ShortTermRental();
    shortTermRental.setDescription("desc");
    shortTermRental.setType(ApplicationType.BRIDGE_BANNER);
    shortTermRental.setCommercial(true);
    return shortTermRental;
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
  public Integer insertLocation(String streetAddress, Geometry geometry) {
    Location location = new Location();
    location.setGeometry(geometry);
    location.setStreetAddress(streetAddress);
    return locationDao.insert(location).getId();
  }

  /**
   * Insert a dummy person into database.
   *
   * @return the person's ID
   */
  public Integer insertPerson() {
    Applicant personApplicant = new Applicant();
    personApplicant.setName("Pentti");
    personApplicant.setType(ApplicantType.PERSON);
    personApplicant.setRegistryKey("121212-xxxx");
    personApplicant.setEmail("pena@dev.null");
    Applicant insertedPerson = applicantDao.insert(personApplicant);
    return insertedPerson.getId();
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
    user.setAllowedApplicationTypes(Arrays.asList(ApplicationType.OUTDOOREVENT));
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName(userName);
    return userDao.insert(user);
  }

  private static final String[] DELETE_ALL_DATA = new String[] {
      "delete from allu.decision",
      "delete from allu.application_contact",
      "delete from allu.contact",
      "delete from allu.attachment",
      "delete from allu.application",
      "delete from allu.project",
      "delete from allu.applicant",
      "delete from allu.geometry",
      "delete from allu.location",
      "delete from allu.outdoor_pricing",
      "delete from allu.fixed_location",
      "delete from allu.user_application_type",
      "delete from allu.user_role",
      "delete from allu.user",
   };
}
