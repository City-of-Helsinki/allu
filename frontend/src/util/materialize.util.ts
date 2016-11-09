import {toast as materializeToast} from 'angular2-materialize';

declare var Materialize: any;


export class MaterializeUtil {

  static updateTextFields(afterTimeout: number): void {
    setTimeout(() => Materialize.updateTextFields(), 50);
  }

  static toast(message: string, timeVisible: number, cssClass?: string): void {
    materializeToast(message, timeVisible);
  }
}
