import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../TextEditToggle';
import { loadLabels, updateIssue } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldLabel extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originLabels: [],
      selectLoading: true,
      newLabelIssueRelVOList: [],
    };
  }

  componentDidMount() {
    this.loadIssuePriorities();
  }

  transToArr = (arr, pro, type = 'string') => {
    if (!arr.length) {
      return type === 'string' ? '无' : [];
    } else if (typeof arr[0] === 'object') {
      return type === 'string' ? _.map(arr, pro).join() : _.map(arr, pro);
    } else {
      return type === 'string' ? arr.join() : arr;
    }
  };

  loadIssuePriorities = () => {
    loadLabels().then((res) => {
      this.setState({
        originLabels: res,
        selectLoading: false,
      });
    });
  };

  updateIssuePriority = () => {
    const { newLabelIssueRelVOList, originLabels } = this.state;
    const {
      store, onUpdate, reloadIssue, AppState,
    } = this.props;
    const issue = store.getIssue;
    const { labelIssueRelVOList = [], issueId, objectVersionNumber } = issue;
    if (JSON.stringify(labelIssueRelVOList) !== JSON.stringify(newLabelIssueRelVOList)) {
      const labelList = [];
      newLabelIssueRelVOList.forEach((label) => {
        const target = _.find(originLabels, { labelName: label });
        if (target) {
          labelList.push(target);
        } else {
          labelList.push({
            labelName: label,
            projectId: AppState.currentMenuType.id,
          });
        }
      });
      const obj = {
        issueId,
        objectVersionNumber,
        labelIssueRelVOList: labelList,
      };
      updateIssue(obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
          this.loadIssuePriorities();
        });
    }
  };

  render() {
    const { selectLoading, originLabels } = this.state;
    const { store, disabled } = this.props;
    const issue = store.getIssue;
    const { labelIssueRelVOList = [] } = issue;

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'标签：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            disabled={disabled}
            formKey="label"
            onSubmit={this.updateIssuePriority}
            originData={this.transToArr(labelIssueRelVOList, 'labelName', 'array')}
          >
            <Text>
              {
                labelIssueRelVOList.length > 0 ? (
                  <div style={{ display: 'flex', flexWrap: 'wrap' }}>
                    {
                      this.transToArr(labelIssueRelVOList, 'labelName', 'array').map(label => (
                        <div
                          key={label}
                          style={{
                            color: '#000',
                            borderRadius: '100px',
                            fontSize: '13px',
                            lineHeight: '24px',
                            padding: '2px 12px',
                            background: 'rgba(0, 0, 0, 0.08)',
                            marginRight: '8px',
                            marginBottom: 3,
                          }}
                        >
                          {label}
                        </div>
                      ))
                    }
                  </div>
                ) : (
                  <div>
                    {'无'}
                  </div>
                )
              }
            </Text>
            <Edit>
              <Select
                mode="tags"
                loading={selectLoading}
                tokenSeparators={[',']}
                getPopupContainer={() => document.getElementById('detail')}       
                onChange={(value) => {
                  this.setState({
                    newLabelIssueRelVOList: value.map(
                      item => item.substr(0, 10),
                    ),
                  });
                }}
              >
                {originLabels.map(label => (
                  <Option
                    key={label.labelName}
                    value={label.labelName}
                  >
                    {label.labelName}
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

export default withRouter(injectIntl(FieldLabel));
