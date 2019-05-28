/* eslint-disable react/destructuring-assignment */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';


@observer
class ProjectCard extends Component {
  render() {
    const {
      project, 
    } = this.props;    
    const { projectName } = project;
    return (      
      <td 
        role="none"      
        style={{
          width: 140, minWidth: 140, textAlign: 'center', fontWeight: 500, 
        }}
      >          
        {projectName}
      </td>     
    );
  }
}

ProjectCard.propTypes = {

};
export default ProjectCard;
