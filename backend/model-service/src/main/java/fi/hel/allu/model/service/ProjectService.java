package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.util.ObjectComparer;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.changehistory.ApplicationChange;
import fi.hel.allu.model.domain.changehistory.ContactChange;
import fi.hel.allu.model.domain.changehistory.CustomerChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Business logic for project (hanke) related functionality.
 */
@Service
public class ProjectService {

  private static final List<ChangeType> infoChangeTypes = Arrays.asList(ChangeType.STATUS_CHANGED);

  private final ProjectDao projectDao;
  private final ApplicationDao applicationDao;
  private final LocationDao locationDao;
  private final HistoryDao historyDao;
  private final ObjectComparer objectComparer;
  private final CustomerDao customerDao;
  private final ContactDao contactDao;

  @Autowired
  public ProjectService(ProjectDao projectDao,
      ApplicationDao applicationDao,
      LocationDao locationDao,
      HistoryDao historyDao,
      CustomerDao customerDao,
      ContactDao contactDao) {
    this.projectDao = projectDao;
    this.applicationDao = applicationDao;
    this.locationDao = locationDao;
    this.historyDao = historyDao;
    this.customerDao = customerDao;
    this.contactDao = contactDao;
    this.objectComparer = new ObjectComparer();
  }

  @Transactional(readOnly = true)
  public Project find(int id) {
      return findProject(id, "Project not found");
  }

  /**
   * Find projects by ids.
   *
   * @param   ids   Ids of projects to be fetched.
   * @return  List of projects.
   */
  public List<Project> findByIds(List<Integer> ids) {
    return projectDao.findByIds(ids);
  }

  @Transactional(readOnly = true)
  public List<Project> findProjectChildren(int id) {
    return projectDao.findProjectChildren(id);
  }

  /**
   * Find all projects, with paging
   *
   * @param pageRequest the paging request
   * @return a page of projects
   */
  @Transactional(readOnly = true)
  public Page<Project> findAll(Pageable pageRequest) {
    return projectDao.findAll(pageRequest);
  }

  /**
   * Resolve parent and grandparents of the given project.
   *
   * @param projectId Project id whose parents should be resolved.
   * @return List of projects, which has the given project as first item and
   *         most grandparent project as last item.
   */
  @Transactional(readOnly = true)
  public List<Project> findProjectParents(Integer projectId) {
    ArrayList<Project> resolvedProjects = new ArrayList<>();
    Project currentProject = findProject(projectId, "Project not found");
    resolvedProjects.add(currentProject);
    if (currentProject.getParentId() == null) {
      return resolvedProjects;
    } else {
      resolvedProjects.addAll(findProjectParents(currentProject.getParentId()));
      return resolvedProjects;
    }
  }

