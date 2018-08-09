import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';

@Component({
  selector: 'pdf-view',
  templateUrl: './pdf-view.component.html',
  styleUrls: ['./pdf-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PdfViewComponent {

  @Input() loading;
  @Input() processing;

  pdfUrl: SafeResourceUrl;
  pdfDownloadUrl: SafeUrl;

  constructor(private sanitizer: DomSanitizer) {}

  @Input() set pdf(pdf: Blob) {
    if (pdf) {
      const url = URL.createObjectURL(pdf);
      this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    }
  }
}
