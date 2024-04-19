import {Component, Input} from '@angular/core';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {Project} from '../../../model/project/project';
import {CityDistrict} from '../../../model/common/city-district';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {findTranslation} from '../../../util/translations';
import {filter} from 'rxjs/internal/operators';
import * as fromProject from '../reducers';
import {Store} from '@ngrx/store';
import {Delete} from '../actions/project-actions';

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

  constructor(
    private dialog: MatDialog,
    private store: Store<fromProject.State>) {
  }

  remove(): void {
      const data = {
        title: findTranslation('project.confirmCancel.title'),
        confirmText: findTranslation('project.confirmCancel.confirmText'),
        cancelText: findTranslation('project.confirmCancel.cancelText'),
        description: findTranslation('project.confirmCancel.description')
      };
      this.dialog.open(ConfirmDialogComponent, {data}).afterClosed().pipe(
        filter(result => !!result) // Ignore no answers
      ).subscribe(() => this.removeProject());
   }

   removeProject(): void {
    this.store.dispatch(new Delete());
   }

}
