import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Table, Button, Modal, Form, message, Tooltip, Icon,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores,
} from '@choerodon/boot';

import './IssueTypeList.scss';
import IssueTypeCreate from '../issueTypeCreate';
import TypeTag from '../../../components/TypeTag/TypeTag';

const { AppState } = stores;
const { info } = Modal;

@observer
class IssueTypeList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id: '',
      visible: false,
      deleteVisible: false,
      issueType: false,
      tableParam: {},
    };
    this.modelRef = false;
  }

  componentDidMount() {
    this.loadIssueType();
  }

  getColumn = () => ([{
    title: <FormattedMessage id="issueType.name" />,
    dataIndex: 'name',
    key: 'name',
    filters: [],
    render: (text, record) => (
      <TypeTag
        data={record}
        showName
        style={{ margin: 0 }}
      />
    ),
  }, {
    title: <FormattedMessage id="issueType.des" />,
    dataIndex: 'description',
    key: 'description',
    filters: [],
    className: 'issue-table-ellipsis',
  }, {
    title: <FormattedMessage id="issueType.scheme" />,
    dataIndex: 'scheme',
    key: 'scheme',
    render: (text, record) => (
      <div>
        {record.issueTypeSchemeRelationList && record.issueTypeSchemeRelationList.length
          ? <a
            onClick={() => this.showSchemes(record)}
          >
            {record.issueTypeSchemeRelationList.length}个关联的方案
          </a>
          : '-'
        }
      </div>
    ),
  }, {
    align: 'right',
    key: 'action',
    render: (test, record) => (
      <div>
        {/* <Tooltip */}
          {/* placement="bottom" */}
          {/* title={<FormattedMessage id="edit" />} */}
        {/* > */}
          {/* <Button */}
            {/* size="small" */}
            {/* shape="circle" */}
            {/* onClick={this.showEdit.bind(this, record.id)} */}
          {/* > */}
            {/* <i className="icon icon-mode_edit" /> */}
          {/* </Button> */}
        {/* </Tooltip> */}
        {/* <Tooltip */}
          {/* placement="bottom" */}
          {/* title={<FormattedMessage id="delete" />} */}
        {/* > */}
          {/* <Button */}
            {/* size="small" */}
            {/* shape="circle" */}
            {/* onClick={this.openRemove.bind(this, record)} */}
          {/* > */}
            {/* <i className="icon icon-delete" /> */}
          {/* </Button> */}
        {/* </Tooltip> */}
      </div>
    ),
  }]);

  linkToScheme = (schemeId) => {
    this.modelRef.destroy();
    const { history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    // 方案详情目前不可访问
    history.push(`/agile/issue-type-schemes?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  showSchemes = (data) => {
    const { intl } = this.props;
    this.modelRef = info({
      title: `${data.name}关联的方案`,
      content: (
        <ul className="issue-issueType-ul">
          {
            data.issueTypeSchemeRelationList.map(scheme => (
              <li key={scheme.issueTypeSchemeId}>
                <a onClick={() => this.linkToScheme(scheme.issueTypeSchemeId)}>
                  {scheme.issueTypeSchemeName}
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

  loadIssueType = () => {
    const { IssueTypeStore } = this.props;
    const {
      sorter, tableParam, page, pageSize,
    } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeStore.loadIssueType(orgId, page ? page - 1 : undefined, pageSize, sorter, tableParam);
  };

  refresh = () => {
    this.loadIssueType();
  };

  showCreate = () => {
    const { IssueTypeStore } = this.props;
    IssueTypeStore.setCreateTypeShow('create');
  };

  showEdit =(id) => {
    const { IssueTypeStore } = this.props;
    this.setState({ id });
    IssueTypeStore.setCreateTypeShow('edit');
  };

  hideSidebar = () => {
    const { IssueTypeStore } = this.props;
    this.setState({ id: false });
    IssueTypeStore.setCreateTypeShow(false);
    this.loadIssueType();
  };

  openRemove = (record) => {
    const { IssueTypeStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeStore.checkDelete(orgId, record.id)
      .then((data) => {
        if (data) {
          if (data.canDelete) {
            this.setState({ deleteVisible: true, issueType: record, id: record.id });
          } else {
            this.setState({ visible: true });
          }
        }
      });
  };

  closeRemove = () => {
    this.setState({
      deleteVisible: false, visible: false, id: false, issueType: false,
    });
  };

  handleDelete = () => {
    const { IssueTypeStore, intl } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeStore.deleteIssueType(orgId, this.state.id)
      .then((data) => {
        if (data) {
          message.success(intl.formatMessage({ id: 'deleteSuccess' }));
        } else {
          message.error(intl.formatMessage({ id: 'deleteFailed' }));
        }
        this.closeRemove();
        this.loadIssueType();
      }).catch((error) => {
        message.error(intl.formatMessage({ id: 'deleteFailed' }));
        this.closeRemove();
      });
  };

  handleTableChange =(pagination, filters, sorter, param) => {
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
    if (param && param.length) {
      searchParam = {
        ...searchParam,
        param: param.toString(),
      };
    }
    this.setState({
      sorter: sorter.column ? sorter : undefined,
      tableParam: searchParam,
      page: pagination.current,
      pageSize: pagination.pageSize,
    }, () => this.loadIssueType());
  };

  render() {
    const { IssueTypeStore, intl } = this.props;
    const {
      id, submitting, deleteVisible, issueType, visible,
    } = this.state;

    return (
      <Page className="issue-region">
        <Header title={<FormattedMessage id="issueType.title" />}>
          {/* <Button onClick={() => this.showCreate('create')}> */}
            {/* <i className="icon-add icon" /> */}
            {/* <FormattedMessage id="issueType.create" /> */}
          {/* </Button> */}
          <Button onClick={this.refresh}>
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content
          description={intl.formatMessage({ id: 'issueType.tip' })}
          link="https://choerodon.io/zh/docs/user-guide/system-configuration/issue-configuration/issue-type-setup/"
        >
          <Table
            dataSource={IssueTypeStore.getIssueTypes}
            columns={this.getColumn()}
            loading={IssueTypeStore.getIsLoading}
            rowKey={record => record.id}
            pagination={IssueTypeStore.pageInfo}
            onChange={this.handleTableChange}
            filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
            className="issue-table"
          />
        </Content>
        {IssueTypeStore.createTypeShow && (
          <IssueTypeCreate
            id={id}
            store={IssueTypeStore}
            visible={!!IssueTypeStore.createTypeShow}
            onClose={this.hideSidebar}
          />
        )}
        <Modal
          confirmLoading={submitting}
          visible={deleteVisible}
          title={<FormattedMessage id="issueType.action.delete" />}
          closable={false}
          footer={[
            <Button key="back" onClick={this.closeRemove}>{<FormattedMessage id="cancel" />}</Button>,
            <Button key="submit" type="danger" onClick={this.handleDelete} loading={submitting}>
              {intl.formatMessage({ id: 'delete' })}
            </Button>,
          ]}
        >
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueType.delete' })}
            <span className="issue-issueType-bold">{issueType.name}</span>
          </p>
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueType.delete.confirm' })}
          </p>
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueType.delete.noUse' })}
          </p>
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueType.delete.noUseTip' })}
          </p>
        </Modal>
        <Modal
          visible={visible}
          title={<FormattedMessage id="issueType.action.delete" />}
          closable={false}
          footer={[
            <Button key="back" onClick={this.closeRemove}>{<FormattedMessage id={'cancel'} />}</Button>,
          ]}
        >
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueType.delete' })}
            <span className="issue-issueType-bold">{issueType.name}</span>
          </p>
          <p className="issue-issueType-tip">
            <Icon type="error" className="issue-issueType-icon issue-error-msg" />
            {intl.formatMessage({ id: 'issueType.delete.forbidden' })}
          </p>
          <p className="issue-issueType-tip">
            <FormattedMessage
              id="issueType.delete.inUse"
              values={{
                num: 1,
              }}
            />
          </p>
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueType.delete.inUseTip' })}
          </p>
        </Modal>
      </Page>
    );
  }
}

export default withRouter(injectIntl(IssueTypeList));
