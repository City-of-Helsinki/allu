package fi.hel.allu.model.testUtils;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for routines shared between all tests
 *
 *
 */
@Component
public class TestCommon {

  @Autowired
  private SqlRunner sqlRunner;

  public void deleteAllData() throws SQLException {
    sqlRunner.runSql(DELETE_ALL_DATA);
  }

  private static final String[] DELETE_ALL_DATA = new String[] {
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
