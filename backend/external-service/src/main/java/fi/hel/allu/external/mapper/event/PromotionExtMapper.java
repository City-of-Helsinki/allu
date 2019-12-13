package fi.hel.allu.external.mapper.event;

import java.util.List;
import java.util.Optional;

import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.servicecore.domain.EventJson;

@Component
public class PromotionExtMapper extends ApplicationExtMapper<PromotionExt> {

  @Override
  protected EventJson createExtension(PromotionExt event) {
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
  protected ApplicationKind getApplicationKind(PromotionExt event) {
    return ApplicationKind.PROMOTION;
  }

  @Override
  protected List<Integer> getFixedLocationIds(PromotionExt event) {
    return event.getFixedLocationIds();
  }
}
