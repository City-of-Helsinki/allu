import {ComponentFixture, TestBed, async} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../../src/feature/common/allu-common.module';
import {CommentsComponent} from '../../../../src/feature/application/comment/comments.component';
import {CommentComponent} from '../../../../src/feature/application/comment/comment.component';
import {ApplicationState} from '../../../../src/service/application/application-state';

class ApplicationStateMock {}

describe('CommentsComponent', () => {
  let comp: CommentsComponent;
  let fixture: ComponentFixture<CommentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AlluCommonModule, FormsModule],
      declarations: [CommentsComponent, CommentComponent],
      providers: [
        { provide: ApplicationState, useClass: ApplicationStateMock }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentsComponent);
    comp = fixture.componentInstance;
  });

  it('should show header', () => {
    let title = fixture.debugElement.query(By.css('h1 :first-child')).nativeElement;
    expect(title.textContent).toEqual('Kommentit');
  });
});
