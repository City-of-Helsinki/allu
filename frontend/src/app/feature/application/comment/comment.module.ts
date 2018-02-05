import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {CommentsComponent} from './comments.component';
import {CommentComponent} from './comment.component';
import {CommentService} from '../../../service/application/comment/comment.service';
import {CommentHub} from '../../../service/application/comment/comment-hub';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule
  ],
  declarations: [
    CommentsComponent,
    CommentComponent
  ],
  providers: [
    CommentService,
    CommentHub
  ]
})
export class CommentModule {}
