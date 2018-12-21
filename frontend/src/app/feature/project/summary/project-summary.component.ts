import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {select, Store} from '@ngrx/store';

import {Project} from '@model/project/project';
import * as fromRoot from '@feature/allu/reducers';
import * as fromProject from '../reducers';
import * as fromMapLayers from '@feature/map/reducers';
import {CityDistrict} from '@model/common/city-district';
import {MapStore} from '@service/map/map-store';
import {MapComponent} from '@feature/map/map.component';
import {Comment} from '@model/application/comment/comment';
import {takeUntil} from 'rxjs/internal/operators';
import {ChangeHistoryItem} from '@model/history/change-history-item';
import {MatSlideToggleChange} from '@angular/material';
import {ShowBasicInfo} from '../actions/project-actions';
import {shrinkFadeInOut} from '@feature/common/animation/common-animations';

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
  selectedLayers$: Observable<string[]>;
  availableLayers$: Observable<string[] | number[]>;

  private destroy$ = new Subject<boolean>();
  @ViewChild(MapComponent) private map: MapComponent;

  constructor(private store: Store<fromRoot.State>, private mapStore: MapStore) {}

  ngOnInit(): void {
    this.districts$ = this.store.pipe(select(fromProject.getProjectDistricts));
    this.project$ = this.store.pipe(select(fromProject.getCurrentProject));
    this.comments$ = this.store.pipe(select(fromProject.getLatestComments('desc')));
    this.changes$ = this.store.pipe(select(fromProject.getHistory));
    this.showBasicInfo$ = this.store.pipe(select(fromProject.getShowBasicInfo));
    this.availableLayers$ = this.store.pipe(select(fromMapLayers.getLayerIds));
    this.selectedLayers$ = this.store.pipe(select(fromMapLayers.getSelectedLayerIds));
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
