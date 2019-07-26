import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Table, Button, Modal, Form, Select, Input, Tooltip,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores,
} from '@choerodon/boot';
import { getStageMap, getStageList } from '../../../common/utils';
import Tips from '../../../components/Tips';

import './StateList.scss';

const { AppState } = stores;
const { Sidebar, info } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
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
const prefixCls = 'issue-state';

const stageMap = getStageMap();
const stageList = getStageList();
@observer
class StateList extends Component {
  constructor(props) {
    const menu = AppState.currentMenuType;
    super(props);
    this.state = {
      page: 1,
      pageSize: 10,
      initialTotal: 0,
      total: 0,
      show: false,
      submitting: false,
      deleteVisible: false,
    };
    this.modelRef = false;
  }

  componentDidMount() {
    this.loadState({ 
      page: undefined,
      size: undefined, 
      sort: undefined, 
      param: undefined, 
      isSetInitialTotal: true, 
    });
  }

  getColumn = () => ([{
    title: <FormattedMessage id="state.name" />,
    dataIndex: 'name',
    key: 'name',
    filters: [],
  }, {
    title: <FormattedMessage id="state.des" />,
    dataIndex: 'description',
    key: 'description',
    filters: [],
    className: 'issue-table-ellipsis',
  }, {
    title: <FormattedMessage id="state.stage" />,
    dataIndex: 'type',
    key: 'type',
    filters: stageList.filter(s => s.code !== 'none').map(s => ({ text: s.name, value: s.code })),
    render: record => (
      <div>
        <div className="issue-state-block" style={{ backgroundColor: stageMap[record].colour }} />
        <span style={{ verticalAlign: 'middle' }}>{stageMap[record].name}</span>
      </div>
    ),
  }, {
    title: <FormattedMessage id="state.stateMachine" />,
    dataIndex: 'stateMachine',
    key: 'stateMachine',
    render: (text, record) => (
      <div>
        {record.stateMachineInfoList && record.stateMachineInfoList.length
          ? (
            <a
              role="none"
              onClick={() => this.showStateMachines(record)}
            >
              {record.stateMachineInfoList.length}
个关联的状态机
            </a>
          )
          : '-'
        }
      </div>
    ),
  }, {
    align: 'right',
    width: 104,
    key: 'action',
    render: (text, record) => (
      <div>
        <Tooltip placement="top" title={<FormattedMessage id="edit" />}>
          <Button shape="circle" size="small" onClick={this.showSideBar.bind(this, 'edit', record.id)}>
            <i className="icon icon-mode_edit" />
          </Button>
        </Tooltip>
        {record.code || (record.stateMachineInfoList && record.stateMachineInfoList.length)
          ? <div className="issue-del-space" />
          : (
            <Tooltip placement="top" title={<FormattedMessage id="delete" />}>
              <Button shape="circle" size="small" onClick={this.confirmDelete.bind(this, record)}>
                <i className="icon icon-delete" />
              </Button>
            </Tooltip>
          )
        }
      </div>
    ),
  }]);

