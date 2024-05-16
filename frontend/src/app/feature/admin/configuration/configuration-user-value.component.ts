import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, Validators} from '@angular/forms';
import {Configuration} from '@model/config/configuration';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Save} from '@feature/admin/configuration/actions/configuration-actions';
import {User} from '@model/user/user';

@Component({
  selector: 'configuration-user-value',
  templateUrl: './configuration-user-value.component.html',
  styleUrls: ['./configuration-value.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationUserValueComponent implements OnInit {

  @Input() configuration: Configuration;
  @Input() users: User[] = [];

  valueCtrl: UntypedFormControl;

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    this.valueCtrl = this.fb.control(this.configuration.value, [Validators.required]);
  }

  submit(): void {
    const configuration: Configuration = {
      ...this.configuration,
      value: this.valueCtrl.value
    };
    this.store.dispatch(new Save(configuration));
  }
}
