import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {UserListComponent} from '@feature/admin/user/user-list.component';
import {Router, provideRouter} from '@angular/router';
import {UserServiceMock} from 'test/mocks';
import {UserService} from '@service/user/user-service';
import {Store, StoreModule} from '@ngrx/store';
import {DebugElement, LOCALE_ID} from '@angular/core';
import {By} from '@angular/platform-browser';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {User} from '@model/user/user';
import {of} from 'rxjs/internal/observable/of';
import * as fromRoot from '@feature/allu/reducers';
import * as CityDistrictActions from '@feature/allu/actions/city-district-actions';
import {CityDistrict} from '@model/common/city-district';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const users: User[] = [
  new User(2, 'user2', 'realnameSame', 'email', 'phone', 'title', false, new Date(2019, 1, 1)),
  new User(3, 'user3', 'realnameSame', 'email', 'phone', 'title', true, new Date(2018, 2, 2)),
  new User(1, 'user1', 'realnameOther', 'email', 'phone', 'title', true, new Date(2018, 3, 3))
];

describe('UserListComponent', () => {
  let comp: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;
  let de: DebugElement;
  let router: Router;
  let userService: UserServiceMock;
  let store: Store<fromRoot.State>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        StoreModule.forRoot(fromRoot.reducers),
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        NoopAnimationsModule
      ],
      declarations: [
        UserListComponent
      ],
      providers: [
        provideRouter([]),
        {provide: UserService, useClass: UserServiceMock},
        {provide: LOCALE_ID, useValue: 'fi'}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserListComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    router = TestBed.inject(Router) as Router;
    userService = TestBed.inject(UserService) as UserServiceMock;
    store = TestBed.inject(Store);

    spyOn(userService, 'getAllUsers').and.returnValue(of(users));
    store.dispatch(new CityDistrictActions.LoadSuccess([new CityDistrict(1, 1, 'cd1')]));

    fixture.detectChanges();
  });

  it('should initialize', () => {
    expect(de.query(By.css('.main-content'))).toBeDefined();
  });

  it('should list existing users', () => {
    expect(de.queryAll(By.css('.mat-row')).length).toEqual(users.length);
  });

  it('should sort by clicked field', () => {
    const firstRow = de.queryAll(By.css('.mat-row'))[0];
    expect(firstRow.query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[0].userName);
    const usernameHeader = de.query(By.css('.mat-column-userName')).nativeElement;

    // Sort by username
    usernameHeader.click();
    const firstRowAfterSort = de.queryAll(By.css('.mat-row'))[0];
    expect(firstRowAfterSort.query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[2].userName);
  });

  it('should sort by date', () => {
    const lastLoginHeader = de.query(By.css('.mat-column-lastLogin')).nativeElement;

    lastLoginHeader.click();
    const rowAscending = de.queryAll(By.css('.mat-row'));
    expect(rowAscending[0].query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[1].userName);
    expect(rowAscending[1].query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[2].userName);
    expect(rowAscending[2].query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[0].userName);

    lastLoginHeader.click();
    const rowsDescending = de.queryAll(By.css('.mat-row'));
    expect(rowsDescending[0].query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[0].userName);
    expect(rowsDescending[1].query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[2].userName);
    expect(rowsDescending[2].query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[1].userName);
  });

  it('should filter users by given filter', () => {
    const filterInput: HTMLInputElement = de.query(By.css('input')).nativeElement;
    filterInput.value = 'realNameOther';
    filterInput.dispatchEvent(new Event('keyup'));

    fixture.detectChanges();
    expect(de.queryAll(By.css('.mat-row')).length).toEqual(1);
    const row = de.queryAll(By.css('.mat-row'))[0];
    expect(row.query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[2].userName);
  });

  it('should filter by partial value', () => {
    const filterInput: HTMLInputElement = de.query(By.css('input')).nativeElement;
    filterInput.value = 'realNameOth';
    filterInput.dispatchEvent(new Event('keyup'));

    fixture.detectChanges();
    expect(de.queryAll(By.css('.mat-row')).length).toEqual(1);
    const row = de.queryAll(By.css('.mat-row'))[0];
    expect(row.query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[2].userName);
  });

  it('should filter by multiple filter values', () => {
    const filterInput: HTMLInputElement = de.query(By.css('input')).nativeElement;
    filterInput.value = 'realNameSame Kyllä'; // realName and isActive
    filterInput.dispatchEvent(new Event('keyup'));

    fixture.detectChanges();
    expect(de.queryAll(By.css('.mat-row')).length).toEqual(1);
    const row = de.queryAll(By.css('.mat-row'))[0];
    expect(row.query(By.css('.mat-column-userName')).nativeElement.textContent).toEqual(users[1].userName);
  });
});
