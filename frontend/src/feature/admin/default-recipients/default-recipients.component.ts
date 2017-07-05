import {AfterViewInit, Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {RecipientsByTypeComponent} from './recipients-by-type.component';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'default-recipients',
  template: require('./default-recipients.component.html'),
  styles: [
    require('./default-recipients.component.scss')
  ]
})
export class DefaultRecipientsComponent implements OnInit {
  applicationTypes = EnumUtil.enumValues(ApplicationType);

  constructor(private defaultRecipientHub: DefaultRecipientHub) {
  }

  ngOnInit(): void {
    this.defaultRecipientHub.loadDefaultRecipients();
  }

  itemCountByType(type: string): Observable<number> {
    return this.defaultRecipientHub.defaultRecipientsByType(type)
      .map(dr => dr.length);
  }
}
