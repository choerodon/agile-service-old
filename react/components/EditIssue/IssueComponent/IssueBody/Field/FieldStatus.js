import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import { STATUS } from '../../../../../common/Constant';
import { loadStatus, updateStatus } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldStatus extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originStatus: [],
      selectLoading: true,
      transformId: undefined,
    };
  }

  componentDidMount() {
    this.loadIssueStatus();
  }

  componentWillReceiveProps() {
    this.loadIssueStatus();
  }

  loadIssueStatus = () => {
    const {
      store, projectId, applyType, disabled, 
    } = this.props;
    if (disabled) {
      return;
    }
    const issue = store.getIssue;
    const {
      issueTypeVO = {},
      issueId,
      statusId,
      activePi = {},
    } = issue;
    const typeId = issueTypeVO.id;
    loadStatus(statusId, issueId, typeId, applyType, projectId).then((res) => {
      if (applyType === 'program') {
        if (activePi && activePi.statusCode === 'doing') {
          this.setState({
            originStatus: res,
            selectLoading: false,
          });
        } else if (activePi && activePi.statusCode === 'todo') {
          this.setState({
            originStatus: res.filter(item => item.statusVO && ['prepare', 'todo'].indexOf(item.statusVO.type) !== -1),
            selectLoading: false,
          });
        } else {
          this.setState({
            originStatus: res.filter(item => item.statusVO && ['prepare'].indexOf(item.statusVO.type) !== -1),
            selectLoading: false,
          });
        } 
      } else {
        this.setState({
          originStatus: res,
          selectLoading: false,
        });
      }
    });
  };

  updateIssueStatus = () => {
    const { transformId } = this.state;
    const {
      store, onUpdate, reloadIssue, applyType, 
    } = this.props;
    const issue = store.getIssue;
    const { issueId, objectVersionNumber } = issue;
    if (transformId) {
      updateStatus(transformId, issueId, objectVersionNumber, applyType)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
          this.setState({
            transformId: undefined,
          });
        });
    }
  };

  render() {
    const { selectLoading, originStatus } = this.state;
    const { store, disabled } = this.props;
    const issue = store.getIssue;
    const { statusVO = {}, statusId } = issue;
    const { type, name } = statusVO;
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'状态：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            disabled={disabled}
            formKey="status"
            onSubmit={this.updateIssueStatus}
            originData={statusId}            
          >
            <Text>
              {
                statusId ? (
                  <div
                    style={{
                      background: STATUS[type],
                      color: '#fff',
                      borderRadius: '2px',
                      padding: '0 8px',
                      display: 'inline-block',
                      margin: '2px auto 2px 0',
                    }}
                  >
                    {name}
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
                // style={{ width: 150 }}
                loading={selectLoading}
                getPopupContainer={() => document.getElementById('detail')}
                onChange={(value, item) => {
                  this.setState({
                    transformId: item.key,
                  });
                }}
              >
                {
                  originStatus && originStatus.length
                    ? originStatus.map(transform => (transform.statusVO ? (
                      <Option key={transform.id} value={transform.endStatusId}>
                        {transform.statusVO.name}
                      </Option>
                    ) : ''))
                    : null
                }
              </Select>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldStatus));
