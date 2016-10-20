import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {FormGroup, FormBuilder, FormControl} from '@angular/forms';

import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {translations} from '../../../util/translations';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status-change';
import {ApplicationType} from '../../../model/application/type/application-type';
import '../../../rxjs-extensions.ts';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {ApplicationSearchQueryForm} from '../../../model/search/ApplicationSearchQueryForm';
import {User} from '../../../model/common/user';
import {CurrentUser} from '../../../service/user/current-user';

declare var Materialize: any;

const TAB_OWN = 'Omat';
const HANDLER_FIELD = 'handler';

@Component({
  selector: 'workqueue-filter',
  template: require('./workqueue-filter.component.html'),
  styles: [
    require('./workqueue-filter.component.scss')
  ]
})
export class WorkQueueFilterComponent implements OnInit {
  queryForm: FormGroup;
  @Input() handlers: Array<User>;
  @Output() onQueryChange = new EventEmitter<ApplicationSearchQuery>();
  pickadateParams = PICKADATE_PARAMETERS;

  private translations = translations;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
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
      .distinctUntilChanged()
      .subscribe(query => this.onQueryChange.emit(ApplicationSearchQuery.from(query)));
  }

  @Input() set selectedTab(tab: string) {
    let control = this.queryForm.get(HANDLER_FIELD);

    if (TAB_OWN === tab) {
      CurrentUser.userName().do(userName => control.setValue([userName]));
    } else {
      control.setValue([]);
    }
  }
}
