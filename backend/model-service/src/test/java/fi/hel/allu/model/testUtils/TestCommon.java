package fi.hel.allu.model.testUtils;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fi.hel.allu.common.types.ChangeType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;

import static org.geolatte.geom.builder.DSL.*;

/**
 * Helper class for routines shared between all tests
 *
 *
 */
@Component
public class TestCommon {

  private static int projectNbr = 0;

  @Autowired
  private SqlRunner sqlRunner;
  @Autowired
  private ApplicationDao applicationDao;
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
    addLocation(app);
    return app;
  }

  private Application dummyBasicApplication(String name, String owner, Customer customer) {
    Integer projectId = insertProject("dummyProject" + (projectNbr++));
    User user = insertUser(owner);
    Application app = new Application();
    app.setCustomersWithContacts(
            Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, Collections.emptyList())));
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
    addLocation(app);
    return app;
  }

  public CustomerWithContacts dummyCustomerWithContacts( CustomerRoleType type){
    Customer person = insertPerson();
     CustomerWithContacts customerWithContacts = new CustomerWithContacts(type, person, Collections.emptyList());
    return customerWithContacts;
  }

  public void addLocation(Application app) {
    Location location = new Location();
    location.setUnderpass(false);
    location.setStartTime(app.getStartTime());
    location.setEndTime(app.getEndTime());
    location.setPaymentTariff("3");
    app.setLocations(Collections.singletonList(location));
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

  public Application dummyOutdoorApplication(String name, String owner, Customer customer) {
    Application app = dummyBasicApplication(name, owner, customer);
    app.setType(ApplicationType.EVENT);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OUTDOOREVENT, Collections.emptyList()));
    app.setExtension(dummyOutdoorEvent());
    return app;
  }

  public Application dummyOutdoorApplicationWithLocation(String name, String owner) {
    Application app = dummyOutdoorApplication(name, owner);
    Geometry geometry = polygon(3879,
            ring(c(25492000, 6675000), c(25492500, 6675000), c(25492100, 6675100), c(25492000, 6675000)));
    Location location = createLocation("address1", geometry, ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
    location.setCityDistrictId(1);
    app.setLocations(Collections.singletonList(location));
    return app;
  }

  public Application dummyShortTermRentalApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.SHORT_TERM_RENTAL);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OTHER, Collections.emptyList()));
    app.setExtension(dummyShortTermRental());
    return app;
  }

  public Application dummyBridgeBannerApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.SHORT_TERM_RENTAL);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.BRIDGE_BANNER, Collections.emptyList()));
    app.setExtension(dummyShortTermRental());
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

  public Application dummyPlacementContractApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.PLACEMENT_CONTRACT);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.STREET_AND_GREEN, Collections.emptyList()));
    app.setExtension(dummyPlacementContract());
    return app;
  }

  public Application dummyExcavationAnnouncementApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.STREET_AND_GREEN, Collections.emptyList()));
    app.setExtension(dummyExcavationAnnouncement());
    return app;
  }

  public Application dummyCableReportApplication(String name, String owner) {
    Application app = dummyBasicApplication(name, owner);
    app.setType(ApplicationType.CABLE_REPORT);
    app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.STREET_AND_GREEN, Collections.emptyList()));
    app.setExtension(dummyCableReport());
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
    event.setSurfaceHardness(SurfaceHardness.HARD);
    return event;
  }

  public ApplicationExtension dummyShortTermRental() {
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

  public ApplicationExtension dummyPlacementContract() {
    PlacementContract placementContract = new PlacementContract();
    placementContract.setAdditionalInfo("Some additional info");
    return placementContract;
  }

  public ApplicationExtension dummyExcavationAnnouncement() {
    ExcavationAnnouncement excavationAnnouncement = new ExcavationAnnouncement();
    excavationAnnouncement.setAdditionalInfo("Some additional info");
    return excavationAnnouncement;
  }

  public ApplicationExtension dummyCableReport() {
    CableReport cableReport = new CableReport();
    return cableReport;
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

  public Integer insertApplication(Application application) {
    return applicationDao.insert(application).getId();
  }

  public void insertDummyApplicationHistoryChange(int userId, int applicationId, ChangeType changeType, String changeSpec1, String changeSpec2, ZonedDateTime changeTime) throws SQLException {
    String formattedChangeTime = changeTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    String sql = String.format(
      "insert into allu.change_history (user_id, application_id, change_type, change_specifier, change_specifier_2, change_time) " +
        "values (%d, %d, '%s', '%s', '%s', '%s')",
      userId,
      applicationId,
      changeType.name(),
      changeSpec1,
      changeSpec2,
      formattedChangeTime
    );
    sqlRunner.runSql(sql);
  }

  public void insertDummyAnonymizableApplicationIds(List<Integer> ids) throws SQLException {
    StringBuilder sql = new StringBuilder("insert into allu.anonymizable_application (application_id) VALUES ");
    for (int i = 0; i < ids.size(); i++) {
      sql.append("(").append(ids.get(i)).append(")");
      if (i < ids.size() - 1) {
        sql.append(", ");
      }
    }
    sql.append(";");
    sqlRunner.runSql(sql.toString());
  }

  /**
   * Create a location with given street address and geometry.
   *
   * @param streetAddress
   * @param geometry
   */
  public Location createLocation(String streetAddress, Geometry geometry, ZonedDateTime startTime,
      ZonedDateTime endTime) {
    Location location = new Location();
    location.setGeometry(geometry);
    location.setPostalAddress(new PostalAddress(streetAddress, null, null));
    location.setUnderpass(false);
    location.setStartTime(startTime);
    location.setEndTime(endTime);
    return location;
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
    project.setCreatorId(insertUser(RandomStringUtils.randomAlphabetic(12)).getId());
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
      "delete from allu.person_audit_log",
      "delete from allu.decision",
      "delete from allu.application_customer_contact",
      "delete from allu.default_attachment_application_type",
      "delete from allu.default_attachment",
      "delete from allu.application_attachment",
      "delete from allu.attachment",
      "delete from allu.attachment_data",
      "delete from allu.invoice_row",
      "delete from allu.charge_basis",
      "delete from allu.application_tag",
      "delete from allu.supervision_task",
      "delete from allu.location_flids",
      "delete from allu.location_geometry",
      "delete from allu.location",
      "delete from allu.field_change",
      "delete from allu.change_history",
      "delete from allu.distribution_entry",
      "delete from allu.application",
      "delete from allu.project",
      "delete from allu.contact",
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

  public void deleteFrom(String table) throws SQLException {
    sqlRunner.runSql("delete from allu." + table);
  }
}
