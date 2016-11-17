import {Injectable, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {SearchbarFilter} from '../searchbar-filter';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationService} from './application.service';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {StructureMeta} from '../../model/application/structure-meta';
import {DefaultText} from '../../model/application/cable-report/default-text';

export type ApplicationSearch = ApplicationLocationQuery | number;

@Injectable()
export class ApplicationHub {

  private metaData$ = new Subject<StructureMeta>();

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
  public loadMetaData = (applicationType: string) => this.applicationService.loadMetadata(applicationType)
    .do(meta => this.metaData$.next(meta));

  public metaData = () => this.metaData$.asObservable();

  /**
   * Saves given application (new / update) and returns saved application
   */
  public save = (application: Application) => this.applicationService.saveApplication(application);

  /**
   * Changes applications status according to statusChange.
   * Returns updated application.
   */
  public changeStatus = (statusChange: ApplicationStatusChange) => this.applicationService.applicationStatusChange(statusChange);

  /**
   * Changes handler of given applications. Does not return anytyhing. Use Observable's subscribe complete.
   */
  public changeHandler =
    (handler: number, applicationIds: Array<number>) => this.applicationService.applicationHandlerChange(handler, applicationIds);

  /**
   * Removes handler of given applications. Does not return anytyhing. Use Observable's subscribe complete.
   */
  public removeHandler =
    (applicationIds: Array<number>) => this.applicationService.applicationHandlerRemove(applicationIds);

  public loadDefaultTexts = () => this.applicationService.loadDefaultTexts();

  public saveDefaultText = (text: DefaultText) => this.applicationService.saveDefaultText(text);
}
