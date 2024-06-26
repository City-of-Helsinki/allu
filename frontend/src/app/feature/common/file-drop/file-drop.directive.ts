import { Directive, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({selector: '[fileDrop]'})
export class FileDropDirective {

  @Output() onFileDrop = new EventEmitter<FileList>();
  @Output() onFileOver = new EventEmitter<boolean>();


  public constructor() {
  }

  @HostListener('drop', ['$event'])
  public onDrop(event: any): void {
    this.preventDefault(event);
    const files = this.getFiles(event);
    this.onFileDrop.emit(files);
    this.onFileOver.emit(false);
  }

  @HostListener('dragover', ['$event'])
  public onDragOver(event: any): void {
    this.preventDefault(event);
  }

  @HostListener('dragenter', ['$event'])
  public onDragEnter(event: any): void {
    this.preventDefault(event);
    this.onFileOver.emit(true);
  }

  @HostListener('dragleave', ['$event'])
  public onDragLeave(event: any): void {
    this.preventDefault(event);
    this.onFileOver.emit(false);
  }

  private preventDefault(event: any): any {
    event.preventDefault();
    event.stopPropagation();
  }

  private getFiles(event: any): FileList {
    return event.dataTransfer ? event.dataTransfer.files : [];
  }
}

