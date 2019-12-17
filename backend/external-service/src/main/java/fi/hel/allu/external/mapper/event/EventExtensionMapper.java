package fi.hel.allu.external.mapper.event;

import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.external.domain.BigEventExt;
import fi.hel.allu.external.domain.EventAdditionalDetails;
import fi.hel.allu.external.domain.EventExt;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.servicecore.domain.EventJson;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventExtensionMapper {
  public EventJson createPromotionExtension(PromotionExt promotion) {
    EventJson eventJson = createExtension(promotion);
    eventJson.setNature(EventNature.PROMOTION);
    return eventJson;
  }

  public EventJson createBigEventExtension(BigEventExt bigEvent) {
    EventJson eventJson = createExtensionWithAdditionalDetails(bigEvent, bigEvent.getAdditionalDetails());
    eventJson.setNature(EventNature.BIG_EVENT);
    return eventJson;
  }

  private EventJson createExtensionWithAdditionalDetails(EventExt event, EventAdditionalDetails details) {
    EventJson eventJson = createExtension(event);
    eventJson.setUrl(details.getUrl());
    eventJson.setAttendees(details.getAttendees());
    eventJson.setEntryFee(details.getEntryFee());
    eventJson.setEcoCompass(BooleanUtils.isTrue(details.getEcoCompass()));
    eventJson.setFoodSales(BooleanUtils.isTrue(details.getFoodSales()));
    eventJson.setFoodProviders(details.getFoodProviders());
    eventJson.setMarketingProviders(details.getMarketingProviders());
    eventJson.setTimeExceptions(details.getTimeExceptions());
    return eventJson;
  }

  private EventJson createExtension(EventExt event) {
    EventJson extension = new EventJson();
    extension.setStructureArea(Optional.ofNullable(event.getStructureArea()).orElse(0));
    extension.setStructureDescription(event.getStructureDescription());
    extension.setEventStartTime(event.getEventStartTime());
    extension.setEventEndTime(event.getEventEndTime());
    extension.setDescription(event.getDescription());
    extension.setSurfaceHardness(SurfaceHardness.HARD);
    return extension;
  }
}
