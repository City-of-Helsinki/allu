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
        {provide: Router, useClass: RouterMock},
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    const store = {};

    spyOn(localStorage, 'getItem').and.callFake( (key: string): string => {
      return store[key] || null;
    });
    spyOn(localStorage, 'removeItem').and.callFake((key: string): void =>  {
      delete store[key];
    });
    spyOn(localStorage, 'setItem').and.callFake((key: string, value: string): string =>  {
      return store[key] = <string>value;
    });
    fixture = TestBed.createComponent(Oauth2Component);
    comp = fixture.componentInstance;
    router = TestBed.get(Router) as RouterMock;
  });

  it('should redirect user when url is found in localstorage', () => {
    const url = '/some/url/to/redirect';
    localStorage.setItem('redirect_url', url);
    const navigateByUrl = spyOn(router, 'navigateByUrl');
    comp.ngOnInit();
    expect(localStorage.removeItem(REDIRECT_URL)).toBeUndefined();
     expect(navigateByUrl).toHaveBeenCalledWith(url);
  });

  it('should redirect to home when no url is found in localstorage', () => {
    const navigate = spyOn(router, 'navigate');
    comp.ngOnInit();
    expect(navigate).toHaveBeenCalledWith(['home']);
  });
});
