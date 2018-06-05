package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.ChangeHistoryItemInfo;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.servicecore.domain.ChangeHistoryItemInfoJson;
import fi.hel.allu.servicecore.domain.ChangeHistoryItemJson;
import fi.hel.allu.servicecore.domain.FieldChangeJson;
import fi.hel.allu.servicecore.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ChangeHistoryMapper {

  private final UserService userService;

  @Autowired
  public ChangeHistoryMapper(@Lazy UserService userService) {
    this.userService = userService;
  }

  /*
   * Map a change item from model space to UI space
   */
  public ChangeHistoryItemJson mapToJson(ChangeHistoryItem c) {
    return new ChangeHistoryItemJson(
        Optional.ofNullable(c.getUserId()).map(userId -> userService.findUserById(userId)).orElse(null),
        mapToJson(c.getInfo()),
        c.getChangeType(),
        c.getNewStatus(),
        c.getChangeTime(),
        mapToJson(c.getFieldChanges()));
  }

  /*
   * Map a list of field changes from model space to UI space
   */
  private List<FieldChangeJson> mapToJson(List<FieldChange> fieldChanges) {
    return fieldChanges.stream().map(fc -> mapToJson(fc)).collect(Collectors.toList());
  }

  /*
   * Map a single field change from model space to UI space
   */
  private FieldChangeJson mapToJson(FieldChange fc) {
    return new FieldChangeJson(fc.getFieldName(), fc.getOldValue(), fc.getNewValue());
  }

  private ChangeHistoryItemInfoJson mapToJson(ChangeHistoryItemInfo info) {
    if (info == null) {
      return null;
    }
    final ChangeHistoryItemInfoJson infoJson = new ChangeHistoryItemInfoJson();
    infoJson.setId(info.getId());
    infoJson.setName(info.getName());
    infoJson.setApplicationId(info.getApplicationId());
    return infoJson;
  }
}
