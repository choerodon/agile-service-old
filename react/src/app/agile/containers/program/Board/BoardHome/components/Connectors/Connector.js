import React, { Component } from 'react';
import PropTypes from 'prop-types';
import './Connector.scss';


class Connector extends Component {
  render() {
    const { from, to } = this.props;

    const ax = (from.x + to.x) / 2 + 5;
    const ay = (from.y + to.y) / 2 + 9;
    return (
      [<path
        className="helperLine"
        d={`
        M${from.x},${from.y} 
        C${ax},${ay} ${ax},${ay} ${to.x},${to.y}`}
      />, <path
        className="line"
        d={`
        M${from.x},${from.y} 
        C${ax},${ay} ${ax},${ay} ${to.x},${to.y}`}
        markerStart="url(#StartMarker)"
        markerEnd="url(#arrowhead)"
      />]
    );
  }
}

Connector.propTypes = {

};

export default Connector;
