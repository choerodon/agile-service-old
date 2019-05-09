import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select, Tooltip } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../TextEditToggle';
import { loadVersions, updateIssue } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldVersion extends Component {
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
    loadVersions([]).then((res) => {
      this.setState({
        originVersions: res,
        selectLoading: false,
      });
    });
  };

  updateIssueVersion = () => {
    const { newVersion, originVersions } = this.state;
    const {
      store, onUpdate, reloadIssue, AppState, onCreateVersion,
    } = this.props;
    const issue = store.getIssue;
    const { versionIssueRelDTOList = [], issueId, objectVersionNumber } = issue;
    const influenceVersions = _.filter(versionIssueRelDTOList, { relationType: 'influence' }) || [];

    if (JSON.stringify(influenceVersions) !== JSON.stringify(newVersion)) {
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
            relationType: 'influence',
            projectId: AppState.currentMenuType.id,
          });
        }
      });
      const obj = {
        issueId,
        objectVersionNumber,
        versionIssueRelDTOList: versionList,
        versionType: 'influence',
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
    const influenceVersions = _.filter(versionIssueRelDTOList, { relationType: 'influence' }) || [];

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <Tooltip title="对于非当前版本所发现的缺陷进行版本选择">
            <span className="c7n-property">
              {'影响的版本：'}
            </span>
          </Tooltip>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            formKey="version"
            onSubmit={this.updateIssueVersion}
            originData={this.transToArr(influenceVersions, 'name', 'array')}
          >
            <Text>
              {
                influenceVersions.length ? (
                  <div>
                    <p style={{ color: '#3f51b5', wordBreak: 'break-word' }}>
                      {_.map(influenceVersions, 'name').join(' , ')}
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
                label="影响的版本"
                value={this.transToArr(influenceVersions, 'name', 'array')}
                mode="multiple"
                loading={selectLoading}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                tokenSeparators={[',']}
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

export default withRouter(injectIntl(FieldVersion));
