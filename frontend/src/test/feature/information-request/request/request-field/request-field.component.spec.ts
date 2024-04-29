import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlluCommonModule } from '@app/feature/common/allu-common.module';
import { ReactiveFormsModule } from '@angular/forms';
import { RequestFieldComponent } from '@app/feature/information-request/request/request-field/request-field.component';

describe('RequestFieldComponent', () => {
  let component: RequestFieldComponent;
  let fixture: ComponentFixture<RequestFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, ReactiveFormsModule],
      declarations: [ RequestFieldComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
