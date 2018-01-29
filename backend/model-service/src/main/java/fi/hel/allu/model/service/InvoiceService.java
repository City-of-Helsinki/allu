package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.InvoiceDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;

/**
 * The service class responsible for managing invoices
 */
@Service
public class InvoiceService {
  private ChargeBasisDao chargeBasisDao;
  private InvoiceDao invoiceDao;
  private PricingService pricingService;
  private ApplicationDao applicationDao;

  @Autowired
  public InvoiceService(ChargeBasisDao chargeBasisDao, InvoiceDao invoiceDao, PricingService pricingService, ApplicationDao applicationDao) {
    this.chargeBasisDao = chargeBasisDao;
    this.invoiceDao = invoiceDao;
    this.pricingService = pricingService;
    this.applicationDao = applicationDao;
  }

  @Transactional(readOnly = true)
  public List<Invoice> findByApplication(int id) {
    return invoiceDao.findByApplication(id);
  }

  @Transactional
  public void createInvoices(int applicationId, boolean sapIdPending) {
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisDao.getChargeBasis(applicationId);
    List<InvoiceRow> invoiceRows = pricingService.toSingleInvoice(chargeBasisEntries);
    ZonedDateTime invoicingDate = applicationDao.getInvoicingDate(applicationId);
    Invoice invoice = new Invoice(null, applicationId, invoicingDate, false, sapIdPending, invoiceRows);
    invoiceDao.deleteByApplication(applicationId);
    invoiceDao.insert(applicationId, invoice);
  }

  /**
   * Retrieve the list of invoices waiting to be sent
   *
   * @return list of invoices
   */
  @Transactional(readOnly = true)
  public List<Invoice> findPending() {
    return invoiceDao.findPending();
  }

  /**
   * Mark given invoices as sent
   *
   * @param invoiceIds the database IDs of the invoices
   */
  @Transactional
  public void markSent(List<Integer> invoiceIds) {
    invoiceDao.markSent(invoiceIds);
    // Mark application invoiced after invoice is sent.
    // When application is invoiced with more than one invoice, this must be done
    // when last invoice of application is sent.
    applicationDao.markInvoiced(invoiceDao.getApplicationIdsForInvoices(invoiceIds));

  }

  /**
   * Release pending invoice (sets sapIdPending to false)
   * @param id
   */
  @Transactional
  public void releasePending(Integer id) {
    invoiceDao.releasePendingInvoice(id);
  }

}
