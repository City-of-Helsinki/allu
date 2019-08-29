import {TreeFlatNode, TreeNode, TreeStructureNode} from '@feature/common/tree/tree-node';

export class MapLayerNode implements TreeNode {
  constructor(public id: string, public children?: MapLayerNode[]) {}
}

export class MapLayerFlatNode implements TreeFlatNode {
  constructor(public id: string, public expandable: boolean, public level: number) {}
}

export const transformer = (node: MapLayerNode, level: number) => {
  return new MapLayerFlatNode(node.id, !!node.children, level);
};

export function buildTree(structure: TreeStructureNode<void>, level: number = 0): MapLayerNode[] {
  return Object.keys(structure).reduce<MapLayerNode[]>((accumulator, key) => {
    const value = structure[key];
    const node = new MapLayerNode(key);

    if (value != null && typeof value === 'object') {
      node.children = buildTree(value, level + 1);
    }

    return accumulator.concat(node);
  }, []);
}
