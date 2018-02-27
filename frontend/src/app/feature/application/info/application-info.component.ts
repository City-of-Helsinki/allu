import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {ApplicationStore} from '../../../service/application/application-store';
import {UrlUtil} from '../../../util/url.util';
import {ActivatedRoute} from '@angular/router';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {findTranslation} from '../../../util/translations';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit, CanComponentDeactivate {

  type: string;
  showDraftSelection: boolean;
  readonly: boolean;
  formDirty: boolean;

  constructor(private applicationStore: ApplicationStore, private route: ActivatedRoute, private dialog: MatDialog) {}

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.type = application.type;
    this.showDraftSelection = this.applicationStore.isNew;

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary');
    this.formDirty = false;
  }

  formDirtyChanged(dirty: boolean): void {
    this.formDirty = dirty;
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.formDirty) {
      return this.confirmChanges();
    } else {
      return true;
    }
  }

  private confirmChanges(): Observable<boolean> {
    const data = {
      title: findTranslation(['application.confirmDiscard.title']),
      description: findTranslation(['application.confirmDiscard.description']),
      confirmText: findTranslation(['application.confirmDiscard.confirmText']),
      cancelText: findTranslation(['application.confirmDiscard.cancelText'])
    };
    return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
  }
}
