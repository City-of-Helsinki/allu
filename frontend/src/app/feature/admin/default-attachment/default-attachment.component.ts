import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import * as filesaver from 'file-saver';

import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {CityDistrict} from '../../../model/common/city-district';
import {AttachmentHub} from '../../application/attachment/attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {MaterializeUtil} from '../../../util/materialize.util';
import {ArrayUtil} from '../../../util/array-util';
import {FixedLocationArea} from '../../../model/common/fixed-location-area';
import {FixedLocationService} from '../../../service/map/fixed-location.service';
import {Store} from '@ngrx/store';
import * as fromRoot from '../../allu/reducers';
import {filter, map, switchMap} from 'rxjs/internal/operators';

@Component({
  selector: 'default-attachment',
  templateUrl: './default-attachment.component.html',
  styleUrls: []
})
export class DefaultAttachmentComponent implements OnInit {

  attachmentForm: FormGroup;
  districts: Observable<Array<CityDistrict>>;
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  hasFileOverDropzone = false;
  attachmentType: string;
  areas = this.fixedLocationService.existing
    .pipe(map(areas => areas.sort(ArrayUtil.naturalSort((area: FixedLocationArea) => area.name))));

  file: Blob;

  constructor(private fb: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private fixedLocationService: FixedLocationService,
              private store: Store<fromRoot.State>,
              private attachmentHub: AttachmentHub) {

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
        MaterializeUtil.toast('Liite ' + attachment.name + ' tallennettu');
        this.router.navigate(['../'], { relativeTo: this.route });
      },
      error => MaterializeUtil.toast('Liitteen ' + attachmentInfo.name + ' tallentaminen epäonnistui'));
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
