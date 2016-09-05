import {List} from 'immutable';
/**
 * State of UI containing notification messages and current errors
 * which can be shown in UI
 */
export class UIState {
  constructor(public messages: List<string>, public errors: List<string>) {}
}
