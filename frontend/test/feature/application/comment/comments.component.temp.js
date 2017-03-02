"use strict";
var testing_1 = require("@angular/core/testing");
var platform_browser_1 = require("@angular/platform-browser");
var comments_component_1 = require("../../../../src/feature/application/comment/comments.component");
var allu_common_module_1 = require("../../../../src/feature/common/allu-common.module");
var application_state_1 = require("../../../../src/service/application/application-state");
var forms_1 = require("@angular/forms");
var ApplicationStateMock = (function () {
    function ApplicationStateMock() {
    }
    return ApplicationStateMock;
}());
/**
 * Named as temp since this test is not working yet (some materialize import error)
 */
describe('CommentsComponent', function () {
    var comp;
    var fixture;
    beforeEach(testing_1.async(function () {
        testing_1.TestBed.configureTestingModule({
            imports: [allu_common_module_1.AlluCommonModule, forms_1.FormsModule],
            declarations: [comments_component_1.CommentsComponent],
            providers: [
                { provide: application_state_1.ApplicationState, useClass: ApplicationStateMock }
            ]
        }).compileComponents();
    }));
    beforeEach(function () {
        fixture = testing_1.TestBed.createComponent(comments_component_1.CommentsComponent);
        comp = fixture.componentInstance;
    });
    it('should show header', function () {
        var title = fixture.debugElement.query(platform_browser_1.By.css('h1 :first-child')).nativeElement;
        expect(title.textContent).toEqual('Kommentit');
    });
});
