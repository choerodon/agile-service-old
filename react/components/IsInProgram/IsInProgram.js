import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Loading from '../Loading';
import { getProjectsInProgram } from '../../api/CommonApi';

class IsInProgram extends Component {
  state = {
    loading: false,
    program: null,
  }

  componentDidMount() {
    this.setState({
      loading: true,
    });
    getProjectsInProgram().then((res) => {
      this.setState({
        program: res,
        loading: false,
      });
    });
  }

  renderChildren=() => {
    const { program } = this.state;
    const { children, project: ProjectElement } = this.props;
    if (program) {
      return children(program);
    } else {
      return ProjectElement || null;
    }
  }

  render() {
    const { loading } = this.state;
    return (loading ? <Loading /> : this.renderChildren());
  }
}

IsInProgram.propTypes = {

};

export default IsInProgram;
