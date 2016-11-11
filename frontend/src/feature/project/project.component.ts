import {Component, Input} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {translations} from '../../util/translations';
import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ProjectForm} from './project.form';
import {ProjectHub} from '../../service/project/project-hub';


@Component({
  selector: 'project',
  template: require('./project.component.html'),
  styles: []
})
export class ProjectComponent {
  projectInfoForm: FormGroup;
  applications = new Array<Application>();
  applicationSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;
  translations = translations;

  constructor(private router: Router, private route: ActivatedRoute,
              private applicationHub: ApplicationHub, private projectHub: ProjectHub,
              private fb: FormBuilder) {
    this.initForm();

    this.matchingApplications = this.applicationSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forApplicationId(idSearch))
      .switchMap(search => this.applicationHub.searchApplications(search));
  }

  add(application: Application) {
    this.applications.push(application);
  }

  remove(applicationId: number) {
    this.applications = this.applications.filter(app => applicationId !== app.id);
  }

  onSubmit(form: ProjectForm) {
    let project = ProjectForm.toProject(form);
    this.projectHub.save(project)
      .switchMap(p => this.projectHub.projectApplications(p.id, this.applications.map(app => app.id)))
      .subscribe(status => console.log('status', status));
  }

  set identifierSearch(identifier: string) {
    this.applicationSearch.next(identifier);
  }

  private initForm() {
    this.projectInfoForm = this.fb.group({
      name: ['', Validators.required],
      ownerName: ['', Validators.required],
      contactName: ['', Validators.required],
      email: ['', Validators.pattern('.+@.+\\..+')],
      phone: [''],
      customerReference: ['', Validators.required],
      additionalInfo: ['']
    });
  }
}
