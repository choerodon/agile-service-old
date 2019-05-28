import React, { Component } from 'react';
import RoadMap from '../../program/RoadMap/RoadMap';
import IsInProgram from '../../../components/IsInProgram';

class Test extends Component {
  render() { 
    return (
      <IsInProgram>
        {program => <RoadMap {...this.props} projectId={program.id} />}
      </IsInProgram>
    );
  }
}

Test.propTypes = {

};

export default Test;
