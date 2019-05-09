/* eslint-disable */
import React, { Component, Fragment } from 'react';
import {
  Modal, Radio, Select, message, Icon,
} from 'choerodon-ui';
import { Content, stores } from 'choerodon-front-boot';
import { getUsers } from '../../../../api/CommonApi';
import { createComponent } from '../../../../api/ComponentApi';
import { loadComponents, deleteComponent } from '../../../../api/ComponentApi';
import './component.scss';

const { confirm } = Modal;
const RadioGroup = Radio.Group;
const { Option } = Select;
const { AppState } = stores;

class DeleteComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      component: {},
      radio: 1,
      relatedComponentId: undefined,
      originComponents: [],
      loading: false,
      confirmShow: false,
      filters: {
        searchArgs: {},
        advancedSearchArgs: {
          defaultAssigneeRole: [],
          contents: [],
        },
      },
    };
  }

  componentDidMount() {
    this.init();
  }

  onRadioChange = (e) => {
    this.setState({
      radio: e.target.value,
    });
  };

  init() {
    this.setState({
      component: this.props.component || {},
      radio: 1,
      relatedComponentId: undefined,
    });
  }

  deleteComponent() {
    let relatedComponentId;
    if (this.state.radio === 1) {
      relatedComponentId = 0;
    } else if (!this.state.relatedComponentId) {
      message.warning('请选择关联的模块');
      return;
    } else {
      relatedComponentId = this.state.relatedComponentId;
    }
    this.setState({
      loading: true,
    });
    deleteComponent(this.state.component.componentId, relatedComponentId)
      .then((res) => {
        this.setState({
          loading: false,
        });
        this.props.onOk();
      })
      .catch((error) => {
        this.setState({
          loading: false,
        });
      });
  }

  handleRelatedComponentChange = (value) => {
    this.setState({ relatedComponentId: value });
  };

  renderDelete() {
    const { radio, relatedComponentId } = this.state;   
    return (
      <Fragment>
        <RadioGroup label="" onChange={this.onRadioChange} value={this.state.radio}>
          <Radio style={{ display: 'block' }} value={1}>
            删除模块
          </Radio>
          <Radio style={{ display: 'block', marginTop: 5 }} value={2}>
            删除模块，陷关的问题关联到其他模块
          </Radio>
        </RadioGroup>
        {radio === 2 && (
          <Select
            label="模块"
            placeholder="请选择一个新的模块"      
            style={{ width: '100%' }}
            value={relatedComponentId}
            onChange={this.handleRelatedComponentChange.bind(this)}
            onFocus={() => {
              loadComponents(this.state.filters, this.state.component.componentId).then((res) => {
                this.setState({
                  originComponents: res.content,
                });
              });
            }}
          >
            {this.state.originComponents.map(component => (
              <Option key={component.componentId} value={component.componentId}>
                {component.name}
              </Option>
            ))}
          </Select>
        )}
      </Fragment>
    );
  }

  render() {
    const menu = AppState.currentMenuType;
    const urlParams = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const { component } = this.state;
    const { issueCount } = component;
    const hasIssue = component.issueCount;
    return (
      <Modal
        title={`删除模块`}
        visible={this.props.visible || false}
        confirmLoading={this.state.loading}
        onOk={this.deleteComponent.bind(this)}
        onCancel={this.props.onCancel.bind(this)}
        okText="删除"
        okType="danger"
      >
        <div style={{ padding: '20px 0' }}>
          {
            hasIssue ? (
              <Fragment>
                <div>
                  <div>
                    删除模块:
                    <span style={{ margin: '0 10px', fontWeight: 500 }}>{component.name}</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', marginTop: 10 }}>
                    <Icon
                      style={{
                        color: '#d50000', fontSize: '16px', marginRight: 5,
                      }}
                      type="error"
                    />
                    当前有
                    <span style={{ margin: '0 5px', color: 'red' }}>{issueCount}</span>
                    个问题使用此模块
                  </div>
                  <div style={{ margin: '10px 0' }}>
                    注意：将会从所有使用的问题中删除此模块，相关的问题可以选择关联到其他模块，或不关联模块。
                  </div>
                </div>
                <div>{this.renderDelete()}</div>
              </Fragment>
            )
              : (
                <Fragment>
                  确定要删除
                  <span style={{ margin: '0 5px', fontWeight: 500 }}>{component.name}</span>
                  模块吗？
                </Fragment>
              )
          }
        </div>
      </Modal>
    );
  }
}

export default DeleteComponent;
