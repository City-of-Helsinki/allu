import {Directive, HostListener, ElementRef, Output, EventEmitter} from '@angular/core';

@Directive({selector: '[file-select]'})
export class FileSelectDirective {

  @Output() private attachmentsSelected = new EventEmitter();

  private element: ElementRef;

  public constructor(element: ElementRef) {
    this.element = element;
  }

  @HostListener('change')
  public onChange(): any {
    let files = this.element.nativeElement.files;
    let fileArray = [];
    for (let i = 0; i < files.length; ++i) {
      fileArray.push(files[i]);
    }
    this.attachmentsSelected.emit(fileArray);
  }
}
