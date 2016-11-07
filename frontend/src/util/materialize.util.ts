declare var Materialize: any;

export class MaterializeUtil {

  static updateTextFields(afterTimeout: number): void {
    setTimeout(() => Materialize.updateTextFields(), 50);
  }
}
