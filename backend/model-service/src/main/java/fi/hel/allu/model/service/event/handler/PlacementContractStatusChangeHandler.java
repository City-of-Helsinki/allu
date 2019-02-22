package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.util.Collections;

import fi.hel.allu.model.dao.InformationRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PlacementContract;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;

@Service
public class PlacementContractStatusChangeHandler extends ApplicationStatusChangeHandler {

  private static final int PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET = 1;
  private final DecisionDao decisionDao;

  @Autowired
  public PlacementContractStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService,
      ApplicationDao applicationDao, ChargeBasisService chargeBasisService,
      HistoryDao historyDao, InformationRequestDao informationRequestDao,
      DecisionDao decisionDao) {
    super(applicationService, supervisionTaskService, locationService,
            applicationDao, chargeBasisService, historyDao, informationRequestDao);
    this.decisionDao = decisionDao;
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);
    finishInvoicing(application);
    PlacementContract pc = (PlacementContract)application.getExtension();
    pc.setSectionNumber(decisionDao.getPlacementContractSectionNumber());
    getApplicationService().update(application.getId(), application, userId);
    Location location = getLocationService().findSingleByApplicationId(application.getId());
    final ZonedDateTime startTime = TimeUtil.startOfDay(TimeUtil.homeTime(ZonedDateTime.now()));
    location.setStartTime(startTime);
    final ZonedDateTime endTime = TimeUtil.endOfDay(startTime.plusYears(PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET));
    location.setEndTime(endTime);
    getLocationService().updateApplicationLocations(application.getId(), Collections.singletonList(location), userId);

  }


}
