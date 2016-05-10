
import {Event} from './event';

export interface EventListener {
  handle(event: Event): void;
}
