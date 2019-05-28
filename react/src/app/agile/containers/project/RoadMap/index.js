import React, { Component } from 'react';
import RoadMap from './RoadMap';
import IsInProgram from '../../../components/IsInProgram';

class Test extends Component {
  render() { 
    return (
      <IsInProgram>
        {program => <RoadMap {...this.props} programId={program.id} />}
      </IsInProgram>
    );
  }
}

Test.propTypes = {

};

export default Test;
