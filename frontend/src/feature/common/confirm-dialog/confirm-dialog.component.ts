import {Component, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';

@Component({
  selector: 'confirm-dialog',
  template: require('./confirm-dialog.component.html'),
  styles: [
    require('./confirm-dialog.component.scss')
  ]
})
export class ConfirmDialogComponent {
  @Input() title = 'Haluatko varmasti suorittaa toiminnon';
  @Input() description = '';
  @Input() confirmText = 'Vahvista';
  @Input() cancelText = 'Peruuta';

  constructor(public dialogRef: MdDialogRef<ConfirmDialogComponent>) {}

  cancel() {
    this.dialogRef.close(false);
  }

  confirm() {
    this.dialogRef.close(true);
  }
}
