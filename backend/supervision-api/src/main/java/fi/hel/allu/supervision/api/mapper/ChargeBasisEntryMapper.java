package fi.hel.allu.supervision.api.mapper;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.supervision.api.domain.ChargeBasisEntryJson;

public class ChargeBasisEntryMapper {

  public static ChargeBasisEntryJson mapToJson(ChargeBasisEntry entry) {
    ChargeBasisEntryJson json = new ChargeBasisEntryJson();
    json.setId(entry.getId());
    json.setType(entry.getType());
    json.setUnit(entry.getUnit());
    if (!(entry.getType() == ChargeBasisType.DISCOUNT && entry.getUnit() == ChargeBasisUnit.PIECE)) {
      json.setQuantity(entry.getQuantity());
    }
    json.setText(entry.getText());
    json.setExplanation(Optional.ofNullable(entry.getExplanation()).map(ex -> Arrays.asList(ex)).orElse(null));
    if (entry.getUnit() != ChargeBasisUnit.PERCENT) {
      json.setUnitPrice(entry.getUnitPrice());
      json.setNetPrice(entry.getNetPrice());
    }
    json.setInvoicingPeriodId(entry.getInvoicingPeriodId());
    json.setTag(entry.getTag());
    json.setReferredTag(entry.getReferredTag());
    json.setReferrable(entry.isReferrable());
    json.setEditable(entry.getManuallySet() && BooleanUtils.isNotTrue(entry.getLocked()));
    json.setLocked(entry.getLocked() != null && entry.getLocked());
    json.setInvoicable(entry.isInvoicable());
    return json;
  }

  public static ChargeBasisEntry mapToModel(ChargeBasisEntryJson json) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setType(json.getType());
    entry.setUnit(json.getUnit());
    if (json.getType() == ChargeBasisType.DISCOUNT && json.getUnit() == ChargeBasisUnit.PIECE) {
      entry.setQuantity(1);
    } else {
      entry.setQuantity(json.getQuantity());
    }
    entry.setText(json.getText());
    entry.setExplanation(Optional.ofNullable(json.getExplanation()).map(e -> e.toArray(new String[e.size()])).orElse(null));
    if (json.getUnit() != ChargeBasisUnit.PERCENT) {
      entry.setUnitPrice(json.getUnitPrice());
      entry.setNetPrice(json.getNetPrice());
    } else {
      entry.setUnitPrice(0);
      entry.setNetPrice(0);
    }
    entry.setReferredTag(json.getReferredTag());
    return entry;
  }
}
