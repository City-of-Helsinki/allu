export interface Option<A> {
  isDefined(): boolean;
  value(): A;
  map<B>(fn: (a: A) => B): Option<B>;
  do(fn: (a: A) => any): void;
  filter(predicate: (a: A) => boolean): Option<A>;
  orElse(val: A): A;
}

export function Some<T>(val: T): Option<T> {
  /* tslint:disable:no-null-keyword */
  return val === undefined || val === null
    ? new NoneOpt()
    : new SomeOpt(val);
}

export function None() {
  return new NoneOpt();
}

export class SomeOpt<T> implements Option<T> {
  constructor(private val: T) {}

  isDefined(): boolean {
    /* tslint:disable:no-null-keyword */
    return this.val !== undefined && this.val !== null;
  }

  value(): T {
    return this.val;
  }

  map<B>(fn: (a: T) => B): Option<B> {
    let result = fn(this.val);
    return result === undefined ? new NoneOpt() : new SomeOpt(result);
  }

  do(fn: (a: T) => any): void {
    if (this.isDefined()) {
      fn(this.val);
    }
  }

  filter(predicate: (a: T) => boolean): Option<T> {
    return predicate(this.val) ? Some(this.val) : None();
  }

  orElse(val: T): T {
    return this.val;
  }
}

export class NoneOpt implements Option<any> {
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

  do(fn: (a: any) => any): void {}


  filter(predicate: (a: never) => boolean): Option<any> {
    return None();
  }

  orElse(val: any): any {
    return val;
  }
}
