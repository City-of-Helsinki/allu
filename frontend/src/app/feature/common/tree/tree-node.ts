import {Observable} from 'rxjs';
import {of} from 'rxjs';

export interface TreeStructureNode<T> {
  [key: string]: TreeStructureNode<T> | T | null;
}

export interface TreeNode {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  id: any;
  children?: TreeNode[];
}

export class TreeFlatNode {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  id: any;
  expandable: boolean;
  level: number;
}

export const isRoot = (node: TreeFlatNode) => node.level === 0;

export const getLevel = (node: TreeFlatNode) => node.level;

export const isExpandable = (node: TreeFlatNode) => node.expandable;

export const getChildren = (node: TreeNode): Observable<TreeNode[]> => of(node.children);

export const hasChild = (_: number, nodeData: TreeFlatNode) => nodeData.expandable;
