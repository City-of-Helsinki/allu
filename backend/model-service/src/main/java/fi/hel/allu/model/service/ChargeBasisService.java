package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChargeBasisService {

  private ChargeBasisDao chargeBasisDao;

  @Autowired
  public ChargeBasisService(ChargeBasisDao chargeBasisDao) {
    this.chargeBasisDao = chargeBasisDao;
  }

  /**
   * Stores calculated charge basis entries for application
   * @param applicationId id of application for which entries are set
   * @param entries charge basis entries to set
   */
  @Transactional
  public void setCalculatedChargeBasis(int applicationId, List<ChargeBasisEntry> entries) {
    chargeBasisDao.setChargeBasis(
        applicationId,
        entries.stream().filter(e -> !e.getManuallySet()).collect(Collectors.toList()),
        false);
  }

  /**
   * Stores manual charge basis entries for application
   * @param applicationId id of application for which entries are set
   * @param entries charge basis entries to set
   */
  @Transactional
  public void setManualChargeBasis(int applicationId, List<ChargeBasisEntry> entries) {
    chargeBasisDao.setChargeBasis(
        applicationId,
        entries.stream().filter(e -> e.getManuallySet()).collect(Collectors.toList()),
        true);
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
