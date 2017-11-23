enum SortDirection {
  ASC,
  DESC
}

export function directionFrom(direction: string) {
  return direction ? SortDirection[direction.toUpperCase()] : undefined;
}

export { SortDirection };
export { SortDirection as Direction };
