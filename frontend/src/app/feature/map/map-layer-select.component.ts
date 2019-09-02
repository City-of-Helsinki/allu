import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromMapLayers from '@feature/map/reducers';
import {Subject} from 'rxjs/internal/Subject';
import {SelectLayers} from '@feature/map/actions/map-layer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {
  getChildren,
  getLevel,
  hasChild as nodeHasChild,
  isExpandable,
  isRoot as nodeIsRoot,
  TreeStructureNode
} from '@feature/common/tree/tree-node';
import {buildTree, MapLayerFlatNode, MapLayerNode, transformer} from './map-layer-node';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material';
import {SelectionModel} from '@angular/cdk/collections';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {StoredFilterType} from '@model/user/stored-filter-type';


@Component({
  selector: 'map-layer-select',
  templateUrl: './map-layer-select.component.html',
  styleUrls: [
    './map-layer-select.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MapLayerSelectComponent implements OnInit, OnDestroy {
  @Input() targetType: ActionTargetType = ActionTargetType.Home;
  @Input() layerTree: TreeStructureNode<void>;
  @Input() classNames: string[] = [];

  treeControl: FlatTreeControl<MapLayerFlatNode>;
  treeFlattener: MatTreeFlattener<MapLayerNode, MapLayerFlatNode>;
  dataSource: MatTreeFlatDataSource<MapLayerNode, MapLayerFlatNode>;
  checklistSelection = new SelectionModel<string>(true /* multiple */);

  hasChild = nodeHasChild;
  isRoot = nodeIsRoot;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private store: Store<fromMapLayers.State>, private storedFilterStore: StoredFilterStore) {}

  ngOnInit(): void {
    this.treeFlattener = new MatTreeFlattener(transformer, getLevel, isExpandable, getChildren);
    this.treeControl = new FlatTreeControl<MapLayerFlatNode>(getLevel, isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.dataSource.data = buildTree(this.layerTree);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set selected(selected: string[]) {
    this.checklistSelection.clear();
    this.checklistSelection.select(...selected);
  }

  isSelected(layerId: string): boolean {
    return this.checklistSelection.isSelected(layerId);
  }

  toggleLayer(node: MapLayerFlatNode): void {
    this.checklistSelection.toggle(node.id);
    this.store.dispatch(new SelectLayers(this.targetType, this.checklistSelection.selected));
    // TODO: stored filters should be moved to common store and reset handled there
    this.storedFilterStore.resetCurrent(StoredFilterType.MAP);
  }
}
