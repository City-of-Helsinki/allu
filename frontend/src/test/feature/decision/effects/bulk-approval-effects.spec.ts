import {TestBed} from '@angular/core/testing';
import {EffectsMetadata, getEffectsMetadata} from '@ngrx/effects';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import {provideMockActions} from '@ngrx/effects/testing';
import {ReplaySubject} from 'rxjs/internal/ReplaySubject';
import {Observable} from 'rxjs/internal/Observable';
import {EMPTY, of, throwError} from 'rxjs';
import {BulkApprovalEffects} from '@feature/decision/effects/bulk-approval-effects';
import {ApplicationStatus} from '@model/application/application-status';
import {DecisionDetails} from '@model/decision/decision-details';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {Application} from '@model/application/application';
import * as fromDecision from '@feature/decision/reducers';
import {DecisionService} from '@service/decision/decision.service';
import {ApplicationService} from '@service/application/application.service';
import {BulkApprovalEntry} from '@model/decision/bulk-approval-entry';
import {DistributionEntry} from '@model/common/distribution-entry';
import {DistributionType} from '@model/common/distribution-type';
import {
  Approve,
  ApproveComplete,
  ApproveEntryComplete,
  BulkApprovalActionType,
  Load,
  LoadComplete
} from '@feature/decision/actions/bulk-approval-actions';
import {take} from 'rxjs/operators';
import {ErrorInfo} from '@service/error/error-info';

class DecisionServiceMock {
  public sendByStatus(applicationId: number, status: ApplicationStatus, emailDetails: DecisionDetails): Observable<object> {
    return EMPTY;
  }

  public getBulkApprovalEntries(applicationIds: number[]): Observable<BulkApprovalEntry[]> {
    return EMPTY;
  }
}

class ApplicationServiceMock {
  public changeStatus(appId: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    return EMPTY;
  }
}

const defaultDistribution = [
  new DistributionEntry(5, 'some name', DistributionType.EMAIL, 'some.name@foo.bar'),
  new DistributionEntry(6, 'some other name', DistributionType.EMAIL, 'some.other.name@foo.bar')
];

const approvalEntries = [
  new BulkApprovalEntry(1, 'application1', ApplicationStatus.DECISION, false, undefined, defaultDistribution),
  new BulkApprovalEntry(2, 'application2', ApplicationStatus.OPERATIONAL_CONDITION, false, undefined, defaultDistribution),
  new BulkApprovalEntry(3, 'application2', ApplicationStatus.FINISHED, false, undefined, defaultDistribution)
];

