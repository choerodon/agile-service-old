import { Quill } from 'react-quill';

const Link = Quill.import('formats/link');
export default class CustomLink extends Link {
  static sanitize(url) {
    const value = super.sanitize(url);
    if (value) {
      for (let i = 0; i < CustomLink.PROTOCOL_WHITELIST.length; i += 1) { 
        if (value.startsWith(CustomLink.PROTOCOL_WHITELIST[i])) { return value; } 
      }
      return `http://${value}`;
    }
    return value;
  }
}
