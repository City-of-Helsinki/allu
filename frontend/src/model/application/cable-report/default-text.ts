import {CableInfoType} from './cable-info-type';

export class DefaultText {

  constructor()
  constructor(id: number, type: CableInfoType, text: string)
  constructor(public id?: number, public type?: CableInfoType, public text?: string) {}

  static mapBackend(backendText: BackendDefaultText): DefaultText {
    return new DefaultText(backendText.id, CableInfoType[backendText.type], backendText.text);
  }

  static mapFrontend(defaultText: DefaultText): BackendDefaultText {
    return {
      id: defaultText.id,
      type: CableInfoType[defaultText.type],
      text: defaultText.text
    };
  }
}

interface BackendDefaultText {
  id: number;
  type: string;
  text: string;
}