  linkToStateMachine = (machineId, status) => {
    this.modelRef.destroy();
    const { history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    history.push(`/agile/state-machines/edit/${machineId}/${status || 'state_machine_active'}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  showStateMachines = (data) => {
    const { intl } = this.props;
    this.modelRef = info({
      title: `${data.name}关联的状态机`,
      content: (
        <ul className="issue-state-ul">
          {
            data.stateMachineInfoList.map(stateMachine => (
              <li key={stateMachine.stateMachineId}>
                <a
                  role="none"
                  onClick={() => this.linkToStateMachine(
                    stateMachine.stateMachineId, stateMachine.stateMachineStatus,
                  )}
                >
                  {stateMachine.stateMachineName}
                </a>
              </li>
            ))
          }
        </ul>
      ),
      onOk() {},
      okText: intl.formatMessage({ id: 'confirm' }),
    });
  };

  showSideBar = (state, id = '') => {
    const { StateStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    if (state === 'edit') {
      StateStore.loadStateById(orgId, id).then((data) => {
        if (data && data.failed) {
          Choerodon.prompt(data.message);
        } else {
          this.setState({
            editState: data,
          });
        }
      });
    }
    this.setState({
      show: true,
      type: state,
    });
  };

  confirmDelete = (record) => {
    this.setState({
      deleteVisible: true,
      deleteId: record.id,
      deleteName: record.name,
    });
  };

  handleCancel = () => {
    this.setState({
      deleteVisible: false,
      deleteId: '',
    });
  };

  handleDelete = () => {
    const { StateStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const {
      deleteId, page, pageSize, sorter, tableParam,
    } = this.state;
    StateStore.deleteState(orgId, deleteId).then((data) => {
      if (data && data.failed) {
        Choerodon.prompt(data.message);
      } else {
        this.loadState({ 
          page, size: pageSize, sort: sorter, param: tableParam, 
        });
        this.setState({
          deleteVisible: false,
          deleteId: '',
        });
      }
    });
  };

  hideSidebar = () => {
    this.setState({
      show: false,
      type: '',
      editState: false,
    });
  };

  loadState = ({ 
    page = 1, size = 10, sort = { field: 'id', order: 'desc' }, param = {}, isSetInitialTotal = false, 
  }) => {
    const { StateStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    StateStore.loadStateList(orgId, page, size, sort, param, isSetInitialTotal).then((data) => {
      this.setState({
        statesList: data.list,
        total: data.total,
      });
      if (isSetInitialTotal) {
        this.setState({
          initialTotal: data.total,
        });
      }
    });
  };

  handleSubmit = () => {
    const { StateStore, form } = this.props;
    const {
      type, editState, page, pageSize, sorter, tableParam,
    } = this.state;
    const orgId = AppState.currentMenuType.organizationId;

    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        const postData = data;
        postData.organizationId = orgId;
        this.setState({
          submitting: true,
        });
        if (type === 'create') {
          StateStore.createState(orgId, postData)
            .then((res) => {
              if (res && res.failed) {
                // eslint-disable-next-line no-console
                console.log(res.message);
              } else {
                this.loadState({ 
                  page, size: pageSize, sort: sorter, param: tableParam, 
                });
                this.setState({
                  type: false,
                  show: false,
                  editState: false,
                });
              }
              this.setState({
                submitting: false,
              });
            }).catch((error) => {
              this.setState({
                submitting: false,
              });
            });
        } else {
          StateStore.updateState(orgId, editState.id, Object.assign(editState, postData))
            .then((res) => {
              if (res && res.failed) {
                Choerodon.prompt(res.message);
              } else {
                this.loadState({
                  page, size: pageSize, sort: sorter, param: tableParam, 
                });
                this.setState({ type: false, show: false, editState: {} });
              }
              this.setState({
                submitting: false,
              });
            });
        }
      }
    });
  };

  refreshData = () => {
    const {
      page, pageSize, sorter, tableParam,
    } = this.state;
    this.loadState({
      page, size: pageSize, sort: sorter, param: tableParam, 
    });
  };


  tableChange = (pagination, filters, sorter, param) => {
    const sort = {};
    if (sorter.column) {
      const { field, order } = sorter;
      sort[field] = order;
    }
    let searchParam = {};
    if (filters && filters.name && filters.name.length) {
      searchParam = {
        ...searchParam,
        name: filters.name[0],
      };
    }
    if (filters && filters.description && filters.description.length) {
      searchParam = {
        ...searchParam,
        description: filters.description[0],
      };
    }
    if (filters && filters.type && filters.type.length) {
      searchParam = {
        ...searchParam,
        type: filters.type[0],
      };
    }
    if (param && param.length) {
      searchParam = {
        ...searchParam,
        param: param.toString(),
      };
    }
    this.setState({
      page: pagination.current,
      pageSize: pagination.pageSize,
      sorter: sorter.column ? sorter : undefined,
      tableParam: searchParam,
    });
    this.loadState({
      page: pagination.current,
      size: pagination.pageSize,
      sort: sorter.column ? sorter : undefined,
      param: searchParam,
    });
  };

  checkName = async (rule, value, callback) => {
    if (!value || !value.trim()) {
      callback();
      return;
    }
    const { type, editState } = this.state;
    const { StateStore, intl } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    if (type === 'create' || value !== (editState && editState.name)) {
      this.setState({
        submitting: true,
      });
      const res = await StateStore.checkName(orgId, value);
      this.setState({
        submitting: false,
      });
      if (res && res.statusExist) {
        callback(intl.formatMessage({ id: 'priority.create.name.error' }));
      } else {
        callback();
      }
    } else {
      callback();
    }
  };

  render() {
    const { StateStore, intl, form } = this.props;
    const {
      statesList = [], deleteName, editState, page, pageSize, initialTotal, total, show, submitting, type, deleteVisible,
    } = this.state;
    const { getFieldDecorator } = form;
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
                message: intl.formatMessage({ id: 'state.name.required' }),
              }, {
                validator: this.checkName,
              }],
              initialValue: editState ? editState.name : '',
            })(
              <Input
                style={{ width: 520 }}
                autoFocus
                label={<FormattedMessage id="state.name" />}
                size="default"
                maxLength={15}
              />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            className="issue-sidebar-form"
          >
            {getFieldDecorator('description', {
              initialValue: editState ? editState.description : '',
            })(
              <TextArea
                style={{ width: 520 }}
                label={<FormattedMessage id="state.des" />}
                maxLength={45}
              />,
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
          >
            {getFieldDecorator('type', {
              rules: [{
                required: true,
                whitespace: true,
                message: intl.formatMessage({ id: 'required' }),
              }],
              initialValue: editState ? editState.type : 'todo',
            })(
              <Select
                style={{ width: 520 }}
                label={<FormattedMessage id="state.stage" />}
                dropdownMatchSelectWidth
                size="default"
              >
                {stageList.map(stage => (
                  <Option
                    value={stage.code}
                    key={stage.code}
                  >
                    <div style={{ display: 'inline-block' }}>
                      <div className="issue-state-block" style={{ backgroundColor: stage.colour }} />
                      <span style={{ verticalAlign: 'middle', width: '100%' }}>{stage.name}</span>
                    </div>
                  </Option>
                ))}
              </Select>,

            )}
          </FormItem>
          <div className="issue-state-tips-wrapper">
            <div className="issue-state-tips"><FormattedMessage id="state.tips" /></div>
            <div className="issue-state-tips">
              <Tips tips={[intl.formatMessage({ id: 'state.tips2' })]} />
            </div>
          </div>
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
        <Header title={<FormattedMessage id="state.title" />}>
          {!initialTotal
            ? (
              <Tooltip placement="bottom" title="请创建项目后再创建状态机">
                <Button disabled>
                  <i className="icon-add icon" />
                  <FormattedMessage id="state.create" />
                </Button>
              </Tooltip>
            )
            : (
              <Button onClick={() => this.showSideBar('create')}>
                <i className="icon-add icon" />
                <FormattedMessage id="state.create" />
              </Button>
            )
          }
          <Button onClick={this.refreshData}>
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content>
          <Table
            dataSource={statesList}
            columns={this.getColumn()}
            filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
            rowKey={record => record.id}
            loading={StateStore.getIsLoading}
            pagination={pageInfo}
            onChange={this.tableChange}
            className="issue-table"
          />
        </Content>
        {show && (
        <Sidebar
          title={<FormattedMessage id={type === 'create' ? 'state.create' : 'state.edit'} />}
          visible={show}
          onOk={this.handleSubmit}
          okText={<FormattedMessage id={type === 'create' ? 'create' : 'save'} />}
          cancelText={<FormattedMessage id="cancel" />}
          confirmLoading={submitting}
          onCancel={this.hideSidebar}
        >
          {formContent}
        </Sidebar>
        )}
        <Modal
          title={<FormattedMessage id="state.delete" />}
          visible={deleteVisible}
          onOk={this.handleDelete}
          onCancel={this.handleCancel}
        >
          <p className={`${prefixCls}-del-content`}>
            <FormattedMessage id="state.delete" />
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

export default Form.create({})(withRouter(injectIntl(StateList)));
