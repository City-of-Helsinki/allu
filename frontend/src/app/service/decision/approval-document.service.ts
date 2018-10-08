import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/internal/operators';
import {ErrorHandler} from '@service/error/error-handler.service';
import {findTranslation} from '@util/translations';
import {ApprovalDocument, ApprovalDocumentType} from '@model/decision/approval-document';

const URL_PREFIX = '/api/applications';

@Injectable()
export class ApprovalDocumentService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public fetch(applicationId: number, documentType: ApprovalDocumentType): Observable<ApprovalDocument> {
    const url = `${URL_PREFIX}/${applicationId}/approvalDocument/${documentType}`;

    return this.http.get(url, {responseType: 'blob'}).pipe(
      map(pdf => new ApprovalDocument(documentType, applicationId, pdf)),
      catchError(error => this.errorHandler.handle(error, findTranslation('approvalDocument.error.fetchFailed')))
    );
  }
}
