package fi.hel.allu.model.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fi.hel.allu.common.domain.types.StoredFilterType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.StoredFilterDao;
import fi.hel.allu.model.domain.StoredFilter;

@Service
public class StoredFilterService {
  private StoredFilterDao dao;

  @Autowired
  public StoredFilterService(StoredFilterDao dao) {
    this.dao = dao;
  }

  @Transactional
  public StoredFilter findById(int id) {
    return dao.findById(id).orElseThrow(() -> new NoSuchEntityException("Stored filter not found", Integer.toString(id)));
  }

  @Transactional
  public List<StoredFilter> findByUserAndType(int userId, StoredFilterType type) {
    return dao.findByUserAndType(userId, type);
  }

  @Transactional
  public StoredFilter insert(StoredFilter filter) {
    return dao.insert(filter);
  }

  @Transactional
  public StoredFilter update(StoredFilter filter) {
    return dao.update(filter);
  }

  @Transactional
  public void delete(int id) {
    dao.delete(id);
  }

  @Transactional
  public void setAsDefault(int filterId) {
    dao.setAsDefault(filterId);
  }
}
