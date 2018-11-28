package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.TrafficArrangementImageExt;
import fi.hel.allu.servicecore.service.AttachmentService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/trafficarrangementimages")
@Api(value = "v1/trafficarrangementimages")
public class TrafficArrangementImageController {

  @Autowired
  private AttachmentService attachmentService;

  @ApiOperation(value = "Lists traffic arrangement images for given application type",
      produces = "application/json",
      response = TrafficArrangementImageExt.class,
      responseContainer="List",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<TrafficArrangementImageExt>> getAllImages(@ApiParam(value = "Application type", required = true)
                                                      @RequestParam(required = true) ApplicationType applicationType) {
    List<TrafficArrangementImageExt> images = attachmentService.getDefaultImagesForApplicationType(applicationType).stream()
        .map(a -> new TrafficArrangementImageExt(a.getId(), a.getName())).collect(Collectors.toList());
    return ResponseEntity.ok(images);
  }

  @ApiOperation(value = "Gets traffic arrangement image PDF for given ID.",
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Image retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No image found for ID", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
    byte[] data = attachmentService.getDefaultImage(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }
}
