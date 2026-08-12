export interface Option<A> {
  isDefined(): boolean;
  value(): A;
  map<B>(fn: (a: A) => B): Option<B>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  do(fn: (a: A) => any): Option<A>;
  filter(predicate: (a: A) => boolean): Option<A>;
  orElse(val: A): A;
  orElseGet(fn: () => A): A;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export class NoneOpt implements Option<any> {
  constructor() {}

  isDefined(): boolean {
    return false;
  }

  value(): never {
    throw new Error('No value');
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  map(_fn: (a: any) => any): NoneOpt {
    return new NoneOpt();
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  do(_fn: (a: any) => any): NoneOpt {
    return this;
  }


// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  filter(_predicate: (a: never) => boolean): Option<any> {
    return None();
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  orElse(val: any): any {
    return val;
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
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

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  do(fn: (a: T) => any): Option<T> {
    if (this.isDefined()) {
      fn(this.val);
    }
    return this;
  }

  filter(predicate: (a: T) => boolean): Option<T> {
    return predicate(this.val) ? Some(this.val) : None();
  }

  orElse(_val: T): T {
    return this.val;
  }

  orElseGet(_fn: () => T): T {
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
