import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {ArrayUtil} from '../src/app/util/array-util';

export function getMatIconButton(debugElement: DebugElement, buttonIcon: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('button.mat-icon-button'))
    .filter(btn => btn.query(By.css('mat-icon')).nativeElement.textContent === buttonIcon)
    .map(btn => btn.nativeElement));
}

export function getButtonWithMatIcon(debugElement: DebugElement, buttonIcon: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('button'))
    .filter(btn => btn.query(By.css('mat-icon')).nativeElement.textContent === buttonIcon)
    .map(btn => btn.nativeElement));
}

export function getMatButtonToggleWithMatIcon(debugElement: DebugElement, buttonIcon: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('.mat-button-toggle-label'))
    .filter(btn => btn.query(By.css('mat-icon')).nativeElement.textContent === buttonIcon)
    .map(btn => btn.nativeElement));
}

export function getButtonWithText(debugElement: DebugElement, text: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('button'))
    .map(btn => btn.nativeElement)
    .filter(btnEle => btnEle.textContent.trim() === text));
}
