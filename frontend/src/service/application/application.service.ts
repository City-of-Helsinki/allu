import {Injectable} from '@angular/core';
import {AuthHttp} from '../../../node_modules/angular2-jwt/angular2-jwt';
import {URLSearchParams} from '@angular/http';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {ApplicationLoadFilter} from '../../event/load/application-load-filter';
import {StructureMetaMapper} from './../mapper/structure-meta-mapper';
import {StructureMeta} from '../../model/application/structure-meta';
import {ApplicationHub} from './application-hub';
import {ApplicationSearch} from './application-hub';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationLocationQueryMapper} from './../mapper/application-location-query-mapper';
import {UIStateHub} from './../ui-state/ui-state-hub';
import {ErrorUtil} from '../../util/error.util.ts';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status-change';
import {translations} from '../../util/translations';

@Injectable()
export class ApplicationService {
  static APPLICATIONS_URL = '/api/applications';
  static SEARCH_LOCATION = '/search_location';
  static METADATA_URL = '/api/meta';

  private statusToUrl = new Map<ApplicationStatus, string>();

  constructor(private authHttp: AuthHttp, private applicationHub: ApplicationHub, private uiState: UIStateHub) {
    applicationHub.applicationSearch().subscribe((search) => this.applicationSearch(search));
    applicationHub.applicationStatusChange().subscribe((statusChange) => this.applicationStatusChange(statusChange));

    this.statusToUrl.set(ApplicationStatus.CANCELLED, 'status/cancelled');
    this.statusToUrl.set(ApplicationStatus.PENDING, 'status/pending');
    this.statusToUrl.set(ApplicationStatus.HANDLING, 'status/handling');
    this.statusToUrl.set(ApplicationStatus.DECISIONMAKING, 'status/decisionmaking');
    this.statusToUrl.set(ApplicationStatus.DECISION, 'status/decision');
    this.statusToUrl.set(ApplicationStatus.REJECTED, 'status/rejected');
    this.statusToUrl.set(ApplicationStatus.RETURNED_TO_PREPARATION, 'status/toPreparation');
    this.statusToUrl.set(ApplicationStatus.FINISHED, 'status/finished');
  }

  public listApplications(filter: ApplicationLoadFilter): Promise<Array<Application>> {
    let searchUrl = ApplicationService.APPLICATIONS_URL;
    let params = new URLSearchParams();

    console.log('ApplicationService.listApplications', filter);
    if (filter.applicationId) {
      searchUrl = searchUrl + '/' + filter.applicationId;
      return new Promise<Array<Application>>((resolve, reject) =>
        this.authHttp.get(searchUrl, { search: params }).subscribe(
          data => {
            console.log('ApplicationService.listApplications', data);
            let applications: Array<Application> = [ ApplicationMapper.mapBackend(data.json()) ];
            console.log('Listing applications', applications);
            resolve(applications);
          },
          err => console.log('ApplicationService.listApplications', err),
          () => console.log('Request Complete')
        )
      );
    } else {
      if (filter.handler) {
        params.set('handler', filter.handler);
      } else {
        params.set('handler', 'TestHandler');
      }
      return new Promise<Array<Application>>((resolve, reject) =>
        this.authHttp.get(searchUrl, { search: params }).subscribe(
          data => {
            console.log('ApplicationService.listApplications', data);
            let json = data.json();
            if (json) {
              let applications: Array<Application> = json.map((p) => ApplicationMapper.mapBackend(p));
              console.log('Listing applications', applications);
              resolve(applications);
            } else {
              resolve([]);
            }
          },
          err => console.log('ApplicationService.listApplications', err),
          () => console.log('Request Complete')
        )
      );
    }
  }

  public addApplication(application: Application): Promise<Application> {
    console.log('ApplicationService.addApplication', application);
    return new Promise<Application>((resolve, reject) =>
      this.authHttp.post(
        ApplicationService.APPLICATIONS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .subscribe(
          data => { 
            console.log('ApplicationService.addApplication processing', data.json());
            console.log('ApplicationService.addApplication map', data.json());
            let appl = ApplicationMapper.mapBackend(data.json());
            console.log('Created application', appl);
            resolve(appl);
          },
          err => {
            console.log('ApplicationService.addApplication error', err);
            reject(err);
          },
          () => console.log('Request Complete')
        )
    );
  }

  public updateApplication(application: Application): Promise<Application> {
    console.log('ApplicationService.updateApplication', application);
    return new Promise<Application>((resolve, reject) =>
      this.authHttp.put(
        ApplicationService.APPLICATIONS_URL + '/' + application.id,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .subscribe(
          data => {
            console.log('ApplicationService.updateApplication processing', data.json());
            let appl = ApplicationMapper.mapBackend(data.json());
            console.log('Created application', appl);
            resolve(appl);
          },
          err => {
            console.log('ApplicationService.updateApplication error', err);
            reject(err);
          },
          () => console.log('Request Complete')
        )
    );
  }

  public loadApplicationMetadata(applicationType: string): Promise<StructureMeta> {
    console.log('ApplicationService.loadApplicationMetadata', applicationType);
    return new Promise<StructureMeta>((resolve, reject) =>
      this.authHttp.get(ApplicationService.METADATA_URL + '/' + applicationType).subscribe(
        data => {
          console.log('ApplicationService.loadApplicationMetadata', data);
          let structureMeta = StructureMetaMapper.mapBackend(data.json());
          console.log('Loaded metadata', structureMeta);
          resolve(structureMeta);
        },
        err => console.log('ApplicationService.loadApplicationMetadata', err),
        () => console.log('Request Complete')
      )
    );
  }

  private applicationSearch(search: ApplicationSearch) {
    console.log('ApplicationService.applicationSearch', search);
    if (search instanceof ApplicationLocationQuery) {
      let searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH_LOCATION;
      this.authHttp.post(
          searchUrl,
          JSON.stringify(ApplicationLocationQueryMapper.mapFrontend(search)))
        .map(response => response.json())
        .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
        .subscribe(
          applications => this.applicationHub.addApplications(applications),
          err => this.uiState.addError(ErrorUtil.extractMessage(err))
        );
    } else if (!Number.isNaN(search)) {
      this.authHttp.get(ApplicationService.APPLICATIONS_URL + '/' + search)
        .map(response => response.json())
        .map(app => [ApplicationMapper.mapBackend(app)])
        .subscribe(
          applications => this.applicationHub.addApplications(applications),
          err => this.uiState.addError(ErrorUtil.extractMessage(err))
        );
    }
  }

  private applicationStatusChange(statusChange: ApplicationStatusChange) {
    let url = ApplicationService.APPLICATIONS_URL + '/' + statusChange.id + '/' + this.statusToUrl.get(statusChange.status);
    this.authHttp.put(url, JSON.stringify(ApplicationMapper.mapComment(statusChange.comment)))
      .subscribe(
        response => this.uiState.addMessage('Application status changed to ' + ApplicationStatus[statusChange.status]),
        err => this.uiState.addError(translations.application.error.statusChangeFailed)
      );
  }
}
