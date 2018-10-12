package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.InvoiceRecipientDao;
import fi.hel.allu.model.dao.InvoiceDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRecipient;
import fi.hel.allu.model.domain.InvoiceRow;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The service class responsible for managing invoices
 */
@Service
public class InvoiceService {
  private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

  private final ChargeBasisService chargeBasisService;
  private final InvoiceDao invoiceDao;
  private final PricingService pricingService;
  private final ApplicationDao applicationDao;
  private final InvoiceRecipientDao invoiceRecipientDao;
  private final CustomerDao customerDao;

  @Autowired
  public InvoiceService(ChargeBasisService chargeBasisService, InvoiceDao invoiceDao, PricingService pricingService,
                        ApplicationDao applicationDao, InvoiceRecipientDao invoiceRecipientDao, CustomerDao customerDao) {
    this.chargeBasisService = chargeBasisService;
    this.invoiceDao = invoiceDao;
    this.pricingService = pricingService;
    this.applicationDao = applicationDao;
    this.invoiceRecipientDao = invoiceRecipientDao;
    this.customerDao = customerDao;
  }

  @Transactional(readOnly = true)
  public List<Invoice> findByApplication(int id) {
    return invoiceDao.findByApplication(id);
  }

  @Transactional
  public void createInvoices(int applicationId, boolean sapIdPending) {
    List<ChargeBasisEntry> chargeBasisEntries = getChargeBasisEntriesToInvoice(applicationId);
    List<InvoiceRow> invoiceRows = pricingService.toSingleInvoice(chargeBasisEntries);
    Application application = applicationDao.findById(applicationId);
    ZonedDateTime invoicingDate = getInvoicingDate(application);
    final Optional<Customer> customerOpt = customerDao.findById(application.getInvoiceRecipientId());
    if (!customerOpt.isPresent()) {
      throw new NoSuchEntityException("invoice.create.customer.notFound");
    }
    final Customer customer = customerOpt.get();
    final InvoiceRecipient invoiceRecipient = new InvoiceRecipient(customer);
    final int invoiceRecipientId = invoiceRecipientDao.insert(invoiceRecipient);

    invoiceDao.deleteOpenInvoicesByApplication(applicationId);
    if (!invoiceRows.isEmpty()) {
      Invoice invoice = new Invoice(null, applicationId, invoicingDate, false, sapIdPending, invoiceRows, invoiceRecipientId);
      invoiceDao.insert(applicationId, invoice);
    }
  }

  // Gets charge basis entries that aren't in previously locked invoice
  private List<ChargeBasisEntry> getChargeBasisEntriesToInvoice(int applicationId) {
    List<Integer> entryIdsInLockedInvoice = invoiceDao.getChargeBasisIdsInLockedInvoice(applicationId);
    List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getChargeBasis(applicationId)
        .stream().filter(c -> !entryIdsInLockedInvoice.contains(c.getId())).collect(Collectors.toList());
    return chargeBasisEntries;
  }

  private ZonedDateTime getInvoicingDate(Application application) {
    if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      // Excavation announcements invoiced when final operational condition / work finished dates are approved
      return null;
    }
    return application.getInvoicingDate();
  }

  @Transactional
  public void setInvoicableTime(int applicationId, ZonedDateTime invoicableTime) {
    applicationDao.setInvoicingDate(applicationId, invoicableTime);
    invoiceDao.setInvoicableTime(applicationId, invoicableTime);
  }

  /**
   * Retrieve the list of invoices waiting to be sent
   *
   * @return list of invoices
   */
  @Transactional(readOnly = true)
  public List<Invoice> findPending() {
    List<Invoice> invoices = invoiceDao.findPending();
    invoices.stream().forEach(i -> {
      Optional<InvoiceRecipient> opt = invoiceRecipientDao.findById(i.getRecipientId());
      if (opt.isPresent()) {
        i.setInvoiceRecipient(opt.get());
      } else {
        logger.error("Invoice recipient not found with ID {}", i.getRecipientId());
      }
    });
    return invoices;
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

  @Transactional(readOnly = true)
  public boolean applicationHasInvoiced(int applicationId) {
    return findByApplication(applicationId).stream()
        .anyMatch(invoice -> invoice.isInvoiced());
  }

  public boolean hasInvoices(int applicationId) {
    return invoiceDao.hasInvoices(applicationId);
  }

  public void lockInvoices(int applicationId) {
    invoiceDao.lockInvoices(applicationId);
  }
}
