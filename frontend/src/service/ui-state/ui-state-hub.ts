import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {UIState} from './ui-state.ts';
import {List} from 'immutable';
import '../../rxjs-extensions.ts';
import {toast} from 'angular2-materialize';

/**
 * Class for handling UIState changes and notify
 * listening components about them.
 *
 * All changes create a new state into ui-state stream.
 */
@Injectable()
export class UIStateHub {

  private uiState$: BehaviorSubject<UIState> = new BehaviorSubject(new UIState(List([]), List([])));

  constructor() {}

  /**
   * Observable which conveys latest state of UI.
   */
  public uiState = () => this.uiState$.asObservable();

  /**
   * For adding a single notification message
   */
  public addMessage(message: string) {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(
      new UIState(
        List.of(message),
        currentState.errors
      ));
    return Observable.empty();
  }

  /**
   * For clearing current notification messages
   */
  public clearMessages(): void {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(new UIState(List([]), currentState.errors));
  }

  /**
   * For setting multiple notification messages
   */
  public setMessages(messages: Array<string>) {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(
      new UIState(
        List(messages),
        currentState.errors
      ));
    return Observable.empty();
  }

  /**
   * For adding a single error message
   */
  public addError(error: string) {
    console.log('Error:', error);
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(
      new UIState(
        currentState.messages,
        List.of(error)
      ));
    toast(error, 4000);
    return Observable.empty();
  }

  /**
   * For clearing current error messages
   */
  public clearErrors(): void {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(new UIState(currentState.messages, List([])));
  }

  /**
   * For setting multiple error messages
   */
  public setErrors(errors: Array<string>) {
    let currentState: UIState = this.uiState$.getValue();
    this.uiState$.next(
      new UIState(
        currentState.messages,
        List(errors)
      ));
    return Observable.empty();
  }

  public clear(): void {
    this.uiState$.next(new UIState(List([]), List([])));
  }
}
