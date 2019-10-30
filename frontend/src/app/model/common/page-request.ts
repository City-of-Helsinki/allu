import {Page} from '@model/common/page';

export class PageRequest {
  constructor(
    public readonly page: number = 0,
    public readonly size: number = 10000) {}
}
