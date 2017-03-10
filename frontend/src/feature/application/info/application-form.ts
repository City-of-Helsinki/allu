export interface ApplicationForm {
  terms?: string;
  communication?: CommunicationForm;
}

export interface CommunicationForm {
  communicationByEmail?: boolean;
  publicityType?: string;
}
