import {Component, DebugElement, Input} from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {HistoryComponent} from '@feature/history/history.component';
import {By} from '@angular/platform-browser';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ChangeHistoryItem} from '@model/history/change-history-item';
import {User} from '@model/user/user';
import {ChangeHistoryItemInfo} from '@model/history/change-history-item-info';
import {TimeUtil} from '@util/time.util';
import {Load, LoadSuccess} from '@feature/history/actions/history-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {StructureMeta} from '@model/application/meta/structure-meta';
import { MatLegacySpinner as MatSpinner } from '@angular/material/legacy-progress-spinner';
import { MatLegacySlideToggle as MatSlideToggle } from '@angular/material/legacy-slide-toggle';
import {FieldChange} from '@model/history/field-change';

function createChangeHistoryItem(name: string, changeTime: Date): ChangeHistoryItem {
  const item = new ChangeHistoryItem();
  item.user = new User(1, 'userName');
  const itemInfo = new ChangeHistoryItemInfo(1, name);
  item.info = itemInfo;
  item.changeSpecifier = 'someSpecifier';
  item.changeType = 'changeType';
  item.changeTime = changeTime;
  return item;
}

const changesToday: ChangeHistoryItem[] = [
  createChangeHistoryItem('today1', new Date()),
  createChangeHistoryItem('today2', new Date())
];

const changesWithinWeek: ChangeHistoryItem[] = [
  createChangeHistoryItem('withinWeek1', TimeUtil.subract(new Date, 5, 'day')),
  createChangeHistoryItem('withinWeek2', TimeUtil.subract(new Date, 7, 'day'))
];

const olderChanges: ChangeHistoryItem[] = [
  createChangeHistoryItem('older1', TimeUtil.subract(new Date, 8, 'day')),
  createChangeHistoryItem('older2', TimeUtil.subract(new Date, 25, 'day')),
  createChangeHistoryItem('older3', TimeUtil.subract(new Date, 50, 'day')),
];

const changeHistory: ChangeHistoryItem[] = [
  ...changesToday,
  ...changesWithinWeek,
  ...olderChanges
];

@Component({
  selector: 'test-host',
  template: `
    <history [targetType]="'Application'"></history>
  `
}) class MockHostComponent {}

@Component({
  selector: 'history-item-group',
  template: ''
}) class MockHistoryItemGroupComponent {
  @Input() title: string;
  @Input() changes: ChangeHistoryItem[];
  @Input() meta: StructureMeta;
  @Input() fieldsVisible: boolean;
}

describe('HistoryComponent', () => {
  let fixture: ComponentFixture<MockHostComponent>;
  let testHost: MockHostComponent;
  let de: DebugElement;
  let historyComponent: HistoryComponent;
  let store: Store<fromRoot.State>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        StoreModule.forRoot({
          'application': combineReducers(fromApplication.reducers)
        })
      ],
      declarations: [
        MockHostComponent,
        HistoryComponent,
        MockHistoryItemGroupComponent
      ],
      providers: []
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockHostComponent);
    testHost = fixture.componentInstance;
    de = fixture.debugElement;
    store = TestBed.inject(Store);
    historyComponent = de.query(By.directive(HistoryComponent)).componentInstance;

    fixture.detectChanges();
  });

  it('should init', () => {
    expect(de.query(By.directive(HistoryComponent))).toBeDefined();
  });

  it('should show loading indicator when loading history', () => {
    store.dispatch(new Load(ActionTargetType.Application));
    fixture.detectChanges();
    expect(de.query(By.directive(MatSpinner))).toBeDefined();
  });

  it('should show grouped history items by time period', () => {
    store.dispatch(new LoadSuccess(ActionTargetType.Application, changeHistory));
    fixture.detectChanges();
    const groups = de.queryAll(By.directive(MockHistoryItemGroupComponent));

    expect(groups.length).toEqual(3);
    expect(groups[0].componentInstance.changes).toEqual(changesToday);
    expect(groups[1].componentInstance.changes).toEqual(changesWithinWeek);
    expect(groups[2].componentInstance.changes).toEqual(olderChanges);
  });

  it('should make group components show field changes when neccessary', () => {
    store.dispatch(new LoadSuccess(ActionTargetType.Application, changeHistory));
    fixture.detectChanges();

    const toggle = de.query(By.css('.mat-slide-toggle-label')).nativeElement;
    toggle.click();
    fixture.detectChanges();

    de.queryAll(By.directive(MockHistoryItemGroupComponent))
      .map(group => group.componentInstance)
      .forEach(groupComponent => expect(groupComponent.fieldsVisible).toEqual(true));
  });
});
