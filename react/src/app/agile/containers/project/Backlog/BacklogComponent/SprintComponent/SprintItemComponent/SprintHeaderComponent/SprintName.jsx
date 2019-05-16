import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Icon } from 'choerodon-ui';
import EasyEdit from '../../../../../../../components/EasyEdit/EasyEdit';
// import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@inject('AppState', 'HeaderStore')
@observer class SprintHeader extends Component {
  handleChange = (value) => {
    const { handleBlurName } = this.props;
    handleBlurName(value);
  };

  render() {
    const {
      expand, sprintName, toggleSprint, type, data,
    } = this.props;
    return (
      <div className="c7n-backlog-sprintName">
        <Icon
          style={{ fontSize: 20, cursor: 'pointer' }}
          type={expand ? 'baseline-arrow_drop_down' : 'baseline-arrow_right'}
          role="none"
          onClick={toggleSprint}
        />
        {type !== 'backlog' ? (
          <EasyEdit
            width={150}
            maxLength={30}
            type="input"
            defaultValue={sprintName}
            enterOrBlur={this.handleChange}
            disabled={!!data.piId}
          >
            <span
              style={{ marginLeft: 8, cursor: 'pointer', whiteSpace: 'nowrap' }}
              role="none"
            >
              {sprintName}
            </span>
          </EasyEdit>
        ) : (
          <span
            style={{ marginLeft: 8, cursor: 'pointer', whiteSpace: 'nowrap' }}
            role="none"
          >
            {sprintName}
          </span>
        )}
      </div>
    );
  }
}

export default SprintHeader;
