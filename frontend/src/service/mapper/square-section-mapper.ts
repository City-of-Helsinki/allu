import {BackendSquareSection} from '../backend-model/backend-square-section';
import {SquareSection} from '../../model/common/square-section';
export class SquareSectionMapper {

  public static mapBackend(backendSquareSection: BackendSquareSection): SquareSection {
    return (backendSquareSection) ? new SquareSection(
      backendSquareSection.id,
      backendSquareSection.square,
      backendSquareSection.section)
      : undefined;
  }

  public static mapFrontend(squareSection: SquareSection): BackendSquareSection {
    return (squareSection) ?
    {
      id: squareSection.id,
      square: squareSection.square,
      section: squareSection.section
    } : undefined;
  }
}
