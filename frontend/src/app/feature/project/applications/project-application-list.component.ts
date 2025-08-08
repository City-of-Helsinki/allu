import {AfterViewInit, ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {Application} from '@model/application/application';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {Subject} from 'rxjs';
import {Option, Some} from '@util/option';
import * as fromRoot from '../../allu/reducers';
import {Store} from '@ngrx/store';
import {Dictionary} from '@ngrx/entity/src/models';
import {CityDistrict} from '@model/common/city-district';
import {take} from 'rxjs/internal/operators';

@Component({
  selector: 'project-application-list',
  templateUrl: './project-application-list.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationListComponent implements OnInit, AfterViewInit, OnDestroy {

  @Input() controls = false;
  @Input() loading = false;

  @Output() applicationAdd = new EventEmitter<number>();
  @Output() applicationRemove = new EventEmitter<number>();

  dataSource = new MatTableDataSource<ApplicationElement>([]);

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  displayedColumns = [
    'ownerName', 'applicationId', 'type', 'status',
    'customerName', 'cityDistrict',
    'creationTime', 'startTime'
  ];

  private destroy = new Subject<boolean>();
  private districts: Dictionary<CityDistrict>;

  constructor(private router: Router,
              private store: Store<fromRoot.State>) {
    this.store.select(fromRoot.getCityDistrictEntities).pipe(take(1))
      .subscribe(districts => this.districts = districts);
  }

  ngOnInit(): void {
    this.displayedColumns = this.controls
      ? ['controls'].concat(this.displayedColumns)
      : this.displayedColumns;
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set applications(applications: Application[]) {
    this.dataSource.data = applications.map(app => this.toApplicationElement(app));
  }

  private toApplicationElement(application: Application): ApplicationElement {
    return {
      id: application.id,
      ownerName: Some(application.owner).map(owner => owner.realName).orElse(undefined),
      applicationId: application.applicationId,
      type: application.type,
      status: application.status,
      customerName: Some(application.applicant.customer).map(c => c.name).orElse(undefined),
      cityDistrict: this.cityDistrict(application).map(cd => cd.name).orElse(undefined),
      creationTime: application.creationTime,
      startTime: application.startTime,
    };
  }

  private cityDistrict(application: Application): Option<CityDistrict> {
    return Some(application.firstLocation)
      .map(l => l.effectiveCityDistrictId)
      .map(id => this.districts[id]);
  }
}

interface ApplicationElement {
  id: number;
  ownerName: string;
  applicationId: string;
  type: string;
  status: string;
  customerName: string;
  cityDistrict: string;
  creationTime: Date;
  startTime: Date;
}
