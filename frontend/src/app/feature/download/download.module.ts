import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {DownloadComponent} from './download.component';
import {downloadRoutes} from './download.routing';
import {DownloadService} from './download.service';

@NgModule({
  imports: [
    RouterModule.forChild(downloadRoutes)
  ],
  declarations: [
    DownloadComponent
  ],
  providers: [
    DownloadService
  ]
})
export class DownloadModule {}
