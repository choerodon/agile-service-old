import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../../../../../components/TextEditToggle';
import { loadEpics, updateIssue } from '../../../../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldEpic extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originEpics: [],
      selectLoading: true,
      newEpicId: undefined,
    };
  }

  componentDidMount() {
    this.loadIssueEpic();
  }

  loadIssueEpic = () => {
    loadEpics().then((res) => {
      this.setState({
        originEpics: res,
        selectLoading: false,
      });
    });
  };

  updateIssueEpic = () => {
    const { newEpicId } = this.state;
    const { store, onUpdate, reloadIssue } = this.props;
    const issue = store.getIssue;
    const { epicId, issueId, objectVersionNumber } = issue;
    if (epicId !== newEpicId) {
      const obj = {
        issueId,
        objectVersionNumber,
        epicId: newEpicId || 0,
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
    const { selectLoading, originEpics } = this.state;
    const { store } = this.props;
    const issue = store.getIssue;
    const { epicColor, epicId, epicName } = issue;
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'史诗：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            formKey="epic"
            onSubmit={this.updateIssueEpic}
            originData={epicId || []}
          >
            <Text>
              {
                epicId ? (
                  <div
                    style={{
                      color: epicColor,
                      borderWidth: '1px',
                      borderStyle: 'solid',
                      borderColor: epicColor,
                      borderRadius: '2px',
                      fontSize: '13px',
                      lineHeight: '20px',
                      padding: '0 8px',
                      display: 'inline-block',
                    }}
                  >
                    {epicName}
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
                getPopupContainer={triggerNode => triggerNode.parentNode}
                style={{ width: '150px' }}
                allowClear
                loading={selectLoading}
                onChange={(value) => {
                  this.setState({
                    newEpicId: value,
                  });
                }}
              >
                {originEpics.map(epic => <Option key={`${epic.issueId}`} value={epic.issueId}>{epic.epicName}</Option>)}
              </Select>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldEpic));
