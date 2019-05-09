import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Icon, Dropdown, Menu } from 'choerodon-ui';
import moment from 'moment';
import classnames from 'classnames';
import CloseSprint from '../../CloseSprint';
import StartSprint from '../../StartSprint';

@inject('AppState')
@observer class SprintStatus extends Component {
  constructor(props) {
    super(props);
    this.state = {
      startSprintVisible: false,
      finishSprintVisible: false,
    };
  }

  menu = handleDeleteSprint => (
    <Menu
      onClick={handleDeleteSprint}
    >
      <Menu.Item key="0">
        {'删除sprint'}
      </Menu.Item>
    </Menu>
  );

  render() {
    const {
      handleDeleteSprint, statusCode, data, store, refresh, sprintId,
    } = this.props;
    const { finishSprintVisible, startSprintVisible } = this.state;
    // TODO: 内部接口逻辑
    return (
      <div className="c7n-backlog-sprintTitleSide">
        {statusCode === 'started' ? (
          <React.Fragment>
            <p className="c7n-backlog-sprintStatus">
              {'活跃'}
            </p>
            <div style={{ display: 'flex' }}>
              <p
                className="c7n-backlog-closeSprint"
                role="none"
                onClick={(e) => {
                  e.stopPropagation();
                  store.axiosGetSprintCompleteMessage(sprintId).then((res) => {
                    store.setSprintCompleteMessage(res);
                  }).catch(() => {
                  });
                  this.setState({
                    finishSprintVisible: true,
                  });
                }}
              >
                {'完成冲刺'}
              </p>
            </div>
          </React.Fragment>
        ) : (
          <React.Fragment>
            <p className="c7n-backlog-sprintStatus2">
              {'未开始'}
            </p>
            <div style={{ display: 'flex' }}>
              <p
                className={classnames('c7n-backlog-closeSprint', {
                  'c7n-backlog-canCloseSprint': store.getHasActiveSprint || !data.issueSearchDTOList || data.issueSearchDTOList.length === 0,
                })}
                role="none"
                onClick={(e) => {
                  e.stopPropagation();
                  if (!store.getHasActiveSprint && data.issueSearchDTOList && data.issueSearchDTOList.length > 0) {
                    this.setState({
                      startSprintVisible: true,
                    });
                    const year = moment().year();
                    store.axiosGetWorkSetting(year);
                    store.axiosGetOpenSprintDetail(data.sprintId).then((res) => {
                      store.setOpenSprintDetail(res);
                    }).catch(() => {
                    });
                  }
                }}
              >
                {'开启冲刺'}
              </p>
              {data.piId
                ? '' : (
                  <Dropdown overlay={this.menu(handleDeleteSprint)} trigger={['click']}>
                    <Icon style={{ cursor: 'pointer', marginLeft: 5 }} type="more_vert" />
                  </Dropdown>
                )
              }
            </div>
          </React.Fragment>
        )}
        <StartSprint
          store={store}
          visible={startSprintVisible}
          onCancel={() => {
            this.setState({
              startSprintVisible: false,
            });
          }}
          data={data}
          refresh={refresh}
        />
        <CloseSprint
          store={store}
          visible={finishSprintVisible}
          onCancel={() => {
            this.setState({
              finishSprintVisible: false,
            });
          }}
          data={data}
          refresh={refresh}
        />
      </div>
    );
  }
}

export default SprintStatus;
