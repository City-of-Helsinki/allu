import {Some} from '../../app/util/option';

/**
 * Helper class for DOM events
 */
export class EventUtil {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  static targetHasClass(event: any, className: string): boolean {
    return Some(event)
      .map(e => e.target)
      .map(target => target.className)
      .filter(classNames => classNames.indexOf(className) < 0)
      .orElse(false);
  }
}
