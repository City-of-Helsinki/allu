package fi.hel.allu.model.service.chargeBasis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fi.hel.allu.model.service.InvoicingPeriodService;
import fi.hel.allu.model.service.PricingService;
import org.apache.commons.lang3.BooleanUtils;
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
  private  final UpdateChargeBasisService updateChargeBasisService;

  public ChargeBasisService(ChargeBasisDao chargeBasisDao, ApplicationDao applicationDao,
                            ApplicationEventPublisher invoicingChangeEventPublisher, InvoicingPeriodService invoicingPeriodService,
                            PricingService pricingService, UpdateChargeBasisService updateChargeBasisService) {
    this.chargeBasisDao = chargeBasisDao;
    this.applicationDao = applicationDao;
    this.invoicingChangeEventPublisher = invoicingChangeEventPublisher;
    this.invoicingPeriodService = invoicingPeriodService;
    this.pricingService = pricingService;
    this.updateChargeBasisService = updateChargeBasisService;
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
    ChargeBasisModification modification = updateChargeBasisService.getModifications(applicationId,
        calculatedEntries, false);
    // Filter locked entries when updating calculated entries
    handleModifications(modification.filtered(chargeBasisDao.getLockedChargeBasisIds(applicationId)));
    deleteEntriesWithoutInvoicePeriod(applicationId);
    return modification.hasChanges();
  }

  private void handleModifications(ChargeBasisModification modification) {
    if (modification.hasChanges()) {
      validateModificationsAllowed(modification);
      chargeBasisDao.setChargeBasis(modification);
      handleInvoicingChanged(modification.getApplicationId());
    }
  }


  private void deleteEntriesWithoutInvoicePeriod(int applicationId) {
    List<ChargeBasisEntry> oldEntries = getChargeBasis(applicationId).stream()
      .filter(e -> !e.getManuallySet()).collect(Collectors.toList());
    // If there are entries with invoicingPeriodId, delete entries without invoicingPeriodId.
    if (oldEntries.stream().anyMatch(oe -> oe.getInvoicingPeriodId() != null)) {
      chargeBasisDao.deleteEntries(
        oldEntries.stream()
          .filter(oe -> oe.getInvoicingPeriodId() == null)
          .map(ChargeBasisEntry::getId)
          .collect(Collectors.toList())
      );
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
    Map<Integer, ChargeBasisEntry> map = new HashMap<>();
    map.put(entryId, entry);
    chargeBasisDao.updateEntries(map);
    handleInvoicingChanged(applicationId);
    return chargeBasisDao.findChargeBasisEntry(applicationId, entryId);
  }

  @Transactional
  public void deleteEntry(int applicationId, int entryId) {
    validateModificationsAllowed(Collections.singletonList(entryId), applicationId);
    chargeBasisDao.deleteEntries(Collections.singletonList(entryId));
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
    final AtomicInteger i = getMaxAreaUsageNumber(entries);
    List<ChargeBasisEntry> manualEntries = entries.stream()
        .filter(ChargeBasisEntry::getManuallySet)
        .map(e -> setAreaUsageTagIfMissing(e, i))
        .collect(Collectors.toList());
    manualEntries.forEach(entry -> setPeriodIfMissing(applicationId, entry));
    ChargeBasisModification  modification = updateChargeBasisService.getModifications(
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
            .max(Comparator.comparingInt(this::getNumberPartAreaUsageTag));
    return new AtomicInteger(maxEntry.map(this::getNumberPartAreaUsageTag).orElse(0));
  }

  @Transactional
  public void setInvoicingPeriodForManualEntries(Integer applicationId) {
    Optional<InvoicingPeriod> invoicingPeriod = invoicingPeriodService.findFirstOpenPeriod(applicationId);
    chargeBasisDao.setInvoicingPeriodForManualEntries(invoicingPeriod.map(InvoicingPeriod::getId).orElse(null), applicationId);
  }

  private void setPeriodIfMissing(int applicationId, ChargeBasisEntry e) {
    if (e.getInvoicingPeriodId() == null) {
      if (e.getReferredTag() != null) {
        Optional<ChargeBasisEntry> referredEntry =  chargeBasisDao.findByTag(applicationId, e.getReferredTag());
        referredEntry.ifPresent(referred -> e.setInvoicingPeriodId(referred.getInvoicingPeriodId()));
      } else {
        Optional<InvoicingPeriod> invoicingPeriod = invoicingPeriodService.findFirstOpenPeriod(applicationId);
        invoicingPeriod.ifPresent(p -> e.setInvoicingPeriodId(p.getId()));
      }
    }
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
    return chargeBasisDao.getChargeBasis(applicationId);
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
    if (status == StatusType.DECISION || status == StatusType.OPERATIONAL_CONDITION || status == StatusType.TERMINATED) {
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
  public void unlockEntries(Integer applicationId) {
    chargeBasisDao.unlockEntries(applicationId);
  }

  @Transactional
  public void lockEntriesOfPeriod(Integer invoicingPeriodId) {
    chargeBasisDao.lockEntriesOfPeriod(invoicingPeriodId);
  }

  @Transactional
  public ChargeBasisEntry setInvoicable(int applicationId, int entryId, boolean invoiced) {
    validateModificationsAllowed(Collections.singleton(entryId), applicationId);
    ChargeBasisEntry entry = chargeBasisDao.setInvoicable(entryId, invoiced);
    updateSubCharges(invoiced, entry.getTag());
    handleInvoicingChanged(applicationId);
    return entry;
  }
  private void updateSubCharges(boolean invoiced, String parentTag ){
   if (parentTag != null) {
     chargeBasisDao.setSubChargesInvoicable(invoiced, parentTag);
    }
  }

  public int getInvoicableSumForLocation(int applicationId, Integer locationId) {
    List<ChargeBasisEntry> applicationEntries = chargeBasisDao.getChargeBasis(applicationId);
    List<ChargeBasisEntry> locationEntries = applicationEntries.stream()
      .filter(c -> Objects.equals(locationId, c.getLocationId()) && c.isInvoicable())
      .collect(Collectors.toList());
    return locationEntries.stream().mapToInt(le -> getEntryPriceWithDiscounts(le, applicationEntries)).sum();

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

  @Transactional(readOnly = true)
  public List<ChargeBasisEntry> findSingleInvoiceByApplicationId(int id) {
    // Get calculated entries without dividing to periods
    List<ChargeBasisEntry> calculatedEntries = pricingService.calculateChargeBasisWithoutInvoicingPeriods(applicationDao.findById(id));
    // Sets invoicable value for calculated entries not divided to periods (divided entries must be handled separately since period specific row has different tag)
    Map<String, Boolean> mapInvoicables = chargeBasisDao.isInvoicable(id, calculatedEntries.stream().map(ChargeBasisEntry::getTag).collect(
            Collectors.toList()),  false);
    calculatedEntries.forEach(e -> e.setInvoicable(BooleanUtils.isTrue(mapInvoicables.get(e.getTag()))));
    // Fetch manual entries from db and add to result.
    return Stream.concat(calculatedEntries.stream(), getChargeBasis(id).stream().filter(ChargeBasisEntry::getManuallySet))
        .collect(Collectors.toList());
  }

  private List<ChargeBasisEntry> getManualEntries(int applicationId) {
    return chargeBasisDao.getChargeBasis(applicationId)
        .stream()
        .filter(ChargeBasisEntry::getManuallySet)
        .collect(Collectors.toList());
  }
}