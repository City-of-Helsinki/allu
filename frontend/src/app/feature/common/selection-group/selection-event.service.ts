import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Subscription} from 'rxjs/Subscription';

@Injectable()
export class SelectionEventService {
  private selection: Subject<SelectionEvent> = new Subject<SelectionEvent>();

  subscribe(onSuccess, onError?, onComplete?): Subscription {
    return this.selection.subscribe(onSuccess, onError, onComplete);
  }

  next(selection: any): void {
    this.selection.next(selection);
  }
}

export interface SelectionEvent {
  item: any;
  selected: boolean;
}
