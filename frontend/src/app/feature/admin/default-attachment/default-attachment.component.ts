import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import * as filesaver from 'file-saver';

import {ApplicationType} from '../../../model/application/type/application-type';
import {CityDistrict} from '../../../model/common/city-district';
import {AttachmentHub} from '../../application/attachment/attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {ArrayUtil} from '../../../util/array-util';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '../../allu/reducers';
import {filter, map, switchMap} from 'rxjs/internal/operators';
import {NotificationService} from '../../notification/notification.service';
import {FixedLocationArea} from '@model/common/fixed-location-area';

@Component({
  selector: 'default-attachment',
  templateUrl: './default-attachment.component.html',
  styleUrls: []
})
export class DefaultAttachmentComponent implements OnInit {

  attachmentForm: UntypedFormGroup;
  districts: Observable<Array<CityDistrict>>;
  applicationTypes = Object.keys(ApplicationType)
    .sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type));
  hasFileOverDropzone = false;
  attachmentType: string;
  areas: Observable<FixedLocationArea[]>;

  file: Blob;

  constructor(private fb: UntypedFormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private store: Store<fromRoot.State>,
              private attachmentHub: AttachmentHub,
              private notification: NotificationService) {

    this.attachmentForm = this.fb.group({
      id: [undefined],
      type: [''],
      name: ['', Validators.required],
      description: [''],
      district: [],
      defaultAttachmentId: [],
      applicationTypes: [],
      fixedLocationId: []
    });
  }


  ngOnInit(): void {
    this.districts = this.store.select(fromRoot.getAllCityDistricts);
    this.areas = this.store.pipe(select(fromRoot.getAllFixedLocationAreas));

    this.route.params.pipe(
      map(params => params['id']),
      filter(id => !!id),
      switchMap(id => this.loadDefaultAttachment(id))
    ).subscribe(attachment => this.updateForm(attachment));

    this.route.data.subscribe((data: {attachmentType: string}) => {
      this.attachmentType = data.attachmentType;
      this.attachmentForm.patchValue({type: data.attachmentType, applicationTypes: []});
    });
  }

  attachmentSelected(files: File[]): void {
    if (files && files.length > 0) {
      const file = files[0];
      this.attachmentForm.patchValue({name: file.name});
      this.file = file;
    }
  }

  applicationTypesChange(types: Array<string>): void {
    this.attachmentForm.patchValue({applicationTypes: types});
  }

  save(): void {
    const attachmentInfo = DefaultAttachmentInfo.fromForm(this.attachmentForm.value);
    attachmentInfo.file = this.file;
    attachmentInfo.mimeType = this.file.type;
    this.attachmentHub.saveDefaultAttachment(attachmentInfo).subscribe(
      attachment => {
        this.notification.success('Liite ' + attachment.name + ' tallennettu');
        this.router.navigate(['../'], { relativeTo: this.route });
      },
      error => this.notification.error('Liitteen ' + attachmentInfo.name + ' tallentaminen epäonnistui'));
  }

  remove(): void {
    this.file = undefined;
    this.attachmentForm.patchValue({name: undefined});
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  fileOverDropzone(hasFileOverDropzone: boolean) {
    this.hasFileOverDropzone = hasFileOverDropzone;
  }

  download(): void {
    filesaver.saveAs(this.file, this.attachmentForm.value.name);
  }

  private loadDefaultAttachment(id: number): Observable<DefaultAttachmentInfo> {
    return this.attachmentHub.defaultAttachmentInfo(id);
  }

  private updateForm(attachment: DefaultAttachmentInfo): void {
    this.attachmentForm.patchValue(DefaultAttachmentInfo.toForm(attachment));

    if (attachment.id) {
      this.attachmentHub.download(attachment.id)
        .subscribe(file => this.file = file);
    }
  }
}
