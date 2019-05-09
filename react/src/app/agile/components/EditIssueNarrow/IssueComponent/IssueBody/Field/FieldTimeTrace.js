import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Progress } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../TextEditToggle';
import VisibleStore from '../../../../../stores/common/visible/VisibleStore';

const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldTimeTrace extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
  }

  getWorkloads = () => {
    const { store } = this.props;
    const worklogs = store.getWorkLogs;
    if (!Array.isArray(worklogs)) {
      return 0;
    }
    return _.reduce(worklogs, (sum, v) => sum + (v.workTime || 0), 0);
  };

  render() {
    const { store } = this.props;
    const issue = store.getIssue;
    const { remainingTime } = issue;
    const workloads = this.getWorkloads();
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'时间跟踪：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            style={{ width: '100%' }}
            disabled
          >
            <Text>
              <div style={{ display: 'flex' }}>
                <Progress
                  style={{ flex: 1, maxWidth: 100 }}
                  percent={
                    workloads !== 0
                      ? (workloads * 100)
                      / (workloads + (remainingTime || 0))
                      : 0
                  }
                  size="small"
                  status="success"
                />
                <span>
                  {workloads}
                  {'小时/'}
                  {workloads + (remainingTime || 0)}
                  {'小时'}
                </span>
                
              </div>
              <div
                role="none"
                style={{
                  marginLeft: '8px',
                  color: '#3f51b5',
                  cursor: 'pointer',
                }}
                onClick={() => {
                  VisibleStore.setWorkLogShow(true);
                }}
              >
                {'登记工作'}
              </div>
            </Text>
            <Edit>
              <div>{remainingTime}</div>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldTimeTrace));
