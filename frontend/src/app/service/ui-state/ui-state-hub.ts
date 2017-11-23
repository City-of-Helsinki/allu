import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {UIState} from './ui-state';
import {toast} from 'angular2-materialize';
import {ErrorInfo} from './error-info';
import {message} from './error-type';

/**
 * Class for handling UIState changes and notify
 * listening components about them.
 *
 * All changes create a new state into ui-state stream.
 */
@Injectable()
export class UIStateHub {

  private uiState$: BehaviorSubject<UIState> = new BehaviorSubject(new UIState());
  private displayedMessage$: Subject<string> = new Subject<string>();

  constructor() {
    this.displayedMessage$.asObservable()
      .debounceTime(50)
      .distinctUntilChanged()
      .subscribe(message => toast(message, 4000));
  }

  /**
   * Observable which conveys latest state of UI.
   */
  public uiState = () => this.uiState$.asObservable();

  /**
   * For adding a notification message
   */
  public addMessage(message: string) {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(new UIState(message, currentState.error));
    return Observable.empty();
  }

  /**
   * For clearing current notification message
   */
  public clearMessage(): void {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(new UIState(undefined, currentState.error));
  }

  /**
   * For adding an error message
   */
  public addError(error: ErrorInfo) {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(new UIState(currentState.message, error));
    this.displayedMessage$.next(message(error.type));
    return Observable.empty();
  }

  /**
   * For clearing current error message
   */
  public clearError(): void {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(new UIState(currentState.message, undefined));
  }

  public clear(): void {
    this.uiState$.next(new UIState());
  }
}
