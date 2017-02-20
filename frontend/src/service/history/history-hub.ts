import {Injectable} from '@angular/core';
import '../../rxjs-extensions.ts';
import {HistoryService} from './history-service';

@Injectable()
export class HistoryHub {

  constructor(private historyService: HistoryService) {}

  public applicationHistory = (applicationId: number) => this.historyService.getApplicationHistory(applicationId);
}
