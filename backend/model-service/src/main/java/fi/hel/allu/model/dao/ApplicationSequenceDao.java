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
    KP(),
    /** In Finnish: aluevuokraus */
    AL(),
    /** In Finnish: tilapäinen liikennejärjestely */
    LJ(),
    /** In Finnish: johtoselvitys */
    JS(
        ApplicationType.CITY_STREET_AND_GREEN,
        ApplicationType.WATER_AND_SEWAGE,
        ApplicationType.HKL,
        ApplicationType.ELECTRIC_CABLE,
        ApplicationType.DISTRICT_HEATING,
        ApplicationType.DISTRICT_COOLING,
        ApplicationType.TELECOMMUNICATION,
        ApplicationType.GAS,
        ApplicationType.AD_PILLARS_AND_STOPS,
        ApplicationType.PROPERTY_MERGER,
        ApplicationType.SOIL_INVESTIGATION,
        ApplicationType.JOINT_MUNICIPAL_INFRASTRUCTURE,
        ApplicationType.ABSORBING_SEWAGE_SYSTEM,
        ApplicationType.UNDERGROUND_CONSTRUCTION,
        ApplicationType.OTHER_CABLE_REPORT
    ),
    /** In Finnish: sijoituslupa */
    SL(),
    /** In Finnish: tapahtumalupa */
    TP(ApplicationType.OUTDOOREVENT),
    /** In Finnish: lyhytaikainen maanvuokraus */
    VL(
        ApplicationType.ART,
        ApplicationType.BENJI,
        ApplicationType.BRIDGE_BANNER,
        ApplicationType.CARGO_CONTAINER,
        ApplicationType.CIRCUS,
        ApplicationType.DOG_TRAINING_EVENT,
        ApplicationType.DOG_TRAINING_FIELD,
        ApplicationType.KESKUSKATU_SALES,
        ApplicationType.OTHER_SHORT_TERM_RENTAL,
        ApplicationType.PROMOTION_OR_SALES,
        ApplicationType.SEASON_SALE,
        ApplicationType.SMALL_ART_AND_CULTURE,
        ApplicationType.STORAGE_AREA,
        ApplicationType.SUMMER_THEATER,
        ApplicationType.URBAN_FARMING
    ),
    /** In Finnish: muistiinpano */
    MP();

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
   * Returns the next value for given applicatio type's sequence.
   *
   * @param   prefix  Prefix to use for finding the right seqeunce value.
   *
   * @return  The value of the sequence.
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
