package fi.hel.allu.ui.security;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSecurityServiceTest {
  @Mock
  private UserService userService;
  @InjectMocks
  private ApplicationSecurityService securityService;

  @Test
  public void shouldAllowCreateApplicationWhenUserHasMatchingApplicationType() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_CREATE_APPLICATION), Collections.singletonList(ApplicationType.EVENT));
    Assert.assertTrue(securityService.canCreate(ApplicationType.EVENT));
  }

  @Test
  public void shouldNotAllowCreateApplicationWhenUserHasNoMatchingApplicationType() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_CREATE_APPLICATION), Collections.singletonList(ApplicationType.SHORT_TERM_RENTAL));
    Assert.assertFalse(securityService.canCreate(ApplicationType.EVENT));
  }

  @Test
  public void shouldNotAllowCreateApplicationWhenUserHasNoValidRole() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_VIEW), Collections.singletonList(ApplicationType.EVENT));
    Assert.assertFalse(securityService.canCreate(ApplicationType.EVENT));
  }

  @Test
  public void shouldAllowModifyingAllTagsWithCreateOrProcessRoles() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_PROCESS_APPLICATION), Collections.EMPTY_LIST);
    for (ApplicationTagType tagType : ApplicationTagType.values()) {
      Assert.assertTrue(securityService.canModifyTag(tagType));
    }
  }

  @Test
  public void shouldAllowModifySurveyRequiredWithSurveyRole() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_MANAGE_SURVEY), Collections.EMPTY_LIST);
    Assert.assertTrue(securityService.canModifyTag(ApplicationTagType.SURVEY_REQUIRED));
  }

  @Test
  public void shouldNotAllowModifyOtherTagTypesWithSurveyRole() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_MANAGE_SURVEY), Collections.EMPTY_LIST);
    Stream.of(ApplicationTagType.values())
        .filter(tagType -> tagType != ApplicationTagType.SURVEY_REQUIRED)
        .forEach(tagType -> Assert.assertFalse(securityService.canModifyTag(tagType)));
  }

  @Test
  public void shouldNotAllowModifyTagsWithOtherRoles() {
    setCurrentUser(Collections.singletonList(RoleType.ROLE_VIEW), Collections.EMPTY_LIST);
    Stream.of(ApplicationTagType.values())
        .forEach(tagType -> Assert.assertFalse(securityService.canModifyTag(tagType)));
  }

  private void setCurrentUser(List<RoleType> roles, List<ApplicationType> applicationTypes) {
    UserJson user = new UserJson();
    user.setAssignedRoles(roles);
    user.setAllowedApplicationTypes(applicationTypes);
    when(userService.getCurrentUser()).thenReturn(user);
  }
}
