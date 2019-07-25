import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Table, Button, Modal, Form, Select, Input, Tooltip, message, 
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores, 
} from '@choerodon/boot';
import './StateMachineList.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const prefixCls = 'issue-state-machine';
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
class StateMachineList extends Component {
  constructor(props) {
    const menu = AppState.currentMenuType;
    super(props);
    this.state = {
      page: 1,
      pageSize: 10,
      id: '',
      organizationId: menu.organizationId,
      projectId: menu.id,
      openRemove: false,
      show: false,
      submitting: false,
      statesMachineList: [],
    };
  }

  componentDidMount() {
    this.loadStateMachine();
  }

  getColumn = () => ([{
    title: <FormattedMessage id="stateMachine.name" />,
    dataIndex: 'name',
    key: 'name',
    filters: [],
  }, {
    title: <FormattedMessage id="stateMachine.related" />,
    dataIndex: 'stateMachineSchemeVOS',
    key: 'stateMachineSchemeVOS',
    className: 'issue-table-ellipsis',
    render: (text, record) => {
      const map = [];
      if (text && text.length) {
        map.push(text.map(data => (
          <li key={data.id}>
            <a onClick={() => this.handleSchemeClick(data.id)} role="none">{data.name}</a>
          </li>
        )));
      } else {
        return <div>-</div>;
      }
      return <ul className={`${prefixCls}-related`}>{map}</ul>;
    },
  }, {
    align: 'right',
    width: 104,
    key: 'action',
    render: (test, record) => (
      <div>
        <Tooltip placement="top" title={<FormattedMessage id="edit" />}>
          <Button shape="circle" size="small" onClick={this.handleEdit.bind(this, record.id, record.status)}>
            <i className="icon icon-mode_edit" />
          </Button>
        </Tooltip>
        {!(record && record.stateMachineSchemeVOS && record.stateMachineSchemeVOS.length)
        && !record.default
          ? (
            <Tooltip placement="top" title={<FormattedMessage id="delete" />}>
              <Button shape="circle" size="small" onClick={this.confirmDelete.bind(this, record)}>
                <i className="icon icon-delete" />
              </Button>
            </Tooltip>
          ) : <div className="issue-del-space" />
        }
      </div>
    ),
  }])

  showSideBar = (state, id = '') => {
    this.setState({
      show: true,
      type: state,
    });
  };

  hideSidebar = () => {
    this.setState({
      show: false,
      type: '',
    });
  };

  confirmDelete = (record) => {
    this.setState({
      deleteVisible: true,
      deleteId: record.id,
      deleteName: record.name,
    });
  };

