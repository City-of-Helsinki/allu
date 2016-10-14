import {Injectable, OnInit} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {SearchbarFilter} from '../searchbar-filter';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {Subject} from 'rxjs/Subject';
import {ApplicationService} from './application.service';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';

export type ApplicationSearch = ApplicationLocationQuery | number;

@Injectable()
export class ApplicationHub {
  constructor(private applicationService: ApplicationService) {}

  /**
   * Fetches single application
   */
  public getApplication = (id: number) => this.applicationService.getApplication(id);

  /**
   * Fetches applications based on given search query
   */
  public searchApplications = (searchQuery: ApplicationSearchQuery) => this.applicationService.searchApplications(searchQuery);

  /**
   * Loads metadata for given application type
   */
  public loadMetaData = (applicationType: string) => this.applicationService.loadMetadata(applicationType);

  /**
   * Saves given application (new / update) and returns saved application
   */
  public save = (application: Application) => this.applicationService.saveApplication(application);

  /**
   * Changes applications status according to statusChange.
   * Returns updated application.
   */
  public changeStatus = (statusChange: ApplicationStatusChange) => this.applicationService.applicationStatusChange(statusChange);
}
