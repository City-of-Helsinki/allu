import {Component, Input, OnInit} from '@angular/core';
import {Configuration} from '@model/config/configuration';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Save} from '@feature/allu/actions/configuration-actions';

@Component({
  selector: 'configuration-entry',
  templateUrl: './configuration-entry.component.html',
  styleUrls: ['./configuration-entry.component.scss']
})
export class ConfigurationEntryComponent implements OnInit {

  @Input() configuration: Configuration;

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.form = this.fb.group({
      value: [this.configuration.value, Validators.required]
    });
  }

  submit(): void {
    const configuration = {
      ...this.configuration,
      value: this.form.get('value').value
    };
    this.store.dispatch(new Save(configuration));
  }
}

