import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Project} from '../../../model/project/project';
import {Application} from '../../../model/application/application';
import {ApplicationStatus, decided, isBefore} from '../../../model/application/application-status';

@Component({
  selector: 'project-header',
  templateUrl: './project-header.component.html',
  styleUrls: ['project-header.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectHeaderComponent {
  @Input() project: Project;
  @Input() relatedProjects = 0;
  @Input() parent: Project;

  activeApplications = 0;
  decidedApplications = 0;

  @Input() set applications(applications: Application[]) {
    this.activeApplications = applications
      .filter(app => isBefore(app.status, ApplicationStatus.DECISION))
      .length;

    this.decidedApplications = applications
      .filter(app => decided.indexOf(app.status) >= 0)
      .length;
  }
}