  /**
   * Insert given project to database.
   *
   * @param   project Project to be inserted.
   * @return  Inserted project.
   */
  @Transactional
  public Project insert(Project project, int userId) {
    if (project.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    final Project insertedProject = projectDao.insert(project);
    addChangeItem(insertedProject.getId(), userId, null, insertedProject, ChangeType.CREATED);
    return insertedProject;
  }

  /**
   * Update given project to database.
   *
   * @param   id Id of the project to be updated.
   * @param   project Project to be updated.
   * @return  Updated project.
   */
  @Transactional
  public Project update(int id, Project project, int userId) {
    project.setId(id);
    final Project currentProject = findProject(id, "Tried to update non-existent project");

    if (!Objects.equals(currentProject.getParentId(), project.getParentId())) {
      updateProjectParent(project.getId(), project.getParentId(), userId);
    }

    // These values are updated from current project since
    // they should be updated when related applications / projects change
    // TODO: refactor project updating logic to handle this better
    project.setStartTime(currentProject.getStartTime());
    project.setEndTime(currentProject.getEndTime());
    project.setCityDistricts(currentProject.getCityDistricts());
    project.setParentId(currentProject.getParentId());
    final Project updatedProject = projectDao.update(id, project);
    addProjectChangeItems(userId, currentProject, updatedProject);
    return updatedProject;
  }

  /**
   * Finds applications by project.
   *
   * @param   id    Id of the project whose applications are fetched.
   * @return  List of applications under given project. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public List<Application> findApplicationsByProject(int id) {
    return applicationDao.findByProject(id);
  }

  @Transactional
  public List<Integer> addApplications(int id, List<Integer> applicationIds, int userId) {
    Set<Integer> changedProjects = new HashSet<>();
    changedProjects.add(id);
    changedProjects.addAll(getRelatedProjects(applicationIds));

    applicationIds.forEach((appId) -> {
      final Application application = applicationDao.findById(appId);
      if (application != null) {
        if (application.getProjectId() != null) {
          addChangeItem(application.getProjectId(), userId, new ApplicationChange(application), null, ChangeType.APPLICATION_REMOVED);
        }
        addChangeItem(id, userId, null, new ApplicationChange(application), ChangeType.APPLICATION_ADDED);
      }
    });

    applicationDao.updateProject(id, applicationIds);
    updateProjectInformation(new ArrayList<>(changedProjects), userId);

    return applicationIds;
  }

  @Transactional
  public void removeApplication(int applicationId, int userId) {
    final Application app = applicationDao.findById(applicationId);
    applicationDao.updateProject(null, Collections.singletonList(applicationId));
    updateProjectInformation(Collections.singletonList(app.getProjectId()), userId);
    addChangeItem(app.getProjectId(), userId, new ApplicationChange(app), null, ChangeType.APPLICATION_REMOVED);
  }

  /**
   * Updates project parent to the given parent. Updates existing parent and new parent information.
   *
   * @param id              Project whose parent is updated.
   * @param parentProject   Parent to be set. Use <code>null</code> to clear existing parent.
   */
  @Transactional
  public Project updateProjectParent(int id, Integer parentProject, int userId) {
    final Project currentProject = findProject(id, "Tried to update parent of non-existent project");
    final Project originalProject = new Project(currentProject);

    ArrayList<Integer> changedProjects = new ArrayList<>();
    if (parentProject != null) {
      // make sure there's no circular references
      List<Integer> newParents = resolveParentIds(parentProject);
      if (newParents.contains(currentProject.getId())) {
        throw new IllegalArgumentException("project.update.parent.circular");
      }
      changedProjects.add(parentProject);
    }

    if (currentProject.getParentId() != null) {
      changedProjects.add(currentProject.getParentId());
    }

    currentProject.setParentId(parentProject);
    projectDao.update(currentProject.getId(), currentProject);
    addChangeItem(id, userId, originalProject, currentProject, ChangeType.CONTENTS_CHANGED);
    updateProjectInformation(changedProjects, userId);

    return projectDao.findById(id).get();
  }

  /**
   * Go through all the given projects and their related projects to possibly update at least the following information:
   * - Project duration
   * - Project districts
   *
   * @param projectIds
   */
  @Transactional
  public List<Project> updateProjectInformation(List<Integer> projectIds, Integer userId) {
    List<Integer> rootParentIds = new ArrayList<>();
    HashSet<Integer> resolvedProjectIds = new HashSet<>();
    for (Integer projectId : projectIds) {
      if (resolvedProjectIds.contains(projectId)) {
        // the root of this project has been discovered previously
        continue;
      }
      List<Integer> parents = resolveParentIds(projectId);
      rootParentIds.add(parents.get(parents.size() - 1));
      resolvedProjectIds.addAll(parents);
    }

    HashSet<Project> updatedProjects = new HashSet<>();
    rootParentIds.forEach(pId -> {
      ProjectSummary ps = calculateSummaryAndUpdate(pId, userId);
      updatedProjects.addAll(ps.updatedProjects);
    });
    return new ArrayList<>(updatedProjects);
  }

  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getProjectChanges(Integer projectId) {
    final List<ChangeHistoryItem> items = historyDao.getProjectHistory(projectId);
    items.stream().forEach(item -> {
      final ChangeHistoryItemInfo info = item.getInfo();
      if (info.getId() != null && infoChangeTypes.contains(item.getChangeType())) {
        final Application app = applicationDao.findById(info.getId());
        if (app != null) {
          info.setApplicationId(app.getApplicationId());
          info.setName(app.getName());
        }
      }
    });
    return items;
  }

  @Transactional
  public int getNextProjectNumber() {
    return projectDao.getNextProjectNumber();
  }

  /**
   * Resolve parent and grandparent ids of the given project.
   *
   * @param   projectId   Project id whose parents should be resolved.
   * @return  List of project ids, which has the given project as first item and most grandparent project as last item.
   */
  private List<Integer> resolveParentIds(Integer projectId) {
    List<Project> resolvedProjects = findProjectParents(projectId);
    return resolvedProjects.stream().map(Project::getId).collect(Collectors.toList());
  }

  /**
   * Calculates and sets summary information of project. The summary consists of information calculated from applications linked directly
   * to the project plus the applications linked to children or grandchildren of the project.
   *
   * @param   projectId   Project whose summary information is calculated.
   * @return  Summary of the data calculated from applications in the project or its children.
   */
  private ProjectSummary calculateSummaryAndUpdate(int projectId, Integer userId) {
    Project project = findProject(projectId, "Project not found");
    final Project origProject = new Project(project);
    ProjectSummary summary = new ProjectSummary();
    List<Project> children = projectDao.findProjectChildren(project.getId());

    for (Project child : children) {
      ProjectSummary tmpSummary = calculateSummaryAndUpdate(child.getId(), userId);
      summary = mergeSummaries(summary, tmpSummary);
    }

    List<Application> applications = applicationDao.findByProject(project.getId());
    for (Application application : applications) {
      // TODO: do not use Johtoselvitys for calculating summary! Add when johtoselvitys is supported elsewhere in the code
      summary = calculateSummaryFromApplication(application, summary);
    }
    project.setStartTime(summary.minStartTime);
    project.setEndTime(summary.maxEndTime);
    project.setCityDistricts(summary.districts.toArray(new Integer[0]));
    project = projectDao.update(project.getId(), project);
    if (userId != null) {
      addChangeItem(projectId, userId, origProject, project, ChangeType.CONTENTS_CHANGED);
    }
    summary.updatedProjects.add(project);

    return summary;
  }

  private ProjectSummary calculateSummaryFromApplication(Application application, ProjectSummary projectSummary) {
    ProjectSummary ps = new ProjectSummary();
    ps.minStartTime = application.getStartTime();
    ps.maxEndTime = application.getEndTime();
    List<Location> locations = locationDao.findByApplicationId(application.getId());
    List<Integer> cityDistrictIds = locations.stream()
        .map(l -> l.getEffectiveCityDistrictId())
        .filter(id -> id != null)
        .collect(Collectors.toList());
    ps.districts.addAll(cityDistrictIds);
    return mergeSummaries(ps, projectSummary);
  }

  /**
   * Merges given project summaries in the following way:
   * - earliest start time is returned as start time
   * - latest end time is returned as end time
   * - all districts are combined together
   *
   * @param summary1  Summary to be merged.
   * @param summary2  Summary to be merged.
   * @return Merged summary.
   */
  private ProjectSummary mergeSummaries(ProjectSummary summary1, ProjectSummary summary2) {
    ProjectSummary ps = new ProjectSummary();
    if (summary1.minStartTime != null) {
      if (summary2.minStartTime == null || summary1.minStartTime.isBefore(summary2.minStartTime)) {
        ps.minStartTime = summary1.minStartTime;
      } else {
        ps.minStartTime = summary2.minStartTime;
      }
    } else {
      ps.minStartTime = summary2.minStartTime;
    }
    if (summary1.maxEndTime != null) {
      if (summary2.maxEndTime == null || summary1.maxEndTime.isAfter(summary2.maxEndTime)) {
        ps.maxEndTime = summary1.maxEndTime;
      } else {
        ps.maxEndTime = summary2.maxEndTime;
      }
    } else {
      ps.maxEndTime = summary2.maxEndTime;
    }
    ps.updatedProjects.addAll(summary1.updatedProjects);
    ps.updatedProjects.addAll(summary2.updatedProjects);
    ps.districts.addAll(summary1.districts);
    ps.districts.addAll(summary2.districts);
    return ps;
  }

  private static class ProjectSummary {
    ZonedDateTime minStartTime;
    ZonedDateTime maxEndTime;
    HashSet<Integer> districts = new HashSet<>();
    List<Project> updatedProjects = new ArrayList<>();
  }

  private Set<Integer> getRelatedProjects(List<Integer> applicationIds) {
    return applicationDao.findByIds(applicationIds).stream()
        .map(app -> app.getProjectId())
        .filter(id -> id != null)
        .collect(Collectors.toSet());
  }

  private Project findProject(final Integer projectId, final String errorText) {
    return projectDao.findById(projectId).orElseThrow(() -> new NoSuchEntityException(errorText, projectId));
  }

  /* Separates customer and contact changes into own change types. */
  private void addProjectChangeItems(int userId, Project origCurrentProject, Project origUpdatedProject) {
    final Project currentProject = new Project(origCurrentProject);
    final Project updatedProject = new Project(origUpdatedProject);
    final Integer currentCustomerId = currentProject.getCustomerId();
    final Integer updatedCustomerId = updatedProject.getCustomerId();
    currentProject.setCustomerId(null);
    updatedProject.setCustomerId(null);
    final Integer currentContactId = currentProject.getContactId();
    final Integer updatedContactId = updatedProject.getContactId();
    currentProject.setContactId(null);
    updatedProject.setContactId(null);

    addChangeItem(currentProject.getId(), userId, currentProject, updatedProject, ChangeType.CONTENTS_CHANGED);
    if (!Objects.equals(currentCustomerId, updatedCustomerId)) {
      addChangeItem(currentProject.getId(), userId, new CustomerChange(getCustomer(currentCustomerId)),
          new CustomerChange(getCustomer(updatedCustomerId)), ChangeType.CUSTOMER_CHANGED);
    }
    if (!Objects.equals(currentContactId, updatedContactId)) {
      addChangeItem(currentProject.getId(), userId, new ContactChange(getContact(currentContactId)),
          new ContactChange(getContact(updatedContactId)), ChangeType.CONTACT_CHANGED);
    }
  }

  private Customer getCustomer(Integer id) {
    if (id == null) {
      return null;
    }
    return customerDao.findById(id).get();
  }

  private Contact getContact(Integer id) {
    if (id == null) {
      return null;
    }
    return contactDao.findById(id).get();
  }

  /*
   * Add a change item to given user's change history. Compare oldData with
   * newData and log all differences between them.
   */
  private void addChangeItem(int projectId, int userId, Object oldData, Object newData, ChangeType changeType) {
    final List<FieldChange> fieldChanges = objectComparer.compare(oldData, newData).stream()
        .map(d -> new FieldChange(d.keyName, d.oldValue, d.newValue)).collect(Collectors.toList());
    if (!fieldChanges.isEmpty()) {
      final ChangeHistoryItem change = new ChangeHistoryItem(userId, null, changeType, null, ZonedDateTime.now(), fieldChanges);
      historyDao.addProjectChange(projectId, change);
    }
  }

  public void delete(int id) {
    projectDao.delete(id);
  }
}