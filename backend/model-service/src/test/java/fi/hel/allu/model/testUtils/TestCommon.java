package fi.hel.allu.model.testUtils;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Calendar;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.OutdoorEvent;
import fi.hel.allu.model.domain.Person;
import fi.hel.allu.model.domain.Project;

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
  private ApplicantDao applicantDao;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private CustomerDao customerDao;
  @Autowired
  private LocationDao locationDao;
  @Autowired
  private PersonDao personDao;
  @Autowired
  private ProjectDao projectDao;

  public void deleteAllData() throws SQLException {
    sqlRunner.runSql(DELETE_ALL_DATA);
  }

  /**
   * Create a dummy application for insertion into database.
   *
   * Creates dummy project, person, customer, and applicant in DB and prepares
   * an Application that uses them.
   *
   * @param name
   *          application name
   * @param handler
   *          application handler's name
   * @return prepared application
   */
  public Application dummyApplication(String name, String handler) {
    Integer personId = insertPerson();
    Integer customerId = insertPersonCustomer(personId);
    Integer projectId = insertProject(personId);
    Integer applicantId = insertPersonApplicant(personId);
    Application app = new Application();
    app.setCustomerId(customerId);
    app.setApplicantId(applicantId);
    app.setProjectId(projectId);
    app.setCreationTime(ZonedDateTime.now());
    app.setType(ApplicationType.OUTDOOREVENT);
    app.setMetadataVersion(1);
    app.setDecisionTime(ZonedDateTime.now());
    app.setName(name);
    app.setHandler(handler);
    app.setEvent(dummyOutdoorEvent());
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
    event.setNature("nature");
    return event;
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
    Application appl = dummyApplication(name, handler);
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
    Person person = new Person();
    person.setName("Pentti");
    person.setSsn("121212-xxxx");
    person.setEmail("pena@dev.null");
    Person insertedPerson = personDao.insert(person);
    return insertedPerson.getId();
  }

  /**
   * Insert a person-type applicant into database
   *
   * @param personId
   *          the applicant's person ID
   * @return applicant ID
   */
  public Integer insertPersonApplicant(Integer personId) {
    Applicant applicant = new Applicant();
    applicant.setType(CustomerType.PERSON);
    applicant.setPersonId(personId);
    Applicant insertedApplicant = applicantDao.insert(applicant);
    return insertedApplicant.getId();
  }

  /**
   * Insert a person-type customer into database
   *
   * @param personId
   *          the customer's person ID
   * @return customer ID
   */
  public Integer insertPersonCustomer(Integer personId) {
    Customer cust = new Customer();
    cust.setPersonId(personId);
    cust.setType(CustomerType.PERSON);
    Customer insertedCust = customerDao.insert(cust);
    return insertedCust.getId();
  }

  /**
   * Insert a dummy project into database.
   *
   * @param personId
   *          Owner's and contacts's ID.
   * @return Inserted project's ID
   * @throws Exception
   */
  public Integer insertProject(Integer personId) {
    Project project = new Project();
    project.setName("Viemärityö");
    project.setOwnerId(personId);
    project.setContactId(personId);
    project.setStartDate(Calendar.getInstance().getTime());
    Project insertedProject = projectDao.insert(project);
    return insertedProject.getId();
  }

  private static final String[] DELETE_ALL_DATA = new String[] {
      "delete from allu.decision",
      "delete from allu.application_contact",
      "delete from allu.project_contact",
      "delete from allu.contact",
      "delete from allu.attachment",
      "delete from allu.application",
      "delete from allu.project",
      "delete from allu.applicant",
      "delete from allu.customer",
      "delete from allu.person",
      "delete from allu.geometry",
      "delete from allu.location",
   };
}
