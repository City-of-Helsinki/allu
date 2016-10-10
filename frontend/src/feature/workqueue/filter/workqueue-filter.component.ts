import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {FormGroup, FormBuilder} from '@angular/forms';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {translations} from '../../../util/translations';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status-change';
import {ApplicationType} from '../../../model/application/type/application-type';
import '../../../rxjs-extensions.ts';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {ApplicationSearchQueryForm} from '../../../model/search/ApplicationSearchQueryForm';

@Component({
  selector: 'workqueue-filter',
  template: require('./workqueue-filter.component.html'),
  styles: [
    require('./workqueue-filter.component.scss')
  ]
})
export class WorkQueueFilterComponent implements OnInit {
  queryForm: FormGroup;
  @Output() onQueryChange = new EventEmitter<ApplicationSearchQuery>();
  pickadateParams = PICKADATE_PARAMETERS;
  private translations = translations;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  private handlers: Array<string> = ['TestHandler'];
  private applicationStatuses = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypes = EnumUtil.enumValues(ApplicationType);

  constructor(fb: FormBuilder) {
    this.queryForm = fb.group({
      type: undefined,
      handler: undefined,
      status: undefined,
      district: undefined,
      startTime: undefined,
      endTime: undefined
    });
  }

  ngOnInit(): void {
    this.queryForm.valueChanges
      .debounceTime(300)
      .distinctUntilChanged()
      .subscribe(query => this.onQueryChange.emit(ApplicationSearchQuery.from(query)));
  }
}
