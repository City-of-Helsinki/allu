"use strict";
var cable_info_type_1 = require("./cable-info-type");
var option_1 = require("../../../util/option");
var DefaultText = (function () {
    function DefaultText(id, type, text) {
        this.id = id;
        this.type = type;
        this.text = text;
    }
    DefaultText.ofType = function (type) {
        return new DefaultText(undefined, type, undefined);
    };
    DefaultText.mapBackend = function (cableInfoText) {
        return new DefaultText(cableInfoText.id, cable_info_type_1.CableInfoType[cableInfoText.cableInfoType], cableInfoText.textValue);
    };
    DefaultText.mapFrontend = function (defaultText) {
        return {
            id: defaultText.id,
            cableInfoType: cable_info_type_1.CableInfoType[defaultText.type],
            textValue: defaultText.text
        };
    };
    // Creates a map (texts by type) from array of DefaultTexts
    DefaultText.groupByType = function (texts) {
        var result = texts.reduce(function (map, text) {
            map[cable_info_type_1.CableInfoType[text.type]] = option_1.Some(map[cable_info_type_1.CableInfoType[text.type]])
                .map(function (textsForType) { return textsForType.concat(text); })
                .orElse([text]);
            return map;
        }, {});
        return result;
    };
    return DefaultText;
}());
exports.DefaultText = DefaultText;
