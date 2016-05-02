"use strict";
var core_1 = require('angular2/core');
var workqueue_service_1 = require('../../service/workqueue.service');
var WorkqueueComponent = (function () {
    function WorkqueueComponent(WorkqueueService) {
        this.WorkqueueService = WorkqueueService;
    }
    WorkqueueComponent.prototype.jobClick = function (job) {
        console.log(job);
    };
    WorkqueueComponent = __decorate([
        core_1.Component({
            selector: 'workqueue',
            moduleId: module.id,
            templateUrl: './component/workqueue/workqueue.component.html',
            styles: [
                require('./workqueue.component.scss')
            ],
            directives: [],
            providers: [workqueue_service_1.WorkqueueService]
        }), 
        __metadata('design:paramtypes', [workqueue_service_1.WorkqueueService])
    ], WorkqueueComponent);
    return WorkqueueComponent;
}());
exports.WorkqueueComponent = WorkqueueComponent;
//# sourceMappingURL=workqueue.component.js.map