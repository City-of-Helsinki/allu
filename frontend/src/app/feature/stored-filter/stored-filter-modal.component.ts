import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {StoredFilterService} from '../../service/stored-filter/stored-filter.service';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {StoredFilter} from '../../model/user/stored-filter';
import {NotificationService} from '../notification/notification.service';
import {StoredFilterStore} from '../../service/stored-filter/stored-filter-store';

export const STORED_FILTER_MODAL_CONFIG = {width: '800PX', disableClose: false, data: {}};

@Component({
  selector: 'stored-filter-modal',
  templateUrl: './stored-filter-modal.component.html',
  styleUrls: [
    './stored-filter-modal.component.scss'
  ]
})
export class StoredFilterModalComponent implements OnInit {

  filterType: StoredFilterType;
  form: FormGroup;
  typeName: string;

  private userId: number;
  private filter: any;

  constructor(private dialogRef: MatDialogRef<StoredFilterModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: StoredFilterModalData,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      defaultFilter: [false],
    });

    this.filterType = this.data.filterType;
    this.userId = this.data.userId;
    this.filter = this.data.filter;
    this.typeName = StoredFilterType[this.filterType];
  }

  onSubmit(): void {
    const form = this.form.getRawValue();
    const filter = new StoredFilter(
      undefined,
      this.filterType,
      form.name,
      form.defaultFilter,
      this.filter,
      this.userId
    );

    this.dialogRef.close(filter);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }
}

export interface StoredFilterModalData {
  filterType: StoredFilterType;
  userId: number;
  filter: any;
}
