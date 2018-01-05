package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.QueryParameterJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.servicecore.domain.UserJson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import java.util.*;

public class WorkQueueServiceTest {

  private static final String TEST_USER = "testuser";
  private UserJson userJson =
      new UserJson(
          1,
          TEST_USER,
          "foo bar",
          "foo@bar.fi",
          "titteli",
          true,
          null,
          Collections.emptyList(),
          Collections.emptyList(),
          Collections.emptyList());
  private ApplicationServiceComposer applicationServiceComposer;
  private UserService userService;
  private WorkQueueService workQueueService;

  private ArgumentCaptor<QueryParametersJson> queryParametersArgumentCaptor = ArgumentCaptor.forClass(QueryParametersJson.class);
  private List<ApplicationJson> emptyList = Collections.emptyList();

  @Before
  public void init() {
    applicationServiceComposer = Mockito.mock(ApplicationServiceComposer.class);
    userService = Mockito.mock(UserService.class);
    workQueueService = new WorkQueueService(applicationServiceComposer, userService);

    userJson.setAssignedRoles(Arrays.asList(RoleType.ROLE_VIEW, RoleType.ROLE_PROCESS_APPLICATION));
    userJson.setAllowedApplicationTypes(Arrays.asList(ApplicationType.EVENT));
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);
    Mockito.when(applicationServiceComposer.search(Mockito.any(QueryParametersJson.class), Mockito.any(Pageable.class)))
        .thenReturn(emptyList);
  }

  @Test
  public void testSearchSharedByGroup() {

    Mockito.when(userService.findUserByUserName(TEST_USER)).thenReturn(userJson);

    List<ApplicationJson> result = workQueueService.searchSharedByGroup(new QueryParametersJson(), null);

    Mockito.verify(applicationServiceComposer).search(queryParametersArgumentCaptor.capture(), Mockito.any());
    QueryParametersJson searchQuery = queryParametersArgumentCaptor.getValue();

    Assert.assertEquals(emptyList, result);
    Assert.assertEquals(2, searchQuery.getQueryParameters().size());
    Assert.assertTrue(searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals(QueryParameter.FIELD_NAME_APPLICATION_TYPE)).findFirst() != null);
    QueryParameterJson queryParameterJson = searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals(QueryParameter.FIELD_NAME_APPLICATION_TYPE)).findFirst()
        .orElseThrow(() -> new RuntimeException("value not set"));
    Assert.assertEquals(ApplicationType.EVENT.name(), queryParameterJson.getFieldMultiValue().get(0));
    queryParameterJson = searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals(QueryParameter.FIELD_NAME_STATUS)).findFirst()
        .orElseThrow(() -> new RuntimeException("value not set"));
    HashSet<String> compSet = new HashSet<>(Arrays.asList(
            StatusType.PRE_RESERVED.name(),
            StatusType.PENDING.name(),
            StatusType.HANDLING.name(),
            StatusType.RETURNED_TO_PREPARATION.name()));
    Assert.assertEquals(compSet.size(), queryParameterJson.getFieldMultiValue().size());
    Assert.assertTrue(compSet.containsAll(queryParameterJson.getFieldMultiValue()));
  }

  @Test
  public void testSearchSharedByGroupWithParameters() {

    QueryParametersJson queryParametersJson = new QueryParametersJson();
    QueryParameterJson dummyParameter = new QueryParameterJson("dummy", Collections.emptyList());
    QueryParameterJson typeParameter = new QueryParameterJson(QueryParameter.FIELD_NAME_STATUS, Collections.singletonList(StatusType.REJECTED.name()));
    queryParametersJson.setQueryParameters(new ArrayList<>(Arrays.asList(dummyParameter, typeParameter)));
    List<ApplicationJson> result = workQueueService.searchSharedByGroup(queryParametersJson, null);

    Mockito.verify(applicationServiceComposer).search(queryParametersArgumentCaptor.capture(), Mockito.any());
    QueryParametersJson searchQuery = queryParametersArgumentCaptor.getValue();

    Assert.assertEquals(emptyList, result);
    Assert.assertEquals(3, searchQuery.getQueryParameters().size());

    Assert.assertTrue(searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals("dummy")).findFirst()
        .orElseThrow(() -> new RuntimeException("value not set")) != null);

    Assert.assertTrue(searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals(QueryParameter.FIELD_NAME_APPLICATION_TYPE)).findFirst()
        .orElseThrow(() -> new RuntimeException("value not set")) != null);
    QueryParameterJson queryParameterJson = searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals(QueryParameter.FIELD_NAME_APPLICATION_TYPE)).findFirst()
        .orElseThrow(() -> new RuntimeException("value not set"));
    Assert.assertEquals(ApplicationType.EVENT.name(), queryParameterJson.getFieldMultiValue().get(0));

    queryParameterJson = searchQuery.getQueryParameters().stream().filter(
        qp -> qp.getFieldName().equals(QueryParameter.FIELD_NAME_STATUS)).findFirst()
        .orElseThrow(() -> new RuntimeException("value not set"));
    HashSet<String> compSet = new HashSet<>(
        Arrays.asList(StatusType.PRE_RESERVED.name(),
            StatusType.PENDING.name(),
            StatusType.HANDLING.name(),
            StatusType.RETURNED_TO_PREPARATION.name(),
            StatusType.REJECTED.name()));
    Assert.assertEquals(compSet.size(), queryParameterJson.getFieldMultiValue().size());
    Assert.assertTrue(compSet.containsAll(queryParameterJson.getFieldMultiValue()));
  }

}
