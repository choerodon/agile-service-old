/* eslint-disable no-bitwise */
const AUTOSCROLL_RATE = 7;
// const isScrollable = (...values) => values.some(value => value === 'auto' || value === 'scroll');
// function findScroller(n) {
//   let node = n;
//   while (node) {
//     const style = window.getComputedStyle(node);
//     if (isScrollable(style.overflow, style.overflowY, style.overflowX)) {
//       return node;
//     } else {
//       node = node.parentNode;
//     }
//   }
//   return null;
// }
export default class AutoScroll {
  constructor({
    rate = AUTOSCROLL_RATE,
    scrollElement = window,
    pos = {
      left: 5,
      top: 5,
      bottom: 5,
      right: 5,
    },
    onMouseMove = () => {},
    onMouseUp = () => {},
    type = 'move',
  } = {}) {
    this.rate = rate;
    this.scrollElement = scrollElement;
    this.pos = pos;
    this.onMouseMove = onMouseMove;
    this.onMouseUp = onMouseUp;
    this.type = type;
  }

  prepare = ({
    clientX, clientY,
  }) => {    
    // 为自动滚动做准备
    const scroller = this.scrollElement;
    const {
      left, width, top, height, 
    } = scroller.getBoundingClientRect();
    const scrollRightPosition = left + width;
    const scrollLeftPosition = left;
    const scrollTopPosition = top;
    const scrollBottomPosition = top + height;
    this.autoScroll = {
      scroller,
      scrollLeftPosition,
      scrollRightPosition,
      scrollTopPosition,
      scrollBottomPosition,
    };
    this.initScrollPosition = {
      x: clientX,
      y: clientY,
      scrollPos: 0,
      scrollLeft: scroller.scrollLeft,
      scrollTop: scroller.scrollTop,
    };
    if (this.type === 'move') {
      document.addEventListener('mouseup', this.handleMouseUp);
      document.addEventListener('mousemove', this.handleMouseMove);
    } else {
      document.addEventListener('dragover', this.handleMouseMove);
      document.addEventListener('drop', this.handleMouseUp);
    }
  }


  handleMouseMove = (e) => {
    e.stopPropagation();
    // e.preventDefault();
    const {
      scroller,
      scrollLeftPosition,
      scrollRightPosition,
      scrollTopPosition,
      scrollBottomPosition,
    } = this.autoScroll;
    this.initMouseX = e.clientX;    
    const {
      left, top, bottom, right, 
    } = this.pos;
    // console.log(scrollLeftPosition, scrollRightPosition, e.clientX);
    if (scrollLeftPosition + left >= e.clientX) {
      this.startAutoScroll({ clientX: e.clientX, clientY: e.clientY }, 'left');
    } else if (scrollRightPosition - right <= e.clientX) {
      this.startAutoScroll({ clientX: e.clientX, clientY: e.clientY }, 'right');
    } else if (scrollTopPosition + top >= e.clientY) {
      this.startAutoScroll({ clientX: e.clientX, clientY: e.clientY }, 'top');
    } else if (scrollBottomPosition - bottom <= e.clientY) {
      this.startAutoScroll({ clientX: e.clientX, clientY: e.clientY }, 'bottom');
    } else {
      this.stopAutoScroll(e.clientX);
    }
    const { scrollLeft, scrollTop } = this.initScrollPosition;
    const scrollLeftPos = scroller.scrollLeft - scrollLeft;
    const scrollTopPos = scroller.scrollTop - scrollTop;
    this.onMouseMove(e, {
      left: scrollLeftPos,
      top: scrollTopPos,
    });
  }

  handleMouseUp = (e) => {       
    document.removeEventListener('mouseup', this.handleMouseUp);
    document.removeEventListener('mousemove', this.handleMouseMove); 
    document.removeEventListener('dragover', this.handleMouseMove);    
    document.removeEventListener('drop', this.handleMouseUp);        
    this.stopAutoScroll();
    this.onMouseUp(e);   
  }


  // 拖动时，当鼠标到边缘时，自动滚动
  startAutoScroll = ({ clientX, clientY }, mode) => {
    const { scroller } = this.autoScroll;

    // 到最左或最右，停止滚动
    const shouldStop = () => {
      switch (mode) {
        case 'left': {
          return scroller.scrollLeft === 0;
        }
        case 'right': {
          return ~~(scroller.scrollLeft + scroller.offsetWidth) === scroller.scrollWidth;
        }
        case 'top': {
          return scroller.scrollTop === 0;
        }
        case 'bottom': {
          return ~~(scroller.scrollTop + scroller.offsetHeight) === scroller.scrollHeight;
        } 
        default: return false;
      }
    };
    if (shouldStop()) {
      cancelAnimationFrame(this.scrollTimer);
      return;
    }
    if (this.scrollTimer) {
      cancelAnimationFrame(this.scrollTimer);
    }
    const scrollFunc = () => {
      switch (mode) {
        case 'left': {
          scroller.scrollLeft -= this.rate;
          break;
        }
        case 'right': {
          scroller.scrollLeft += this.rate;
          break;
        }
        case 'top': {
          scroller.scrollTop -= this.rate;
          break;
        }
        case 'bottom': {
          scroller.scrollTop += this.rate;
          break;
        }
        default: break;
      }
      
      const { scrollLeft, scrollTop } = this.initScrollPosition;
      this.initScrollPosition.scrollPos = scroller.scrollLeft - scrollLeft;      
      const scrollLeftPos = scroller.scrollLeft - scrollLeft;
      const scrollTopPos = scroller.scrollTop - scrollTop;
      // 因为鼠标并没有move，所以这里要手动触发    
      this.onMouseMove({ clientX, clientY }, {
        left: scrollLeftPos,
        top: scrollTopPos,
      });
      if (shouldStop()) {
        cancelAnimationFrame(this.scrollTimer);
      } else {
        this.scrollTimer = requestAnimationFrame(scrollFunc);
      }
    };
    this.scrollTimer = requestAnimationFrame(scrollFunc);
  }

  // 停止自动滚动
  stopAutoScroll = () => {
    cancelAnimationFrame(this.scrollTimer);
  }
}
