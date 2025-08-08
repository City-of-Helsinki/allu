import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DistributionEntry} from '@model/common/distribution-entry';
import {SaveDistributionAndNotify} from '@feature/application/actions/application-actions';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';

export interface DistributionModalData {
  distribution: DistributionEntry[];
}

export const DISTRIBUTION_MODAL_CONFIG = {
  width: '1200px',
  autoFocus: false,
  data: {
    distribution: undefined
  }
};

@Component({
  selector: 'distribution-modal',
  templateUrl: './distribution-modal.component.html',
  styleUrls: []
})
export class DistributionModalComponent {
  constructor(
    public dialogRef: MatDialogRef<DistributionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DistributionModalData,
    private store: Store<fromRoot.State>) {
  }

  saveDistribution(distribution: DistributionEntry[]): void {
    this.store.dispatch(new SaveDistributionAndNotify(distribution));
  }

  close() {
    this.dialogRef.close();
  }
}
