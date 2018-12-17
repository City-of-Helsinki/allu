import {Observable} from 'rxjs/internal/Observable';
import {of} from 'rxjs/internal/observable/of';

export interface TreeNode {
  id: any;
  children?: TreeNode[];
}

export class TreeFlatNode {
  id: any;
  expandable: boolean;
  level: number;
}

export const isRoot = (node: TreeFlatNode) => node.level === 0;

export const getLevel = (node: TreeFlatNode) => node.level;

export const isExpandable = (node: TreeFlatNode) => node.expandable;

export const getChildren = (node: TreeNode): Observable<TreeNode[]> => of(node.children);

export const hasChild = (_: number, nodeData: TreeFlatNode) => nodeData.expandable;
