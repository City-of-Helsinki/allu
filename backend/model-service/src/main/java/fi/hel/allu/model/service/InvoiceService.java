package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.InvoiceDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * The service class responsible for managing invoices
 */
@Service
public class InvoiceService {
  private ChargeBasisDao chargeBasisDao;
  private InvoiceDao invoiceDao;
  private PricingService pricingService;

  @Autowired
  public InvoiceService(ChargeBasisDao chargeBasisDao, InvoiceDao invoiceDao, PricingService pricingService) {
    this.chargeBasisDao = chargeBasisDao;
    this.invoiceDao = invoiceDao;
    this.pricingService = pricingService;
  }

  @Transactional(readOnly = true)
  public List<Invoice> findByApplication(int id) {
    return invoiceDao.findByApplication(id);
  }

  @Transactional
  public void createInvoices(int applicationId) {
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisDao.getChargeBasis(applicationId);
    List<InvoiceRow> invoiceRows = pricingService.toSingleInvoice(chargeBasisEntries);
    Invoice invoice = new Invoice(null, applicationId, ZonedDateTime.now(), false, invoiceRows);
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
  }

}
