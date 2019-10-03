package fi.hel.allu.external.mapper;

import java.util.List;
import java.util.Optional;

import fi.hel.allu.common.domain.types.SurfaceHardness;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.external.domain.EventExt;
import fi.hel.allu.servicecore.domain.EventJson;

@Component
public class EventExtMapper extends ApplicationExtMapper<EventExt> {

  @Override
  protected EventJson createExtension(EventExt event) {
    EventJson extension = new EventJson();
    extension.setStructureArea(Optional.ofNullable(event.getStructureArea()).orElse(0));
    extension.setStructureDescription(event.getStructureDescription());
    extension.setEventStartTime(event.getEventStartTime());
    extension.setEventEndTime(event.getEventEndTime());
    extension.setNature(EventNature.PROMOTION);
    extension.setDescription(event.getDescription());
    extension.setSurfaceHardness(SurfaceHardness.HARD);
    return extension;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  @Override
  protected ApplicationKind getApplicationKind(EventExt event) {
    return ApplicationKind.PROMOTION;
  }

  @Override
  protected List<Integer> getFixedLocationIds(EventExt event) {
    return event.getFixedLocationIds();
  }
}
