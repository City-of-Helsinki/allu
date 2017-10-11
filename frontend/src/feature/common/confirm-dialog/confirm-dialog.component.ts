import {Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
  selector: 'confirm-dialog',
  template: require('./confirm-dialog.component.html'),
  styles: [
    require('./confirm-dialog.component.scss')
  ]
})
export class ConfirmDialogComponent {
  @Input() confirmText = 'Vahvista';
  @Input() cancelText = 'Peruuta';

  title = 'Haluatko varmasti suorittaa toiminnon';
  description = '';

  constructor(public dialogRef: MatDialogRef<ConfirmDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {title: string, description: string}) {
    this.title = data.title || this.title;
    this.description = data.description || this.description;
  }

  cancel() {
    this.dialogRef.close(false);
  }

  confirm() {
    this.dialogRef.close(true);
  }
}
