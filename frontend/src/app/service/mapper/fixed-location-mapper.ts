import {BackendFixedLocationArea} from '../backend-model/backend-fixed-location-area';
import {ApplicationKind} from '@model/application/type/application-kind';
import {FixedLocationArea} from '@model/common/fixed-location-area';
import {BackendFixedLocationSection} from '../backend-model/backend-fixed-location-section';
import {FixedLocationSection} from '@model/common/fixed-location-section';
import {Some} from '@util/option';

export class FixedLocationMapper {

  public static mapBackend(area: BackendFixedLocationArea): FixedLocationArea {
    return (area) ? new FixedLocationArea(
      area.id,
      area.name,
      Some(area.sections).map(sections => sections.map(s => FixedLocationMapper.mapBackendSection(s))).orElse([])
    ) : undefined;
  }

  public static mapFrontend(area: FixedLocationArea): BackendFixedLocationArea {
    return (area) ?
    {
      id: area.id,
      name: area.name,
      sections: Some(area.sections).map(sections => sections.map(s => FixedLocationMapper.mapFrontendSection(s))).orElse([])
    } : undefined;
  }

  private static mapBackendSection(section: BackendFixedLocationSection): FixedLocationSection {
    return new FixedLocationSection(
      section.id,
      section.name,
      ApplicationKind[section.applicationKind],
      section.geometry,
      section.active
    );
  }

  private static mapFrontendSection(section: FixedLocationSection): BackendFixedLocationSection {
    return {
      id: section.id,
      name: section.name,
      applicationKind: ApplicationKind[section.applicationKind],
      geometry: section.geometry,
      active: section.active
    };
  }
}