describe('Bulk approval effects', () => {
  let effects: BulkApprovalEffects;
  let actions$: ReplaySubject<any>;
  let metadata: EffectsMetadata<BulkApprovalEffects>;
  let decisionService: DecisionServiceMock;
  let applicationService: ApplicationServiceMock;
  let store: Store<fromDecision.State>;

  let changeStatusSpy: jasmine.Spy;
  let sendDecisionSpy: jasmine.Spy;
  let dispatchSpy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({
          'decision': combineReducers(fromDecision.reducers)
        })
      ],
      providers: [
        BulkApprovalEffects,
        {provide: DecisionService, useClass: DecisionServiceMock},
        {provide: ApplicationService, useClass: ApplicationServiceMock},
        provideMockActions(() => actions$),
      ],
    });

    effects = TestBed.inject(BulkApprovalEffects);
    metadata = getEffectsMetadata(effects);
    decisionService = TestBed.inject(DecisionService) as DecisionServiceMock;
    applicationService = TestBed.inject(ApplicationService) as ApplicationServiceMock;
    store = TestBed.inject(Store);
    actions$ = new ReplaySubject(1);
  });

  beforeEach(() => {
    changeStatusSpy = spyOn(applicationService, 'changeStatus');
    sendDecisionSpy = spyOn(decisionService, 'sendByStatus');
    dispatchSpy = spyOn(store, 'dispatch');

    changeStatusSpy.and.returnValue(of(new Application()));
    sendDecisionSpy.and.returnValue(of({}));
    dispatchSpy.and.callThrough();
  });

  it('should register effects', () => {
    expect(metadata.loadBulkApprovalEntries).toEqual({ dispatch: true, useEffectsErrorHandler: true });
    expect(metadata.approve).toEqual({ dispatch: true, useEffectsErrorHandler: true });
  });

  it('should return loaded entries', () => {
    spyOn(decisionService, 'getBulkApprovalEntries').and.returnValue(of(approvalEntries));
    actions$.next(new Load([1, 2, 3]));
    effects.loadBulkApprovalEntries.pipe(take(1)).subscribe((action: LoadComplete) => {
      expect(action.type).toEqual(BulkApprovalActionType.LoadComplete);
      expect(action.payload.entries).toEqual(approvalEntries);
      expect(action.payload.error).toBeFalsy();
    });
  });

  it('should return completed with error when error happens', () => {
    const errorInfo = new ErrorInfo('title');
    spyOn(decisionService, 'getBulkApprovalEntries').and.callFake(() => throwError(errorInfo));
    actions$.next(new Load([1, 2, 3]));
    effects.loadBulkApprovalEntries.pipe(take(1)).subscribe((action: LoadComplete) => {
      expect(action.type).toEqual(BulkApprovalActionType.LoadComplete);
      expect(action.payload.entries).toEqual([]);
      expect(action.payload.error).toEqual(errorInfo);
    });
  });

  it('should do bulk approve and complete', () => {
    actions$.next(new Approve(approvalEntries));
    effects.approve.pipe(take(1)).subscribe((action: ApproveComplete) => {
      expect(action.type).toEqual(BulkApprovalActionType.ApproveComplete);
      verifyChangeStatusForAll(approvalEntries);
      verifySendByStatusForAll(approvalEntries);
      verifyApproveEntryComplete(approvalEntries.map(entry => entry.id));
    });
  });

  it('should handle and report errors but approve rest', () => {
    const errorInfo = new ErrorInfo('title');
    const failingId = approvalEntries[1].id;
    sendDecisionSpy.and.callFake((id: number, targetState: ApplicationStatus, info: StatusChangeInfo) => {
      if (failingId === id) {
        throwError(errorInfo);
      }
      return of({});
    });
    const expectedToSucceed = approvalEntries.filter(entry => entry.id !== failingId);
    const expectedToFail = approvalEntries.filter(entry => entry.id === failingId);

    actions$.next(new Approve(approvalEntries));
    effects.approve.pipe(take(1)).subscribe((action: ApproveComplete) => {
      expect(action.type).toEqual(BulkApprovalActionType.ApproveComplete);
      verifyChangeStatusForAll(approvalEntries);
      verifySendByStatusForAll(approvalEntries);
      verifyApproveEntryComplete(expectedToSucceed.map(entry => entry.id));
      verifyApproveEntryComplete(expectedToFail.map(entry => entry.id, errorInfo));
    });
  });

  const verifyChangeStatusForAll = (entries: BulkApprovalEntry[]) => entries.every(entry => verifyChangeStatus(entry));

  const verifyChangeStatus = (entry: BulkApprovalEntry) =>
    expect(applicationService.changeStatus).toHaveBeenCalledWith(entry.id, entry.targetState, new StatusChangeInfo());

  const verifySendByStatusForAll = (entries: BulkApprovalEntry[]) => entries.every(entry => verifySendByStatus(entry));

  const verifySendByStatus = (entry: BulkApprovalEntry) =>
    expect(decisionService.sendByStatus).toHaveBeenCalledWith(entry.id, entry.targetState, new DecisionDetails(entry.distributionList));

  const verifyApproveEntryComplete = (ids: number[], error?: ErrorInfo) => {
    if (error) {
      ids.every(id => expect(store.dispatch).toHaveBeenCalledWith(new ApproveEntryComplete({id, error})));
    } else {
      ids.every(id => expect(store.dispatch).toHaveBeenCalledWith(new ApproveEntryComplete({id})));
    }
  };
});
