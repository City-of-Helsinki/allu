import {toast as materializeToast} from 'angular2-materialize';

declare var Materialize: any;
declare var $: any;


export class MaterializeUtil {

  static updateTextFields(afterTimeout: number): void {
    let timeout = afterTimeout || 50;
    setTimeout(() => Materialize.updateTextFields(), timeout);
  }

  static resizeTextArea(selector: string, afterTimeout?: number): void {
    let timeout = afterTimeout || 50;
    setTimeout(() => $(selector).trigger('autoresize'), timeout);
  }

  static toast(message: string, timeVisible: number, cssClass?: string): void {
    materializeToast(message, timeVisible);
  }
}
