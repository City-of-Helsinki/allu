package fi.hel.allu.model.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.ChargeBasisModification;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.pricing.ChargeBasisTag;
import fi.hel.allu.model.service.event.InvoicingChangeEvent;

@Service
public class ChargeBasisService {

  private final ChargeBasisDao chargeBasisDao;
  private final ApplicationDao applicationDao;
  private final ApplicationEventPublisher invoicingChangeEventPublisher;
  private final InvoicingPeriodService invoicingPeriodService;
  private final PricingService pricingService;

  @Autowired
  public ChargeBasisService(ChargeBasisDao chargeBasisDao, ApplicationDao applicationDao,
      ApplicationEventPublisher invoicingChangeEventPublisher, InvoicingPeriodService invoicingPeriodService,
      PricingService pricingService) {
    this.chargeBasisDao = chargeBasisDao;
    this.applicationDao = applicationDao;
    this.invoicingChangeEventPublisher = invoicingChangeEventPublisher;
    this.invoicingPeriodService = invoicingPeriodService;
    this.pricingService = pricingService;
  }

  /**
   * Stores calculated charge basis entries for application. Returns value
   * indicating whether changes were made.
   * @param applicationId id of application for which entries are set
   * @param entries charge basis entries to set
   */
  @Transactional
  public boolean setCalculatedChargeBasis(int applicationId, List<ChargeBasisEntry> entries) {
    List<ChargeBasisEntry> calculatedEntries = entries.stream().filter(e -> !e.getManuallySet()).collect(Collectors.toList());
    ChargeBasisModification modification = chargeBasisDao.getModifications(applicationId,
        calculatedEntries, false);
    // Filter locked entries when updating calculated entries
    handleModifications(modification.filtered(chargeBasisDao.getLockedChargeBasisIds(applicationId)));
    return modification.hasChanges();
  }

  private void handleModifications(ChargeBasisModification modification) {
    if (modification.hasChanges()) {
      validateModificationsAllowed(modification);
      chargeBasisDao.setChargeBasis(modification);
      handleInvoicingChanged(modification.getApplicationId());
    }
  }

  @Transactional(readOnly = true)
  public ChargeBasisEntry getEntry(int applicationId, int entryId) {
    ChargeBasisEntry entry = chargeBasisDao.findChargeBasisEntry(applicationId, entryId);
    if (entry == null) {
      throw new NoSuchEntityException("chargebasis.notFound");
    }
    return entry;
  }

  @Transactional
  public ChargeBasisEntry insert(int applicationId, ChargeBasisEntry entry) {
    setPeriodIfMissing(applicationId, entry);
    setAreaUsageTagIfMissing(applicationId, entry);
    ChargeBasisEntry inserted = chargeBasisDao.insertManualEntry(applicationId, entry);
    handleInvoicingChanged(applicationId);
    return inserted;
  }

  @Transactional
  public ChargeBasisEntry updateEntry(int applicationId, int entryId, ChargeBasisEntry entry) {
    validateModificationsAllowed(Collections.singletonList(entryId), applicationId);
    setPeriodIfMissing(applicationId, entry);
    setAreaUsageTagIfMissing(applicationId, entry);
    ChargeBasisEntry updated = chargeBasisDao.updateEntry(entryId, entry);
    handleInvoicingChanged(applicationId);
    return updated;
  }

  @Transactional
  public void deleteEntry(int applicationId, int entryId) {
    validateModificationsAllowed(Collections.singletonList(entryId), applicationId);
    chargeBasisDao.deleteEntries(Collections.singletonList(entryId), applicationId);
    handleInvoicingChanged(applicationId);
  }

