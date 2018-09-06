import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {InformationRequestField} from '@model/information-request/information-request-field';

export class InformationRequestForm {
  constructor(
    public selectedCUSTOMER?: boolean,
    public CUSTOMER?: string,
    public selectedINVOICING_CUSTOMER?: boolean,
    public INVOICING_CUSTOMER?: string,
    public selectedGEOMETRY?: boolean,
    public GEOMETRY?: string,
    public selectedSTART_TIME?: boolean,
    public START_TIME?: string,
    public selectedEND_TIME?: boolean,
    public END_TIME?: string,
    public selectedIDENTIFICATION_NUMBER?: boolean,
    public IDENTIFICATION_NUMBER?: string,
    public selectedCLIENT_APPLICATION_KIND?: boolean,
    public CLIENT_APPLICATION_KIND?: string,
    public selectedAPPLICATION_KIND?: boolean,
    public APPLICATION_KIND?: string,
    public selectedPOSTAL_ADDRESS?: boolean,
    public POSTAL_ADDRESS?: string,
    public selectedWORK_DESCRIPTION?: boolean,
    public WORK_DESCRIPTION?: string,
    public selectedPROPERTY_IDENTIFICATION_NUMBER?: boolean,
    public PROPERTY_IDENTIFICATION_NUMBER?: string,
    public selectedATTACHMENT?: boolean,
    public ATTACHMENT?: string
  ) {
  }

  public static formGroup(fb: FormBuilder, request: InformationRequest = new InformationRequest()): FormGroup {
    return fb.group({
      selectedCUSTOMER: this.isSelected(InformationRequestFieldKey.CUSTOMER, request.fields),
      CUSTOMER: this.getDescription(InformationRequestFieldKey.CUSTOMER, request.fields),
      selectedINVOICING_CUSTOMER: this.isSelected(InformationRequestFieldKey.INVOICING_CUSTOMER, request.fields),
      INVOICING_CUSTOMER: this.getDescription(InformationRequestFieldKey.INVOICING_CUSTOMER, request.fields),
      selectedGEOMETRY: this.isSelected(InformationRequestFieldKey.GEOMETRY, request.fields),
      GEOMETRY: this.getDescription(InformationRequestFieldKey.GEOMETRY, request.fields),
      selectedSTART_TIME: this.isSelected(InformationRequestFieldKey.START_TIME, request.fields),
      START_TIME: this.getDescription(InformationRequestFieldKey.START_TIME, request.fields),
      selectedEND_TIME: this.isSelected(InformationRequestFieldKey.END_TIME, request.fields),
      END_TIME: this.getDescription(InformationRequestFieldKey.END_TIME, request.fields),
      selectedIDENTIFICATION_NUMBER: this.isSelected(InformationRequestFieldKey.IDENTIFICATION_NUMBER, request.fields),
      IDENTIFICATION_NUMBER: this.getDescription(InformationRequestFieldKey.IDENTIFICATION_NUMBER, request.fields),
      selectedCLIENT_APPLICATION_KIND: this.isSelected(InformationRequestFieldKey.CLIENT_APPLICATION_KIND, request.fields),
      CLIENT_APPLICATION_KIND: this.getDescription(InformationRequestFieldKey.CLIENT_APPLICATION_KIND, request.fields),
      selectedAPPLICATION_KIND: this.isSelected(InformationRequestFieldKey.APPLICATION_KIND, request.fields),
      APPLICATION_KIND: this.getDescription(InformationRequestFieldKey.APPLICATION_KIND, request.fields),
      selectedPOSTAL_ADDRESS: this.isSelected(InformationRequestFieldKey.POSTAL_ADDRESS, request.fields),
      POSTAL_ADDRESS: this.getDescription(InformationRequestFieldKey.POSTAL_ADDRESS, request.fields),
      selectedWORK_DESCRIPTION: this.isSelected(InformationRequestFieldKey.WORK_DESCRIPTION, request.fields),
      WORK_DESCRIPTION: this.getDescription(InformationRequestFieldKey.WORK_DESCRIPTION, request.fields),
      selectedPROPERTY_IDENTIFICATION_NUMBER: this.isSelected(InformationRequestFieldKey.PROPERTY_IDENTIFICATION_NUMBER, request.fields),
      PROPERTY_IDENTIFICATION_NUMBER: this.getDescription(InformationRequestFieldKey.PROPERTY_IDENTIFICATION_NUMBER, request.fields),
      selectedATTACHMENT: this.isSelected(InformationRequestFieldKey.ATTACHMENT, request.fields),
      ATTACHMENT: this.getDescription(InformationRequestFieldKey.ATTACHMENT, request.fields)
    });
  }

  public static toInformationRequest(form: InformationRequestForm,
                                     request: InformationRequest = new InformationRequest()): InformationRequest {
    const fields = new Array<InformationRequestField>();
    this.addIfSelected(form.selectedCUSTOMER, InformationRequestFieldKey.CUSTOMER, form.CUSTOMER, fields);
    this.addIfSelected(form.selectedINVOICING_CUSTOMER, InformationRequestFieldKey.INVOICING_CUSTOMER, form.INVOICING_CUSTOMER, fields);
    this.addIfSelected(form.selectedGEOMETRY, InformationRequestFieldKey.GEOMETRY, form.GEOMETRY, fields);
    this.addIfSelected(form.selectedSTART_TIME, InformationRequestFieldKey.START_TIME, form.START_TIME, fields);
    this.addIfSelected(form.selectedEND_TIME, InformationRequestFieldKey.END_TIME, form.END_TIME, fields);
    this.addIfSelected(form.selectedIDENTIFICATION_NUMBER, InformationRequestFieldKey.IDENTIFICATION_NUMBER,
      form.IDENTIFICATION_NUMBER, fields);
    this.addIfSelected(form.selectedCLIENT_APPLICATION_KIND, InformationRequestFieldKey.CLIENT_APPLICATION_KIND,
      form.CLIENT_APPLICATION_KIND, fields);
    this.addIfSelected(form.selectedAPPLICATION_KIND, InformationRequestFieldKey.APPLICATION_KIND, form.APPLICATION_KIND, fields);
    this.addIfSelected(form.selectedPOSTAL_ADDRESS, InformationRequestFieldKey.POSTAL_ADDRESS, form.POSTAL_ADDRESS, fields);
    this.addIfSelected(form.selectedWORK_DESCRIPTION, InformationRequestFieldKey.WORK_DESCRIPTION, form.WORK_DESCRIPTION, fields);
    this.addIfSelected(form.selectedPROPERTY_IDENTIFICATION_NUMBER, InformationRequestFieldKey.PROPERTY_IDENTIFICATION_NUMBER,
      form.PROPERTY_IDENTIFICATION_NUMBER, fields);
    this.addIfSelected(form.selectedATTACHMENT, InformationRequestFieldKey.ATTACHMENT, form.ATTACHMENT, fields);
    request.fields = fields;
    return request;
  }

  private static getDescription(key: InformationRequestFieldKey, fields: InformationRequestField[]): string {
    const field = fields.find(f => f.fieldKey === key);
    if (field) {
      return field.description;
    }
    return undefined;
  }

  private static isSelected(key: InformationRequestFieldKey, fields: InformationRequestField[]): boolean {
    return !!fields.find(f => f.fieldKey === key);
  }

  private static addIfSelected(selected: boolean, fieldKey: InformationRequestFieldKey, description: string,
                               fields: InformationRequestField[]): void {
    if (selected) {
      fields.push(new InformationRequestField(fieldKey, description));
    }
  }
}
