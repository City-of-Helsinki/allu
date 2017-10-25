import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {Application} from '../../../model/application/application';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {ProjectForm} from './project.form';
import {Project} from '../../../model/project/project';
import {emailValidator} from '../../../util/complex-validator';
import {ProjectState} from '../../../service/project/project-state';
import {NotificationService} from '../../../service/notification/notification.service';


@Component({
  selector: 'project-edit',
  template: require('./project-edit.component.html'),
  styles: [
    require('./project-edit.component.scss')
  ]
})
export class ProjectEditComponent {
  projectInfoForm: FormGroup;
  applications = new Array<Application>();
  applicationSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;

  private parentProject: number;

  constructor(private router: Router, private route: ActivatedRoute,
              private applicationHub: ApplicationHub,
              private projectState: ProjectState,
              private fb: FormBuilder) {
    this.initForm();

    let project = this.projectState.project;
    this.projectInfoForm.patchValue(project);

    this.projectState.applications.subscribe(apps => this.applications = apps);

    this.route.queryParams
      .map((params: {parentProject: number}) => params.parentProject)
      .subscribe(parentProject => this.parentProject = parentProject);

    this.matchingApplications = this.applicationSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forApplicationId(idSearch))
      .switchMap(search => this.applicationHub.searchApplications(search))
      .catch(err => NotificationService.errorCatch(err, []));
  }

  add(application: Application) {
    this.applications.push(application);
  }

  remove(applicationId: number) {
    this.applications = this.applications.filter(app => applicationId !== app.id);
  }

  onSubmit(form: ProjectForm) {
    let project = ProjectForm.toProject(form);
    project.parentId = this.parentProject || project.parentId;

    this.projectState.save(project)
      .switchMap(p => this.projectState.updateApplications(this.applications.map(app => app.id)))
      .subscribe(p => this.navigateAfterSubmit(p, this.parentProject));
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
      customerReference: [''],
      additionalInfo: ['']
    });
  }

  private navigateAfterSubmit(project: Project, relatedProjectId: number): void {
    if (relatedProjectId) {
      this.router.navigate(['/projects', relatedProjectId, 'projects']);
    } else {
      this.router.navigate(['/projects', project.id]);
    }
  }
}
