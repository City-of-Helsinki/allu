"use strict";
function Some(val) {
    /* tslint:disable:no-null-keyword */
    return val === undefined || val === null
        ? new NoneOpt()
        : new SomeOpt(val);
}
exports.Some = Some;
function None() {
    return new NoneOpt();
}
exports.None = None;
var SomeOpt = (function () {
    function SomeOpt(val) {
        this.val = val;
    }
    SomeOpt.prototype.isDefined = function () {
        /* tslint:disable:no-null-keyword */
        return this.val !== undefined && this.val !== null;
    };
    SomeOpt.prototype.value = function () {
        return this.val;
    };
    SomeOpt.prototype.map = function (fn) {
        var result = fn(this.val);
        return result === undefined ? new NoneOpt() : new SomeOpt(result);
    };
    SomeOpt.prototype.do = function (fn) {
        if (this.isDefined()) {
            fn(this.val);
        }
    };
    SomeOpt.prototype.filter = function (predicate) {
        return predicate(this.val) ? Some(this.val) : None();
    };
    SomeOpt.prototype.orElse = function (val) {
        return this.val;
    };
    return SomeOpt;
}());
exports.SomeOpt = SomeOpt;
var NoneOpt = (function () {
    function NoneOpt() {
    }
    NoneOpt.prototype.isDefined = function () {
        return false;
    };
    NoneOpt.prototype.value = function () {
        throw new Error('No value');
    };
    NoneOpt.prototype.map = function (fn) {
        return new NoneOpt();
    };
    NoneOpt.prototype.do = function (fn) { };
    NoneOpt.prototype.filter = function (predicate) {
        return None();
    };
    NoneOpt.prototype.orElse = function (val) {
        return val;
    };
    return NoneOpt;
}());
exports.NoneOpt = NoneOpt;
