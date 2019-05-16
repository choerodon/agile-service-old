import { createElement, Component } from 'react';
import { findDOMNode } from 'react-dom';
import * as DOMHelpers from './dom-helpers';
import testPassiveEventSupport from './passive-polyfill';
import uid from './uid';

let passiveEventSupport;

const handlersMap = {};
const enabledInstances = {};

const touchEvents = ['touchstart', 'touchmove'];
export const IGNORE_CLASS_NAME = 'ignore-react-onclickoutside';

function getEventHandlerOptions(instance, eventName) {
  let handlerOptions = null;
  const isTouchEvent = touchEvents.indexOf(eventName) !== -1;

  if (isTouchEvent && passiveEventSupport) {
    handlerOptions = { passive: !instance.props.preventDefault };
  }
  return handlerOptions;
}

export default function onClickOutsideHOC(WrappedComponent, config) {
  return class onClickOutside extends Component {
    static defaultProps = {
      eventTypes: ['mousedown', 'touchstart'],
      excludeScrollbar: (config && config.excludeScrollbar) || false,
      outsideClickIgnoreClass: IGNORE_CLASS_NAME,
      preventDefault: false,
      stopPropagation: false,
    };

    constructor(props) {
      super(props);
      this.uid = uid();
    }

    componentDidMount() {
      if (typeof document === 'undefined' || !document.createElement) return;
      const instance = this.getInstance();
      if (config && typeof config.handleClickOutside === 'function') {
        this.clickOutsideHandlerProp = config.handleClickOutside(instance);
        if (typeof this.clickOutsideHandlerProp !== 'function') {
          throw new Error('instance handle not a function');
        }
      }
      this.componentNode = findDOMNode(this.getInstance());
      this.enableOnClickOutside();
    }

    componentDidUpdate() {
      this.componentNode = findDOMNode(this.getInstance());
    }

    componentWillUnmount() {
      this.disableOnClickOutside();
    }

    getInstance() {
      if (!WrappedComponent.prototype.isReactComponent) {
        return this;
      }
      const ref = this.instanceRef;
      return ref.getInstance ? ref.getInstance() : ref;
    }

    getRef = (ref) => {
      this.instanceRef = ref;
    }

    enableOnClickOutside = () => {
      if (typeof document === 'undefined' || enabledInstances[this.uid]) return;
      passiveEventSupport = passiveEventSupport || testPassiveEventSupport();
      enabledInstances[this.uid] = true;
      let events = this.props.eventTypes;
      if (!Array.isArray(events)) {
        events = [events];
      }
      handlersMap[this.uid] = (event) => {
        if (this.props.disableOnClickOutside || this.componentNode === null) return;
        if (this.props.preventDefault) { event.preventDefault(); }
        if (this.props.stopPropagation) { event.stopPropagation(); }
        if (this.props.excludeScrollbar && DOMHelpers.clickedScrollbar(event)) return;
        const current = event.target;
        if (DOMHelpers.findHighest(current, this.componentNode, this.props.outsideClickIgnoreClass) 
          !== document) return;
        this.outsideClickHandler(event);
      };

      events.forEach((eventName) => {
        document.addEventListener(
          eventName,
          handlersMap[this.uid],
          getEventHandlerOptions(this, eventName),
        );
      });
    };

    disableOnClickOutside = () => {
      delete enabledInstances[this.uid];
      const fn = handlersMap[this.uid];

      if (fn && typeof document !== 'undefined') {
        let events = this.props.eventTypes;
        if (!events.forEach) {
          events = [events];
        }
        events.forEach(eventName =>
          document.removeEventListener(
            eventName,
            fn,
            getEventHandlerOptions(this, eventName),
          ),
        );
        delete handlersMap[this.uid];
      }
    };

    outsideClickHandler = (event) => {
      if (typeof this.clickOutsideHandlerProp === 'function') {
        this.clickOutsideHandlerProp(event);
        return;
      }
      const instance = this.getInstance();
      if (typeof instance.props.handleClickOutside === 'function') {
        instance.props.handleClickOutside(event);
        return;
      }
      if (typeof instance.handleClickOutside === 'function') {
        instance.handleClickOutside(event);
        return;
      }
      throw new Error(
        'need function handle click out side',
      );
    };

    render() {
      const { ...props } = this.props;

      if (WrappedComponent.prototype.isReactComponent) {
        props.ref = this.getRef;
      } else {
        props.wrappedRef = this.getRef;
      }

      props.disableOnClickOutside = this.disableOnClickOutside;
      props.enableOnClickOutside = this.enableOnClickOutside;

      return createElement(WrappedComponent, props);
    }
  };
}
