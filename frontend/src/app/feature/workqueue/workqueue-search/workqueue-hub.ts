
import {Injectable} from '@angular/core';
import {WorkQueueService} from './workqueue.service';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class WorkQueueHub {
  private searchQuery$ = new Subject<ApplicationSearchQuery>();

  constructor(private workQueueService: WorkQueueService) {}

  /**
   * Fetches applications based on given search query and constraints based on the assigned user roles and application types.
   */
  public searchApplicationsSharedByGroup = (searchQuery: ApplicationSearchQuery) =>
    this.workQueueService.searchApplicationsSharedByGroup(searchQuery)

  public addSearchQuery = (searchQuery: ApplicationSearchQuery) =>
    this.searchQuery$.next(searchQuery)

  get searchQuery(): Observable<ApplicationSearchQuery> {
    return this.searchQuery$.asObservable();
  }
}
