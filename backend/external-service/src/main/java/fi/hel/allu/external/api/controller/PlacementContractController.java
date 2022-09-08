package fi.hel.allu.external.api.controller;

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
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import fi.hel.allu.external.validation.DefaultImageValidator;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.servicecore.service.ContractService;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping({"/v1/placementcontracts", "/v2/placementcontracts"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Placement contracts")
public class PlacementContractController extends BaseApplicationController<PlacementContractExt,
        PlacementContractExtMapper> {


		private final PlacementContractExtMapper placementContractMapper;


		private final ContractService contractService;


		private final CommentService commentService;

		public PlacementContractController(ApplicationServiceExt applicationService,
																			 ApplicationExtGeometryValidator geometryValidator,
																			 DefaultImageValidator defaultImageValidator,
																			 DecisionService decisionService,
																			 TerminationService terminationService,
																			 PlacementContractExtMapper placementContractMapper,
																			 ContractService contractService,
																			 CommentService commentService) {
				super(applicationService, geometryValidator, defaultImageValidator, decisionService, terminationService);
				this.placementContractMapper = placementContractMapper;
				this.contractService = contractService;
				this.commentService = commentService;
		}

		@Override
		protected PlacementContractExtMapper getMapper() {
				return placementContractMapper;
		}

		@Operation(summary = "Gets contract proposal PDF for application with given ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Contract proposal retrieved successfully",
										content = @Content(schema = @Schema(implementation = byte.class))),
						@ApiResponse(responseCode = "404",
                    description = "No contract found for given application or contract is not in proposal state",
										content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
		})
		@GetMapping(value = "/{id}/contract/proposal", produces = "application/pdf")
		@PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
		public ResponseEntity<byte[]> getContractProposal(@PathVariable Integer id) throws IOException {
				Integer applicationId = applicationService.getApplicationIdForExternalId(id);
				applicationService.validateOwnedByExternalUser(applicationId);
				byte[] contract = contractService.getContractProposal(applicationId);
				List<byte[]> attachments = applicationService.getDecisionAttachmentDocuments(applicationId);
				return PdfResponseBuilder.createResponseEntity(PdfMerger.appendDocuments(contract, attachments));
		}

		@Operation(summary = "Gets final contract PDF for application with given ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Contract retrieved successfully",
										content = @Content(schema = @Schema(implementation = byte.class))),
						@ApiResponse(responseCode = "404",
                    description = "No contract found for given application or contract is still waiting decision",
										content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
		})
		@GetMapping(value = "/{id}/contract/final", produces = "application/pdf")
		@PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
		public ResponseEntity<byte[]> getFinalContract(@PathVariable Integer id) throws IOException {
				Integer applicationId = applicationService.getApplicationIdForExternalId(id);
				applicationService.validateOwnedByExternalUser(applicationId);
				byte[] contract = contractService.getFinalContract(applicationId);
				List<byte[]> attachments = applicationService.getDecisionAttachmentDocuments(applicationId);
				return PdfResponseBuilder.createResponseEntity(PdfMerger.appendDocuments(contract, attachments));
		}

		@Operation(summary = "Gets contract metadata for application with given ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Contract metadata retrieved successfully",
										content = @Content(schema = @Schema(implementation = ContractExt.class))),
						@ApiResponse(responseCode = "404", description = "No contract found for given application",
										content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
		})
		@GetMapping(value = "/{id}/contract/metadata")
		@PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
		public ResponseEntity<ContractExt> getContractMetadata(@PathVariable Integer id) {
				Integer applicationId = applicationService.getApplicationIdForExternalId(id);
				applicationService.validateOwnedByExternalUser(applicationId);
				ContractInfo contractInfo = contractService.getContractInfo(applicationId);
				if (contractInfo == null) {
						throw new NoSuchEntityException("contract.notFound");
				}
				UserExt handler = applicationService.getHandler(applicationId);
				UserExt decisionMaker = contractInfo.getStatus() == ContractStatusType.FINAL ?
                applicationService.getDecisionMaker(
								applicationId) : null;
				return ResponseEntity.ok(
								new ContractExt(handler, decisionMaker, contractInfo.getStatus(), contractInfo.getCreationTime()));

		}


		@Operation(summary = "Approve contract")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Contract approved successfully", content = @Content),
						@ApiResponse(responseCode = "400", description = "Invalid request data",
										content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
		})
		@PostMapping(value = "/{id}/contract/approved", produces = "application/json")
		@PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
		public ResponseEntity<Void> approve(
						@Parameter(description = "Application ID of the contract") @PathVariable Integer id,
						@Parameter(description = "Signing information")
						@Valid @RequestBody ContractSigningInfoExt signingInfo) {
				Integer applicationId = applicationService.getApplicationIdForExternalId(id);
				applicationService.validateOwnedByExternalUser(applicationId);
				contractService.approveContract(applicationId, signingInfo.getSigner(), signingInfo.getSigningTime());
				return new ResponseEntity<>(HttpStatus.OK);
		}

		@Operation(summary = "Reject contract")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Contract rejected successfully", content = @Content),
						@ApiResponse(responseCode = "400", description = "Invalid request data",
										content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
		})
		@PostMapping(value = "/{id}/contract/rejected", produces = "application/json")
		@PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
		public ResponseEntity<Void> reject(
						@Parameter(description = "Application ID of the contract") @PathVariable Integer id,
						@Parameter(description = "Reject reason", required = true) @NotBlank(message = "{contract.rejectreason}") @RequestBody String rejectReason) {
				Integer applicationId = applicationService.getApplicationIdForExternalId(id);
				applicationService.validateOwnedByExternalUser(applicationId);
				commentService.addApplicationComment(applicationId, new CommentJson(CommentType.EXTERNAL_SYSTEM,
                                                                            rejectReason));
				contractService.rejectContractProposal(applicationId, rejectReason);
				return new ResponseEntity<>(HttpStatus.OK);
		}

		@Operation(summary = "Gets termination document for application with given ID")
		@ApiResponses(value = {
						@ApiResponse(responseCode = "200", description = "Termination document retrieved successfully",
										content = @Content(schema = @Schema(implementation = byte.class))),
						@ApiResponse(responseCode = "404", description = "No termination document found for given application",
										content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
		})
		@GetMapping(value = "/{id}/termination", produces = "application/pdf")
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
