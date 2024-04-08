import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {select, Store} from '@ngrx/store';

import {Project} from '@model/project/project';
import * as fromRoot from '@feature/allu/reducers';
import * as fromProject from '../reducers';
import {CityDistrict} from '@model/common/city-district';
import {MapComponent} from '@feature/map/map.component';
import {Comment} from '@model/application/comment/comment';
import {filter, takeUntil, takeWhile} from 'rxjs/operators';
import {ChangeHistoryItem} from '@model/history/change-history-item';
import {MatSlideToggleChange} from '@angular/material/slide-toggle';
import {ShowBasicInfo} from '../actions/project-actions';
import {shrinkFadeInOut} from '@feature/common/animation/common-animations';
import {MapLayer} from '@service/map/map-layer';
import {SearchSuccess} from '@feature/map/actions/application-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {TreeStructureNode} from '@feature/common/tree/tree-node';

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
  selectedLayersIds$: Observable<string[]>;
  selectedLayers$: Observable<MapLayer[]>;
  availableLayers$: Observable<MapLayer[]>;
  layerTree$: Observable<TreeStructureNode<void>>;

  private destroy$ = new Subject<boolean>();
  @ViewChild(MapComponent) private map: MapComponent;

  constructor(private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.districts$ = this.store.pipe(select(fromProject.getProjectDistricts));
    this.project$ = this.store.pipe(select(fromProject.getCurrentProject));
    this.comments$ = this.store.pipe(select(fromProject.getLatestComments('desc')));
    this.changes$ = this.store.pipe(select(fromProject.getHistory));
    this.showBasicInfo$ = this.store.pipe(select(fromProject.getShowBasicInfo));
    this.selectedLayersIds$ = this.store.pipe(select(fromProject.getSelectedLayerIds));
    this.availableLayers$ = this.store.pipe(select(fromProject.getAllLayers));
    this.selectedLayers$ = this.store.pipe(select(fromProject.getSelectedLayers));
    this.layerTree$ = this.store.pipe(select(fromProject.getTreeStructure));
  }

  ngAfterViewInit(): void {
    // TODO: This should be done using ngrx store after map is refactored
    combineLatest(
      this.store.select(fromProject.getApplications),
      this.store.select(fromProject.getShowBasicInfo)
    ).pipe(
      takeUntil(this.destroy$),
      takeWhile(() => !!this.map),
      filter(([applications, show]) => show)
    ).subscribe(([applications, show]) => {
      if (show && !!this.map) {
        this.store.dispatch(new SearchSuccess(ActionTargetType.Project, applications));
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
