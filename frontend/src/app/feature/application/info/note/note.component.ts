import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationStore} from '../../../../service/application/application-store';
import {NoteForm} from './note.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {MAX_YEAR, MIN_YEAR} from '../../../../util/time.util';

@Component({
  selector: 'note',
  viewProviders: [],
  templateUrl: './note.component.html',
  styleUrls: []
})
export class NoteComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder, route: ActivatedRoute, applicationStore: ApplicationStore) {
    super(fb, route, applicationStore);
  }

  ngOnInit(): any {
    super.ngOnInit();
    this.applicationForm.patchValue(NoteForm.from(this.application));
  }

  protected update(form: NoteForm) {
    const application = super.update(form);
    application.name = form.name;
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.recurringEndYear = form.recurringEndYear;
    application.extension = NoteForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      description: [''],
      recurringEndYear: [undefined, ComplexValidator.betweenOrEmpty(MIN_YEAR, MAX_YEAR)]
    });
  }
}
