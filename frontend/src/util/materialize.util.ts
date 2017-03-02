import {toast as materializeToast} from 'angular2-materialize';

const defaultToastTime = 4000;

export class MaterializeUtil {
  static toast(message: string, timeVisible?: number, cssClass?: string): void {
    let visible = timeVisible || defaultToastTime;
    materializeToast(message, visible);
  }
}
