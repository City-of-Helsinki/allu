package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for populating <code>ApplicationJson</code>s.
 */
@Service
public class ApplicationJsonService {

  private final ApplicationMapper applicationMapper;
  private final ProjectService projectService;
  private final UserService userService;
  private final AttachmentService attachmentService;
  private final CommentService commentService;
  private final LocationService locationService;
  private final TerminationService terminationService;
  private volatile Map<Integer, FixedLocationJson> fixedLocations;

  private final CodeSetService codeSetService;

  @Autowired
  public ApplicationJsonService(
      ApplicationMapper applicationMapper,
      ProjectService projectService,
      UserService userService,
      LocationService locationService,
      AttachmentService attachmentService,
      CommentService commentService,
      TerminationService terminationService,
      CodeSetService codeSetService) {
    this.applicationMapper = applicationMapper;
    this.projectService = projectService;
    this.userService = userService;
    this.attachmentService = attachmentService;
    this.commentService = commentService;
    this.locationService = locationService;
    this.terminationService = terminationService;
    this.fixedLocations = null;
    this.codeSetService = codeSetService;
  }

  /**
   * Returns fully populated application json i.e. having all related data structures like customer and project populated.
   *
   * @param   applicationModel  Application to be mapped to fully populated application json.
   * @return  fully populated application json.
   */
  public ApplicationJson getFullyPopulatedApplication(Application applicationModel) {
    ApplicationJson applicationJson = populateCommon(applicationModel);

    applicationJson.setHandler(applicationModel.getHandler() != null ? userService.findUserById(applicationModel.getHandler()) : null);
    applicationJson.setAttachmentList(attachmentService.findAttachmentsForApplication(applicationModel.getId()));

    return applicationJson;
  }

  public List<ApplicationJson> getCompactPopulatedApplicationList(List<Application> modelList) {
    return populateCommonList(modelList);
  }

  public List<ApplicationJson> populateCommonList(List<Application> modelList) {
    Map<Integer, CodeSet> codeSetMap = updateCustomersWithCodeSet(modelList);
    List<ApplicationJson> applicationJsons = modelList.stream()
            .map(application->applicationMapper.mapApplicationToJson(application, codeSetMap))
            .collect(Collectors.toList());

    applicationJsons = populateProjects(applicationJsons);
    applicationJsons = populateOwners(applicationJsons);
    applicationJsons = populateDecisionMakers(applicationJsons);
    applicationJsons.forEach(this::setAddress);

    Map<Integer, ApplicationJson> mappedApplications = applicationJsons.stream().collect(Collectors.toMap(
            ApplicationJson::getId, Function.identity()));

    populateComments(mappedApplications);
    populateTerminationTimes(mappedApplications);
    return new ArrayList<>(mappedApplications.values());
  }

  private Map<Integer, CodeSet> updateCustomersWithCodeSet(List<Application> modelList) {
    List<Integer> countryIds = collectCountryId(modelList);
    return codeSetService.findByIds(countryIds).stream()
            .collect(Collectors.toMap(CodeSet::getId, Function.identity()));

  }


  private void nullSafeAdd(Set<Integer> result, CustomerWithContacts customerWithContacts) {
    if (customerWithContacts != null && customerWithContacts.getCustomer() != null && customerWithContacts.getCustomer()
            .getCountryId() != null) {
      result.add(customerWithContacts.getCustomer().getCountryId());
    }
  }

  private List<Integer> collectCountryId(List<Application> applicationList) {
    Set<Integer> result = new LinkedHashSet<>();
    for (Application application : applicationList) {
      if (application.getClientApplicationData() != null) {
        ClientApplicationData client = application.getClientApplicationData();
        nullSafeAdd(result, client.getRepresentative());
        nullSafeAdd(result, client.getContractor());
        nullSafeAdd(result, client.getCustomer());

        if (null != client.getInvoicingCustomer() && null != client.getInvoicingCustomer().getCountryId())
          result.add(client.getInvoicingCustomer().getCountryId());
      }
      application.getCustomersWithContacts().parallelStream().forEach(e -> result.add(e.getCustomer().getCountryId()));
    }
    result.remove(null);
    return new ArrayList<>(result);
  }

  private <T extends IdInterface, U> List<U> populateValues(Map<Integer, List<U>> mappedApplications,
                                                            BiConsumer<U, T> setValue, List<T> listToPopulate) {
    List<U> result = new ArrayList<>();
    for (T jsonValue : listToPopulate) {
      List<U> applicationJsonList = mappedApplications.get(jsonValue.getId());
      applicationJsonList.forEach(applicationJson -> setValue.accept(applicationJson, jsonValue));
      result.addAll(applicationJsonList);
    }
    return result;
  }

  private List<ApplicationJson> populateProjects(List<ApplicationJson> listToPopulated) {
    Map<Integer, List<ApplicationJson>> projectIds = listToPopulated.stream().filter(e -> e.getProject() != null)
            .collect(Collectors.groupingBy(e -> e.getProject().getId()));
    List<ApplicationJson> populatedProjectIds = listToPopulated.stream().filter(e -> e.getProject() == null)
            .collect(Collectors.toList());
    List<ProjectJson> projectJsonList = projectService.findByIds(new ArrayList<>(projectIds.keySet()));
    populatedProjectIds.addAll(populateValues(projectIds, ApplicationJson::setProject, projectJsonList));
    return populatedProjectIds;
  }

