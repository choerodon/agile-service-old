import { observer } from 'mobx-react';

const Injecter = observer(({ children, item, store }) => (Array.isArray(item) ? children(item.map(key => store[key])) : children(store[item])));

export default Injecter;
