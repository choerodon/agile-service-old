import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../TextEditToggle';
import { loadComponents, updateIssue } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originComponents: [],
      selectLoading: true,
      newComponents: [],
    };
  }

  componentDidMount() {
    this.loadIssueComponents();
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

  loadIssueComponents = () => {
    loadComponents().then((res) => {
      this.setState({
        originComponents: res.content,
        selectLoading: false,
      });
    });
  };

  updateIssueComponents = () => {
    const { newComponents, originComponents } = this.state;
    const {
      store, onUpdate, reloadIssue, AppState,
    } = this.props;
    const issue = store.getIssue;
    const { componentIssueRelDTOList = [], issueId, objectVersionNumber } = issue;

    if (JSON.stringify(componentIssueRelDTOList) !== JSON.stringify(newComponents)) {
      const componentList = [];
      newComponents.forEach((label) => {
        const target = _.find(originComponents, { name: label });
        if (target) {
          componentList.push(target);
        } else {
          componentList.push({
            name: label,
            projectId: AppState.currentMenuType.id,
          });
        }
      });
      const obj = {
        issueId,
        objectVersionNumber,
        componentIssueRelDTOList: componentList,
      };
      updateIssue(obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
          this.setState({
            newComponents: [],
          });
        });
    }
  };

  render() {
    const { selectLoading, originComponents } = this.state;
    const { store } = this.props;
    const issue = store.getIssue;
    const { componentIssueRelDTOList = [] } = issue;
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'模块：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            formKey="component"
            onSubmit={this.updateIssueComponents}
            originData={componentIssueRelDTOList.map(component => component.name)}
          >
            <Text>
              {componentIssueRelDTOList && componentIssueRelDTOList.length
                ? (
                  <div>
                    <p style={{ color: '#3f51b5', wordBreak: 'break-word', marginTop: 2 }}>
                      {this.transToArr(componentIssueRelDTOList, 'name')}
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
                loading={selectLoading}
                mode="multiple"
                getPopupContainer={triggerNode => triggerNode.parentNode}
                tokenSeparators={[',']}
                style={{ width: '150px', marginTop: 0, paddingTop: 0 }}
                onChange={(value) => {
                  const newComponents = value.filter(v => v && v.trim()).map((item) => {
                    if (_.find(originComponents, { name: item })) {
                      return item;
                    } else {
                      return item.trim().substr(0, 15);
                    }
                  });
                  this.setState({
                    newComponents,
                  });
                }}
              >
                {originComponents && originComponents.map(component => (
                  <Option
                    key={component.name}
                    value={component.name}
                  >
                    {component.name}
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

export default withRouter(injectIntl(FieldComponent));
