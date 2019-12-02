package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.servicecore.validation.ValidationMessageTranslator;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.servicecore.domain.BulkApprovalEntryJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BulkApprovalServiceTest {

  @Mock
  private ApplicationService applicationService;
  @Mock
  private ApplicationMapper applicationMapper;
  @Mock
  private ApplicationHistoryService applicationHistoryService;
  @Mock
  private ValidationMessageTranslator validationMessageTranslator;


  private BulkApprovalService bulkApprovalService;

  private List<Application> applications;

  @Before
  public void setup() {
    bulkApprovalService = new BulkApprovalService(applicationService, applicationMapper, applicationHistoryService, validationMessageTranslator);

    applications = Arrays.asList(
      application(1, ApplicationType.EVENT, StatusType.DECISIONMAKING),
      application(2, ApplicationType.EVENT, StatusType.HANDLING),
      application(3, ApplicationType.EXCAVATION_ANNOUNCEMENT, StatusType.DECISIONMAKING)
    );

    when(applicationService.findApplicationsById(anyList())).thenReturn(applications);
    when(applicationHistoryService.hasStatusInHistory(anyInt(), any(StatusType.class))).thenReturn(false);
  }

  @Test
  public void shouldReturnEntriesForApplicationsInDecisionmaking() {
    List<BulkApprovalEntryJson> entries = bulkApprovalService.getBulkApprovalEntries(Arrays.asList(1, 2, 3));

    assertEquals(2, entries.size());
    assertTrue(entries.stream().anyMatch(e -> e.getId() == applications.get(0).getId()));
    assertTrue(entries.stream().anyMatch(e -> e.getId() == applications.get(2).getId()));
  }

  @Test
  public void shouldMarkApplicationsWithOperationalConditionInHistoryAsBlocked() {
    List<Application> moreApplications = new ArrayList<>(applications);
    Application replacingWithOperationalCondition = application(5, ApplicationType.EXCAVATION_ANNOUNCEMENT, StatusType.DECISIONMAKING);
    replacingWithOperationalCondition.setReplacesApplicationId(6);
    moreApplications.add(replacingWithOperationalCondition);

    when(applicationService.findApplicationsById(anyList())).thenReturn(moreApplications);
    when(applicationHistoryService.hasStatusInHistory(
      eq(replacingWithOperationalCondition.getId()), eq(StatusType.OPERATIONAL_CONDITION))).thenReturn(true);

    List<BulkApprovalEntryJson> entries = bulkApprovalService.getBulkApprovalEntries(Arrays.asList(1, 2, 3, 5));

    // Should be checked for replacing application
    verify(applicationHistoryService, times(1)).hasStatusInHistory(anyInt(), eq(StatusType.OPERATIONAL_CONDITION));

    assertEquals(3, entries.size());
    assertTrue(entries.stream().anyMatch(e -> e.getId() == applications.get(0).getId()));
    assertTrue(entries.stream().anyMatch(e -> e.getId() == applications.get(2).getId()));
    assertTrue(entries.stream().anyMatch(e ->
      e.getId() == replacingWithOperationalCondition.getId() && e.getBulkApprovalBlocked()));
  }

  private Application application(Integer id, ApplicationType type, StatusType status) {
    Application application =  new Application();
    application.setId(id);
    application.setType(type);
    application.setStatus(status);
    application.setDecisionDistributionList(Collections.singletonList(createDistributionEntry()));
    return application;
  }

  private DistributionEntry createDistributionEntry() {
    DistributionEntry entry = new DistributionEntry();
    entry.setId(5);
    entry.setDistributionType(DistributionType.EMAIL);
    entry.setEmail("email.some@place.fi");
    return entry;
  }
}
