import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {Store} from '@ngrx/store';

import {Project} from '../../../model/project/project';
import * as fromProject from '../reducers';
import {CityDistrict} from '../../../model/common/city-district';
import {MapStore} from '../../../service/map/map-store';
import {MapComponent} from '../../map/map.component';
import {Comment} from '../../../model/application/comment/comment';
import {filter, takeUntil, takeWhile} from 'rxjs/internal/operators';
import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {MatSlideToggleChange} from '@angular/material';
import {ShowBasicInfo} from '../actions/project-actions';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {shrinkFadeInOut} from '../../common/animation/common-animations';

@Component({
  selector: 'project-summary',
  templateUrl: './project-summary.component.html',
  styleUrls: ['./project-summary.component.scss'],
  animations: [shrinkFadeInOut]
})
export class ProjectSummaryComponent implements OnInit, OnDestroy, AfterViewInit {
  project$: Observable<Project>;
  districts$: Observable<CityDistrict[]>;
  comments$: Observable<Comment[]>;
  changes$: Observable<ChangeHistoryItem[]>;
  showBasicInfo$: Observable<boolean>;

  private destroy$ = new Subject<boolean>();
  @ViewChild(MapComponent) private map: MapComponent;

  constructor(private store: Store<fromProject.State>, private mapStore: MapStore) {}

  ngOnInit(): void {
    this.districts$ = this.store.select(fromProject.getProjectDistricts);
    this.project$ = this.store.select(fromProject.getCurrentProject);
    this.comments$ = this.store.select(fromProject.getLatestComments('desc'));
    this.changes$ = this.store.select(fromProject.getHistory);
    this.showBasicInfo$ = this.store.select(fromProject.getShowBasicInfo);
  }

  ngAfterViewInit(): void {
    // TODO: This should be done using ngrx store after map is refactored
    combineLatest(
      this.store.select(fromProject.getApplications),
      this.store.select(fromProject.getShowBasicInfo)
    ).pipe(
      takeUntil(this.destroy$)
    ).subscribe(([applications, show]) => {
        this.mapStore.applicationsChange(applications);
        if (show && !!this.map) {
          this.map.centerAndZoomOnDrawn();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  toggleBasicInfo(toggleChange: MatSlideToggleChange): void {
    this.store.dispatch(new ShowBasicInfo(toggleChange.checked));
  }
}
