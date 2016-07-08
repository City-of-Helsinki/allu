import {LoadEvent} from './load-event';

export class MetaLoadEvent extends LoadEvent {
  constructor(public applicationType: string) {
    super();
  }
}
