package fi.hel.allu.model.service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
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


  @Autowired
  public ChargeBasisService(ChargeBasisDao chargeBasisDao, ApplicationDao applicationDao,
      ApplicationEventPublisher invoicingChangeEventPublisher, InvoicingPeriodService invoicingPeriodService) {
    this.chargeBasisDao = chargeBasisDao;
    this.applicationDao = applicationDao;
    this.invoicingChangeEventPublisher = invoicingChangeEventPublisher;
    this.invoicingPeriodService = invoicingPeriodService;
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
    handleModifications(modification);
    return modification.hasChanges();
  }

  private void handleModifications(ChargeBasisModification modification) {
    if (modification.hasChanges()) {
      validateModificationsAllowed(modification);
      chargeBasisDao.setChargeBasis(modification);
      handleInvoicingChanged(modification.getApplicationId());
    }
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
    final Optional<ChargeBasisEntry> maxEntry =
        entries.stream()
            .filter(e -> e.getType() == ChargeBasisType.AREA_USAGE_FEE && e.getTag() != null)
            .max((e1, e2) -> Integer.compare(getNumberPartAreaUsageTag(e1), getNumberPartAreaUsageTag(e2)));
    final AtomicInteger i = new AtomicInteger(maxEntry.map(e -> getNumberPartAreaUsageTag(e)).orElse(0));

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

  @Transactional
  public void setInvoicingPeriodForManualEntries(Integer applicationId) {
    Optional<InvoicingPeriod> invoicingPeriod = invoicingPeriodService.findFirstOpenPeriod(applicationId);
    chargeBasisDao.setInvoicingPeriodForManualEntries(invoicingPeriod.map(InvoicingPeriod::getId).orElse(null), applicationId);
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

  private ChargeBasisEntry setAreaUsageTagIfMissing(ChargeBasisEntry entry, AtomicInteger i) {
    if (entry.getTag() == null && entry.getType() == ChargeBasisType.AREA_USAGE_FEE) {
      entry.setTag(ChargeBasisTag.AreaUsageTag().toString() + i.addAndGet(1));
      entry.setReferrable(true);
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

  private void validateModificationsAllowed(Set<Integer> modifiedEntries, int applicationId) {
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

  private boolean containsLockedEntries(Set<Integer> modifiedEntries, int applicationId) {
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
}
