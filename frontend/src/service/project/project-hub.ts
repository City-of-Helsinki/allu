import {Injectable, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Project} from '../../model/project/project';
import {ProjectService} from './project.service';

@Injectable()
export class ProjectHub {

  constructor(private projectService: ProjectService) {}

  /**
   * Fetch single project with given id
   */
  public getProject = (id: number) => this.projectService.getProject(id);

  /**
   * Search projects with given search query TODO: Define query
   */
  public searchProjects = (search: any) => this.projectService.searchProjects(search);

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
}
