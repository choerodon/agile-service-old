import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../TextEditToggle';
import { loadVersions, updateIssue } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldFixVersion extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originVersions: [],
      selectLoading: true,
      newVersion: [],
    };
  }

  componentDidMount() {
    this.loadIssueVersions();
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

  loadIssueVersions = () => {
    loadVersions(['version_planning', 'released']).then((res) => {
      this.setState({
        originVersions: res,
        selectLoading: false,
      });
    });
  };

  updateIssueFixVersion = () => {
    const { newVersion, originVersions } = this.state;
    const {
      store, onUpdate, reloadIssue, AppState, onCreateVersion,
    } = this.props;
    const issue = store.getIssue;
    const { versionIssueRelDTOList = [], issueId, objectVersionNumber } = issue;
    const fixVersions = _.filter(versionIssueRelDTOList, { relationType: 'fix' }) || [];

    if (JSON.stringify(fixVersions) !== JSON.stringify(newVersion)) {
      const versionList = [];
      let newSign = false;
      newVersion.forEach((version) => {
        const target = _.find(originVersions, { name: version });
        if (target) {
          versionList.push(target);
        } else {
          newSign = true;
          versionList.push({
            name: version,
            relationType: 'fix',
            projectId: AppState.currentMenuType.id,
          });
        }
      });
      const obj = {
        issueId,
        objectVersionNumber,
        versionIssueRelDTOList: versionList,
        versionType: 'fix',
      };
      updateIssue(obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
          // 新建版本，刷新版本侧边栏
          if (newSign && onCreateVersion) {
            onCreateVersion();
          }
        });
    }
  };

  render() {
    const { selectLoading, originVersions } = this.state;
    const { store, hasPermission } = this.props;
    const issue = store.getIssue;
    const { versionIssueRelDTOList = [] } = issue;
    const fixVersionsTotal = _.filter(versionIssueRelDTOList, { relationType: 'fix' }) || [];
    const fixVersionsFixed = _.filter(fixVersionsTotal, { statusCode: 'archived' }) || [];
    const fixVersions = _.filter(fixVersionsTotal, v => v.statusCode !== 'archived') || [];

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'版本：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          {
            fixVersionsFixed.length ? (
              <div>
                <span>已归档版本：</span>
                <span>
                  {_.map(fixVersionsFixed, 'name').join(' , ')}
                </span>
              </div>
            ) : null
          }
          <TextEditToggle
            formKey="fixVersion"
            onSubmit={this.updateIssueFixVersion}
            originData={this.transToArr(fixVersions, 'name', 'array')}
          >
            <Text>
              {
                fixVersionsFixed.length || fixVersions.length ? (
                  <div>
                    <div style={{ color: '#000' }}>
                      {_.map(fixVersionsFixed, 'name').join(' , ')}
                    </div>
                    <p style={{ color: '#3f51b5', wordBreak: 'break-word' }}>
                      {_.map(fixVersions, 'name').join(' , ')}
                    </p>
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
                label="未归档版本"
                mode="multiple"
                loading={selectLoading}
                tokenSeparators={[',']}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                style={{ width: '150px', marginTop: 0, paddingTop: 0 }}
                onChange={(value) => {
                  const versions = value.filter(v => v && v.trim()).map((item) => {
                    if (_.find(originVersions, { name: item })) {
                      return item;
                    } else {
                      return item.trim().substr(0, 15);
                    }
                  });
                  this.setState({
                    newVersion: versions,
                  });
                }}
              >
                {originVersions.map(version => (
                  <Option
                    key={version.name}
                    value={version.name}
                  >
                    {version.name}
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

export default withRouter(injectIntl(FieldFixVersion));
