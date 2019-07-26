import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Table, Button, Modal, Form, Select, Input, Tooltip, Tabs, Checkbox } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import { Content, Header, Page, Permission, stores } from '@choerodon/boot';
import './EditConfigSelect.scss';

const prefixCls = 'issue-state-machine-config-select';
const { AppState } = stores;

const Sidebar = Modal.Sidebar;
const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 26 },
  },
};

@observer
class EditConfigSelect extends Component {
  constructor(props) {
    const menu = AppState.currentMenuType;
    super(props);
    const { id, type, machineId } = this.props.match.params;
    this.state = {
      id,
      configType: type,
      machineId,
      organizationId: menu.organizationId,
      submitData: false,
      loading: false,
    };
    this.graph = null;
  }

  componentDidMount() {
    this.loadConfigList();
  }

  getColumn = () => {
    const { transferData, nodeData } = this.state;
    return (
      [{
        title: <FormattedMessage id="stateMachine.config.name" />,
        dataIndex: 'name',
        key: 'name',
        width: 300,
      }, {
        title: <FormattedMessage id="stateMachine.config.des" />,
        dataIndex: 'description',
        key: 'description',
      }]
    );
  }

  loadConfigList = () => {
    const { StateMachineStore } = this.props;
    const { organizationId, id, configType } = this.state;
    StateMachineStore.loadTransferConfigList(organizationId, id, configType).then((data) => {
      if (data) {
        this.setState({
          transferList: data,
        });
      }
    });
  }

  refresh = () => {
    this.loadConfigList();
  }

  handleCancel = () => {
    const { StateMachineStore, intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    const { machineId, configType, id: configId } = this.state;
    history.push(`/agile/state-machines/${machineId}/editconfig/${configId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  }

  handleSubmit = () => {
    const { StateMachineStore } = this.props;
    const { organizationId, id, configType, machineId, submitData } = this.state;
    if (submitData) {
      const data = {
        code: submitData.code,
        type: submitData.type,
        stateMachineId: machineId,
        transformId: id,
      };
      this.setState({
        loading: true,
      });
      StateMachineStore.addConfig(organizationId, machineId, data).then((res) => {
        this.setState({
          loading: false,
        });
        if (res) {
          this.handleCancel();
        }
      });
    }
  }

  render() {
    const { StateMachineStore, intl } = this.props;
    const {
      nodeData,
      transferData,
      configType,
      transferList = [],
      machineId,
      id: configId,
      loading,
    } = this.state;


    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId, name } = menu;
    const cType = configType === 'state' ? 'state' : 'transfer';
    const rowSelection = {
      type: 'radio',
      onSelect: (record, selected, selectedRows, nativeEvent) => {
        this.setState({
          submitData: record,
        });
      },
    };
    return (
      <Page>
        <Header
          title={<FormattedMessage id={`stateMachine.${configType}.add`} />}
          backPath={`/agile/state-machines/${machineId}/editconfig/${configId}?type=${type}&id=${projectId}&name=${encodeURIComponent(name)}&organizationId=${orgId}`}
        >
          <Button
            onClick={this.refresh}
            funcType="flat"
          >
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content>
          <Table
            dataSource={transferList}
            columns={this.getColumn()}
            filterBar={false}
            rowKey={record => record.id}
            loading={StateMachineStore.getIsLoading}
            pagination={false}
            onChange={this.tableChange}
            className="issue-table"
            rowSelection={rowSelection}
          />
          <div className={`${prefixCls}-toolbar`}>
            <Button onClick={() => this.handleSubmit('condition')} type="primary" funcType="raised" loading={loading}>
              <FormattedMessage id="add" />
            </Button>
            <Button className="issue-btn-raised-cancel" onClick={() => this.handleCancel()} funcType="raised">
              <FormattedMessage id="cancel" />
            </Button>
          </div>
        </Content>
      </Page>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(EditConfigSelect)));
