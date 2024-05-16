import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, Validators} from '@angular/forms';
import {Configuration} from '@model/config/configuration';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Save} from '@feature/admin/configuration/actions/configuration-actions';
import {ConfigurationType} from '@model/config/configuration-type';

@Component({
  selector: 'configuration-text-value',
  templateUrl: './configuration-text-value.component.html',
  styleUrls: ['./configuration-value.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationTextValueComponent implements OnInit {

  @Input() configuration: Configuration;

  valueCtrl: UntypedFormControl;

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    const validators = this.configuration.type === ConfigurationType.EMAIL
      ? [Validators.required, Validators.email]
      : [Validators.required];

    this.valueCtrl = this.fb.control(this.configuration.value, validators);
  }

  submit(): void {
    const configuration: Configuration = {
      ...this.configuration,
      value: this.valueCtrl.value
    };
    this.store.dispatch(new Save(configuration));
  }
}
