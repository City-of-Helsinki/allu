package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;

import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
  public List<InvoiceRow> getInvoiceRows(int applicationId) {
    return queryFactory.select(invoiceRowBean).from(invoiceRow).where(invoiceRow.applicationId.eq(applicationId))
        .orderBy(invoiceRow.manuallySet.asc(), invoiceRow.rowNumber.asc()).fetch();
  }

  /**
   * Set the invoice rows for an application
   *
   * @param applicationId application ID
   * @param rows list of invoice rows. Empty list is allowed and will remove
   *          existing rows.
   * @param manuallySet should the rows be marked as manually set or not?
   */
  @Transactional
  public void setInvoiceRows(int applicationId, List<InvoiceRow> rows, boolean manuallySet) {
    queryFactory.delete(invoiceRow)
        .where(invoiceRow.applicationId.eq(applicationId).and(invoiceRow.manuallySet.eq(manuallySet))).execute();
    if (!rows.isEmpty()) {
      SQLInsertClause insert = queryFactory.insert(invoiceRow);
      for (int row = 0; row < rows.size(); ++row) {
        insert
            .populate(rows.get(row),
                new ExcludingMapper(NullHandling.WITH_NULL_BINDINGS, Arrays.asList(invoiceRow.manuallySet)))
            .set(invoiceRow.applicationId, applicationId).set(invoiceRow.rowNumber, row)
            .set(invoiceRow.manuallySet, manuallySet)
            .addBatch();
      }
      long numInserts = insert.execute();
      if (numInserts != rows.size()) {
        throw new QueryException("Failed to insert the rows, numInserts=" + numInserts);
      }
    }
  }
}
