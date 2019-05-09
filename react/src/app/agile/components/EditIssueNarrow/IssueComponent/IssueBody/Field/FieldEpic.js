import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import { loadEpics, updateIssue } from '../../../../../api/NewIssueApi';
import { getFeaturesByEpic } from '../../../../../api/FeatureApi';
import { getProjectsInProgram } from '../../../../../api/CommonApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldEpic extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originEpics: [],
      originFeatures: [],
      selectLoading: true,
      newEpicId: undefined,
      newFeatureId: undefined,
      isInProgram: false,
    };
  }

  componentDidMount() {
    this.init();
  }

  componentWillReceiveProps() {
    this.init();
  }

  init = () => {
    const { store } = this.props;
    const issue = store.getIssue;
    const { epicId } = issue;
    loadEpics().then((res) => {
      this.setState({
        originEpics: res,
        selectLoading: false,
      });
    });
    getProjectsInProgram().then((res) => {
      getFeaturesByEpic().then((data) => {
        this.setState({
          originFeatures: data,
          selectLoading: false,
          isInProgram: Boolean(res),
        });
      });
    });
  };

  updateIssueEpic = () => {
    const { newEpicId, isInProgram } = this.state;
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
          if (isInProgram) {
            getFeaturesByEpic().then((data) => {
              this.setState({
                originFeatures: data,
              });
            });
          }
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
        });
    }
  };

  updateIssueFeature = () => {
    const { newFeatureId } = this.state;
    const { store, onUpdate, reloadIssue } = this.props;
    const issue = store.getIssue;
    const { featureId = 1, issueId, objectVersionNumber } = issue;
    if (featureId !== newFeatureId) {
      const obj = {
        issueId,
        objectVersionNumber,
        featureId: newFeatureId || 0,
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
    const {
      selectLoading, originEpics, originFeatures, isInProgram,
    } = this.state;
    const { store } = this.props;
    const issue = store.getIssue;
    const {
      epicColor, epicId, epicName, typeCode,
      featureId, featureName,
    } = issue;
    return (
      <React.Fragment>
        {typeCode === 'story' && isInProgram
          ? (
            <div className="line-start mt-10">
              <div className="c7n-property-wrapper">
                <span className="c7n-property">
                  {'特性：'}
                </span>
              </div>
              <div className="c7n-value-wrapper">
                <TextEditToggle
                  formKey="epic"
                  onSubmit={this.updateIssueFeature}
                  originData={featureId || []}
                >
                  <Text>
                    {featureName ? (
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
                        {featureName}
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
                          newFeatureId: value,
                        });
                      }}
                    >
                      {originFeatures.map(epic => <Option key={`${epic.issueId}`} value={epic.issueId}>{epic.summary}</Option>)}
                    </Select>
                  </Edit>
                </TextEditToggle>
              </div>
            </div>
          ) : ''
        }
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
              // disabled={!!featureName}
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
      </React.Fragment>
    );
  }
}

export default withRouter(injectIntl(FieldEpic));
