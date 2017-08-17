package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CableReportMapper {
  public static CableReportJson modelToJson(Application application) {
    CableReport cableReport = (CableReport) application.getExtension();
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
    cableReportJson.setOrdererIndex(createOrdererIndex(cableReport.getOrderer(), application.getCustomersWithContacts()));
    return ApplicationExtensionMapper.modelToJson(cableReport, cableReportJson);
  }

  public static CableReport jsonToModel(CableReportJson json, List<CustomerWithContactsJson> customersWithContactsJson) {
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
    cableReport.setOrderer(getOrdererJson(json.getOrdererIndex(), customersWithContactsJson).map(contact -> contact.getId()).orElse(null));
    return ApplicationExtensionMapper.jsonToModel(json, cableReport);
  }

  /**
   * Method for getting orderer (contact) from customer by given index
   * @param index orderer's index
   * @param cwc CustomerWithContactsJson structure containing all customers contacts
   * @return orderer (contact) from given index
   */
  public static ContactJson getOrdererJson(Integer index, CustomerWithContactsJson cwc) {
    return cwc.getContacts().get(index);
  }

  /**
   * Method for getting customer structure which contains orderer
   * @param ordererIndex OrdererIndexJson which identifies orderer from customer structure
   * @param customersWithContactsJson list of CustomerWithContactsJson
   * @return Optional of CustomerWithContactsJson which contains orderer or empty Optional when not found
   */
  public static Optional<CustomerWithContactsJson> getCustomerJsonWithOrderer(OrdererIndexJson ordererIndex,
                                                                              List<CustomerWithContactsJson> customersWithContactsJson) {
    return Optional.ofNullable(ordererIndex)
        .flatMap(index -> customersWithContactsJson.stream()
          .filter(cwc -> cwc.getRoleType().equals(index.getCustomerRoleType()))
          .findFirst());
  }

  private static Optional<ContactJson> getOrdererJson(OrdererIndexJson ordererIndex, List<CustomerWithContactsJson> customersWithContactsJson) {
    return Optional.ofNullable(ordererIndex)
        .flatMap(index -> getCustomerJsonWithOrderer(index, customersWithContactsJson))
        .map(cwc -> getOrdererJson(ordererIndex.getIndex(), cwc));
  }

  private static OrdererIndexJson createOrdererIndex(Integer ordererId, List<CustomerWithContacts> customersWithContacts) {
    Optional<CustomerWithContacts> customerWithOrderer = Optional.ofNullable(ordererId)
        .flatMap(id -> customersWithContacts.stream()
        .filter(cwc -> cwc.getContacts().stream().anyMatch(c -> c.getId() == id))
        .findFirst());

    return customerWithOrderer
        .map(customer -> createOrdererIndex(ordererId, customer))
        .orElse(null);
  }

  private static OrdererIndexJson createOrdererIndex(Integer ordererId, CustomerWithContacts customerWithContacts) {
    OrdererIndexJson indexJson = new OrdererIndexJson();
    indexJson.setCustomerRoleType(customerWithContacts.getRoleType());

    List<Contact> contacts = customerWithContacts.getContacts();
    Integer index = IntStream.range(0, contacts.size())
        .filter(i -> ordererId.equals(contacts.get(i).getId()))
        .findFirst().orElseThrow(() -> new IllegalStateException("No orderer found from cable report"));

    indexJson.setIndex(index);
    return indexJson;
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