package fi.hel.allu.model.config;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

import java.sql.SQLException;

public class FallbackTranslator extends SQLStateSQLExceptionTranslator {

    @Override
    protected DataAccessException doTranslate(String task, String sql, SQLException ex) {
        DataAccessException e = super.doTranslate(task, sql, ex);

        if (e != null) {
            return e;
        }

        return new UncategorizedSQLException(task, sql, ex);
    }
}
