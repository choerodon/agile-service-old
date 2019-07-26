import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Table, Button, Modal, Icon, Tooltip, message,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores,
} from '@choerodon/boot';
import './IssueTypeSchemeList.scss';
import IssueTypeSchemeCreate from '../issueTypeSchemeCreate';
import TypeTag from '../../../components/TypeTag/TypeTag';

const { AppState } = stores;

@observer
class IssueTypeSchemeList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id: '',
      deleteVisible: false,
      scheme: false,
      page: 1,
      pageSize: 10,
      tableParam: {},
    };
  }

  componentDidMount() {
    this.loadSchemeList();
  }

  getColumn = () => ([{
    title: <FormattedMessage id="issueTypeScheme.name" />,
    dataIndex: 'name',
    key: 'name',
    filters: [],
    render: (text, record) => (
      <div>
        <img src={record.src} alt="" />
        {record.name}
      </div>
    ),
  }, {
    title: <FormattedMessage id="issueTypeScheme.des" />,
    dataIndex: 'description',
    key: 'description',
    filters: [],
    className: 'issue-table-ellipsis',
  }, {
    title: <FormattedMessage id="issueTypeScheme.type" />,
    dataIndex: 'type',
    key: 'type',
    render: (text, record) => (record.issueTypeWithInfoList.length
      ? (
        <div>
          {record.issueTypeWithInfoList.map(type => (
            <div key={type.id} className="issue-issueTypeScheme-type">
              <TypeTag
                data={type}
                showName
              />
            </div>
          ))
          }
        </div>
      )
      : (
        <div>-</div>
      )),
  }, {
    title: <FormattedMessage id="issueTypeScheme.project" />,
    dataIndex: 'project',
    key: 'project',
    render: (text, record) => (record.projectWithInfoList && record.projectWithInfoList.length
      ? (
        <ul className="issue-issueTypeScheme-ul">
          {record.projectWithInfoList.map(
            project => (<li key={project.projectId}>{project.projectName}</li>),
          )}
        </ul>
      )
      : <div>-</div>),
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
        {/* disabled */}
        {/* size="small" */}
        {/* shape="circle" */}
        {/* onClick={this.showEdit.bind(this, record.id)} */}
        {/* > */}
        {/* <i className="icon icon-mode_edit" /> */}
        {/* </Button> */}
        {/* </Tooltip> */}
        {/* <Tooltip */}
        {/* placement="bottom" */}
        {/* title={<FormattedMessage id="relation" />} */}
        {/* > */}
        {/* <Button */}
        {/* disabled */}
        {/* size="small" */}
        {/* shape="circle" */}
        {/* onClick={ */}
        {/* () => { */}
        {/* const { */}
        {/* name, id, organizationId, type, */}
        {/* } = AppState.currentMenuType; */}
        {/* const { history } = this.props; */}
        {/* history.push(`/agile/issue-type-schemes/ralation/${record.id}?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`); */}
        {/* } */}
        {/* }> */}
        {/* <Icon type="open_in_browser" /> */}
        {/* </Button> */}
        {/* </Tooltip> */}
        {/* <Tooltip */}
        {/* placement="bottom" */}
        {/* title={<FormattedMessage id="copy" />} */}
        {/* > */}
        {/* <Button */}
        {/* disabled */}
        {/* size="small" */}
        {/* shape="circle" */}
        {/* onClick={this.showCopy.bind(this, record.id)} */}
        {/* > */}
        {/* <i className="icon icon-content_copy />" /> */}
        {/* </Button> */}
        {/* </Tooltip> */}
        {/* <Tooltip */}
        {/* placement="bottom" */}
        {/* title={<FormattedMessage id="delete" />} */}
        {/* > */}
        {/* <Button */}
        {/* disabled */}
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

  loadSchemeList = () => {
    const { IssueTypeSchemeStore } = this.props;
    const {
      sorter, tableParam, page, pageSize,
    } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeSchemeStore.loadSchemeList(
      orgId, page || undefined, pageSize, sorter, tableParam,
    );
  };

  refresh = () => {
    this.loadSchemeList();
  };

  showCreate = () => {
    const { IssueTypeSchemeStore } = this.props;
    IssueTypeSchemeStore.setCreateSchemeShow('create');
  };

  showEdit =(id) => {
    const { IssueTypeSchemeStore } = this.props;
    this.setState({ id });
    IssueTypeSchemeStore.setCreateSchemeShow('edit');
  };

  showCopy =(id) => {
    const { IssueTypeSchemeStore } = this.props;
    this.setState({ id });
    IssueTypeSchemeStore.setCreateSchemeShow('copy');
  };

  hideSidebar = () => {
    const { IssueTypeSchemeStore } = this.props;
    this.setState({ id: false });
    IssueTypeSchemeStore.setCreateSchemeShow(false);
    this.loadSchemeList();
  };

  openRemove = (record) => {
    const { IssueTypeSchemeStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeSchemeStore.checkDelete(orgId, record.id)
      .then((data) => {
        if (data) {
          if (data.canDelete) {
            this.setState({ deleteVisible: true, id: record.id, scheme: record });
          }
        }
      });
  };

  closeRemove = () => {
    this.setState({ deleteVisible: false, id: false, scheme: false });
  };

  handleDelete = () => {
    const { IssueTypeSchemeStore, intl } = this.props;
    const { id } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeSchemeStore.deleteScheme(orgId, id)
      .then((data) => {
        if (data) {
          message.success(intl.formatMessage({ id: 'deleteSuccess' }));
        } else {
          message.error(intl.formatMessage({ id: 'deleteFailed' }));
        }
        this.closeRemove();
        this.loadSchemeList();
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
    }, () => this.loadSchemeList());
  };


  render() {
    const { IssueTypeSchemeStore, intl } = this.props;
    const {
      id, submitting, deleteVisible, scheme,
    } = this.state;

    return (
      <Page className="issue-region">
        <Header title={<FormattedMessage id="issueTypeScheme.title" />}>
          {/* <Button */}
          {/* onClick={() => this.showCreate('create')} */}
          {/* > */}
          {/* <i className="icon-add icon" /> */}
          {/* <FormattedMessage id="issueTypeScheme.create" /> */}
          {/* </Button> */}
          <Button onClick={this.refresh}>
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content
          description={intl.formatMessage({ id: 'issueTypeScheme.tip' })}
          link="https://choerodon.io/zh/docs/user-guide/system-configuration/issue-configuration/issue-type-setup/"
        >
          <Table
            dataSource={IssueTypeSchemeStore.getSchemeList}
            columns={this.getColumn()}
            loading={IssueTypeSchemeStore.getIsLoading}
            rowKey={record => record.id}
            pagination={IssueTypeSchemeStore.pageInfo}
            onChange={this.handleTableChange}
            filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
            className="issue-table"
          />
        </Content>
        {IssueTypeSchemeStore.createSchemeShow
        && (
          <IssueTypeSchemeCreate
            id={id}
            store={IssueTypeSchemeStore}
            visible={!!IssueTypeSchemeStore.createSchemeShow}
            onClose={this.hideSidebar}
          />
        )}
        <Modal
          confirmLoading={submitting}
          visible={deleteVisible}
          title={<FormattedMessage id="issueTypeScheme.action.delete" />}
          closable={false}
          footer={[
            <Button key="back" onClick={this.closeRemove}>{<FormattedMessage id="cancel" />}</Button>,
            <Button key="submit" type="danger" onClick={this.handleDelete} loading={submitting}>
              {intl.formatMessage({ id: 'delete' })}
            </Button>,
          ]}
        >
          <p className="issue-issueType-tip">
            {intl.formatMessage({ id: 'issueTypeScheme.delete' })}
            <span className="issue-issueType-bold">{scheme.name}</span>
          </p>
          {scheme.projects
            ? (
              <p className="issue-issueType-tip">
                <span className="issue-issueType-bold">超级橘子运维</span>
                {intl.formatMessage({ id: 'issueTypeScheme.delete.inUse' })}
              </p>
            ) : ''
          }
          {scheme.projects
            ? (
              <p className="issue-issueType-tip">
                {intl.formatMessage({ id: 'issueTypeScheme.delete.tip' })}
              </p>
            ) : ''
          }
          {!scheme.projects
            ? (
              <p className="issue-issueType-tip">
                {intl.formatMessage({ id: 'issueTypeScheme.delete.noUse' })}
              </p>
            ) : ''
          }
        </Modal>
      </Page>
    );
  }
}

export default withRouter(injectIntl(IssueTypeSchemeList));
