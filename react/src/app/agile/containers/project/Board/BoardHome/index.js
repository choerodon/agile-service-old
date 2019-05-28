
import React, { Component } from 'react';
import BoardHome from './BoardHome';
import IsInProgram from '../../../../components/IsInProgram';

class Test extends Component {
  render() { 
    return (
      <IsInProgram>
        {program => <BoardHome {...this.props} isProject programId={program.id} />}
      </IsInProgram>
    );
  }
}

export default Test;
