import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {Router} from '@angular/router';
import {Application} from '../../../model/application/application';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {Subject} from 'rxjs/Subject';
import {Some} from '../../../util/option';

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

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns = [
    'ownerName', 'applicationId', 'type', 'status',
    'customerName', 'cityDistrict',
    'creationTime', 'startTime'
  ];

  private destroy = new Subject<boolean>();

  constructor(private router: Router,
              private cityDistrictService: CityDistrictService) {}

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
    const cityDistrict = Some(application.firstLocation)
      .map(l => l.effectiveCityDistrictId)
      .map(id => this.cityDistrictService.nameImmediate(id));

    return {
      id: application.id,
      ownerName: Some(application.owner).map(owner => owner.realName).orElse(undefined),
      applicationId: application.applicationId,
      type: application.type,
      status: application.status,
      customerName: Some(application.applicant.customer).map(c => c.name).orElse(undefined),
      cityDistrict: cityDistrict.orElse(undefined),
      creationTime: application.creationTime,
      startTime: application.startTime,
    };
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
