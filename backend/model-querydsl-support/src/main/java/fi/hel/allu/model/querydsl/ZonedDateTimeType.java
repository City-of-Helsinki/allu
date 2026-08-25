package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.JSR310ZonedDateTimeType;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Replaces QueryDSL's built-in {@link JSR310ZonedDateTimeType} because PostgreSQL JDBC
 * driver 42.x does not support {@code ZonedDateTime} in the JDBC type inference used by
 * the parent class's read and write paths:
 *
 * <ul>
 *   <li><b>Read</b>: {@code rs.getObject(index, ZonedDateTime.class)} throws PSQLException.
 *       Fix: read as {@code OffsetDateTime} (fully supported for {@code timestamptz}) and convert.
 *   <li><b>Write</b>: {@code st.setObject(index, zonedDateTime)} without an explicit SQL type
 *       throws "Can't infer the SQL type". Fix: convert to {@code OffsetDateTime} and pass
 *       {@code Types.TIMESTAMP_WITH_TIMEZONE} explicitly.
 * </ul>
 *
 * <p>Register this type in {@code JdbcConfiguration} to override the default handler:
 * <pre>configuration.register(new ZonedDateTimeType());</pre>
 */
public class ZonedDateTimeType extends JSR310ZonedDateTimeType {

    @Override
    public ZonedDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
        OffsetDateTime odt = rs.getObject(startIndex, OffsetDateTime.class);
        return odt != null ? odt.toZonedDateTime() : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, ZonedDateTime value) throws SQLException {
        if (value == null) {
            st.setNull(startIndex, Types.TIMESTAMP_WITH_TIMEZONE);
        } else {
            st.setObject(startIndex, value.toOffsetDateTime(), Types.TIMESTAMP_WITH_TIMEZONE);
        }
    }
}
