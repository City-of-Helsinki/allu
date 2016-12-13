import {Component, Input} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {translations} from '../../../util/translations';
import {Application} from '../../../model/application/application';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {ProjectForm} from './project.form';
import {ProjectHub} from '../../../service/project/project-hub';
import {MaterializeUtil} from '../../../util/materialize.util';
import {Project} from '../../../model/project/project';
import {Some} from '../../../util/option';
import {emailValidator} from '../../../util/complex-validator';


@Component({
  selector: 'project-edit',
  template: require('./project-edit.component.html'),
  styles: []
})
export class ProjectEditComponent {
  projectInfoForm: FormGroup;
  applications = new Array<Application>();
  applicationSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;
  translations = translations;

  constructor(private router: Router, private route: ActivatedRoute,
              private applicationHub: ApplicationHub, private projectHub: ProjectHub,
              private fb: FormBuilder) {
    this.initForm();

    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
          this.projectInfoForm.patchValue(project);

          Some(project.id).do(id => {
            this.projectHub.getProjectApplications(id).subscribe(apps => {
              this.applications = apps;
            });
          });

          MaterializeUtil.updateTextFields(50);
    });

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
      .switchMap(p => this.projectHub.updateProjectApplications(p.id, this.applications.map(app => app.id)))
      .subscribe(p => this.router.navigate(['/projects', p.id]));
  }

  public onIdentifierSearchChange(identifier: string) {
    this.applicationSearch.next(identifier);
  }

  private initForm() {
    this.projectInfoForm = this.fb.group({
      id: [undefined],
      name: ['', Validators.required],
      ownerName: ['', Validators.required],
      contactName: ['', Validators.required],
      email: ['', emailValidator],
      phone: [''],
      customerReference: ['', Validators.required],
      additionalInfo: ['']
    });
  }
}
