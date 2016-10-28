
import {Injectable} from '@angular/core';
import {WorkQueueService} from './workqueue.service';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';

@Injectable()
export class WorkQueueHub {
  constructor(private workQueueService: WorkQueueService) {}

  /**
   * Fetches applications based on given search query and constraints based on the assigned user roles and application types.
   */
  public searchApplicationsSharedByGroup =
    (searchQuery: ApplicationSearchQuery) => this.workQueueService.searchApplicationsSharedByGroup(searchQuery);
}
