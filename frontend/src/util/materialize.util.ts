import {toast as materializeToast} from 'angular2-materialize';

declare var Materialize: any;
declare var $: any;

const defaultTime = 50;
const defaultToastTime = 4000;

export class MaterializeUtil {

  static updateTextFields(afterTimeout: number): void {
    let timeout = afterTimeout || defaultTime;
    setTimeout(() => Materialize.updateTextFields(), timeout);
  }

  static resizeTextArea(selector: string, afterTimeout?: number): void {
    let timeout = afterTimeout || defaultTime;
    setTimeout(() => $(selector).trigger('autoresize'), timeout);
  }

  static toast(message: string, timeVisible?: number, cssClass?: string): void {
    let visible = timeVisible || defaultToastTime;
    materializeToast(message, visible);
  }
}
