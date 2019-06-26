// 驼峰转横杠
function hump(str) {
  // 火狐默认大小写都有，chrome为驼峰
  if (str === str.toLowerCase()) {
    return str;
  }
  const reg = /([A-Z]|webkit|moz|ms)/g;
  return str.replace(reg, match => `-${match.toLowerCase()}`);
}

// img转base64
function img2base64(img) {
  // 判断图片是否加载完全
  if (img.complete) {
    // img.setAttribute("crossOrigin", 'Anonymous');
    const canvas = document.createElement('canvas');
    const context = canvas.getContext('2d');
    // 图片的宽高与展示的或许不同，这里获取原始大小
    const width = img.naturalWidth;
    const height = img.naturalHeight;
    // canvas绘制
    canvas.width = width;
    canvas.height = height;
    // 画布清除
    context.clearRect(0, 0, width, height);
    // 绘制图片到canvas
    context.drawImage(img, 0, 0);
    return canvas.toDataURL();
  }
  return '';
}
function download(img) {
  const eleLink = document.createElement('a');
  // 下载图片文件名就按照时间戳来
  eleLink.download = `snap-${(`${+new Date()}`).slice(1, 9)}.png`;
  eleLink.style.display = 'none';
  eleLink.href = img2base64(img);
  // 触发点击
  document.body.appendChild(eleLink);
  eleLink.click();
  // 然后移除
  document.body.removeChild(eleLink);
}
/**
* 计算每个 dom 的样式
 * 
 * 
 * 
 * */
function getElementStyles(el) {
  const css = window.getComputedStyle(el);
  let style = '';
  for (const key of css) {
    // console.log(key,isNaN(key))
    style += `${hump(key)}:${css[key]};`;
  }
  return style;
}
function setStyle(element, elementClone) {
  const tag = element.tagName.toLowerCase();
  elementClone.setAttribute('style', getElementStyles(element));
  if (tag === 'img') {
    const src = img2base64(element);
    elementClone.setAttribute('src', src);
  } else if (tag === 'canvas') {
    // 如果是canvas就直接换成img
    const img = new Image();
    img.src = element.toDataURL();
    img.setAttribute('style', elementClone.getAttribute('style'));
    elementClone.parentNode.replaceChild(img, elementClone);
  }
  if (element.children.length) {
    const len = element.children.length;
    for (let i = 0; i < len; i += 1) {
      setStyle(element.children[i], elementClone.children[i]);
    }
  }
}


// 计算 svg 的字符串
function getSvgDomString(element) {
  const dom = element;
  if (!dom) {
    return this;
  }
  // 复制DOM节点
  const cloneDom = dom.cloneNode(true);
  cloneDom.setAttribute('xmlns', 'http://www.w3.org/1999/xhtml');
  // 将样式都写到style
  setStyle(dom, cloneDom);
  const width = element.offsetWidth;
  const height = element.offsetHeight;
  return `
    <svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}">\n
       <foreignObject width="100%" height="100%">\n
          ${new XMLSerializer().serializeToString(cloneDom)}
       </foreignObject>\n
   </svg>`;
}

function factory() {
  // 主入口函数
  function dom2canvas(target, callback) {
    if (!target) {
      throw new TypeError('dom2canvas目标不是DOM元素');
    }
    // this.getCanvas = function () {

    // };
    const data = getSvgDomString(target);
    const img = new Image();
    img.onload = () => {
      if (callback) {
        callback(img);
      }
      // download(img);
    };
    const src = `data:image/svg+xml;charset=utf-8,${data}`;
    img.src = src;
    return src;
  }
  return dom2canvas;
}
export default factory();
