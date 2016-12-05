package fi.hel.allu.model.dao;

import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.types.ApplicationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class for reading the next sequence value for application type specific id. This implementation expects that the referred sequences exist
 * and that they are never accessed outside this class.
 */
@Repository
public class ApplicationSequenceDao {

  public static final int YEARLY_ID_RANGE = 100000;

  public enum APPLICATION_TYPE_PREFIX {

    /** In Finnish: kaivuilmoitus */
    KP(ApplicationType.EXCAVATION_ANNOUNCEMENT),
    /** In Finnish: aluevuokraus */
    AL(ApplicationType.AREA_RENTAL),
    /** In Finnish: tilapäinen liikennejärjestely */
    LJ(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS),
    /** In Finnish: johtoselvitys */
    JS(ApplicationType.CABLE_REPORT),
    /** In Finnish: sijoituslupa */
    SL(ApplicationType.PLACEMENT_PERMIT),
    /** In Finnish: tapahtumalupa */
    TP(ApplicationType.EVENT),
    /** In Finnish: lyhytaikainen maanvuokraus */
    VL(ApplicationType.SHORT_TERM_RENTAL),
    /** In Finnish: muistiinpano */
    MP(ApplicationType.NOTE);

    // TODO: Simplify the implementation. Since there's a 1-to-1 mapping from
    // application type to prefix, the hashmap based solution is not needed
    // anymore

    private final List<ApplicationType> applicationTypes;
    private static HashMap<ApplicationType, APPLICATION_TYPE_PREFIX> typeToPrefix = new HashMap<>();

    APPLICATION_TYPE_PREFIX(ApplicationType... applicationTypes) {
      this.applicationTypes = Arrays.asList(applicationTypes);
    }

    static {
      for (APPLICATION_TYPE_PREFIX prefix : APPLICATION_TYPE_PREFIX.values()) {
        for (ApplicationType applicationType : prefix.applicationTypes) {
          typeToPrefix.put(applicationType, prefix);
        }
      }
    }

    static public APPLICATION_TYPE_PREFIX of(ApplicationType applicationType) {
      return typeToPrefix.get(applicationType);
    }
  };

  private static final Logger logger = LoggerFactory.getLogger(ApplicationSequenceDao.class);
  private Clock clock = Clock.systemDefaultZone();

  @Autowired
  private SQLQueryFactory queryFactory;

  @Autowired
  private DataSource dataSource;

  /**
   * Returns the next value for given application type's sequence.
   *
   * @param prefix
   *          Prefix to use for finding the right sequence value.
   *
   * @return The value of the sequence.
   */
  @Transactional
  public long getNextValue(APPLICATION_TYPE_PREFIX prefix) {
    synchronized (ApplicationSequenceDao.class) {
      final int millenium = 2000;
      int year = getCurrentTime().getYear() - millenium;
      // sequence starts from 1 each year (meaning 1700000 + 1)
      int currentYearMinimumSeq = year * YEARLY_ID_RANGE + 1;
      SimpleExpression<Long> seqExp = SQLExpressions.nextval(getSequenceName(prefix));
      long seqVal = queryFactory.select(seqExp).fetchOne();
      if (seqVal < currentYearMinimumSeq) {
        resetSequence(prefix, currentYearMinimumSeq);
        return currentYearMinimumSeq;
      } else {
        return seqVal;
      }
    }
  }

  private String getSequenceName(APPLICATION_TYPE_PREFIX prefix) {
    return "allu." + prefix.name() + "_application_type_sequence";
  }

  /**
   * Support for unit testing.
   */
  private ZonedDateTime getCurrentTime() {
    return ZonedDateTime.now(clock);
  }

  /**
   * Resets sequence to the given value.
   */
  void resetSequence(APPLICATION_TYPE_PREFIX prefix, long newValue) {
    String sql = "ALTER SEQUENCE " + getSequenceName(prefix) + " RESTART WITH " + newValue;
    // Using connection from DataSource, because QueryDSL's SQLQueryFactory.getConnection() seems to have problems (or possible problems)
    // with making sure the connection is closed
    try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {
      connection.setAutoCommit(false);
      stmt.executeUpdate(sql);
      connection.commit();
    } catch (SQLException e) {
      logger.error("Failed to reset sequence " + getSequenceName(prefix), e);
      throw new RuntimeException("Failed to reset sequence: " + getSequenceName(prefix), e);
    }

  }

  void setClock(Clock clock) {
    this.clock = clock;
  }
}
