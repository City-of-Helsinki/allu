export class Configuration {
  constructor(
    public id: number,
    public type: string,
    public key: string,
    public value: string) {}
}

export interface ConfigurationKeyMap {
  [key: string]: Configuration;
}
