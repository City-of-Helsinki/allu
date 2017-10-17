import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {ArrayUtil} from '../src/util/array-util';

export function getMdIconButton(debugElement: DebugElement, buttonIcon: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('button'))
    .filter(btn => btn.query(By.css('mat-icon')).nativeElement.textContent === buttonIcon)
    .map(btn => btn.nativeElement));
}

export function getButtonWithText(debugElement: DebugElement, text: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('button'))
    .filter(btn => btn.nativeElement.textContent === text)
    .map(btn => btn.nativeElement));
}
