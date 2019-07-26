export function isNodeFound(current, componentNode, ignoreClass) {
  if (current === componentNode) return true;
  if (current.correspondingElement) {
    return current.correspondingElement.classList.contains(ignoreClass);
  }
  return current.classList.contains(ignoreClass);
}

export function findHighest(current, componentNode, ignoreClass) {
  if (current === componentNode) return true;
  while (current.parentNode) {
    if (isNodeFound(current, componentNode, ignoreClass)) return true;
    current = current.parentNode;
  }
  return current;
}

export function clickedScrollbar(evt) {
  return document.documentElement.clientWidth <= evt.clientX 
    || document.documentElement.clientHeight <= evt.clientY;
}
