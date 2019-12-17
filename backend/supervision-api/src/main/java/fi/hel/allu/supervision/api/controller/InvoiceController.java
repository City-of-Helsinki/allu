package fi.hel.allu.supervision.api.controller;

import java.util.List;

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
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api(tags = "Invoices")
public class InvoiceController {

  @Autowired
  private InvoiceService invoiceService;

  @ApiOperation(value = "Get invoices of the application including invoiced invoices from replaced applications",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = InvoiceJson.class,
      responseContainer="List"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Invoices of application retrieved successfully", response = InvoiceJson.class, responseContainer="List"),
  })
  @RequestMapping(value = "/applications/{applicationId}/invoices", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<InvoiceJson>> getApplicationInvoices(@PathVariable Integer applicationId) {
    return new ResponseEntity<>(invoiceService.findByApplication(applicationId), HttpStatus.OK);
  }
}