  /**
   * Stores manual charge basis entries for application. Returns value
   * indicating whether changes were made.
   * @param applicationId id of application for which entries are set
   * @param entries charge basis entries to set
   */
  @Transactional
  public boolean setManualChargeBasis(int applicationId, List<ChargeBasisEntry> entries) {
    Optional<InvoicingPeriod> invoicingPeriod = invoicingPeriodService.findFirstOpenPeriod(applicationId);
    final AtomicInteger i = getMaxAreaUsageNumber(entries);
    List<ChargeBasisEntry> manualEntries = entries.stream()
        .filter(e -> e.getManuallySet())
        .map(e -> setAreaUsageTagIfMissing(e, i))
        .collect(Collectors.toList());
    invoicingPeriod.ifPresent(p -> setPeriodIfMissing(p.getId(), manualEntries));

    ChargeBasisModification modification = chargeBasisDao.getModifications(
        applicationId,
        manualEntries,
        true);
    handleModifications(modification);
    return modification.hasChanges();

  }

  private AtomicInteger getMaxAreaUsageNumber(List<ChargeBasisEntry> entries) {
    final Optional<ChargeBasisEntry> maxEntry =
        entries.stream()
            .filter(e -> e.getType() == ChargeBasisType.AREA_USAGE_FEE && e.getTag() != null)
            .max((e1, e2) -> Integer.compare(getNumberPartAreaUsageTag(e1), getNumberPartAreaUsageTag(e2)));
    return new AtomicInteger(maxEntry.map(e -> getNumberPartAreaUsageTag(e)).orElse(0));
  }

  @Transactional
  public void setInvoicingPeriodForManualEntries(Integer applicationId) {
    Optional<InvoicingPeriod> invoicingPeriod = invoicingPeriodService.findFirstOpenPeriod(applicationId);
    chargeBasisDao.setInvoicingPeriodForManualEntries(invoicingPeriod.map(InvoicingPeriod::getId).orElse(null), applicationId);
  }

  private void setPeriodIfMissing(int applicationId, ChargeBasisEntry e) {
    Optional<InvoicingPeriod> invoicingPeriod = invoicingPeriodService.findFirstOpenPeriod(applicationId);
    invoicingPeriod.ifPresent(p -> setPeriodIfMissing(p.getId(), Collections.singletonList(e)));
  }

  private void setPeriodIfMissing(Integer invoicingPeriodId, List<ChargeBasisEntry> manualEntries) {
    manualEntries.forEach(e -> {
      if (e.getInvoicingPeriodId() == null) {
        e.setInvoicingPeriodId(invoicingPeriodId);
      }
    });
  }

  private int getNumberPartAreaUsageTag(ChargeBasisEntry entry) {
    return Integer.parseInt(entry.getTag().substring(ChargeBasisTag.AreaUsageTag().toString().length()));
  }

  private void setAreaUsageTagIfMissing(int applicationId, ChargeBasisEntry entry) {
    if (entry.getType() == ChargeBasisType.AREA_USAGE_FEE) {
      final AtomicInteger i = getMaxAreaUsageNumber(getManualEntries(applicationId));
      setAreaUsageTagIfMissing(entry, i);
    }
  }

  private ChargeBasisEntry setAreaUsageTagIfMissing(ChargeBasisEntry entry, AtomicInteger i) {
    if (entry.getType() == ChargeBasisType.AREA_USAGE_FEE) {
      entry.setReferrable(true);
      if (entry.getTag() == null) {
        entry.setTag(ChargeBasisTag.AreaUsageTag().toString() + i.addAndGet(1));
      }
    }
    return entry;
  }

  /**
   * Fetches all charge basis entries (manual & calculated) for specified application
   * @param applicationId id of application containing entries
   * @return List of charge basis entries
   */
  @Transactional(readOnly = true)
  public List<ChargeBasisEntry> getChargeBasis(int applicationId) {
    List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(applicationId);
    return entries;
  }

  private void validateModificationsAllowed(ChargeBasisModification modification) {
    Set<Integer> modifiedEntries = modification.getModifiedEntryIds();
    validateModificationsAllowed(modifiedEntries, modification.getApplicationId());
  }

  private void validateModificationsAllowed(Collection<Integer> modifiedEntries, int applicationId) {
    if (containsLockedEntries(modifiedEntries, applicationId)) {
      throw new IllegalOperationException("chargebasis.locked");
    }
  }

