import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select, Tooltip } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../../../../../components/TextEditToggle';
import { loadSprints, updateIssue } from '../../../../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldSprint extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originSprints: [],
      selectLoading: true,
      newSprintId: undefined,
    };
  }

  componentDidMount() {
    this.loadIssueSprints();
  }

  loadIssueSprints = () => {
    loadSprints(['sprint_planning', 'started']).then((res) => {
      this.setState({
        originSprints: res,
        selectLoading: false,
      });
    });
  };

  updateIssueSprint = () => {
    const { newSprintId } = this.state;
    const {
      store, onUpdate, reloadIssue,
    } = this.props;
    const issue = store.getIssue;
    const { activeSprint = {}, issueId, objectVersionNumber } = issue;
    const sprintId = activeSprint ? activeSprint.sprintId : undefined;

    if (newSprintId !== sprintId) {
      const obj = {
        issueId,
        objectVersionNumber,
        sprintId: newSprintId || 0,
      };
      updateIssue(obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
        });
    }
  };

  render() {
    const { selectLoading, originSprints } = this.state;
    const { store } = this.props;
    const issue = store.getIssue;
    const { closeSprint = [], activeSprint = {} } = issue;
    const sprintId = activeSprint ? activeSprint.sprintId : undefined;

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'冲刺：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          {
            closeSprint.length ? (
              <div>
                <span>已结束冲刺：</span>
                <span>
                  {_.map(closeSprint, 'sprintName').join(' , ')}
                </span>
              </div>
            ) : null
          }
          <TextEditToggle
            formKey="sprint"
            onSubmit={this.updateIssueSprint}
            originData={sprintId}
          >
            <Text>
              <Tooltip
                placement="top"
                title={`该问题经历迭代数：${closeSprint.length + (sprintId ? 1 : 0)}`}
              >
                <div>
                  {
                    !closeSprint.length && !sprintId ? '无' : (
                      <div>
                        <div>
                          {_.map(closeSprint, 'sprintName').join(' , ')}
                        </div>
                        {
                          sprintId && (
                            <div
                              style={{
                                color: '#4d90fe',
                                fontSize: '13px',
                                lineHeight: '20px',
                                display: 'inline-block',
                                marginTop: closeSprint.length ? 5 : 0,
                              }}
                            >
                              {activeSprint.sprintName}
                            </div>
                          )
                        }
                      </div>
                    )
                  }
                </div>
              </Tooltip>
            </Text>
            <Edit>
              <Select
                label="活跃冲刺"
                getPopupContainer={triggerNode => triggerNode.parentNode}
                style={{ width: '172px' }}
                allowClear
                loading={selectLoading}
                onChange={(value) => {
                  this.setState({
                    newSprintId: value,
                  });
                }}
              >
                {originSprints.map(sprint => (
                  <Option key={`${sprint.sprintId}`} value={sprint.sprintId}>
                    <Tooltip placement="left" title={sprint.sprintName}>{sprint.sprintName}</Tooltip>
                  </Option>
                ))}
              </Select>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldSprint));
