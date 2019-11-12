package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import static fi.hel.allu.QInvoiceRecipient.invoiceRecipient;
import static fi.hel.allu.QInvoice.invoice;
import fi.hel.allu.model.domain.InvoiceRecipient;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Repository
public class InvoiceRecipientDao {

  private static final ExcludingMapper EXCLUDE_IDS = new ExcludingMapper(ExcludingMapper.NullHandling.DEFAULT, Arrays.asList(invoiceRecipient.id));

  private final SQLQueryFactory queryFactory;
  private final QBean<InvoiceRecipient> invoiceRecipientBean = bean(InvoiceRecipient.class, invoiceRecipient.all());

  @Autowired
  InvoiceRecipientDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Transactional
  public int insert(InvoiceRecipient newInvoiceRecipient) {
    int invoiceRecipientId = queryFactory.insert(invoiceRecipient)
        .populate(newInvoiceRecipient, EXCLUDE_IDS)
        .executeWithKey(invoiceRecipient.id);
    return invoiceRecipientId;
  }

  @Transactional(readOnly = true)
  public Optional<InvoiceRecipient> findById(int invoiceRecipientId) {
    InvoiceRecipient theInvoiceRecipient = queryFactory.select(invoiceRecipientBean).from(invoiceRecipient).where(invoiceRecipient.id.eq(invoiceRecipientId)).fetchOne();
    if (theInvoiceRecipient == null) {
      return Optional.empty();
    }
    return Optional.of(theInvoiceRecipient);
  }

  @Transactional(readOnly = true)
  public Optional<InvoiceRecipient> findByApplicationId(int applicationId) {
    InvoiceRecipient result = queryFactory.select(invoiceRecipientBean)
      .from(invoiceRecipient)
      .join(invoice).on(invoice.recipientId.eq(invoiceRecipient.id))
      .where(invoice.applicationId.eq(applicationId))
      .orderBy(invoice.invoicableTime.desc())
      .fetchFirst();
    return Optional.ofNullable(result);
  }
}
