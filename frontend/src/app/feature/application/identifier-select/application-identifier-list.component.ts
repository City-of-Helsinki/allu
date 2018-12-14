import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'application-identifier-list',
  templateUrl: './application-identifier-list.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationIdentifierListComponent {

  @Input() controls = false;
  @Input() loading = false;

  @Output() remove = new EventEmitter<string>();

  displayedColumns = ['applicationId'];

  private _identifiers: string[] = [];

  constructor() {}

  @Input()
  get identifiers() { return this._identifiers; }
  set identifiers(identifiers: string[]) {
    this._identifiers = identifiers;
  }
}
