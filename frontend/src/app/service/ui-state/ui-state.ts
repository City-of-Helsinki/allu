import {ErrorInfo} from './error-info';
/**
 * State of UI containing notification message and current error
 * which can be shown in UI
 */
export class UIState {
  constructor()
  constructor(message: string, error: ErrorInfo)
  constructor(public message?: string, public error?: ErrorInfo) {}
}
