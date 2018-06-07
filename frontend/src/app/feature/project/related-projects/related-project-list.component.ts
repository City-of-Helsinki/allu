import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Project} from '../../../model/project/project';

@Component({
  selector: 'related-project-list',
  templateUrl: './related-project-list.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RelatedProjectListComponent {

  @Input() loading: boolean;

  @Input() set projects(projects: Project[]) {
  }
}
