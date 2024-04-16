import {TestBed} from '@angular/core/testing';
import {EffectsMetadata, getEffectsMetadata} from '@ngrx/effects';
import {UserAreaEffects} from '@feature/application/location/effects/user-area-effects.service';
import {LocationService} from '@service/location.service';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromAuth from '@feature/auth/reducers';
import {provideMockActions} from '@ngrx/effects/testing';
import {ReplaySubject} from 'rxjs/internal/ReplaySubject';
import {Observable} from 'rxjs/internal/Observable';
import {Feature, FeatureCollection, GeometryObject, Position} from 'geojson';
import {EMPTY} from 'rxjs';
import {LoggedUserLoaded} from '@feature/auth/actions/auth-actions';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {Load, LoadFailed, LoadSuccess} from '@feature/application/location/actions/user-area-actions';
import {of} from 'rxjs/internal/observable/of';
import {throwError} from 'rxjs/internal/observable/throwError';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {ErrorInfo} from '@service/error/error-info';
import {skip, take} from 'rxjs/operators';

class LocationServiceMock {
  public getUserAreas(): Observable<FeatureCollection<GeometryObject>> {
    return EMPTY;
  }
}

const featureCollection: FeatureCollection<GeometryObject> = {
  type: 'FeatureCollection',
  features: [
    {
      type: 'Feature',
      geometry: {
        type: 'Polygon',
        coordinates: [[1, 2, 3, 4]]
      },
      properties: {},
      id: 'id.1'
    }
  ]
};

describe('User area effects', () => {
  let effects: UserAreaEffects;
  let actions: ReplaySubject<any>;
  let metadata: EffectsMetadata<UserAreaEffects>;
  let locationService: LocationServiceMock;
  let store: Store<fromAuth.State>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({
          'auth': combineReducers(fromAuth.reducers)
        })
      ],
      providers: [
        UserAreaEffects,
        {provide: LocationService, useClass: LocationServiceMock},
        provideMockActions(() => actions),
      ],
    });

    effects = TestBed.inject(UserAreaEffects);
    metadata = getEffectsMetadata(effects);
    locationService = TestBed.inject(LocationService) as LocationServiceMock;
    store = TestBed.inject(Store);
    actions = new ReplaySubject(1);
  });

  it('should register load that dispatches an action', () => {
    expect(metadata.load).toEqual({ dispatch: true });
  });

  it('should dispatch LoadSuccess when user has rights and load is successful', () => {
    spyOn(locationService, 'getUserAreas').and.returnValue(of(featureCollection));
    const user = new User(1);
    user.assignedRoles = [RoleType.ROLE_PROCESS_APPLICATION];
    store.dispatch(new LoggedUserLoaded(user));
    actions.next(new Load());
    effects.load.subscribe(result => {
      expect(result).toEqual(new LoadSuccess(featureCollection));
    });
  });

  it('should not load when user has no rights', () => {
    spyOn(locationService, 'getUserAreas').and.returnValue(of(featureCollection));
    const user = new User(1);
    user.assignedRoles = [];
    store.dispatch(new LoggedUserLoaded(user));
    actions.next(new Load());
    expect(locationService.getUserAreas).toHaveBeenCalledTimes(0);
  });

  it('should dispatch LoadFailed and notification on error', () => {
    const errorInfo = new ErrorInfo('title');
    spyOn(locationService, 'getUserAreas').and.callFake(() => throwError(errorInfo));
    const user = new User(1);
    user.assignedRoles = [RoleType.ROLE_PROCESS_APPLICATION];
    store.dispatch(new LoggedUserLoaded(user));
    actions.next(new Load());

    effects.load.pipe(take(1)).subscribe(result => {
      expect(result).toEqual(new LoadFailed());
    });

    effects.load.pipe(skip(1), take(1)).subscribe(result => {
      expect(result).toEqual(new NotifyFailure(errorInfo));
    });
  });
});
