package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.external.mapper.ShortTermRentalExtMapper;
import fi.hel.allu.external.validation.ShortTermRentalExtValidator;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/shorttermrentals")
@Api(value = "v1/shorttermrentals")
public class ShortTermRentalController extends BaseApplicationController<ShortTermRentalExt, ShortTermRentalExtMapper> {

  @Autowired
  private ShortTermRentalExtMapper shortTermRentalMapper;

  @Autowired
  private ShortTermRentalExtValidator validator;

  @Override
  protected void addApplicationTypeSpecificValidators(WebDataBinder binder) {
    binder.addValidators(validator);
  }

  @Override
  protected ShortTermRentalExtMapper getMapper() {
    return shortTermRentalMapper;
  }

}
