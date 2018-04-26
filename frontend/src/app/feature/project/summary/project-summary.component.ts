import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Store} from '@ngrx/store';

import {Project} from '../../../model/project/project';
import * as fromProject from '../reducers';
import {CityDistrict} from '../../../model/common/city-district';
import {Subject} from 'rxjs/Subject';
import {MapStore} from '../../../service/map/map-store';
import {MapComponent} from '../../map/map.component';

@Component({
  selector: 'project-summary',
  templateUrl: './project-summary.component.html',
  styleUrls: ['./project-summary.component.scss']
})
export class ProjectSummaryComponent implements OnInit, OnDestroy, AfterViewInit {
  project$: Observable<Project>;
  districts$: Observable<CityDistrict[]>;

  private destroy$ = new Subject<boolean>();
  @ViewChild(MapComponent) private map: MapComponent;

  constructor(private store: Store<fromProject.State>, private mapStore: MapStore) {}

  ngOnInit(): void {
    this.districts$ = this.store.select(fromProject.getProjectDistricts);
    this.project$ = this.store.select(fromProject.getCurrentProject);
  }

  ngAfterViewInit(): void {
    // TODO: This should be done using ngrx store after map is refactored
    this.store.select(fromProject.getApplications)
      .takeUntil(this.destroy$)
      .subscribe(applications => {
        this.mapStore.applicationsChange(applications);
        this.map.centerAndZoomOnDrawn();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
