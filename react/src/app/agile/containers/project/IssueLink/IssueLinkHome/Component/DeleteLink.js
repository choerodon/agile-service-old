/*eslint-disable */
import React, { Component, Fragment } from 'react';
import {
  Modal, Radio, Select, message, Icon,
} from 'choerodon-ui';
import { Content, stores, axios } from 'choerodon-front-boot';

const { confirm } = Modal;
const RadioGroup = Radio.Group;
const { Option } = Select;
const { AppState } = stores;

class DeleteLink extends Component {
  constructor(props) {
    super(props);
    this.state = {
      link: {},
      radio: 1,
      relatedComponentId: undefined,
      originComponents: [],
      loading: false,
    };
  }

  componentDidMount() {
    this.init();
  }

  onRadioChange = (e) => {
    this.setState({
      radio: e.target.value,
    });
  }

  init() {
    this.setState({
      link: this.props.link || {},
      radio: 1,
      relatedComponentId: undefined,
    });
    axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types/query_all?issueLinkTypeId=${this.props.link.linkTypeId}`, {
      contents: [],
      linkName: '',
    })
      .then((res) => {
        this.setState({
          originComponents: res.content,
        });
      });
  }

  deleteComponent = () => {
    let relatedComponentId;
    if (this.state.radio === 1) {
      relatedComponentId = 0;
    } else if (!this.state.relatedComponentId) {
      message.warning('请选择关联的链接');
      return;
    } else {
      relatedComponentId = this.state.relatedComponentId;
    }
    this.setState({
      loading: true,
    });
    let url;
    if (relatedComponentId) {
      url = `/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types/${this.state.link.linkTypeId}?toIssueLinkTypeId=${relatedComponentId}`;
    } else {
      url = `/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types/${this.state.link.linkTypeId}`;
    }
    axios.delete(url)
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
  }

  renderDelete() {
    const { radio, relatedComponentId, originComponents } = this.state;
    return (
      <Fragment>
        <RadioGroup label="" onChange={this.onRadioChange} value={radio}>
          <Radio style={{ display: 'block' }} value={1}>删除链接</Radio>
          <Radio style={{ display: 'block', marginTop: 5 }} value={2}>
            删除链接，相关问题关联到其他链接            
          </Radio>
        </RadioGroup>
        {radio === 2 && (
        <Select
          label="链接"
          placeholder="请选择一个新的链接"      
          style={{ width: '100%' }}          
          value={relatedComponentId}
          onChange={this.handleRelatedComponentChange.bind(this)}
        >
          {originComponents.map(component => (
            <Option key={component.linkTypeId} value={component.linkTypeId}>
              {component.linkName}
            </Option>
          ))}
        </Select>
        )}
      </Fragment>
    );
  }

  render() {
    const { link, loading } = this.state;
    const { visible, onCancel } = this.props;
    return (
      <Modal
        title="删除链接"
        visible={visible || false}
        confirmLoading={loading}
        onOk={this.deleteComponent}
        onCancel={onCancel}
        okText="删除"
        okType="danger"
      >
        <div style={{ padding: '20px 0' }}>
          <div>
            删除链接:
            <span style={{ margin: '0 10px', fontWeight: 500 }}>{link.linkName}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'flex-start', margin: '10px 0' }}>
            <Icon
              style={{
                color: '#d50000', fontSize: '16px', marginRight: 5, marginTop: 2,
              }}
              type="error"
            />
            将会从所有相关的任务中删除此链接，相关的问题可以选择关联到其他链接，或者不关联。
          </div>         
          <div>{this.renderDelete()}</div>
        </div>
      </Modal>
    );
  }
}

export default DeleteLink;
