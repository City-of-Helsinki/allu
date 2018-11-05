package fi.hel.allu.model.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.service.event.handler.*;

@Service
public class ApplicationStatusChangeListener {

  private final PlacementContractStatusChangeHandler placementContractStatusChangeHandler;
  private final ExcavationAnnouncementStatusChangeHandler excavationAnnouncementStatusChangeHandler;
  private final CableReportStatusChangeHandler cableReportStatusChangeHandler;
  private final TrafficArrangementStatusChangeHandler trafficArrangementStatusChangeHandler;
  private final ApplicationStatusChangeHandler applicationStatusChangeHandler;
  private final AreaRentalStatusChangeHandler areaRentalStatusChangeHandler;

  @Autowired
  public ApplicationStatusChangeListener(
      PlacementContractStatusChangeHandler placementContractStatusChangeHandler,
      ExcavationAnnouncementStatusChangeHandler excavationAnnouncementStatusChangeHandler,
      CableReportStatusChangeHandler cableReportStatusChangeHandler,
      TrafficArrangementStatusChangeHandler trafficArrangementStatusChangeHandler,
      ApplicationStatusChangeHandler applicationStatusChangeHandler,
      AreaRentalStatusChangeHandler areaRentalStatusChangeHandler) {
    this.placementContractStatusChangeHandler = placementContractStatusChangeHandler;
    this.excavationAnnouncementStatusChangeHandler = excavationAnnouncementStatusChangeHandler;
    this.cableReportStatusChangeHandler = cableReportStatusChangeHandler;
    this.trafficArrangementStatusChangeHandler = trafficArrangementStatusChangeHandler;
    this.applicationStatusChangeHandler = applicationStatusChangeHandler;
    this.areaRentalStatusChangeHandler = areaRentalStatusChangeHandler;
  }

  @EventListener
  public void onApplicationStatusChange(ApplicationStatusChangeEvent event) {
    ApplicationStatusChangeHandler handler = getHandler(event);
    handler.handleStatusChange(event);
  }

  private ApplicationStatusChangeHandler getHandler(ApplicationStatusChangeEvent event) {
    switch (event.getApplication().getType()) {
    case CABLE_REPORT:
      return cableReportStatusChangeHandler;
    case EXCAVATION_ANNOUNCEMENT:
      return excavationAnnouncementStatusChangeHandler;
    case PLACEMENT_CONTRACT:
      return placementContractStatusChangeHandler;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      return trafficArrangementStatusChangeHandler;
    case AREA_RENTAL:
      return areaRentalStatusChangeHandler;
    default:
      return applicationStatusChangeHandler;
    }
  }

}
