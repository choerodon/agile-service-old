import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import { loadPriorities, updateIssue } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldPriority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originPriorities: [],
      selectLoading: true,
      newPriorityId: undefined,
    };
  }

  componentDidMount() {
    this.loadIssuePriorities();
  }

  loadIssuePriorities = () => {
    loadPriorities().then((res) => {
      this.setState({
        originPriorities: res,
        selectLoading: false,
      });
    });
  };

  updateIssuePriority = () => {
    const { newPriorityId } = this.state;
    const { store, onUpdate, reloadIssue } = this.props;
    const issue = store.getIssue;
    const { priorityId, issueId, objectVersionNumber } = issue;
    if (priorityId !== newPriorityId) {
      const obj = {
        issueId,
        objectVersionNumber,
        priorityId: newPriorityId,
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
    const { selectLoading, originPriorities } = this.state;
    const { store } = this.props;
    const issue = store.getIssue;
    const { priorityId, priorityDTO = {} } = issue;
    const { colour, name } = priorityDTO;

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'优先级：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            formKey="priority"
            onSubmit={this.updateIssuePriority}
            originData={priorityId}
          >
            <Text>
              {
                priorityId ? (
                  <div
                    className="c7n-level"
                    style={{
                      backgroundColor: `${colour}1F`,
                      color: colour,
                      borderRadius: '2px',
                      padding: '0 8px',
                      display: 'inline-block',
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
                dropdownStyle={{ minWidth: 185 }}
                style={{ width: '150px' }}
                loading={selectLoading}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                onChange={(value) => {
                  this.setState({
                    newPriorityId: value,
                  });
                }}
              >
                {
                  originPriorities.filter(p => p.enable || p.id === priorityId).map(priority => (
                    <Option key={priority.id} value={priority.id}>
                      <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
                        <div
                          className="c7n-level"
                          style={{
                            borderRadius: '2px',
                            padding: '0 8px',
                            display: 'inline-block',
                          }}
                        >
                          {priority.name}
                        </div>
                      </div>
                    </Option>
                  ))
                }
              </Select>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldPriority));
