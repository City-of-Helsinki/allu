package fi.hel.allu.model.service;

import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.ChargeBasisModification;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.pricing.ChargeBasisTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ChargeBasisService {

  private ChargeBasisDao chargeBasisDao;

  @Autowired
  public ChargeBasisService(ChargeBasisDao chargeBasisDao) {
    this.chargeBasisDao = chargeBasisDao;
  }

  /**
   * Stores calculated charge basis entries for application. Returns value
   * indicating whether changes were made.
   * @param applicationId id of application for which entries are set
   * @param entries charge basis entries to set
   */
  @Transactional
  public boolean setCalculatedChargeBasis(int applicationId, List<ChargeBasisEntry> entries) {
    ChargeBasisModification modification = chargeBasisDao.getModifications(applicationId,
        entries.stream().filter(e -> !e.getManuallySet()).collect(Collectors.toList()), false);
    if (modification.hasChanges()) {
      chargeBasisDao.setChargeBasis(modification);
    }
    return modification.hasChanges();
  }

  /**
   * Stores manual charge basis entries for application. Returns value
   * indicating whether changes were made.
   * @param applicationId id of application for which entries are set
   * @param entries charge basis entries to set
   */
  @Transactional
  public boolean setManualChargeBasis(int applicationId, List<ChargeBasisEntry> entries) {
    final Optional<ChargeBasisEntry> maxEntry =
        entries.stream()
            .filter(e -> e.getType() == ChargeBasisType.AREA_USAGE_FEE && e.getTag() != null)
            .max((e1, e2) -> Integer.compare(getNumberPartAreaUsageTag(e1), getNumberPartAreaUsageTag(e2)));
    final AtomicInteger i = new AtomicInteger(maxEntry.map(e -> getNumberPartAreaUsageTag(e)).orElse(0));
    ChargeBasisModification modification = chargeBasisDao.getModifications(
        applicationId,
        entries.stream()
            .filter(e -> e.getManuallySet())
            .map(e -> setAreaUsageTagIfMissing(e, i))
            .collect(Collectors.toList()),
        true);
    if (modification.hasChanges()) {
      chargeBasisDao.setChargeBasis(modification);
    }
    return modification.hasChanges();

  }

  private int getNumberPartAreaUsageTag(ChargeBasisEntry entry) {
    return Integer.parseInt(entry.getTag().substring(ChargeBasisTag.AreaUsageTag().toString().length()));
  }

  private ChargeBasisEntry setAreaUsageTagIfMissing(ChargeBasisEntry entry, AtomicInteger i) {
    if (entry.getTag() == null && entry.getType() == ChargeBasisType.AREA_USAGE_FEE) {
      entry.setTag(ChargeBasisTag.AreaUsageTag().toString() + i.addAndGet(1));
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
}
