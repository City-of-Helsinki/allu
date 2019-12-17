package fi.hel.allu.external.mapper.event;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.external.domain.BigEventExt;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.servicecore.domain.EventJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BigEventExtMapper extends ApplicationExtMapper<BigEventExt> {

  private final EventExtensionMapper extensionMapper;

  @Autowired
  public BigEventExtMapper(EventExtensionMapper extensionMapper) {
    this.extensionMapper = extensionMapper;
  }

  @Override
  protected EventJson createExtension(BigEventExt event) {
    return extensionMapper.createBigEventExtension(event);
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  @Override
  protected ApplicationKind getApplicationKind(BigEventExt event) {
    return ApplicationKind.BIG_EVENT;
  }
}
