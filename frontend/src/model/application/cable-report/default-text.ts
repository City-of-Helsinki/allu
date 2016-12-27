import {CableInfoType} from './cable-info-type';
import {Some} from '../../../util/option';
import {StringUtil} from '../../../util/string.util';

export class DefaultText {

  constructor()
  constructor(id: number, type: CableInfoType, text: string)
  constructor(public id?: number, public type?: CableInfoType, public text?: string) {}

  static ofType(type: CableInfoType): DefaultText {
    return new DefaultText(undefined, type, undefined);
  }

  static mapBackend(cableInfoText: CableInfoText): DefaultText {
    return new DefaultText(cableInfoText.id, CableInfoType[cableInfoText.cableInfoType], cableInfoText.textValue);
  }

  static mapFrontend(defaultText: DefaultText): CableInfoText {
    return {
      id: defaultText.id,
      cableInfoType: CableInfoType[defaultText.type],
      textValue: defaultText.text
    };
  }

  // Creates a map (texts by type) from array of DefaultTexts
  static groupByType(texts: Array<DefaultText>): DefaultTextMap {
    let result = texts.reduce((map: DefaultTextMap, text) => {
      map[CableInfoType[text.type]] = Some(map[CableInfoType[text.type]])
        .map(textsForType => textsForType.concat(text))
        .orElse([text]);
      return map;
    }, {});
    return result;
  }
}

export interface DefaultTextMap {
  [key: string]: Array<DefaultText>;
}

export interface CableInfoText {
  id: number;
  cableInfoType: string;
  textValue: string;
}

export interface DefaultTextForm {
  defaultTexts: [
    {
      id: string,
      type: string,
      text: string
    }
    ];
}

