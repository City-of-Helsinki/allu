import {AfterViewInit, ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Project} from '../../../model/project/project';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {MatLegacyTableDataSource as MatTableDataSource} from '@angular/material/legacy-table';
import * as fromRoot from '../../allu/reducers';
import {Router} from '@angular/router';
import {Store} from '@ngrx/store';
import {take} from 'rxjs/internal/operators';
import {Dictionary} from '@ngrx/entity/src/models';
import {CityDistrict} from '../../../model/common/city-district';

@Component({
  selector: 'related-project-list',
  templateUrl: './related-project-list.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RelatedProjectListComponent implements OnInit, AfterViewInit {

  @Input() loading: boolean;

  @Output() remove = new EventEmitter<number>();

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  displayedColumns = [
    'controls', 'identifier', 'ownerName', 'active',
    'cityDistricts', 'startTime', 'endTime'
  ];

  dataSource = new MatTableDataSource<ProjectElement>([]);

  private districts: Dictionary<CityDistrict>;

  constructor(private router: Router,
              private store: Store<fromRoot.State>) {
    this.store.select(fromRoot.getCityDistrictEntities).pipe(take(1))
      .subscribe(districts => this.districts = districts);
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  @Input() set projects(projects: Project[]) {
    this.dataSource.data = projects
      ? projects.map(p => ({
        id: p.id,
        identifier: p.identifier,
        ownerName: p.customer ? p.customer.name : undefined,
        active: p.active,
        cityDistricts: this.cityDistricts(p.cityDistricts).map(cd => cd.name),
        startTime: p.startTime,
        endTime: p.endTime
      }))
      : [];
  }

  private cityDistricts(cityDistrictIds: number[] = []): CityDistrict[] {
    return cityDistrictIds.map(id => this.districts[id]);
  }
}

export interface ProjectElement {
  id: number;
  identifier: string;
  ownerName: string;
  active: boolean;
  cityDistricts: string[];
  startTime: Date;
  endTime: Date;
}
