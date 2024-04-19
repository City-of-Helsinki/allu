import {Component, Inject} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {Some} from '../../../util/option';

export const CONFIRM_DIALOG_MODAL_CONFIG = {
  width: '600px',
  data: {}
};

export interface ConfirmDialogData {
  title: string;
  description: string;
  confirmText: string;
  cancelText: string;
}

@Component({
  selector: 'confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: [
    './confirm-dialog.component.scss'
  ]
})
export class ConfirmDialogComponent {
  title = 'Haluatko varmasti suorittaa toiminnon';
  description = '';
  confirmText = 'Vahvista';
  cancelText = 'Peruuta';

  constructor(public dialogRef: MatDialogRef<ConfirmDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData) {
    this.title = Some(data.title).orElse(this.title);
    this.description = Some(data.description).orElse(this.description);
    this.confirmText = Some(data.confirmText).orElse(this.confirmText);
    this.cancelText = Some(data.cancelText).orElse(this.cancelText);
  }

  cancel() {
    this.dialogRef.close(false);
  }

  confirm() {
    this.dialogRef.close(true);
  }
}
