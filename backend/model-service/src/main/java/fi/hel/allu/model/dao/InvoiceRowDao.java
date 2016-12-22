package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;

import fi.hel.allu.model.domain.InvoiceRow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QInvoiceRow.invoiceRow;

@Repository
public class InvoiceRowDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<InvoiceRow> invoiceRowBean = bean(InvoiceRow.class, invoiceRow.all());

  /**
   * Get the invoice rows for an application
   *
   * @param applicationId application ID
   * @return list of invoice rows (empty, if no rows are stored)
   */
  @Transactional(readOnly = true)
  public List<InvoiceRow> getApplicationRows(int applicationId) {
    return queryFactory.select(invoiceRowBean).from(invoiceRow).where(invoiceRow.applicationId.eq(applicationId)).orderBy(invoiceRow.rowNumber.asc()).fetch();
  }

  /**
   * Set the invoice rows for an application
   *
   * @param applicationId application ID
   * @param rows list of invoice rows. Empty list is allowed and will remove existing rows.
   */
  @Transactional
  public void setApplicationRows(int applicationId, List<InvoiceRow> rows) {
    queryFactory.delete(invoiceRow).where(invoiceRow.applicationId.eq(applicationId));
    if (!rows.isEmpty()) {
      SQLInsertClause insert = queryFactory.insert(invoiceRow);
      for (int row = 0; row < rows.size(); ++row) {
        insert.populate(rows.get(row)).set(invoiceRow.applicationId, applicationId).set(invoiceRow.rowNumber, row)
            .addBatch();
      }
      long numInserts = insert.execute();
      if (numInserts != rows.size()) {
        throw new QueryException("Failed to insert the rows, numInserts=" + numInserts);
      }
    }
  }
}
