import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EasyEdit from '../../../../../../../components/EasyEdit/EasyEdit';
// import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@inject('AppState', 'HeaderStore')
@observer class SprintGoal extends Component {
  handler = (value) => {
    const { handleChangeGoal } = this.props;
    handleChangeGoal(value);
  };

  render() {
    const { sprintGoal } = this.props;
    return (
      <div
        style={{
          display: 'flex',
          alignItems: 'flex-start',
          minWidth: '100px',
          justifyContent: 'flex-end',
        }}
      >
        <p
          style={{ whiteSpace: 'nowrap' }}
        >
          {'冲刺目标：'}
        </p>
        <EasyEdit
          type="input"
          width={200}
          defaultValue={sprintGoal}
          enterOrBlur={this.handler}
          maxLength={30}
        >
          <div
            role="none"
            style={{ cursor: 'pointer' }}
          >
            {sprintGoal || '无'}
          </div>
        </EasyEdit>
      </div>
    );
  }
}

export default SprintGoal;
