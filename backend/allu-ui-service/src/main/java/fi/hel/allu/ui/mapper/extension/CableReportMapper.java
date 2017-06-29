package fi.hel.allu.ui.mapper.extension;

import fi.hel.allu.model.domain.CableInfoEntry;
import fi.hel.allu.model.domain.CableReport;
import fi.hel.allu.ui.domain.CableInfoEntryJson;
import fi.hel.allu.ui.domain.CableReportJson;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CableReportMapper {
  public static CableReportJson modelToJson(CableReport cableReport) {
    CableReportJson cableReportJson = new CableReportJson();
    cableReportJson.setCableReportId(cableReport.getCableReportId());
    cableReportJson.setWorkDescription(cableReport.getWorkDescription());
    cableReportJson.setMapExtractCount(cableReport.getMapExtractCount());
    cableReportJson.setCableSurveyRequired(cableReport.isCableSurveyRequired());
    List<CableInfoEntryJson> infoEntries = Optional.ofNullable(cableReport.getInfoEntries())
            .orElse(Collections.emptyList()).stream().map(i -> createCableInfoEntryJson(i)).collect(Collectors.toList());
    cableReportJson.setInfoEntries(infoEntries);
    cableReportJson.setMapUpdated(cableReport.getMapUpdated());
    cableReportJson.setConstructionWork(cableReport.getConstructionWork());
    cableReportJson.setMaintenanceWork(cableReport.getMaintenanceWork());
    cableReportJson.setEmergencyWork(cableReport.getEmergencyWork());
    cableReportJson.setPropertyConnectivity(cableReport.getPropertyConnectivity());
    cableReportJson.setValidityTime(cableReport.getValidityTime());
    return ApplicationExtensionMapper.modelToJson(cableReport, cableReportJson);
  }

  public static CableReport jsonToModel(CableReportJson json) {
    CableReport cableReport = new CableReport();
    cableReport.setCableReportId(json.getCableReportId());
    cableReport.setWorkDescription(json.getWorkDescription());
    cableReport.setMapExtractCount(json.getMapExtractCount());
    cableReport.setCableSurveyRequired(json.getCableSurveyRequired());
    List<CableInfoEntry> infoEntries = Optional.ofNullable(json.getInfoEntries())
            .orElse(Collections.emptyList()).stream().map(i -> createCableInfoEntryModel(i)).collect(Collectors.toList());
    cableReport.setInfoEntries(infoEntries);
    cableReport.setMapUpdated(json.getMapUpdated());
    cableReport.setConstructionWork(json.getConstructionWork());
    cableReport.setMaintenanceWork(json.getMaintenanceWork());
    cableReport.setEmergencyWork(json.getEmergencyWork());
    cableReport.setPropertyConnectivity(json.getPropertyConnectivity());
    cableReport.setValidityTime(json.getValidityTime());
    return ApplicationExtensionMapper.jsonToModel(json, cableReport);
  }

  private static CableInfoEntryJson createCableInfoEntryJson(CableInfoEntry cableInfoEntry) {
    CableInfoEntryJson cableInfoEntryJson = new CableInfoEntryJson();
    cableInfoEntryJson.setType(cableInfoEntry.getType());
    cableInfoEntryJson.setAdditionalInfo(cableInfoEntry.getAdditionalInfo());
    return cableInfoEntryJson;
  }

  private static CableInfoEntry createCableInfoEntryModel(CableInfoEntryJson cableInfoEntryJson) {
    CableInfoEntry cableInfoEntry = new CableInfoEntry();
    cableInfoEntry.setType(cableInfoEntryJson.getType());
    cableInfoEntry.setAdditionalInfo(cableInfoEntryJson.getAdditionalInfo());
    return cableInfoEntry;
  }
}