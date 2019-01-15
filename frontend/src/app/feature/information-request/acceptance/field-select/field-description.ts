export enum SelectFieldType {
  TEXT = 'TEXT',
  GEOMETRY = 'GEOMETRY'
}

export class FieldDescription {
  constructor(
    readonly field: string,
    readonly label: string,
    readonly type: SelectFieldType = SelectFieldType.TEXT
  ) {}
}