  private List<ApplicationJson> populateDecisionMakers(List<ApplicationJson> listToPopulated) {
    Map<Integer, List<ApplicationJson>> decisionMakerIds = listToPopulated.stream()
            .filter(e -> e.getDecisionMaker() != null).collect(Collectors.groupingBy(e -> e.getOwner().getId()));
    List<ApplicationJson> populatedUserIds = listToPopulated.stream().filter(e -> e.getDecisionMaker() == null)
            .collect(Collectors.toList());
    List<UserJson> userJsonList = userService.findByIds(new ArrayList<>(decisionMakerIds.keySet()));
    populatedUserIds.addAll(populateValues(decisionMakerIds, ApplicationJson::setDecisionMaker, userJsonList));
    return populatedUserIds;
  }

  private List<ApplicationJson> populateOwners(List<ApplicationJson> listToPopulated) {
    Map<Integer, List<ApplicationJson>> userIds = listToPopulated.stream().filter(e -> e.getOwner() != null)
            .collect(Collectors.groupingBy(e -> e.getOwner().getId()));
    List<ApplicationJson> populatedUserIds = listToPopulated.stream().filter(e -> e.getOwner() == null)
            .collect(Collectors.toList());
    List<UserJson> userJsonList = userService.findByIds(new ArrayList<>(userIds.keySet()));
    populatedUserIds.addAll(populateValues(userIds, ApplicationJson::setOwner, userJsonList));
    return populatedUserIds;
  }

  private void populateComments(Map<Integer, ApplicationJson> mappedApplications) {
    List<Integer> applicationIds = new ArrayList<>(mappedApplications.keySet());
    List<Comment> commentJsonList = commentService.findByApplicationIds(applicationIds);
    Map<Integer, List<Comment>> mappedComments = commentJsonList.stream()
            .collect(Collectors.groupingBy(Comment::getApplicationId));
    mappedApplications.forEach((k, v) -> v.setComments(mapCommentToCommentJson(mappedComments.get(k))));
  }

  private List<CommentJson> mapCommentToCommentJson(List<Comment> commentList) {
    if (commentList == null || commentList.isEmpty()) {
      return null;
    } else {
      return commentList.stream().map(commentService::mapToJsonWithUserId).collect(Collectors.toList());
    }
  }

  private void populateTerminationTimes(Map<Integer, ApplicationJson> mappedApplications) {
    List<ApplicationJson> tempList = new ArrayList<>(mappedApplications.values());
    List<TerminationInfo> terminationInfoList = terminationService.getTerminationInfoList(tempList);
    Map<Integer, TerminationInfo> mapperTermination = terminationInfoList.stream().collect(Collectors.toMap(
            TerminationInfo::getApplicationId, Function.identity()));
    mappedApplications.forEach((k, v) -> addTerminationTimes(mapperTermination, v));
  }

  private void addTerminationTimes(Map<Integer, TerminationInfo> terminationTimes, ApplicationJson json) {
    Integer id = json.getId();
    if (terminationTimes.containsKey(id) && terminationTimes.get(id) != null) {
      json.setTerminationTime(terminationTimes.get(id).getExpirationTime());
    }
  }

  public ApplicationJson populateCommon(Application model) {
    ApplicationJson json = applicationMapper.mapApplicationToJson(model);

    Optional.ofNullable(model.getProjectId())
            .map(projectService::findById)
            .ifPresent(json::setProject);

    Optional.ofNullable(model.getOwner())
        .map(userService::findUserById)
        .ifPresent(json::setOwner);

    Optional.ofNullable(model.getDecisionMaker())
            .map(userService::findUserById)
            .ifPresent(json::setDecisionMaker);

    setAddress(json);

    json.setComments(commentService.findByApplicationId(model.getId()));

    json.setTerminationTime(terminationService.getTerminationTime(model));

    return json;
  }

  /**
   * If an application has a fixed location defined then address is name of the fixed location + sections.
   * Otherwise it is street address.
   */
  private void setAddress(ApplicationJson applicationJson) {
    if (applicationJson.getLocations() != null) {
      applicationJson.getLocations().stream().filter(loc -> (loc.getFixedLocationIds() != null))
        .forEachOrdered(loc -> {
          String address = "";
          List<FixedLocationJson> localFixedLocations = loc.getFixedLocationIds().stream().map(id -> fixedLocations()
                  .get(id)).collect(Collectors.toList());
          address = getFixedLocationAddresses(localFixedLocations);
          if (!address.isEmpty()) {
            loc.setAddress(address);
          } else {
            loc.setAddress(Optional.ofNullable(loc.getPostalAddress()).map(
                    PostalAddressJson::getStreetAddress).orElse(null));
          }
        }
      );
    }
  }

  private String getFixedLocationAddresses(List<FixedLocationJson> localFixedLocations) {
    return Printable.forAreasWithSections(getAreasWithSections(localFixedLocations));
  }

  private static Map<String, List<String>> getAreasWithSections(List<FixedLocationJson> localFixedLocations) {
    return localFixedLocations.stream().
        collect(Collectors.groupingBy(FixedLocationJson::getArea,
            Collectors.mapping(FixedLocationJson::getSection, Collectors.toList())));
  }

  private Map<Integer, FixedLocationJson> fixedLocations() {
    Map<Integer, FixedLocationJson> localFixedLocations = this.fixedLocations;
    if (localFixedLocations == null) {
      synchronized (ApplicationJsonService.class) {
        localFixedLocations = this.fixedLocations;
        if (localFixedLocations == null) {
          this.fixedLocations = localFixedLocations = locationService.getAllFixedLocations().stream()
                  .collect(Collectors.toMap(FixedLocationJson::getId, item -> item));
        }
      }
    }
    return localFixedLocations;
  }
}