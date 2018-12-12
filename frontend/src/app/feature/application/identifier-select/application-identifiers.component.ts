import {ChangeDetectionStrategy, Component, forwardRef, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs/internal/Observable';
import {IdentifierEntry} from '@feature/application/identifier-select/identifier-entry';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {map, take, takeUntil, tap} from 'rxjs/operators';
import {ArrayUtil} from '@util/array-util';
import {ApplicationType} from '@model/application/type/application-type';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Subject} from 'rxjs/internal/Subject';
import {Application} from '@model/application/application';

const APPLICATION_IDENTIFIERS_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => ApplicationIdentifiersComponent),
  multi: true
};

const typeToPrefix = {
  CABLE_REPORT: 'JS',
  PLACEMENT_CONTRACT: 'SL'
};

@Component({
  selector: 'application-identifiers',
  templateUrl: './application-identifiers.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [APPLICATION_IDENTIFIERS_VALUE_ACCESSOR]
})
export class ApplicationIdentifiersComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() type: ApplicationType;

  matchingIdentifiers$: Observable<IdentifierEntry[]>;
  identifiers$: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);
  isDisabled = false;
  prefix: string;

  private searchResult: Subject<Application[]> = new Subject<Application[]>();
  private destroy: Subject<boolean> = new Subject<boolean>();

  ngOnInit(): void {
    this.prefix = typeToPrefix[this.type];

    this.identifiers$.pipe(
      takeUntil(this.destroy)
    ).subscribe(identifiers => this._onChange(identifiers));

    this.matchingIdentifiers$ = this.searchResult.asObservable().pipe(
      map(applications => applications.map(app => this.toIdentifierEntry(app)))
    );
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
  }

  /** Implemented as part of ControlValueAccessor. */
  writeValue(values: string[] = []): void {
    this.identifiers$.next(values);
  }

  /** Implemented as a part of ControlValueAccessor. */
  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  /** Implemented as part of ControlValueAccessor. */
  registerOnChange(fn: (value: any) => void): void {
    this._onChange = fn;
  }

  /** Implemented as part of ControlValueAccessor. */
  registerOnTouched(fn: () => void): void {
    this._onTouched = fn;
  }

  search(term: string): void {
    this.searchResult.next([]);
  }

  add(identifier: string): void {
    this.identifiers$.pipe(
      take(1),
      map(existing => ArrayUtil.createOrReplace(existing, identifier, item => item === identifier))
    ).subscribe(next => this.identifiers$.next(next));
  }

  remove(identifier: string): void {
    this.identifiers$.pipe(
      take(1),
      map(existing => existing.filter(item => item !== identifier))
    ).subscribe(next => this.identifiers$.next(next));
  }

  private _onChange = (_: any) => {};
  private _onTouched = (_: any) => {};

  private toIdentifierEntry = (application: Application) => ({value: application.applicationId, name: application.name});
}
