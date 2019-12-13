package fi.hel.allu.external.api.controller.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.hel.allu.external.api.controller.BaseApplicationController;
import fi.hel.allu.external.domain.DecisionExt;
import fi.hel.allu.external.domain.InformationRequestResponseExt;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.external.mapper.event.PromotionExtMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/v1/events")
@Api(tags = "Events")
public class V1PromotionController extends BaseApplicationController<PromotionExt, PromotionExtMapper> {

  @Autowired
  private PromotionExtMapper promotionMapper;

  @Override
  protected PromotionExtMapper getMapper() {
    return promotionMapper;
  }

  @Override
  @Deprecated
  public ResponseEntity<Integer> create(@ApiParam(value = "Application data", required = true)
                                        @Valid @RequestBody PromotionExt applicationExt) throws JsonProcessingException {
    return super.create(applicationExt);
  }

  @Override
  @Deprecated
  public ResponseEntity<Integer> update(@ApiParam(value = "Id of the application to update.")
                                        @PathVariable Integer id,
                                        @ApiParam(value = "Application data", required = true)
                                        @Valid @RequestBody PromotionExt application) throws JsonProcessingException {
    return super.update(id, application);
  }

  @Override
  @Deprecated
  public ResponseEntity<Void> addResponse(@ApiParam(value = "Id of the application") @PathVariable("applicationid") Integer applicationId,
                                          @ApiParam(value = "Id of the information request") @PathVariable("requestid") Integer requestId,
                                          @ApiParam(value = "Content of the response") @RequestBody @Valid InformationRequestResponseExt<PromotionExt> response) throws JsonProcessingException {
    return super.addResponse(applicationId, requestId, response);
  }

  @Override
  @Deprecated
  public ResponseEntity<Void> reportChange(@ApiParam(value = "Id of the application") @PathVariable("applicationid") Integer applicationId,
                                           @ApiParam(value = "Contents of the change") @RequestBody @Valid InformationRequestResponseExt<PromotionExt> change) throws JsonProcessingException {
    return super.reportChange(applicationId, change);
  }

  @Override
  @Deprecated
  public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) throws IOException {
    return super.getDecision(id);
  }

  @Override
  @Deprecated
  public ResponseEntity<DecisionExt> getDecisionMetadata(@PathVariable Integer id)  {
    return super.getDecisionMetadata(id);
  }
}
