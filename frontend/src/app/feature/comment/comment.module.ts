import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {CommentListComponent} from './comment-list.component';
import {CommentComponent} from './comment.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {CommentService} from '../../service/application/comment/comment.service';
import {EffectsModule} from '@ngrx/effects';
import {CommentEffects} from './effects/comment-effects';
import {CommentsComponent} from './comments.component';
import {CommentPreviewListComponent} from './comment-preview-list.component';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    EffectsModule.forFeature([CommentEffects]),
    RouterModule.forChild([])
  ],
  declarations: [
    CommentListComponent,
    CommentComponent,
    CommentsComponent,
    CommentPreviewListComponent
  ],
  providers: [
    CommentService
  ],
  exports: [
    CommentComponent,
    CommentsComponent,
    CommentPreviewListComponent
  ]
})
export class CommentModule {}
