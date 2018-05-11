package fi.hel.allu.model.service;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.model.dao.ExternalApplicationDao;

@Service
public class ExternalApplicationService {

  private final ExternalApplicationDao externalApplicationDao;

  public ExternalApplicationService(ExternalApplicationDao externalApplicationDao) {
    this.externalApplicationDao = externalApplicationDao;
  }

  public void save(Integer applicationId, ExternalApplication externalApplication) {
    externalApplication.setApplicationId(applicationId);
    externalApplicationDao.save(externalApplication);
  }

  public ExternalApplication findByApplicationId(Integer applicationId) {
    return externalApplicationDao.findByApplicationId(applicationId);
  }
}
