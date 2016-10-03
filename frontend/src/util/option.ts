export interface Option<A> {
  isDefined(): boolean;
  value(): A;
  map<B>(fn: (a: A) => B): Option<B>;
}

export function Some<T>(val: T) {
  return val === undefined
    ? new NoneOpt()
    : new SomeOpt(val);
}

export function None() {
  return new NoneOpt();
}

export class SomeOpt<T> implements Option<T> {
  constructor(private val: T) {}

  isDefined(): boolean {
    return this.val !== undefined;
  }

  value(): T {
    return this.val;
  }

  map<B>(fn: (a: T) => B): Option<B> {
    let result = fn(this.val);
    return result === undefined ? new NoneOpt() : new SomeOpt(result);
  }
}

export class NoneOpt implements Option<never> {
  constructor() {}

  isDefined(): boolean {
    return false;
  }

  value(): never {
    throw new Error('No value');
  }

  map(fn: (a: any) => any): NoneOpt {
    return new NoneOpt();
  }
}
