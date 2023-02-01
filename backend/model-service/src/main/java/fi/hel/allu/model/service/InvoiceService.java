package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;

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
  private final HistoryDao historyDao;
  private final InvoicingPeriodService invoicingPeriodService;
  private final InvoicingDateService invoicingDateService;

  @Autowired
  public InvoiceService(ChargeBasisService chargeBasisService, InvoiceDao invoiceDao, PricingService pricingService,
                        ApplicationDao applicationDao, InvoiceRecipientDao invoiceRecipientDao, CustomerDao customerDao,
                        HistoryDao historyDao, InvoicingPeriodService invoicingPeriodService, InvoicingDateService invoicingDateService) {
    this.chargeBasisService = chargeBasisService;
    this.invoiceDao = invoiceDao;
    this.pricingService = pricingService;
    this.applicationDao = applicationDao;
    this.invoiceRecipientDao = invoiceRecipientDao;
    this.customerDao = customerDao;
    this.historyDao = historyDao;
    this.invoicingPeriodService = invoicingPeriodService;
    this.invoicingDateService = invoicingDateService;
  }

  @Transactional(readOnly = true)
  public List<Invoice> findByApplication(int id) {
    List<Invoice> invoices = invoiceDao.findByApplication(id);
    List<Integer> replacedApplicationIds = new ArrayList<>();
    historyDao.getReplacedApplicationIds(id, replacedApplicationIds);
    List<Invoice> replacedApplicationInvoices = invoiceDao.findInvoicedInvoices(replacedApplicationIds);
    return Stream.of(invoices, replacedApplicationInvoices)
        .flatMap(Collection::stream)
        .sorted(Comparator.comparing(Invoice::getInvoicableTime, Comparator.nullsFirst(Comparator.reverseOrder())))
        .collect(Collectors.toList());
  }

  @Transactional
  public void createInvoices(int applicationId, boolean sapIdPending) {
    Application application = applicationDao.findById(applicationId);
    final Optional<Customer> customerOpt = customerDao.findById(application.getInvoiceRecipientId());
    if (!customerOpt.isPresent()) {
      throw new NoSuchEntityException("invoice.create.customer.notFound");
    }
    final Customer customer = customerOpt.get();
    final InvoiceRecipient invoiceRecipient = new InvoiceRecipient(customer);
    invoiceDao.deleteOpenInvoicesByApplication(applicationId);
    List<InvoicingPeriod> openPeriods = invoicingPeriodService.findOpenPeriodsForApplicationId(applicationId);

    if (!CollectionUtils.isEmpty(openPeriods)) {
      final int invoiceRecipientId = invoiceRecipientDao.insert(invoiceRecipient);
      openPeriods.forEach(p -> addInvoiceForPeriod(p, application, invoiceRecipientId, sapIdPending));
    } else {
      addInvoiceForApplication(applicationId, sapIdPending, application, invoiceRecipient);
    }
  }

  private void addInvoiceForApplication(int applicationId, boolean sapIdPending, Application application,
      final InvoiceRecipient invoiceRecipient) {
    List<InvoiceRow> invoiceRows = createInvoiceRows(applicationId, null);
    if (!invoiceRows.isEmpty()) {
      final int invoiceRecipientId = invoiceRecipientDao.insert(invoiceRecipient);
      ZonedDateTime invoicingDate = getInvoicingDate(application);
      Invoice invoice = new Invoice(null, applicationId, invoicingDate, false, sapIdPending, invoiceRows, invoiceRecipientId, null);
      invoiceDao.insert(applicationId, invoice);
    }
  }

  @Transactional
  public void addInvoiceForPeriod(InvoicingPeriod period, Integer invoiceRecipientId, boolean sapIdPending) {
    addInvoiceForPeriod(period, applicationDao.findById(period.getApplicationId()), invoiceRecipientId, sapIdPending);
  }

  private void addInvoiceForPeriod(InvoicingPeriod period, Application application, Integer invoiceRecipientId, boolean sapIdPending) {
    List<InvoiceRow> invoiceRows = createInvoiceRows(application.getId(), period.getId());
    if (!invoiceRows.isEmpty()) {
      ZonedDateTime invoicingDate = invoicingDateService.getInvoicingDateForPeriod(application, period);
      Invoice invoice = new Invoice(null, application.getId(), invoicingDate, false, sapIdPending, invoiceRows, invoiceRecipientId, period.getId());
      invoiceDao.insert(application.getId(), invoice);
    }
  }

  private List<InvoiceRow> createInvoiceRows(int applicationId, Integer invoicingPeriodId) {
    List<ChargeBasisEntry> chargeBasisEntries = getChargeBasisEntriesToInvoice(applicationId, invoicingPeriodId);
    List<InvoiceRow> invoiceRows = pricingService.toSingleInvoice(chargeBasisEntries);
    return invoiceRows;
  }

  // Gets charge basis entries that are invoiceable and aren't in previously locked invoice
  private List<ChargeBasisEntry> getChargeBasisEntriesToInvoice(int applicationId, Integer periodId) {
    List<Integer> entryIdsInLockedInvoice = invoiceDao.getChargeBasisIdsInLockedInvoice(applicationId);
    return chargeBasisService.getChargeBasis(applicationId).stream()
        .filter(c -> !entryIdsInLockedInvoice.contains(c.getId()))
        .filter(c -> c.isInvoicable())
        .filter(c -> Objects.equals(c.getInvoicingPeriodId(),  periodId))
        .collect(Collectors.toList());
  }

  private ZonedDateTime getInvoicingDate(Application application) {
    if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT || application.getType() == ApplicationType.AREA_RENTAL) {
      // Excavation announcements invoiced when final operational condition / work finished dates are approved
      // Area rentals invoiced when work finished date approved
      return null;
    }
    return invoicingDateService.getInvoicingDate(application);
  }

  @Transactional
  public void setInvoicableTime(int applicationId, ZonedDateTime invoicableTime) {
    applicationDao.setInvoicingDate(applicationId, invoicableTime);
    invoiceDao.setInvoicableTime(applicationId, invoicableTime);
  }

  @Transactional
  public void setInvoicableTimeForPeriod(int periodId, ZonedDateTime invoicableTime) {
    invoiceDao.setInvoicableTimeForPeriod(periodId, invoicableTime);
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

  private List<Invoice> filterZeroInvoices(List<Invoice> invoices) {
    return invoices.stream().filter(i -> getInvoiceTotal(i) != 0).collect(Collectors.toList());
  }

  private int getInvoiceTotal(Invoice i) {
    return i.getRows().stream().mapToInt(InvoiceRow::getNetPrice).sum();
  }

  /**
   * Mark given invoices as sent
   *
   * @param invoiceIds the database IDs of the invoices
   */
  @Transactional
  public void markSent(List<Integer> invoiceIds) {
    invoiceDao.markSent(invoiceIds);
    List<Integer> invoicePeriodIds = invoiceDao.getInvoicePeriodIds(invoiceIds);
    invoicingPeriodService.closeInvoicingPeriods(invoicePeriodIds);
    // Mark application invoiced after all invoices are sent.
    Map<Integer, List<Integer>> unInvoicedMap = invoiceDao.getUnvoicedInvoices(invoiceIds);
    List<Integer> invoicedApplicationIds =  invoiceDao.getApplicationIdsForInvoices(invoiceIds)
        .stream()
        .filter(id -> !unInvoicedMap.containsKey(id))
        .collect(Collectors.toList());

    applicationDao.markInvoiced(invoicedApplicationIds);
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
    return invoiceDao.findByApplication(applicationId).stream()
        .anyMatch(invoice -> invoice.isInvoiced());
  }

  public boolean hasInvoices(int applicationId) {
    return invoiceDao.hasInvoices(applicationId);
  }

  public void lockInvoices(int applicationId) {
    invoiceDao.lockInvoices(applicationId);
  }

  public void lockInvoicesOfPeriod(int periodId) {
    invoiceDao.lockInvoicesOfPeriod(periodId);
  }

  public void deleteUninvoicedInvoices(Integer id) {
    invoiceDao.deleteUninvoicedByApplication(id);
  }

  @Transactional
  public void updateInvoiceRecipient(int applicationId, int customerId, boolean sapIdPending) {
    final Customer customer = customerDao.findById(customerId)
        .orElseThrow(() -> new NoSuchEntityException("invoice.create.customer.notFound"));
    final InvoiceRecipient invoiceRecipient = new InvoiceRecipient(customer);
    final int invoiceRecipientId = invoiceRecipientDao.insert(invoiceRecipient);
    invoiceDao.setInvoiceRecipient(applicationId, invoiceRecipientId, sapIdPending);
  }

  // Update existing invoice rows. Does not create new rows / delete old rows
  @Transactional
  public void updateInvoiceRows(Integer applicationId) {
    List<InvoiceRow> existingRows = invoiceDao.getInvoiceRows(applicationId);
    Map<Integer, InvoiceRow> updatedRowsByChargeBasisId = pricingService.toSingleInvoice(chargeBasisService.getChargeBasis(applicationId))
        .stream()
        .collect(Collectors.toMap(InvoiceRow::getChargeBasisId, Function.identity()));
    invoiceDao.updateInvoiceRow(existingRows, updatedRowsByChargeBasisId);
  }
}