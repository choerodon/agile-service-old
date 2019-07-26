import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Button,
  Icon,
  Form,
  Table,
  Modal,
  Select,
  Popover,
  Spin,
  Input,
} from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import { FormattedMessage, injectIntl } from 'react-intl';
import Graph from '../../../../components/Graph';
import './EditStateMachineScheme.scss';

import StateMachineStore from '../../../../stores/organization/stateMachine';
import TypeTag from '../../../../components/TypeTag/TypeTag';
import Tips from '../../../../components/Tips';
import PublishSidebar from './PublishSidebar';
import ReadAndEdit from '../../../../components/ReadAndEdit';
import { getRequest } from '../../../../common/utils';

const { Sidebar } = Modal;
const FormItem = Form.Item;
const { Option } = Select;
const { AppState } = stores;
const { TextArea } = Input;
const prefixCls = 'issue-stateMachineScheme';
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
class EditStateMachineScheme extends Component {
  constructor(props) {
    super(props);
    const schemeId = this.props.match.params.id;
    this.state = {
      stateMachineId: '',
      schemeId,
      showStatus: 'draft',
      stateMachineIds: [],
      currentRae: undefined,
      name: '',
      description: '',
      loading: false,
      error: false,
      from: false,
      machineId: false,
    };
  }

  componentDidMount() {
    const { organizationId } = AppState.currentMenuType;
    const { StateMachineSchemeStore, location } = this.props;
    this.loadStateMachine();
    StateMachineSchemeStore.loadAllStateMachine(organizationId);
    this.setState({
      from: getRequest(location.search).fromMachine,
    });
  }

  loadStateMachine = (isDraft = true) => {
    const { organizationId } = AppState.currentMenuType;
    const { StateMachineSchemeStore } = this.props;
    const { schemeId } = this.state;

    StateMachineSchemeStore.loadStateMachine(
      organizationId,
      schemeId,
      isDraft,
    ).then(() => {
      const {
        viewVOS, name, description, objectVersionNumber,
      } = StateMachineSchemeStore.getStateMachine;
      // 过滤已经关联的状态机
      const stateMachineIds = [];
      viewVOS.forEach((data) => {
        if (data.stateMachineVO) {
          stateMachineIds.push(data.stateMachineVO.id);
        }
      });
      this.setState({
        stateMachineIds,
        name,
        description,
        objectVersionNumber,
      });
    });
  };

  refresh = () => {
    const { showStatus } = this.state;
    this.loadStateMachine(showStatus === 'draft');
  };

  handleAddStateMachine = () => {
    const { stateMachineIds } = this.state;
    const { StateMachineSchemeStore, form } = this.props;
    const allStateMachine = StateMachineSchemeStore.getAllStateMachine
      .filter(data => stateMachineIds.indexOf(data.id) === -1);
    StateMachineSchemeStore.setIsAddVisible(true);
    StateMachineSchemeStore.setSelectedIssueTypeId([]);
    if (allStateMachine && allStateMachine.length) {
      this.setState({
        stateMachineId: allStateMachine[0].id,
      }, this.loadGraphData(allStateMachine[0]));
    }
    form.setFieldsValue({
      stateMachineId: allStateMachine.length !== 0 && allStateMachine[0].id,
    });
  };

