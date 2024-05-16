import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map.component';
import {MapUtil} from '@service/map/map.util';
import {MapLayerService} from '@feature/map/map-layer.service';
import {MapDataService} from '@service/map/map-data-service';
import {MapPopupService} from '@service/map/map-popup.service';
import {MapPopupComponent} from './map-popup.component';
import {RouterModule} from '@angular/router';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {MapController} from '@service/map/map-controller';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from './reducers';
import {EffectsModule} from '@ngrx/effects';
import {MapLayerEffects} from '@feature/map/effects/map-layer-effects';
import {MapLayerSelectComponent} from '@feature/map/map-layer-select.component';
import {ReactiveFormsModule} from '@angular/forms';
import {MatTreeModule} from '@angular/material/tree';
import {SimpleMapComponent} from '@feature/map/simple-map.component';
import {AddressSearchEffects} from '@feature/map/effects/address-search-effects';
import {ApplicationEffects} from '@feature/map/effects/application-effects';
import {Projection} from '@feature/map/projection';

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        AlluCommonModule,
        StoreModule.forFeature('map', reducersToken),
        EffectsModule.forFeature([
            MapLayerEffects,
            AddressSearchEffects,
            ApplicationEffects
        ]),
        ReactiveFormsModule,
        MatTreeModule
    ],
    declarations: [
        MapComponent,
        MapPopupComponent,
        MapLayerSelectComponent,
        SimpleMapComponent
    ],
    exports: [
        MapComponent,
        MapLayerSelectComponent,
        SimpleMapComponent
    ],
    providers: [
        MapUtil,
        MapLayerService,
        MapPopupService,
        MapController,
        MapDataService,
        reducersProvider,
        Projection
    ]
})
export class MapModule {}
