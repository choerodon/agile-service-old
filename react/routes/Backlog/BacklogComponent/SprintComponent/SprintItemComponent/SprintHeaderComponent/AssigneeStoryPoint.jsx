import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Tooltip, Button, Select, Icon, Modal, Avatar, Dropdown, Menu,
} from 'choerodon-ui';
import EasyEdit from '../../../../../../../components/EasyEdit/EasyEdit';
import UserHead from '../../../../../../../components/UserHead';
import AssigneeModal from './AssigneeModal';
// import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@inject('AppState', 'HeaderStore')
@observer class AssigneeStoryPoint extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false,
    };
  }

  expandMore = () => {
    this.setState({
      expand: true,
    });
  };

  closeMore = () => {
    this.setState({
      expand: false,
    });
  };

  render() {
    const { assigneeIssues, data } = this.props;
    const { expand } = this.state;
    return (
      <div className="c7n-backlog-sprintName" />
    );
  }
}

export default AssigneeStoryPoint;
