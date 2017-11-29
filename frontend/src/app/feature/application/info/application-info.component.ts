import {Component, OnInit} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit {

  application: Application;

  constructor(private applicationStore: ApplicationStore) {}

  ngOnInit(): void {
    this.application = this.applicationStore.snapshot.application;
  }
}
