import {Component, DebugElement, ViewChild} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Feature, GeometryObject, Point} from 'geojson';
import {UserAreasComponent} from '@feature/application/location/user-areas/user-areas.component';
import {Subject} from 'rxjs/internal/Subject';
import {getElementText} from 'test/selector-helpers';

@Component({
  selector: 'parent',
  template: `<user-areas [loading]="loading$ | async"
                         [userAreas]="userAreas$ | async"
                         (areasSelected)="areasSelected($event)"></user-areas>`
})
class MockParentComponent {
  loading$: Subject<boolean> = new Subject<boolean>();
  userAreas$: Subject<Feature<GeometryObject>[]> = new Subject<Feature<GeometryObject>[]>();

  @ViewChild(UserAreasComponent) userAreasComponent: UserAreasComponent;

  areasSelected(selected: Feature<GeometryObject>[]): void {}
}

describe('UserAreasComponent', () => {
  let parentComp: MockParentComponent;
  let fixture: ComponentFixture<MockParentComponent>;
  let comp: UserAreasComponent;
  let de: DebugElement;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule
      ],
      declarations: [
        MockParentComponent,
        UserAreasComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockParentComponent);
    parentComp = fixture.componentInstance;
    comp = parentComp.userAreasComponent;
    de = fixture.debugElement;
    fixture.detectChanges();
  });

  it('should show loading when loading', () => {
    parentComp.loading$.next(true);
    fixture.detectChanges();
    expect(de.query(By.css('.mat-progress-spinner'))).toBeDefined();
    expect(de.queryAll(By.css('.mat-list-item')).length).toEqual(0);
  });

  it('should show selectable user areas', () => {
    const features: Feature<GeometryObject>[] = [
      createFeature('area 1', createPoint(1, 2)),
      createFeature('area 2', createPoint(5, 6))
    ];
    parentComp.userAreas$.next(features);
    parentComp.loading$.next(false);
    fixture.detectChanges();
    const listItems: DebugElement[] = de.queryAll(By.css('.mat-list-item'));
    expect(listItems.length).toEqual(2);
    expect(getElementText(listItems[0], '.menu-row-header')).toEqual(features[0].properties.id);
    expect(getElementText(listItems[1], '.menu-row-header')).toEqual(features[1].properties.id);
  });

  it('should emit selected area', () => {
    spyOn(parentComp, 'areasSelected').and.callThrough();
    const features: Feature<GeometryObject>[] = [
      createFeature('area 1', createPoint(1, 2)),
      createFeature('area 2', createPoint(5, 6))
    ];
    parentComp.userAreas$.next(features);
    parentComp.loading$.next(false);
    fixture.detectChanges();
    const listItems: DebugElement[] = de.queryAll(By.css('.mat-list-item'));
    listItems[0].nativeElement.click();
    fixture.detectChanges();
    expect(parentComp.areasSelected).toHaveBeenCalledWith([features[0]]);
  });
});

function createFeature(id: string, geometry?: GeometryObject): Feature<GeometryObject> {
  return {
    type: 'Feature',
    id,
    geometry,
    properties: {
      id: id
    }
  };
}

function createPoint(x: number, y: number): Point {
  return {
    type: 'Point',
    coordinates: [x, y]
  };
}
