package fi.hel.allu.external.mapper.event;

import java.util.List;
import java.util.Optional;

import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.servicecore.domain.EventJson;

@Component
public class PromotionExtMapper extends ApplicationExtMapper<PromotionExt> {

  private final EventExtensionMapper extensionMapper;

  @Autowired
  public PromotionExtMapper(EventExtensionMapper extensionMapper) {
    this.extensionMapper = extensionMapper;
  }

  @Override
  protected EventJson createExtension(PromotionExt event) {
    return extensionMapper.createPromotionExtension(event);
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
