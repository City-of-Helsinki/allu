import {Component, OnInit} from '@angular/core';
import {ApplicationStore} from '../../../service/application/application-store';
import {UrlUtil} from '../../../util/url.util';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit {

  type: string;
  showDraftSelection: boolean;
  readonly: boolean;

  constructor(private applicationStore: ApplicationStore, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.type = application.type;
    this.showDraftSelection = this.applicationStore.isNew;

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary');
  }
}
