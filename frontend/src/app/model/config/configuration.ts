import {ConfigurationType} from '@model/config/configuration-type';

export class Configuration {
  constructor(
    public id: number,
    public type: ConfigurationType,
    public key: string,
    public value: string) {}
}
