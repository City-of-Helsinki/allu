import {Directive, HostListener, ElementRef, Output, EventEmitter} from '@angular/core';

@Directive({selector: '[fileSelect]'})
export class FileSelectDirective {

  @Output() private attachmentsSelected = new EventEmitter();

  private element: ElementRef;

  public constructor(element: ElementRef) {
    this.element = element;
  }

  @HostListener('change')
  public onChange(): any {
    const files = this.element.nativeElement.files;
    const fileArray = [];
    for (let i = 0; i < files.length; ++i) {
      fileArray.push(files[i]);
    }
    this.attachmentsSelected.emit(fileArray);
  }
}
