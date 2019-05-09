import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import PiCard from './PiCard';
// import PiWorkCard from './PiWorkCard';
import './RoadMapContent.scss';

class RoadMapContent extends PureComponent {
  renderPiList=() => {
    const { piList, onFeatureClick, currentFeature } = this.props;
    const contents = piList.map(pi => <PiCard pi={pi} onFeatureClick={onFeatureClick} currentFeature={currentFeature} />);

    // piList.forEach((pi, i) => {
    //   const currentPi = pi;      
    //   const piRange = moment(currentPi.endDate).diff(moment(currentPi.startDate), 'days');
    //   contents.push(<PiCard leap={piRange} pi={pi} />);
    //   if (i !== piList.length - 1) {
    //     const nextPi = piList[i + 1];
    //     const piWorkRange = moment(nextPi.startDate).diff(moment(currentPi.endDate), 'days');        
    //     contents.push(<PiWorkCard leap={piWorkRange} />);
    //   }
    // });
    return contents;
  }

  render() {
    return (
      <div className="c7nagile-RoadMapContent">
        {this.renderPiList()}        
      </div>
    );
  }
}

RoadMapContent.propTypes = {

};

export default RoadMapContent;
