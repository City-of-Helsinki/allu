import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {ArrayUtil} from '../src/app/util/array-util';
import {MatIcon} from '@angular/material';

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

export function getButtonWithText(debugElement: DebugElement, text: string) {
  return ArrayUtil.first(debugElement.queryAll(By.css('button'))
    .map(btn => btn.nativeElement)
    .filter(btnEle => btnEle.textContent.trim() === text));
}

export function getMatIcon(debugElement: DebugElement, iconName: string) {
  return ArrayUtil.first(debugElement.queryAll(By.directive(MatIcon))
    .map(icon => icon.nativeElement)
    .filter(iconElem => iconElem.textContent === iconName));
}
