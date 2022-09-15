package fi.hel.allu.supervision.api.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.servicecore.domain.InvoiceJson;
import fi.hel.allu.servicecore.service.InvoiceService;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Invoices")
public class InvoiceController {

  @Autowired
  private InvoiceService invoiceService;

  @Operation(summary = "Get invoices of the application including invoiced invoices from replaced applications")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Invoices of application retrieved successfully",
          content = @Content(schema = @Schema(implementation = InvoiceJson.class))),
  })
  @RequestMapping(value = "/applications/{applicationId}/invoices", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<InvoiceJson>> getApplicationInvoices(@PathVariable Integer applicationId) {
    return new ResponseEntity<>(invoiceService.findByApplication(applicationId), HttpStatus.OK);
  }
}
