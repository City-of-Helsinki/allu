import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {DomSanitizer, SafeHtml, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';

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
  pdfIEElem: SafeHtml;
  pdfDownloadUrl: SafeUrl;
  isIEorEdge = false;

  constructor(private sanitizer: DomSanitizer) {
    this.isIEorEdge = /(MSIE|Trident\/|Edge\/)/i.test(navigator.userAgent);
  }

  @Input() set pdf(pdf: Blob) {
    if (pdf) {
      const url = URL.createObjectURL(pdf);
      if (this.isIEorEdge) {
        // IE / EDGE needs special handling since they block angulars sanitized url
        // Issue https://github.com/angular/angular/issues/25980
        this.pdfIEElem = this.sanitizer.bypassSecurityTrustHtml(`<embed src="${url}" type="application/pdf" height="100%" width="100%" />`);
      } else {
        this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        this.pdfDownloadUrl = this.sanitizer.bypassSecurityTrustUrl(url);
      }
    }
  }
}
