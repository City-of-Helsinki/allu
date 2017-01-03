import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Application} from '../../../model/application/application';

@Component({
  selector: 'application-info',
  viewProviders: [],
  template: require('./application-info.component.html'),
  styles: []
})
export class ApplicationInfoComponent implements OnInit {

  application: Application;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
      });
  }
}
