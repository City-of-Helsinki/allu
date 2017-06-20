import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {NoteForm} from './note.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {MAX_YEAR, MIN_YEAR} from '../../../../util/time.util';
import {Some} from '../../../../util/option';
import {NotificationService} from '../../../../service/notification/notification.service';
import {findTranslation} from '../../../../util/translations';

@Component({
  selector: 'note',
  viewProviders: [],
  template: require('./note.component.html'),
  styles: []
})
export class NoteComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder, route: ActivatedRoute, applicationState: ApplicationState, private router: Router) {
    super(fb, route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    this.applicationForm.patchValue(NoteForm.from(this.application));
  }

  protected update(form: NoteForm) {
    let application = super.update(form);
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
