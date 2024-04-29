"use strict";
exports.__esModule = true;
var platform_browser_1 = require("@angular/platform-browser");
var array_util_1 = require("../src/util/array-util");
function getMdIconButton(debugElement, buttonIcon) {
    return array_util_1.ArrayUtil.first(debugElement.queryAll(platform_browser_1.By.css('button'))
        .filter(function (btn) { return btn.query(platform_browser_1.By.css('md-icon')).nativeElement.textContent === buttonIcon; })
        .map(function (btn) { return btn.nativeElement; }));
}
exports.getMdIconButton = getMdIconButton;
