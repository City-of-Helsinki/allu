package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;


import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApplicationTagControllerTest {

  @Mock
  private ApplicationServiceComposer applicationServiceComposer;
  private ApplicationTagController applicationTagController;


  private static final List<ApplicationTagType> ALLOWED_TAG_TYPES = Arrays.asList(
    ApplicationTagType.WAITING,
    ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
    ApplicationTagType.STATEMENT_REQUESTED,
    ApplicationTagType.COMPENSATION_CLARIFICATION,
    ApplicationTagType.PAYMENT_BASIS_CORRECTION,
    ApplicationTagType.SURVEY_REQUIRED,
    ApplicationTagType.OTHER_CHANGES,
    ApplicationTagType.DECISION_NOT_SENT,
    ApplicationTagType.CONTRACT_REJECTED
  );


  @ParameterizedTest
  @MethodSource("giveApprovalList")
  public void addTag(ApplicationTagType applicationTagType) {
    this.applicationTagController = new ApplicationTagController(applicationServiceComposer);
    applicationTagController.addTag(1, applicationTagType);
    verify(applicationServiceComposer, times(1)).addTag(eq(1), any());
  }


  private static Stream<ApplicationTagType> giveApprovalList(){
    return ALLOWED_TAG_TYPES.stream();
  }
}