  handleCancleAddStateMachine = () => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setIsAddVisible(false);
    this.setState({
      machineId: false,
    });
  };

  handleNextStep = () => {
    const {
      StateMachineSchemeStore, form,
    } = this.props;
    const { schemeId } = this.state;
    const { organizationId } = AppState.currentMenuType;
    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        StateMachineSchemeStore.setIsAddVisible(false);
        StateMachineSchemeStore.setIsConnectVisible(true);
        StateMachineSchemeStore.loadAllIssueType(organizationId, schemeId);
        StateMachineSchemeStore.setSelectedIssueTypeId([]);
      }
    });
  };

  handlePreStep = () => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setIsAddVisible(true);
    StateMachineSchemeStore.setIsConnectVisible(false);
  };

  handleCloseConnectStateMachine = () => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setSchemeVOS([]);
    StateMachineSchemeStore.setIsAddVisible(false);
    StateMachineSchemeStore.setIsConnectVisible(false);
    this.setState({
      machineId: false,
    });
  };

  handleFinishConnectStateMachine = (schemeVOS) => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setSchemeVOS([]);
    StateMachineSchemeStore.setIsAddVisible(false);
    StateMachineSchemeStore.setIsConnectVisible(false);
    StateMachineSchemeStore.setSelectedIssueTypeId(schemeVOS);
  };

  setGraphData = (res) => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setNodeData(res.nodeVOS);
    StateMachineSchemeStore.setTransferData(res.transformVOS);
  };

  loadGraphData = (item) => {
    const { StateMachineSchemeStore } = this.props;
    const { stateMachineIds, stateMachineId } = this.state;
    const { organizationId } = AppState.currentMenuType;
    const stateMachine = item || StateMachineSchemeStore
      .getAllStateMachine.slice().filter(data => stateMachineIds.indexOf(data.id) === -1)[0];
    if (stateMachine.status === 'state_machine_create') {
      StateMachineStore.loadStateMachineDraftById(organizationId, stateMachine.id)
        .then((data) => {
          StateMachineSchemeStore.setGraphLoading(false);
          this.setGraphData(data);
        });
    } else {
      StateMachineStore.loadStateMachineDeployById(organizationId, stateMachine.id)
        .then((data) => {
          StateMachineSchemeStore.setGraphLoading(false);
          this.setGraphData(data);
        });
    }
  };

  handleSelectChange = (value) => {
    const { StateMachineSchemeStore } = this.props;
    const { stateMachineIds } = this.state;
    const allStateMachine = StateMachineSchemeStore.getAllStateMachine
      .filter(data => stateMachineIds.indexOf(data.id) === -1);
    const item = allStateMachine[value];
    this.setState({
      machineId: String(value),
      stateMachineId: item.id,
    });
    this.loadGraphData(item);
  };

  handleRowSelectChange = (selectedRowKeys, selectedRows) => {
    const schemeVOS = [];
    const { StateMachineSchemeStore } = this.props;
    if (selectedRows && selectedRows.length) {
      selectedRows.map((selectedRow) => {
        const row = {};
        row.issueTypeId = selectedRow.id;
        schemeVOS.push(row);
        return true;
      });
    }
    StateMachineSchemeStore.setSchemeVOS(schemeVOS);
  };

  handleFinish = () => {
    const { organizationId } = AppState.currentMenuType;
    const { schemeId, stateMachineId, stateMachineIds } = this.state;
    const { StateMachineSchemeStore } = this.props;
    this.setState({
      loading: true,
    });
    const schemeVOS = StateMachineSchemeStore.getSchemeVOS;

    StateMachineSchemeStore.saveStateMachine(
      organizationId,
      schemeId,
      stateMachineId,
      schemeVOS,
    ).then(() => {
      this.setState({
        loading: false,
        machineId: false,
      });
      this.handleFinishConnectStateMachine(schemeVOS);
      this.refresh();
    });
  };

  // 删除行
  handleDelete = (deleteId) => {
    const { schemeId } = this.state;
    const { StateMachineSchemeStore } = this.props;
    const { organizationId } = AppState.currentMenuType;
    StateMachineSchemeStore.deleteStateMachine(organizationId, schemeId, deleteId).then(() => {
      this.refresh();
    });
  };

  handleEditStateMachine = (stateMachineId) => {
    const issueTypeId = [];
    const { organizationId } = AppState.currentMenuType;
    const { schemeId } = this.state;
    const { StateMachineSchemeStore } = this.props;
    this.setState({
      stateMachineId,
    });
    StateMachineSchemeStore.loadAllIssueType(organizationId, schemeId)
      .then(() => {
        StateMachineSchemeStore.getAllIssueType
          .map((issueType) => {
            if (issueType.stateMachineId === stateMachineId) {
              issueTypeId.push(issueType.id);
            }
            return true;
          });
        StateMachineSchemeStore.setSelectedIssueTypeId(issueTypeId);
        StateMachineSchemeStore.setIsAddVisible(false);
        StateMachineSchemeStore.setIsConnectVisible(true);
      });
  };

  renderAddStateMachineForm = () => {
    const { StateMachineSchemeStore, form, intl } = this.props;
    const { stateMachineIds, machineId } = this.state;
    const { getFieldDecorator } = form;
    const allStateMachine = StateMachineSchemeStore
      .getAllStateMachine.filter(data => stateMachineIds.indexOf(data.id) === -1);

    return (
      <Fragment>
        <Form className="c7nagile-form">
          <FormItem {...formItemLayout} className="issue-sidebar-form">
            {getFieldDecorator('stateMachine', {
              initialValue: machineId || (allStateMachine.length ? '0' : null),
              rules: [
                {
                  required: true,
                  message: intl.formatMessage({ id: 'required' }),
                },
              ],
            })(
              <Select
                label={intl.formatMessage({
                  id: 'stateMachineScheme.stateMachine',
                })}
                onChange={val => this.handleSelectChange(val)}
              >
                {allStateMachine && allStateMachine
                  .map((stateMachineItem, index) => (
                    <Option
                      key={stateMachineItem.id}
                      value={String(index)}
                    >
                      {stateMachineItem.name}
                    </Option>
                  ))}
              </Select>,
            )}
          </FormItem>
        </Form>
        <Spin spinning={StateMachineSchemeStore.graphLoading}>
          <Graph
            renderChanged
            data={
              StateMachineSchemeStore.getNodeData && {
                vertex: StateMachineSchemeStore.getNodeData,
                edge: StateMachineSchemeStore.getTransferData,
              }
            }
            height="calc(100vh - 300px)"
          />
        </Spin>
      </Fragment>
    );
  };

  renderConnectStateMachineForm = () => {
    const { intl } = this.props;
    const { StateMachineSchemeStore } = this.props;
    const allIssueType = StateMachineSchemeStore.getAllIssueType;
    const columns = [
      {
        title: intl.formatMessage({
          id: 'stateMachineScheme.connectIssueType',
        }),
        key: 'connectIssueType',
        render: record => (
          <Fragment>
            <TypeTag
              data={record}
              showName
            />
          </Fragment>
        ),
      },
      {
        title: intl.formatMessage({
          id: 'stateMachineScheme.connectedStateMachine',
        }),
        key: 'connectedStateMachine',
        render: (text, record) => record.stateMachineName || '',
      },
      {
        title: '',
        align: 'right',
        key: 'warning',
        render: (text, record) => record.stateMachineSchemeConfigVO && (
          <Popover
            content={
              <FormattedMessage id="stateMachineScheme.conflictInfo" />
            }
            placement="topLeft"
            overlayClassName="conflct-info"
            arrowPointAtCenter
          >
            <Icon type="warning" style={{ color: '#FADB14' }} />
          </Popover>
        ),
      },
    ];
    const rowSelection = {
      onChange: (selectedRowKeys, selectedRows) => {
        this.handleRowSelectChange(selectedRowKeys, selectedRows);
      },
      getCheckboxProps: record => ({
        defaultChecked: StateMachineSchemeStore.getSelectedIssueTypeId.length !== 0
        && StateMachineSchemeStore.getSelectedIssueTypeId.includes(record.id),
      }),
    };
    return (
      <Form className="c7nagile-form">
        <FormItem {...formItemLayout}>
          <Table
            dataSource={allIssueType}
            columns={columns}
            rowKey={record => record.id}
            rowSelection={rowSelection}
            filterBar={false}
            className="issue-table"
            rowClassName={`${prefixCls}-table-col`}
            pagination={false}
          />
        </FormItem>
      </Form>
    );
  };

  // 发布校验
  handlePublish = () => {
    const { schemeId } = this.state;
    const { StateMachineSchemeStore } = this.props;
    const { organizationId } = AppState.currentMenuType;
    StateMachineSchemeStore.setPublishLoading(true);
    StateMachineSchemeStore.checkPublishStateMachine(organizationId, schemeId);
    StateMachineSchemeStore.setIsPublishVisible(true);
  };

  handleDeleteDraft = () => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setIsMachineDeleteVisible(true);
  };

  // 删除草稿
  confirmDelete = () => {
    const { schemeId } = this.state;
    const { StateMachineSchemeStore } = this.props;
    const { organizationId } = AppState.currentMenuType;
    StateMachineSchemeStore.setIsMachineDeleteVisible(false);
    StateMachineSchemeStore.deleteDraft(organizationId, schemeId).then(() => {
      this.loadStateMachine();
    });
  };

  cancelDelete = () => {
    const { StateMachineSchemeStore } = this.props;
    StateMachineSchemeStore.setIsMachineDeleteVisible(false);
  };

  // 查看原件 or 编辑草稿
  handleShowChange = (isDraft) => {
    this.loadStateMachine(isDraft);
    this.setState({
      showStatus: isDraft ? 'draft' : 'original',
    });
  };

  getColumns = () => [
    {
      title: <FormattedMessage id="stateMachineScheme.stateMachine" />,
      key: 'stateMachine',
      className: 'issue-table-ellipsis',
      render: record => (
        record.stateMachineVO && record.stateMachineVO.length !== 0 && (
          <Fragment>{record.stateMachineVO.name}</Fragment>
        )
      ),
    },
    {
      title: <FormattedMessage id="stateMachineScheme.issueType" />,
      key: 'issueType',
      align: 'left',
      render: record => (
        <div>
          {record.issueTypeVOS.length !== 0 && record.issueTypeVOS
            .map(type => (
              <div key={type.id}>
                <TypeTag data={type} showName />
              </div>
            ))}
        </div>
      ),
    },
    {
      align: 'right',
      title: '',
      key: 'operation',
      render: record => (
        record.issueTypeVOS && record.issueTypeVOS.length
        && record.issueTypeVOS[0].id && this.state.showStatus === 'draft'
          ? (
            <Fragment>
              <Button
                shape="circle"
                size="small"
                onClick={this.handleEditStateMachine.bind(
                  this,
                  record.stateMachineVO && record.stateMachineVO.id,
                )}
              >
                <Icon type="mode_edit" />
              </Button>
              <Button
                shape="circle"
                size="small"
                onClick={this.handleDelete.bind(
                  this,
                  record.stateMachineVO && record.stateMachineVO.id,
                )}
              >
                <Icon type="delete" />
              </Button>
            </Fragment>
          )
          : null
      ),
    },
  ];

  renderFooter = () => {
    const { StateMachineSchemeStore } = this.props;
    const { loading } = this.state;
    return (
      <Fragment>
        {StateMachineSchemeStore.getSelectedIssueTypeId
        && StateMachineSchemeStore.getSelectedIssueTypeId.length === 0 && (
          <Button
            key="pre"
            type="primary"
            onClick={this.handlePreStep}
            disabled={loading}
          >
            {<FormattedMessage id="stateMachineScheme.pre" />}
          </Button>
        )}
        <Button
          key="finish"
          type="primary"
          funcType="raised"
          onClick={this.handleFinish}
          disabled={!(StateMachineSchemeStore.getSchemeVOS
          && StateMachineSchemeStore.getSchemeVOS.length)}
          loading={loading}
        >
          {<FormattedMessage id="stateMachineScheme.finish" />}
        </Button>
        <Button
          key="cancel"
          funcType="raised"
          onClick={this.handleCloseConnectStateMachine}
        >
          {<FormattedMessage id="stateMachineScheme.cancel" />}
        </Button>
      </Fragment>
    );
  };

  changeRae = (currentRae) => {
    this.setState({
      currentRae,
    });
  };

  updateScheme = (code) => {
    const { StateMachineSchemeStore } = this.props;
    const {
      objectVersionNumber,
      [code]: newValue,
      schemeId,
      error,
    } = this.state;
    if (!error) {
      if (code === 'name' && !newValue) {
        const {
          name, description,
        } = StateMachineSchemeStore.getStateMachine;
        this.setState({
          currentRae: undefined,
          name,
          description,
        });
      } else {
        const menu = AppState.currentMenuType;
        const {
          organizationId: orgId,
        } = menu;
        const data = {
          [code]: this.state[code],
          objectVersionNumber,
        };
        StateMachineSchemeStore.editStateMachineScheme(orgId, schemeId, data).then(() => {
          this.refresh();
          this.setState({
            currentRae: undefined,
          });
        });
      }
    }
  };

  resetScheme = (origin, code) => {
    this.setState({
      [code]: origin,
      error: false,
    });
  };

  handleChange = async (e, code) => {
    const { StateMachineSchemeStore, intl } = this.props;
    const menu = AppState.currentMenuType;
    const {
      organizationId: orgId,
    } = menu;
    const { name } = StateMachineSchemeStore.getStateMachine;
    const newName = e.target.value;
    let error = '';
    this.setState({
      [code]: newName,
    });
    if (name !== newName) {
      const res = await StateMachineSchemeStore.checkName(orgId, newName);
      if (res) {
        error = intl.formatMessage({ id: 'priority.create.name.error' });
      }
    }
    this.setState({
      error,
    });
  };

  render() {
    const menu = AppState.currentMenuType;
    const {
      type, id: projectId, organizationId: orgId, name,
    } = menu;
    const { intl, StateMachineSchemeStore } = this.props;
    const {
      showStatus,
      schemeId,
      currentRae,
      name: schemeName,
      description,
      error,
      from,
    } = this.state;
    const {
      getStateMachine,
      getStateMachineLoading,
      getIsAddVisible,
      getIsConnectVisible,
    } = StateMachineSchemeStore;

    return (
      <Page>
        <Header
          title={<FormattedMessage id="stateMachineScheme.edit" />}
          backPath={from
            ? `/agile/state-machines?type=${type}&id=${projectId}&name=${encodeURIComponent(name)}&organizationId=${orgId}`
            : `/agile/state-machine-schemes?type=${type}&id=${projectId}&name=${encodeURIComponent(name)}&organizationId=${orgId}`
          }
        >
          <Button onClick={this.refresh} funcType="flat">
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content>
          {getStateMachine.status === 'draft'
            ? (
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex' }}>
                  <Icon type="warning" style={{ color: '#FADB14', marginRight: 10 }} />
                  <Tips tips={[intl.formatMessage({ id: 'stateMachineScheme.tips' })]} />
                </div>
                {showStatus === 'draft'
                  ? (
                    <div>
                      <Button
                        disabled={getStateMachine.deployStatus === 'doing'}
                        type="primary"
                        onClick={this.handlePublish}
                        funcType="raised"
                        className="issue-options-btn"
                      >
                        {getStateMachine.deployStatus === 'doing'
                          ? <FormattedMessage id="stateMachineScheme.announcing" />
                          : <FormattedMessage id="stateMachineScheme.publish" />
                    }
                      </Button>
                      <Button
                        type="danger"
                        onClick={this.handleDeleteDraft}
                        funcType="raised"
                        className="issue-options-btn"
                      >
                        <FormattedMessage id="stateMachineScheme.deleteDraft" />
                      </Button>
                      <Button
                        onClick={() => this.handleShowChange(false)}
                        funcType="raised"
                      >
                        <FormattedMessage id="stateMachineScheme.original" />
                      </Button>
                    </div>
                  )
                  : (
                    <div>
                      <Button
                        onClick={() => this.handleShowChange(true)}
                        funcType="raised"
                      >
                        <FormattedMessage id="stateMachineScheme.draft" />
                      </Button>
                    </div>
                  )
              }
              </div>
            ) : null
          }
          <div style={{ width: 440 }}>
            <ReadAndEdit
              callback={this.changeRae}
              thisType="name"
              current={currentRae}
              origin={schemeName}
              onOk={() => this.updateScheme('name')}
              onCancel={origin => this.resetScheme(origin, 'name')}
              readModeContent={(
                <div className="issue-scheme-name">
                  {schemeName}
                </div>
              )}
              style={{ marginBottom: 10 }}
              error={error}
            >
              <Input
                size="small"
                maxLength={20}
                value={schemeName}
                onChange={e => this.handleChange(e, 'name')}
                onPressEnter={() => this.updateScheme('name')}
                autoize={{ minRows: 1, maxRows: 1 }}
              />
            </ReadAndEdit>
            <ReadAndEdit
              callback={this.changeRae}
              thisType="description"
              current={currentRae}
              origin={description}
              onOk={() => this.updateScheme('description')}
              onCancel={origin => this.resetScheme(origin, 'description')}
              readModeContent={(
                description
                  ? (
                    <div className="issue-scheme-description">
                      {description}
                    </div>
                  )
                  : (
                    <div style={{ opacity: 0.5 }}>
                      {intl.formatMessage({ id: 'stateMachineScheme.des.none' })}
                    </div>
                  )
              )}
              style={{ marginBottom: 20 }}
            >
              <TextArea
                maxLength={44}
                value={description}
                size="small"
                onChange={e => this.handleChange(e, 'description')}
                onPressEnter={() => this.updateScheme('description')}
                placeholder={intl.formatMessage({ id: 'stateMachineScheme.des' })}
              />
            </ReadAndEdit>
          </div>
          <Button
            type="primary"
            onClick={this.handleAddStateMachine}
            funcType="raised"
            style={{ marginBottom: 11 }}
            disabled={showStatus === 'original'}
          >
            <FormattedMessage id="stateMachineScheme.add" />
          </Button>
          <Table
            loading={getStateMachineLoading}
            columns={this.getColumns()}
            dataSource={getStateMachine.viewVOS || []}
            rowKey={record => record.id}
            className="issue-table"
            rowClassName={`${prefixCls}-table-col`}
            pagination={false}
            filterBar={false}
          />
          <Modal
            title={<FormattedMessage id="stateMachineScheme.delete.draft" />}
            visible={StateMachineSchemeStore.getIsMachineDeleteVisible}
            onOk={this.confirmDelete}
            onCancel={this.cancelDelete}
          >
            <FormattedMessage id="stateMachineScheme.delete.des" />
          </Modal>
          {getIsAddVisible && (
            <Sidebar
              title={<FormattedMessage id="stateMachineScheme.add" />}
              visible={getIsAddVisible}
              onCancel={this.handleCancleAddStateMachine}
              okText={<FormattedMessage id="stateMachineScheme.next" />}
              onOk={this.handleNextStep}
            >
              {this.renderAddStateMachineForm()}
            </Sidebar>
          )}
          {getIsConnectVisible && (
            <Sidebar
              title={<FormattedMessage id="stateMachineScheme.connect" />}
              visible={getIsConnectVisible}
              footer={this.renderFooter()}
            >
              {this.renderConnectStateMachineForm()}
            </Sidebar>
          )}
          <PublishSidebar
            scheme={getStateMachine}
            store={StateMachineSchemeStore}
            refresh={this.refresh}
          />
        </Content>
      </Page>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(EditStateMachineScheme)));