  private void handleInvoicingChanged(int applicationId) {
    StatusType status = applicationDao.getStatus(applicationId);
    if (status == StatusType.DECISION || status == StatusType.OPERATIONAL_CONDITION) {
      // Invoicing changed after last decision
      applicationDao.setInvoicingChanged(applicationId, true);
      invoicingChangeEventPublisher.publishEvent(new InvoicingChangeEvent(this, applicationId));
    }
  }

  private boolean containsLockedEntries(Collection<Integer> modifiedEntries, int applicationId) {
    List<Integer> lockedChargeBasisIds = chargeBasisDao.getLockedChargeBasisIds(applicationId);
    return modifiedEntries.stream().anyMatch(lockedChargeBasisIds::contains);
  }

  @Transactional
  public void lockEntries(Integer applicationId) {
    chargeBasisDao.lockEntries(applicationId);
  }

  @Transactional
  public ChargeBasisEntry setInvoicable(int applicationId, int entryId, boolean invoiced) {
    validateModificationsAllowed(Collections.singleton(entryId), applicationId);
    ChargeBasisEntry entry = chargeBasisDao.setInvoicable(entryId, invoiced);
    handleInvoicingChanged(applicationId);
    return entry;
  }

  public int getInvoicableSumForLocation(int applicationId, Integer locationId) {
    List<ChargeBasisEntry> applicationEntries = chargeBasisDao.getChargeBasis(applicationId);
    List<ChargeBasisEntry> locationEntries = applicationEntries.stream()
      .filter(c -> Objects.equals(locationId, c.getLocationId()) && c.isInvoicable())
      .collect(Collectors.toList());
    int sum = locationEntries.stream().collect(Collectors.summingInt(le -> getEntryPriceWithDiscounts(le, applicationEntries)));
    return sum;
  }

  // Calculates price for one charge basis entry with discounts applied
  private int getEntryPriceWithDiscounts(ChargeBasisEntry entry, List<ChargeBasisEntry> applicationEntries) {
    List<ChargeBasisEntry> discountEntries = applicationEntries
        .stream()
        .filter(re -> Objects.equals(re.getReferredTag(), entry.getTag()) && re.isInvoicable())
        .sorted((re1, re2) -> {
          if (re1.getUnit() == re2.getUnit()) {
            return 0;
          } else if (re1.getUnit() == ChargeBasisUnit.PERCENT) {
            return -1;
          } else {
            return 1;
          }
        })
        .collect(Collectors.toList());
    BigDecimal price = BigDecimal.valueOf(entry.getNetPrice());
    for (ChargeBasisEntry e : discountEntries) {
      price = applyDiscount(price, e);
    }
    return price.setScale(0, RoundingMode.UP).intValue();
  }

  private BigDecimal applyDiscount(BigDecimal price, ChargeBasisEntry e) {
    if (e.getUnit() == ChargeBasisUnit.PERCENT) {
      // Discount quantity is negative
      return price.add(BigDecimal.valueOf(e.getQuantity() / 100.0).multiply(price));
    } else {
      // Discount net price is negative
      return price.add(BigDecimal.valueOf(e.getNetPrice()));
    }
  }

  public List<ChargeBasisEntry> findSingleInvoiceByApplicationId(int id) {
    // Get calculated entries without dividing to periods
    List<ChargeBasisEntry> calculatedEntries = pricingService.calculateChargeBasisWithoutInvoicingPeriods(applicationDao.findById(id));
    // Sets invoicable value for calculated entries not divided to periods (divided entries must be handled separately since period specific row has different tag)
    calculatedEntries.forEach(e -> e.setInvoicable(BooleanUtils.isTrue(chargeBasisDao.isInvoicable(id, e.getTag(), false))));
    // Fetch manual entries from db and add to result.
    return Stream.concat(calculatedEntries.stream(), getChargeBasis(id).stream().filter(e -> e.getManuallySet()))
        .collect(Collectors.toList());
  }

  private List<ChargeBasisEntry> getManualEntries(int applicationId) {
    return chargeBasisDao.getChargeBasis(applicationId)
        .stream()
        .filter(e -> e.getManuallySet())
        .collect(Collectors.toList());
  }
}