import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'application-identifier-list',
  templateUrl: './application-identifier-list.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationIdentifierListComponent implements OnInit {

  @Input() controls = false;
  @Input() loading = false;

  @Output() remove = new EventEmitter<string>();

  displayedColumns = ['applicationId'];

  private _identifiers: string[] = [];

  constructor() {}

  ngOnInit(): void {
    this.displayedColumns = this.controls
      ? ['controls'].concat(this.displayedColumns)
      : this.displayedColumns;
  }

  @Input()
  get identifiers() { return this._identifiers; }
  set identifiers(identifiers: string[]) {
    this._identifiers = identifiers;
  }
}
