import {Component, DebugElement, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import { CommonModule } from '@angular/common';
import {UntypedFormGroup, FormsModule} from '@angular/forms';
import { ComponentFixture, fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {CommentListComponent} from '@feature/comment/comment-list.component';
import {CommentType} from '@model/application/comment/comment-type';
import {Comment} from '@model/application/comment/comment';
import {BehaviorSubject} from 'rxjs';

const COMMENT_ONE = new Comment(
  1,
  CommentType.INTERNAL,
  'Test comment one',
  new Date()
);

const COMMENT_TWO = new Comment(
  2,
  CommentType.INVOICING,
  'Test comment two',
  new Date()
);

@Component({
  selector: 'parent',
  template: `<comment-list [comments]="comments$ | async"
                           (save)="save($event)"
                           (remove)="remove($event)"></comment-list>`
})
class MockParentComponent {
  comments$ = new BehaviorSubject<Comment[]>([]);

  @ViewChild(CommentListComponent, { static: true })
  commentsComponent!: CommentListComponent;

  save(comment: Comment): void {}
  remove(comment: Comment): void {}
}

@Component({
  selector: 'comment',
  template: ''
})
class MockCommentComponent {
  @Input() comment: Comment = new Comment();
  @Output('save') onSave = new EventEmitter<Comment>();
  @Output('remove') onRemove = new EventEmitter<Comment>();

  form = new UntypedFormGroup({});
}

describe('CommentListComponent', () => {
  let parentComp: MockParentComponent;
  let fixture: ComponentFixture<MockParentComponent>;
  let comp: CommentListComponent;
  let de: DebugElement;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        AlluCommonModule
      ],
      declarations: [
        MockParentComponent,
        CommentListComponent,
        MockCommentComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockParentComponent);
    parentComp = fixture.componentInstance;
    comp = parentComp.commentsComponent;
    de = fixture.debugElement;

    parentComp.comments$.next([COMMENT_ONE, COMMENT_TWO]);
    fixture.detectChanges();
  });

  it('should show comments', async () => {
    await fixture.whenStable();
    expect(de.queryAll(By.css('li')).length).toEqual(2, 'Unexpected amount of comments');
  });

  it('should re-emit save', fakeAsync(() => {
    spyOn(parentComp, 'save');
    const commentComp = comp.children.first;
    const emittedComment = new Comment(1);
    commentComp.onSave.emit(emittedComment);
    expect(parentComp.save).toHaveBeenCalledWith(emittedComment);
  }));

  it('should re-emit remove', () => {
    spyOn(parentComp, 'remove');
    const commentComp = comp.children.first;
    const emittedComment = new Comment(1);
    commentComp.onRemove.emit(emittedComment);
    expect(parentComp.remove).toHaveBeenCalledWith(emittedComment);
  });

  it('should tell if any of the comments are marked as dirty', () => {
    const commentComp = comp.children.first;
    commentComp.form.markAsDirty();
    expect(comp.dirty).toEqual(true);
  });
});
