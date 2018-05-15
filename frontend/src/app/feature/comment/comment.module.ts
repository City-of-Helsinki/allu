import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {CommentsComponent} from './comments.component';
import {CommentComponent} from './comment.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {CommentService} from '../../service/application/comment/comment.service';
import {EffectsModule} from '@ngrx/effects';
import {CommentEffects} from './effects/comment-effects';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    EffectsModule.forFeature([CommentEffects])
  ],
  declarations: [
    CommentsComponent,
    CommentComponent
  ],
  providers: [
    CommentService
  ],
  exports: [
    CommentsComponent,
    CommentComponent
  ]
})
export class CommentModule {}
