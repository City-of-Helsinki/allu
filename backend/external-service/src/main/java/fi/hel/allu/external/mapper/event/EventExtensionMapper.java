package fi.hel.allu.external.mapper.event;

import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.external.domain.EventExt;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.servicecore.domain.EventJson;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class EventExtensionMapper {
  public EventJson createPromotionExtension(PromotionExt promotion) {
    EventJson eventJson = createExtension(promotion);
    eventJson.setNature(EventNature.PROMOTION);
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
