"use strict";
(function (Direction) {
    Direction[Direction["ASC"] = 0] = "ASC";
    Direction[Direction["DESC"] = 1] = "DESC";
})(exports.Direction || (exports.Direction = {}));
var Direction = exports.Direction;
var Sort = (function () {
    function Sort(field, direction) {
        this.field = field;
        this.direction = direction;
        this.field = field;
        this.direction = direction;
    }
    Sort.prototype.byDirection = function (original, sorted) {
        switch (this.direction) {
            case Direction.ASC:
                return sorted.reverse();
            case Direction.DESC:
                return sorted;
            default:
                return original;
        }
    };
    Sort.prototype.icon = function () {
        if (this.direction === Direction.DESC) {
            return 'keyboard_arrow_down';
        }
        else if (this.direction === Direction.ASC) {
            return 'keyboard_arrow_up';
        }
        else {
            return '';
        }
    };
    Sort.prototype.sortFn = function () {
        var _this = this;
        var sort = function (left, right) {
            if (left[_this.field] > right[_this.field]) {
                return 1;
            }
            if (left[_this.field] < right[_this.field]) {
                return -1;
            }
            // a must be equal to b
            return 0;
        };
        return sort;
    };
    return Sort;
}());
exports.Sort = Sort;
