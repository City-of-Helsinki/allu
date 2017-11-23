import {RouterMock} from '../../mocks';
import {Router} from '@angular/router';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {REDIRECT_URL} from '../../../src/util/local-storage';
import {Oauth2Component} from '../../../src/app/feature/oauth2/oauth2.component';


describe('Oauth2Component', () => {
  let comp: Oauth2Component;
  let fixture: ComponentFixture<Oauth2Component>;
  let router: RouterMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
      ],
      declarations: [
        Oauth2Component
      ],
      providers: [
        FormBuilder,
        {provide: Router, useClass: RouterMock}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Oauth2Component);
    comp = fixture.componentInstance;
    router = TestBed.get(Router) as RouterMock;
  });

  it('should redirect user when url is found in localstorage', () => {
    const url = '/some/url/to/redirect';
    spyOn(localStorage, 'getItem').and.returnValue(url);
    const removeItem = spyOn(localStorage, 'removeItem');
    const navigateByUrl = spyOn(router, 'navigateByUrl');

    comp.ngOnInit();
    expect(removeItem).toHaveBeenCalledWith(REDIRECT_URL);
    expect(navigateByUrl).toHaveBeenCalledWith(url);
  });

  it('should redirect to home when no url is found in localstorage', () => {
    spyOn(localStorage, 'getItem').and.returnValue(undefined);
    const removeItem = spyOn(localStorage, 'removeItem');
    const navigate = spyOn(router, 'navigate');

    comp.ngOnInit();
    expect(removeItem).not.toHaveBeenCalled();
    expect(navigate).toHaveBeenCalledWith(['home']);
  });
});
