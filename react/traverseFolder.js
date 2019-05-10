const fs = require('fs');
const path = require('path');
const { promisify } = require('util');
const { resolve } = require('path');
const readdir = promisify(fs.readdir);
const rename = promisify(fs.rename);
const stat = promisify(fs.stat);
//定义文件夹节点类型，包含本身路径名和子节点
// var folderNode = function(name, children) {
//   this.name = name;
//   this.children = children;
// };
//遍历函数，传入一个节点，之后按照节点的name进行遍历，找出所有子节点
// function traversefolder(node) {
//   node = node instanceof folderNode ? node : new folderNode(node, null);

//   if (fs.statSync(node.name).isDirectory()) {
//     let arr = fs.readdirSync(node.name);
//     arr = arr.map(one => {
//       let ar = new folderNode(path.join(node.name, one), null);
//       return traversefolder(ar);
//     });
//     node.children = arr;
//   }
//   return node;
// }
// var traversefolder = function(dir) {
//   var results = [];
//   var list = fs.readdirSync(dir);
//   list.forEach(function(file) {
//     file = dir + '/' + file;
//     var stat = fs.statSync(file);
//     if (stat && stat.isDirectory())
//       results = results.concat(traversefolder(file));
//     else results.push(file);
//   });
//   return results;
// };

async function traversefolder(dir) {
  const subdirs = await readdir(dir);
  const files = await Promise.all(
    subdirs.map(async subdir => {
      if (subdir !== 'System Volume Information') {
        const res = resolve(dir, subdir);
        return (await stat(res)).isDirectory() ? traversefolder(res) : res;
      }
    }),
  );
  return files.reduce((a, f) => a.concat(f), []);
}
module.exports = traversefolder;
