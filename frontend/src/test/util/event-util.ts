import {Some} from '../../app/util/option';

/**
 * Helper class for DOM events
 */
export class EventUtil {
  static targetHasClass(event: any, className: string): boolean {
    return Some(event)
      .map(e => e.target)
      .map(target => target.className)
      .filter(classNames => classNames.indexOf(className) < 0)
      .orElse(false);
  }
}
