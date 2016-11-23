import {Injectable, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Project} from '../../model/project/project';
import {ProjectService} from './project.service';
import {ProjectSearchQuery} from '../../model/project/project-search-query';

@Injectable()
export class ProjectHub {

  constructor(private projectService: ProjectService) {}

  /**
   * Fetch single project with given id
   */
  public getProject = (id: number) => this.projectService.getProject(id);

  /**
   * Search projects with given search query
   */
  public searchProjects = (search: ProjectSearchQuery) => this.projectService.searchProjects(search);

  /**
   * Saves given project (create / update)
   */
  public save = (project: Project) => this.projectService.save(project);

  /**
   * Remove project with given id
   */
  public remove = (id: number) => this.projectService.remove(id);

  /**
   * Sets projects applications as given list of applications (empty array of id's removes all applications from project)
   */
  public updateProjectApplications = (id: number, applicationIds: Array<number>) =>
    this.projectService.updateProjectApplications(id, applicationIds);

  /**
   * Fetches projects applications
   */
  public getProjectApplications = (id: number) => this.projectService.getProjectApplications(id);

  /**
   * Fetches childprojects of given project
   */
  public getChildProjects = (id: number) => this.projectService.getChildProjects(id);

  /**
   * Fetches all parents (and grandparents and ...) of given project
   */
  public getParentProjects = (id: number) => this.projectService.getParentProjects(id);
}
