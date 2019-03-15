package fi.hel.allu.supervision.api.mapper;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.supervision.api.domain.ChargeBasisEntryJson;

public class ChargeBasisEntryMapper {

  public static ChargeBasisEntryJson mapToJson(ChargeBasisEntry entry) {
    ChargeBasisEntryJson json = new ChargeBasisEntryJson();
    json.setId(entry.getId());
    json.setType(entry.getType());
    json.setUnit(entry.getUnit());
    json.setQuantity(entry.getQuantity());
    json.setText(entry.getText());
    json.setExplanation(Optional.ofNullable(entry.getExplanation()).map(ex -> Arrays.asList(ex)).orElse(null));
    json.setUnitPrice(entry.getUnitPrice());
    json.setNetPrice(entry.getNetPrice());
    json.setInvoicingPeriodId(entry.getInvoicingPeriodId());
    json.setTag(entry.getTag());
    json.setReferredTag(entry.getReferredTag());
    json.setReferrable(entry.isReferrable());
    json.setEditable(entry.getManuallySet() && BooleanUtils.isNotTrue(entry.getLocked()));
    json.setInvoicable(entry.isInvoicable());
    return json;
  }
}
