import {FixedLocationSection} from './fixed-location-section';
import {ApplicationKind} from '../application/type/application-kind';
import {Option, Some} from '../../util/option';
export class FixedLocationArea {
  constructor(
    public id?: number,
    public name?: string,
    public sections?: Array<FixedLocationSection>) {
    this.sections = sections || [];
  }

  hasSectionsForKind(kind: ApplicationKind): boolean {
    return this.sections.some(s => s.applicationKind === kind);
  }

  namedSectionsForKind(kind: ApplicationKind): Array<FixedLocationSection> {
    return this.sectionsForKind(kind)
      .filter(s => !!s.name);
  }

  hasSectionIds(sectionIds: Array<number>): boolean {
    if (sectionIds.length > 0) {
      const areaSectionids = this.sections.map(s => s.id);
      return sectionIds.every(id => areaSectionids.indexOf(id) >= 0);
    } else {
      return false;
    }
  }

  singleDefaultSectionForKind(kind: ApplicationKind): Option<FixedLocationSection> {
    return Some(this.sectionsForKind(kind).find(s => !s.name));
  }

  private sectionsForKind(kind: ApplicationKind): Array<FixedLocationSection> {
    return this.sections.filter(s => s.applicationKind === kind);
  }
}

