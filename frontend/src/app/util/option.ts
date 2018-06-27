export interface Option<A> {
  isDefined(): boolean;
  value(): A;
  map<B>(fn: (a: A) => B): Option<B>;
  do(fn: (a: A) => any): Option<A>;
  filter(predicate: (a: A) => boolean): Option<A>;
  orElse(val: A): A;
  orElseGet(fn: () => A): A;
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

  do(fn: (a: any) => any): NoneOpt {
    return this;
  }


  filter(predicate: (a: never) => boolean): Option<any> {
    return None();
  }

  orElse(val: any): any {
    return val;
  }

  orElseGet(fn: () => any): any {
    return fn();
  }
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
    const result = fn(this.val);
    return result === undefined ? new NoneOpt() : new SomeOpt(result);
  }

  do(fn: (a: T) => any): Option<T> {
    if (this.isDefined()) {
      fn(this.val);
    }
    return this;
  }

  filter(predicate: (a: T) => boolean): Option<T> {
    return predicate(this.val) ? Some(this.val) : None();
  }

  orElse(val: T): T {
    return this.val;
  }

  orElseGet(fn: () => T): T {
    return this.val;
  }
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
