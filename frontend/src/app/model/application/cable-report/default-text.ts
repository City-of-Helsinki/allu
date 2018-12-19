import {DefaultTextType} from '../default-text-type';
import {Some} from '../../../util/option';
import {ApplicationType} from '../type/application-type';

export class DefaultText {

  constructor()
  constructor(
    id: number,
    applicationType: ApplicationType,
    type: DefaultTextType,
    text: string)
  constructor(
    public id?: number,
    public applicationType?: ApplicationType,
    public type?: DefaultTextType,
    public text?: string) {}

  static ofType(applicationType: ApplicationType, type: DefaultTextType): DefaultText {
    return new DefaultText(undefined, applicationType, type, undefined);
  }

  // Creates a map (texts by type) from array of DefaultTexts
  static groupByType(texts: Array<DefaultText>): DefaultTextMap {
    return texts.reduce((map: DefaultTextMap, text) => {
      map[text.type] = Some(map[text.type])
        .map(textsForType => textsForType.concat(text))
        .orElse([text]);
      return map;
    }, {});
  }
}

export interface DefaultTextMap {
  [key: string]: Array<DefaultText>;
}
