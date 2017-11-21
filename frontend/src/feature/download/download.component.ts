import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {DownloadService} from './download.service';
import * as filesaver from 'filesaver';
import {NotificationService} from '../../service/notification/notification.service';

/**
 * Generic empty component for downloading files from backend
 */
@Component({
  selector: 'download',
  template: ''
})
export class DownloadComponent implements OnInit {

  constructor(private router: Router, private downloadService: DownloadService) { }

  ngOnInit() {
    const url = this.router.routerState.snapshot.url;
    const downloadUrl = url.replace('/download', '');

    this.downloadService.download(downloadUrl)
      .subscribe(file => {
        filesaver.saveAs(file, file.name);
        this.router.navigate(['home']);
      }, err => NotificationService.error(err));
  }
}