  handleSchemeClick = (schemeId) => {
    const { history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    history.push(`/agile/state-machine-schemes/edit/${schemeId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}&fromMachine=true`);
  };

  loadStateMachine = (page = 1, size = 10, sort = { field: 'id', order: 'desc' }, param = {}) => {
    const { StateMachineStore } = this.props;
    const { organizationId } = this.state;
    StateMachineStore.loadStateMachineList(organizationId, sort, { page, size, ...param })
      .then((data) => {
        this.setState({
          statesMachineList: data.list,
          total: data.total,
        });
      });
  };

  tableChange = (pagination, filters, sorter, param) => {
    const orgId = AppState.currentMenuType.organizationId;
    const sort = {};
    if (sorter.column) {
      const { field, order } = sorter;
      sort[field] = order;
    }
    let searchParam = {};
    if (Object.keys(filters).length) {
      searchParam = filters;
    }
    const postData = {
      ...searchParam,
      param: param.toString(),
    };
    this.setState({
      page: pagination.current,
      pageSize: pagination.pageSize,
      sorter: sorter.column ? sorter : undefined,
      tableParam: postData,
    });
    this.loadStateMachine(pagination.current, pagination.pageSize, sorter.column ? sorter : undefined, postData);
  };

  handleSubmit = () => {
    const { StateMachineStore } = this.props;
    const {
      id,
      type,
      editState,
      page,
      pageSize,
      sorter,
      tableParam,
      organizationId,
    } = this.state;

    this.props.form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        const postData = data;
        postData.organizationId = organizationId;
        this.setState({
          submitting: true,
        });
        StateMachineStore.createStateMachine(organizationId, postData)
          .then((res) => {
            if (res) {
              this.loadStateMachine(page, pageSize, sorter, tableParam);
              this.setState({ type: false, show: false });
            }
            this.setState({
              submitting: false,
            });
          }).catch((error) => {
            Choerodon.prompt(error.response.data.message);
            this.setState({
              submitting: false,
            });
          });
      }
    });
  }

  closeRemove = () => {
    this.setState({
      deleteVisible: false,
      deleteId: '',
    });
  }

  handleDelete = () => {
    const { StateMachineStore, intl } = this.props;
    const {
      organizationId, deleteId, page, pageSize, sorter, tableParam, 
    } = this.state;
    StateMachineStore.deleteStateMachine(organizationId, deleteId)
      .then((data) => {
        if (data) {
          message.success(intl.formatMessage({ id: 'deleteSuccess' }));
        } else {
          message.error(intl.formatMessage({ id: 'deleteFailed' }));
        }
        this.closeRemove();
        this.loadStateMachine(page, pageSize, sorter, tableParam);
      }).catch((error) => {
        message.error(intl.formatMessage({ id: 'deleteFailed' }));
        this.closeRemove();
      });
  };

  refresh = () => {
    this.loadStateMachine();
  };

  closeRemove = () => {
    this.setState({
      deleteVisible: false, deleteId: false,
    });
  };

  handleEdit = (stateMachineId, status) => {
    const { StateMachineStore, intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    history.push(`/agile/state-machines/edit/${stateMachineId}/${status === 'state_machine_create' ? status : 'state_machine_active'}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  checkName = async (rule, value, callback) => {
    const { StateMachineStore, intl } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const res = await StateMachineStore.checkName(orgId, value);
    if (res) {
      callback(intl.formatMessage({ id: 'priority.create.name.error' }));
    } else {
      callback();
    }
  };

  render() {
    const { StateMachineStore, intl } = this.props;
    const {
      statesMachineList,
      page,
      pageSize,
      total,
      deleteName,
    } = this.state;

    const { getFieldDecorator } = this.props.form;
    const { singleData } = StateMachineStore;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const formContent = (
      <div className="issue-region">
        <Form layout="vertical" className="issue-sidebar-form c7nagile-form">
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('name', {
              rules: [{
                required: true,
                whitespace: true,
                max: 47,
                message: intl.formatMessage({ id: 'required' }),
              }, {
                validator: this.checkName,
              }],
            })(
              <Input
                style={{ width: 520 }}
                autoFocus
                label={<FormattedMessage id="stateMachine.name" />}
                size="default"
              />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            className="issue-sidebar-form"
          >
            {getFieldDecorator('description', {
              initialValue: singleData ? singleData.name : '',
            })(
              <TextArea
                style={{ width: 520 }}
                label={<FormattedMessage id="stateMachine.des" />}
              />,
            )}
          </FormItem>
        </Form>
      </div>
    );
    const pageInfo = {
      defaultCurrent: page,
      defaultPageSize: pageSize,
      total,
    };
    return (
      <Page>
        <Header title={<FormattedMessage id="stateMachine.title" />}>
          {statesMachineList && statesMachineList.length === 0
            ? (
              <Tooltip placement="bottom" title="请创建项目后再创建状态机">
                <Button
                  disabled
                >
                  <i className="icon-add icon" />
                  <FormattedMessage id="stateMachine.create" />
                </Button>
              </Tooltip>
            )
            : (
              <Button
                onClick={() => this.showSideBar('create')}
              >
                <i className="icon-add icon" />
                <FormattedMessage id="stateMachine.create" />
              </Button>
            )
          }
          <Button onClick={this.refresh}>
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content
          description={intl.formatMessage({ id: 'stateMachine.list.tip' })}
          link="https://choerodon.io/zh/docs/user-guide/system-configuration/issue-configuration/state-machine-setup/state-machine/"
        >
          <Table
            dataSource={statesMachineList}
            columns={this.getColumn()}
            filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
            rowKey={record => record.id}
            loading={StateMachineStore.getIsLoading}
            pagination={pageInfo}
            onChange={this.tableChange}
            className="issue-table"
          />
        </Content>
        {this.state.show && (
        <Sidebar
          title={<FormattedMessage id={this.state.type === 'create' ? 'stateMachine.create' : 'stateMachine.edit'} />}
          visible={this.state.show}
          onOk={this.handleSubmit}
          okText={<FormattedMessage id={this.state.type === 'create' ? 'create' : 'save'} />}
          cancelText={<FormattedMessage id="cancel" />}
          confirmLoading={this.state.submitting}
          onCancel={this.hideSidebar}
        >
          {formContent}
        </Sidebar>
        )}
        <Modal
          title={<FormattedMessage id="stateMachine.delete" />}
          visible={this.state.deleteVisible}
          onOk={this.handleDelete}
          onCancel={this.closeRemove}
        >
          <p className={`${prefixCls}-del-content`}>
            <FormattedMessage id="stateMachine.delete" />
            <span>:</span>
            <span className={`${prefixCls}-del-content-name`}>{deleteName}</span>
          </p>
          <p className={`${prefixCls}-del-tip`}>
            <FormattedMessage id="state.delete.tip" />
          </p>
        </Modal>
      </Page>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(StateMachineList)));
