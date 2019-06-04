import React, { Component } from 'react';
import ArtCalendar from './ArtCalendar';
import IsInProgram from '../../../components/IsInProgram';

class Test extends Component {
  render() {
    return (
      <IsInProgram>
        {program => <ArtCalendar {...this.props} isProject programId={program.id} />}
      </IsInProgram>
    );
  }
}

Test.propTypes = {

};

export default Test;
