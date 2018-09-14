import {Component, OnInit} from '@angular/core';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {Observable} from 'rxjs';
import {map} from 'rxjs/internal/operators';

@Component({
  selector: 'default-recipients',
  templateUrl: './default-recipients.component.html',
  styleUrls: ['./default-recipients.component.scss']
})
export class DefaultRecipientsComponent implements OnInit {
  applicationTypes = Object.keys(ApplicationType);

  constructor(private defaultRecipientHub: DefaultRecipientHub) {
  }

  ngOnInit(): void {
    this.defaultRecipientHub.loadDefaultRecipients();
  }

  itemCountByType(type: string): Observable<number> {
    return this.defaultRecipientHub.defaultRecipientsByApplicationType(type)
      .pipe(map(dr => dr.length));
  }
}
