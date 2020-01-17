package fi.hel.allu.external.api.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.domain.types.ContractStatusType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.util.PdfMerger;
import fi.hel.allu.external.domain.ContractExt;
import fi.hel.allu.external.domain.ContractSigningInfoExt;
import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.external.domain.UserExt;
import fi.hel.allu.external.mapper.PlacementContractExtMapper;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.servicecore.service.ContractService;
import io.swagger.annotations.*;

@RestController
@RequestMapping({"/v1/placementcontracts", "/v2/placementcontracts"})
@Api(tags = "Placement contracts")
public class PlacementContractController extends BaseApplicationController<PlacementContractExt, PlacementContractExtMapper> {

  @Autowired
  private PlacementContractExtMapper placementContractMapper;

  @Autowired
  private ContractService contractService;

  @Autowired
  private CommentService commentService;

  @Override
  protected PlacementContractExtMapper getMapper() {
    return placementContractMapper;
  }

  @ApiOperation(value = "Gets contract proposal PDF for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Contract proposal retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No contract found for given application or contract is not in proposal state", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/contract/proposal", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getContractProposal(@PathVariable Integer id) throws IOException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] contract= contractService.getContractProposal(applicationId);
    List<byte[]> attachments = applicationService.getDecisionAttachmentDocuments(applicationId);
    return PdfResponseBuilder.createResponseEntity(PdfMerger.appendDocuments(contract, attachments));
  }

  @ApiOperation(value = "Gets final contract PDF for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Contract retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No contract found for given application or contract is still waiting decision", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/contract/final", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getFinalContract(@PathVariable Integer id) throws IOException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] contract = contractService.getFinalContract(applicationId);
    List<byte[]> attachments = applicationService.getDecisionAttachmentDocuments(applicationId);
    return PdfResponseBuilder.createResponseEntity(PdfMerger.appendDocuments(contract, attachments));
  }

  @ApiOperation(value = "Gets contract metadata for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      response = ContractExt.class)
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Contract metadata retrieved successfully", response = ContractExt.class),
      @ApiResponse(code = 404, message = "No contract found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/contract/metadata", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ContractExt> getContractMetadata(@PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    ContractInfo contractInfo = contractService.getContractInfo(applicationId);
    if (contractInfo == null) {
      throw new NoSuchEntityException("contract.notFound");
    }
    UserExt handler = applicationService.getHandler(applicationId);
    UserExt decisionMaker = contractInfo.getStatus() == ContractStatusType.FINAL ? applicationService.getDecisionMaker(applicationId) : null;
    return ResponseEntity.ok(new ContractExt(handler, decisionMaker, contractInfo.getStatus(), contractInfo.getCreationTime()));

  }


  @ApiOperation(value = "Approve contract",
      produces = "application/json",
      consumes = "application/json",
      response = Void.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Contract approved successfully", response = Void.class),
      @ApiResponse(code = 400, message = "Invalid request data", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/contract/approved", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> approve(@ApiParam(value = "Application ID of the contract") @PathVariable Integer id,
                                      @ApiParam(value = "Signing information")
                                      @Valid @RequestBody ContractSigningInfoExt signingInfo) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    contractService.approveContract(applicationId, signingInfo.getSigner(), signingInfo.getSigningTime());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Reject contract",
      produces = "application/json",
      response = Void.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Contract rejected successfully", response = Void.class),
      @ApiResponse(code = 400, message = "Invalid request data", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/contract/rejected", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reject(@ApiParam(value = "Application ID of the contract") @PathVariable Integer id,
                                     @ApiParam(value = "Reject reason", required = true) @NotBlank(message = "{contract.rejectreason}") @RequestBody String rejectReason) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    commentService.addApplicationComment(applicationId, new CommentJson(CommentType.EXTERNAL_SYSTEM, rejectReason));
    contractService.rejectContractProposal(applicationId, rejectReason);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Gets termination document for application with given ID",
    authorizations = @Authorization(value ="api_key"),
    response = byte.class,
    responseContainer = "Array")
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Termination document retrieved successfully", response = byte.class, responseContainer = "Array"),
    @ApiResponse(code = 404, message = "No termination document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/termination", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getTermination(@PathVariable Integer id) {
    return getTerminationDocument(id);
  }

  @Override
  protected List<byte[]> getDecisionAttachments(Integer applicationId) {
    // For placement contract attachments are for contracts, not for decisions
    return Collections.emptyList();
  }

}
