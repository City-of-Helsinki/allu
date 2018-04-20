import {Component, Input} from '@angular/core';

import {Project} from '../../../model/project/project';
import {CityDistrict} from '../../../model/common/city-district';

@Component({
  selector: 'project-info',
  templateUrl: './project-info.component.html',
  styleUrls: ['./project-info.component.scss']
})
export class ProjectInfoComponent {
  @Input() project: Project;

  districtNames = [];

  @Input() set cityDistricts(districts: CityDistrict[]) {
    this.districtNames = districts.map(d => d.name);
  }
}
