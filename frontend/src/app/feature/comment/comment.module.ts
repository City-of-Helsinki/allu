import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {CommentListComponent} from './comment-list.component';
import {CommentComponent} from './comment.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {CommentService} from '../../service/application/comment/comment.service';
import {EffectsModule} from '@ngrx/effects';
import {CommentEffects} from './effects/comment-effects';
import {CommentsComponent} from './comments.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    EffectsModule.forFeature([CommentEffects])
  ],
  declarations: [
    CommentListComponent,
    CommentComponent,
    CommentsComponent
  ],
  providers: [
    CommentService
  ],
  exports: [
    CommentComponent,
    CommentsComponent
  ]
})
export class CommentModule {}
