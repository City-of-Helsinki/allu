package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApprovalDocumentMapper extends DecisionJsonMapper {

  @Autowired
  public ApprovalDocumentMapper(LocationService locationService,
      CustomerService customerService,
      ContactService contactService,
      ChargeBasisService chargeBasisService,
      MetaService metaService) {
    super(locationService, customerService, contactService, chargeBasisService, metaService);
  }

  public DecisionJson mapApprovalDocument(ApplicationJson application,
      List<ChargeBasisEntry> chargeBasisEntries, boolean draft, ApprovalDocumentType type) {

    final DecisionJson approvalDocument = super.mapToDocumentJson(application, draft);
    fillCargeBasisInfo(approvalDocument, chargeBasisEntries);

    if (type == ApprovalDocumentType.OPERATIONAL_CONDITION) {
      approvalDocument.setWorkFinished(null);
      approvalDocument.setCustomerWorkFinished(null);
      approvalDocument.setGuaranteeEndTime(null);
    } else if (type == ApprovalDocumentType.WORK_FINISHED) {
      approvalDocument.setWinterTimeOperation(null);
      approvalDocument.setCustomerWinterTimeOperation(null);
    }
    return approvalDocument;
  }
}
