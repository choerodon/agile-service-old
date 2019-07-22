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
        originComponents: res.list,
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
    const { componentIssueRelVOList = [], issueId, objectVersionNumber } = issue;

    if (JSON.stringify(componentIssueRelVOList) !== JSON.stringify(newComponents)) {
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
        componentIssueRelVOList: componentList,
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
    const { store, disabled } = this.props;
    const issue = store.getIssue;
    const { componentIssueRelVOList = [] } = issue;
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'模块：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle            
            disabled={disabled}
            formKey="component"
            onSubmit={this.updateIssueComponents}
            originData={componentIssueRelVOList.map(component => component.name)}
          >
            <Text>
              {componentIssueRelVOList && componentIssueRelVOList.length
                ? (
                  <div>
                    <p style={{ color: '#3f51b5', wordBreak: 'break-word', marginTop: 2 }}>
                      {this.transToArr(componentIssueRelVOList, 'name')}
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
                dropdownMatchSelectWidth={false}
                loading={selectLoading}
                mode="multiple"
                getPopupContainer={() => document.getElementsByClassName('c7n-body-editIssue')[0]}
                tokenSeparators={[',']}
                style={{ marginTop: 0, paddingTop: 0 }}
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
