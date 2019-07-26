import {
  Content, Header, Page, stores,
} from '@choerodon/boot';
import {
  Button, Icon, Table, Tooltip, Modal, message,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import React, { Component } from 'react';
import { injectIntl, FormattedMessage } from 'react-intl';


import './IssueTypeScreenSchemesList.scss';

import Tips from '../../../components/Tips';

const { AppState } = stores;

@injectIntl
@observer
class IssueTypeScreenSchemesList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      scheme: false,
      visible: false,
      deleteVisible: false,
    };
  }

  componentDidMount() {
    this.loadSchemeList();
  }

  getColumns = () => ([
    {
      title: <FormattedMessage id="issueTypeScreenSchemes.name" />,
      dataIndex: 'name',
      key: 'name',
      filters: [],
    },
    {
      title: <FormattedMessage id="issueTypeScreenSchemes.project" />,
      dataIndex: 'project',
      key: 'project',
      filters: [],
      render: text => (
        <p className="issue-type-page-scheme-project">{text}</p>
      ),
    },
    {
      align: 'right',
      key: 'action',
      render: (test, record) => (
        <div>
          <Tooltip
            placement="bottom"
            title={<FormattedMessage id="edit" />}
          >
            <Button size="small" shape="circle" onClick={this.handleToEdit.bind(this, record.id)}>
              <i className="icon icon-mode_edit" />
            </Button>
          </Tooltip>
          <Tooltip
            placement="bottom"
            title={<FormattedMessage id="delete" />}
          >
            <Button size="small" shape="circle" onClick={this.openRemove.bind(this, record)}>
              <i className="icon icon-delete" />
            </Button>
          </Tooltip>
        </div>
      ),
    },
  ]);

  refresh = () => {
    this.loadSchemeList();
  };

  loadSchemeList = () => {
    const { IssueTypeScreenSchemesStore } = this.props;
    const {
      sorter, tableParam, page, pageSize,
    } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeScreenSchemesStore.setScheme(false);
    IssueTypeScreenSchemesStore.loadSchemeList(
      orgId, page ? page - 1 : undefined, pageSize, sorter, tableParam,
    );
  };

  handleTableChange =(pagination, filters, sorter, param) => {
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
      sorter: sorter.column ? sorter : undefined,
      tableParam: postData,
      page: pagination.current,
      pageSize: pagination.pageSize,
    }, () => this.loadSchemeList());
  };

  handleToCreate = () => {
    const { history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    history.push(`/agile/issue-type-screen-schemes/create?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  handleToEdit = (schemeId) => {
    const { history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    history.push(`/agile/issue-type-screen-schemes/edit/${schemeId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  openRemove = (record) => {
    this.setState({ visible: true, scheme: record, id: record.id });
    // const { IssueTypeScreenSchemesStore } = this.props;
    // const orgId = AppState.currentMenuType.organizationId;
    // IssueTypeScreenSchemesStore.checkDelete(orgId, record.id)
    //   .then((data) => {
    //     if (data) {
    //       this.setState({ visible: true, scheme: record, id: record.id });
    //     }
    //   });
  };

  closeRemove = () => {
    this.setState({
      visible: false, id: false, scheme: false,
    });
  };

  handleDelete = () => {
    const { IssueTypeScreenSchemesStore, intl } = this.props;
    const { id } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeScreenSchemesStore.deleteScheme(orgId, id)
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

  renderTips() {
    const { intl } = this.props;
    const tip1 = intl.formatMessage({ id: 'issueTypeScreenSchemes.list.tip1' });
    const tip2 = intl.formatMessage({ id: 'issueTypeScreenSchemes.list.tip2' });

    return (
      <React.Fragment>
        <Tips tips={[tip1, tip2]} />
      </React.Fragment>
    );
  }

  render() {
    const {
      submitting, deleteVisible, scheme, visible,
    } = this.state;
    const { IssueTypeScreenSchemesStore, intl } = this.props;
    return (
      <Page>
        <Header title={<FormattedMessage id="issueTypeScreenSchemes.title" />}>
          <Button
            className="issue-type-page-scheme-create-btn"
            onClick={this.handleToCreate}
          >
            <Icon type="add" />
            <FormattedMessage id="issueTypeScreenSchemes.create" />
          </Button>
          <Button onClick={this.refresh}>
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>

        <Content>
          {
            this.renderTips()
          }
          <Table
            dataSource={IssueTypeScreenSchemesStore.getSchemeList}
            columns={this.getColumns()}
            loading={IssueTypeScreenSchemesStore.getIsLoading}
            rowKey={record => record.id}
            pagination={IssueTypeScreenSchemesStore.pageInfo}
            onChange={this.handleTableChange}
            filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
            className="issue-table"
          />

        </Content>
        <Modal
          visible={visible}
          title={<FormattedMessage id="issueTypeScreenSchemes.action.delete" />}
          closable={false}
          footer={[
            <Button key="back" onClick={this.closeRemove}>{<FormattedMessage id="cancel" />}</Button>,
            <Button key="submit" type="danger" onClick={this.handleDelete}>
              {intl.formatMessage({ id: 'delete' })}
            </Button>,
          ]}
        >
          <p className="issue-type-page-scheme-tip">
            {intl.formatMessage({ id: 'issueTypeScreenSchemes.delete' })}
            <span className="issue-issueType-bold">{scheme.name}</span>
          </p>
          <p className="issue-type-page-scheme-tip">
            <Icon type="error" className="issue-type-page-scheme-icon issue-error-msg" />
            <FormattedMessage
              id="issueTypeScreenSchemes.delete.inUse"
              values={{
                num: 1,
              }}
            />
          </p>
          <p className="issue-type-page-scheme-tip">
            {intl.formatMessage({ id: 'issueTypeScreenSchemes.delete.tip' })}
          </p>
        </Modal>
      </Page>
    );
  }
}

export default IssueTypeScreenSchemesList;
