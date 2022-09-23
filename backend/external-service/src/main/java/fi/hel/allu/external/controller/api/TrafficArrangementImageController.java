package fi.hel.allu.external.controller.api;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.TrafficArrangementImageExt;
import fi.hel.allu.servicecore.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/v1/trafficarrangementimages", "/v2/trafficarrangementimages"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Traffic arrangement images")
public class TrafficArrangementImageController {

    private final AttachmentService attachmentService;

    public TrafficArrangementImageController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Lists traffic arrangement images for given application type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TrafficArrangementImageExt.class))),
            @ApiResponse(responseCode = "404", description = "No images for given type", content = @Content)
    })
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<List<TrafficArrangementImageExt>> getAllImages(
            @Parameter(description = "Application type", required = true) @RequestParam
            ApplicationType applicationType) {
        List<TrafficArrangementImageExt> images = attachmentService.getDefaultImagesForApplicationType(applicationType)
                .stream().map(a -> new TrafficArrangementImageExt(a.getId(), a.getName())).collect(Collectors.toList());
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "Gets traffic arrangement image PDF for given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
                    content = @Content(schema = @Schema(implementation = byte.class))),
            @ApiResponse(responseCode = "404", description = "No image found for ID",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}", produces = "application/pdf")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        byte[] data = attachmentService.getDefaultImage(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
    }
}
