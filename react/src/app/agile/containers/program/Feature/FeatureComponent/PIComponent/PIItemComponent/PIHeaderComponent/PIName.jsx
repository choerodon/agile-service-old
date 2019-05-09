import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Icon } from 'choerodon-ui';
import EasyEdit from '../../../../../../../components/EasyEdit/EasyEdit';

@inject('AppState', 'HeaderStore')
@observer class PIName extends Component {
  handleChange = (value) => {
    const { handleBlurName } = this.props;
    handleBlurName(value);
  };

  render() {
    const {
      expand, piName, toggleSprint,
    } = this.props;
    return (
      <div className="c7n-feature-sprintName">
        <Icon
          style={{ fontSize: 20, cursor: 'pointer' }}
          type={expand ? 'baseline-arrow_drop_down' : 'baseline-arrow_right'}
          role="none"
          onClick={toggleSprint}
        />
        <span
          style={{ marginLeft: 8, cursor: 'pointer', whiteSpace: 'nowrap' }}
          role="none"
        >
          {piName}
        </span>
      </div>
    );
  }
}

export default PIName;
