import React, { Component } from 'react';
import PIAims from '../../program/PI/PIAims';
import IsInProgram from '../../../components/IsInProgram';

class Test extends Component {
  render() { 
    return (
      <IsInProgram>
        {program => <PIAims {...this.props} isProject programId={program.id} />}
      </IsInProgram>
    );
  }
}

Test.propTypes = {

};

export default Test;
