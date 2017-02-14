import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {ActivatedRoute, Router} from '@angular/router';
import * as filesaverLib from 'filesaver';

import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {CityDistrict} from '../../../model/common/city-district';
import {MapHub} from '../../../service/map/map-hub';
import {AttachmentHub} from '../../application/attachment/attachment-hub';
import {DefaultAttachmentInfo, DefaultAttachmentInfoForm} from '../../../model/application/attachment/default-attachment-info';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';
import {MaterializeUtil} from '../../../util/materialize.util';

@Component({
  selector: 'default-attachment',
  template: require('./default-attachment.component.html'),
  styles: []
})
export class DefaultAttachmentComponent implements OnInit {

  attachmentForm: FormGroup;
  districts: Observable<Array<CityDistrict>>;
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  hasFileOverDropzone = false;

  private file: File;

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private router: Router,
              private mapHub: MapHub, private attachmentHub: AttachmentHub) {

    this.attachmentForm = fb.group({
      id: [undefined],
      name: ['', Validators.required],
      description: [''],
      district: [],
      defaultAttachmentId: [],
      applicationTypes: []
    });
    this.attachmentForm.patchValue({applicationTypes: []});
  }


  ngOnInit(): void {
    this.districts = this.mapHub.districts();

    this.route.params
      .map(params => params['id'])
      .filter(id => !!id)
      .switchMap(id => this.loadDefaultAttachment(id))
      .subscribe(attachment => this.updateForm(attachment));
  }

  attachmentSelected(files: File[]): void {
    if (files && files.length > 0) {
      let file = files[0];
      this.attachmentForm.patchValue({name: file.name});
      this.file = file;
    }
  }

  applicationTypesChange(types: Array<string>): void {
    this.attachmentForm.patchValue({applicationTypes: types});
  }

  save(): void {
    let attachmentInfo = DefaultAttachmentInfo.fromForm(this.attachmentForm.value, AttachmentType[AttachmentType.DEFAULT]);
    attachmentInfo.file = this.file;
    this.attachmentHub.saveDefaultAttachments(attachmentInfo).subscribe(
      attachment => {
        MaterializeUtil.toast('Liite ' + attachment.name + ' tallennettu');
        this.router.navigateByUrl('admin/default-attachments');
      },
      error => MaterializeUtil.toast('Liitteen ' + attachmentInfo.name + ' tallentaminen epäonnistui'));
  }

  remove(): void {
    this.file = undefined;
    this.attachmentForm.patchValue({name: undefined});
  }

  cancel(): void {
    this.router.navigateByUrl('admin/default-attachments');
  }

  fileOverDropzone(hasFileOverDropzone: boolean) {
    this.hasFileOverDropzone = hasFileOverDropzone;
  }

  download(): void {
    filesaverLib.saveAs(this.file);
  }

  private loadDefaultAttachment(id: number): Observable<DefaultAttachmentInfo> {
    return this.attachmentHub.defaultAttachmentInfo(id);
  }

  private updateForm(attachment: DefaultAttachmentInfo): void {
    this.attachmentForm.patchValue(DefaultAttachmentInfo.toForm(attachment));

    if (attachment.id) {
      this.attachmentHub.download(attachment.id, this.attachmentForm.value.name)
        .subscribe(file => this.file = file);
    }
  }
}
