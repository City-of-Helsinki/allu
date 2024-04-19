import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {MatLegacyOption as MatOption} from '@angular/material/legacy-core';
import {debounceTime, map, takeUntil} from 'rxjs/internal/operators';
import {IdentifierEntry} from '@feature/application/identifier-select/identifier-entry';
import {Some} from '@util/option';
import {ComplexValidator} from '@util/complex-validator';
import {ArrayUtil} from '@util/array-util';

@Component({
  selector: 'application-identifier-select',
  templateUrl: './application-identifier-select.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationIdentifierSelectComponent implements OnInit, OnDestroy {
  @Input() prefix = '';

  @Output() searchChange = new EventEmitter<string>(true);
  @Output() selectedChange = new EventEmitter<string>(true);

  // Without prefix
  readonly IDENTIFIER_LENGTH = 7;

  searchControl = new UntypedFormControl('', [ComplexValidator.isNumber, ComplexValidator.requiredLength(this.IDENTIFIER_LENGTH)]);

  private _matchingIdentifiers: IdentifierEntry[] = [];
  private destroy = new Subject<boolean>();

  constructor() {
  }

  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      map(term => this.valueWithPrefix(term))
    ).subscribe(term => this.searchChange.emit(term));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input()
  get matchingIdentifiers() { return this._matchingIdentifiers; }
  set matchingIdentifiers(identifiers: IdentifierEntry[]) {
    const value = this.valueWithPrefix(this.searchControl.value);
    const searchValue = this.searchControl.value && this.searchControl.valid ? [{value}] : [];
    const addedIdentifiers = identifiers ? identifiers : [];
    this._matchingIdentifiers = searchValue.concat(addedIdentifiers)
      .filter(ArrayUtil.uniqueItem(i => i.value));
  }

  add(option: MatOption): void {
    this.selectedChange.emit(option.value);
    this.searchControl.reset();
  }

  displayName(entry: IdentifierEntry): string {
    return Some(entry).map(e => {
      if (e.description) {
        return `${e.value}: ${e.description}`;
      } else {
        return e.value;
      }
    }).orElse(undefined);
  }

  private valueWithPrefix(value: string): string {
    return `${this.prefix}${value}`;
  }
}
