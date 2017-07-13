package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.servicecore.domain.ChangeHistoryItemJson;
import fi.hel.allu.servicecore.domain.FieldChangeJson;

import java.util.List;
import java.util.stream.Collectors;

public class ChangeHistoryMapper {

  /*
   * Map a change item from model space to UI space
   */
  public static ChangeHistoryItemJson mapToJson(ChangeHistoryItem c) {
    return new ChangeHistoryItemJson(c.getUserId(), c.getChangeType(), c.getNewStatus(), c.getChangeTime(),
        mapToJson(c.getFieldChanges()));
  }

  /*
   * Map a list of field changes from model space to UI space
   */
  private static List<FieldChangeJson> mapToJson(List<FieldChange> fieldChanges) {
    return fieldChanges.stream().map(fc -> mapToJson(fc)).collect(Collectors.toList());
  }

  /*
   * Map a single field change from model space to UI space
   */
  private static FieldChangeJson mapToJson(FieldChange fc) {
    return new FieldChangeJson(fc.getFieldName(), fc.getOldValue(), fc.getNewValue());
  }

}
