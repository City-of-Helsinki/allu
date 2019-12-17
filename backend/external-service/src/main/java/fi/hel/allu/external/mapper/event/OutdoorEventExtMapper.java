package fi.hel.allu.external.mapper.event;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.domain.OutdoorEventExt;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.servicecore.domain.EventJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutdoorEventExtMapper extends ApplicationExtMapper<OutdoorEventExt> {

  private final EventExtensionMapper extensionMapper;

  @Autowired
  public OutdoorEventExtMapper(EventExtensionMapper extensionMapper) {
    this.extensionMapper = extensionMapper;
  }

  @Override
  protected EventJson createExtension(OutdoorEventExt event) {
    return extensionMapper.createOutdoorEventExtension(event);
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  @Override
  protected ApplicationKind getApplicationKind(OutdoorEventExt event) {
    return ApplicationKind.OUTDOOREVENT;
  }

  @Override
  protected List<Integer> getFixedLocationIds(OutdoorEventExt application) {
    return application.getFixedLocationIds();
  }
}
