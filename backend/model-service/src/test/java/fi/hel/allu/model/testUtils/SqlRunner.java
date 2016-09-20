package fi.hel.allu.model.testUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.sql.SQLQueryFactory;

@Component
public class SqlRunner {

  @Autowired
  private SQLQueryFactory queryFactory;

  /*
   * Execute the given SQL statements.
   */
  @Transactional
  public void runSql(String... sql) throws SQLException {
    Statement stmt = null;
    try {
      Connection conn = queryFactory.getConnection();
      stmt = conn.createStatement();
      for (String s : sql) {
        stmt.executeUpdate(s);
      }
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

}
