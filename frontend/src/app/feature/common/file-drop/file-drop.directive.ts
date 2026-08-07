import { Directive, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({selector: '[fileDrop]'})
export class FileDropDirective {

  @Output() fileDrop = new EventEmitter<FileList>();
  @Output() fileOver = new EventEmitter<boolean>();


  public constructor() {
  }

  @HostListener('drop', ['$event'])
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public onDrop(event: any): void {
    this.preventDefault(event);
    const files = this.getFiles(event);
    this.fileDrop.emit(files);
    this.fileOver.emit(false);
  }

  @HostListener('dragover', ['$event'])
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public onDragOver(event: any): void {
    this.preventDefault(event);
  }

  @HostListener('dragenter', ['$event'])
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public onDragEnter(event: any): void {
    this.preventDefault(event);
    this.fileOver.emit(true);
  }

  @HostListener('dragleave', ['$event'])
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public onDragLeave(event: any): void {
    this.preventDefault(event);
    this.fileOver.emit(false);
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  private preventDefault(event: any): any {
    event.preventDefault();
    event.stopPropagation();
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  private getFiles(event: any): FileList {
    return event.dataTransfer ? event.dataTransfer.files : [];
  }
}

