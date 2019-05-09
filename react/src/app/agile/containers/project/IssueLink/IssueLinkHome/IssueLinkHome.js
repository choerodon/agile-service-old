import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Button, Table, Spin, Popover, Tooltip, Icon, 
} from 'choerodon-ui';
import {
  Page, Header, Content, stores, axios, Permission, 
} from 'choerodon-front-boot';
import CreateLink from './Component/CreateLink';
import EditLink from './Component/EditLink';
import DeleteLink from './Component/DeleteLink';
import './IssueLinkHome.scss';

const { AppState } = stores;

@observer
class Link extends Component {
  constructor(props) {
    super(props);
    this.state = {
      links: [],
      createLinkShow: false,
      editLinkShow: false,
      currentLinkTypeId: undefined,

      link: {},
      currentComponentId: undefined,
      loading: false,
      confirmShow: false,
      editComponentShow: false,
      createComponentShow: false,
      pagination: {
        current: 1,
        pageSize: 10,
        total: undefined,
      },
      filterName: '',
      barFilters: [],
    };
  }

  componentDidMount() {
    const { filterName, barFilters, pagination } = this.state;
    this.loadLinks(pagination.current - 1, pagination.pageSize);
  }

  handleTableChange = (pagination, filters, sorter, barFilters) => {
    this.setState({
      pagination,
      filterName: filters.linkName && filters.linkName[0],
      barFilters,
    }, () => {
      this.loadLinks(pagination.current - 1, pagination.pageSize);
    });
  }

  showLinkType(record) {
    this.setState({
      editLinkShow: true,
      currentLinkTypeId: record.linkTypeId,
    });
  }

  clickDeleteLink(record) {
    this.setState({
      link: record,
      confirmShow: true,
    });
  }

  deleteLink() {
    this.setState({
      confirmShow: false,
    });
    this.loadLinks();
  }

  loadLinks(page = 0, size = 10) {
    const { filterName, barFilters } = this.state;
    this.setState({
      loading: true,
    });
    axios
      .post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types/query_all?page=${page}&size=${size}`, {
        contents: barFilters,
        linkName: filterName,
      })
      .then((res) => {
        this.setState({
          links: res.content,
          loading: false,
          pagination: {
            current: res.number + 1,
            pageSize: res.size,
            total: res.totalElements,
          },
        });
      })
      .catch((error) => {});
  }


  render() {
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const {
      loading, links, pagination, createLinkShow, editLinkShow, currentLinkTypeId, confirmShow, link, filterName, barFilters,
    } = this.state;

    const column = [
      {
        title: '名称',
        dataIndex: 'linkName',
        width: '25%',
        render: linkName => (
          <div style={{ width: '100%', overflow: 'hidden' }}>
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={linkName}>
              <p
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  marginBottom: 0,
                }}
              >
                {linkName}
              </p>
            </Tooltip>
          </div>
        ),
        filters: [],
      },
      {
        title: '链出描述',
        dataIndex: 'outWard',
        width: '30%',
        render: outWard => (
          <div style={{ width: '100%', overflow: 'hidden' }}>
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={outWard}>
              <p
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  marginBottom: 0,
                }}
              >
                {outWard}
              </p>
            </Tooltip>
          </div>
        ),
      },
      {
        title: '链入描述',
        dataIndex: 'inWard',
        width: '30%',
        render: inWard => (
          <div style={{ width: '100%', overflow: 'hidden' }}>
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={inWard}>
              <p
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  marginBottom: 0,
                }}
              >
                {inWard}
              </p>
            </Tooltip>
          </div>
        ),
      },
      {
        title: '',
        dataIndex: 'linkTypeId',
        width: 100,
        render: (linkTypeId, record) => (
          <div>
            <Permission
              type={type}
              projectId={projectId}
              organizationId={orgId}
              service={['agile-service.issue-link-type.updateIssueLinkType']}
            >
              <Popover
                placement="bottom"
                mouseEnterDelay={0.5}
                content={(
                  <div>
                    <span>详情</span>
                  </div>
                )}
              >
                {/* <Button shape="circle" onClick={this.showLinkType.bind(this, record)}> */}
                <Icon type="mode_edit" onClick={this.showLinkType.bind(this, record)} />
                {/* </Button> */}
              </Popover>
            </Permission>
            <Permission
              type={type}
              projectId={projectId}
              organizationId={orgId}
              service={['agile-service.issue-link-type.deleteIssueLinkType']}
            >
              <Popover
                placement="bottom"
                mouseEnterDelay={0.5}
                content={(
                  <div>
                    <span>删除</span>
                  </div>
)}
              >
                {/* <Button shape="circle" onClick={this.clickDeleteLink.bind(this, record)}> */}
                <Icon type="delete_forever" onClick={this.clickDeleteLink.bind(this, record)} />
                {/* </Button> */}
              </Popover>
            </Permission>
          </div>
        ),
      },
    ];
    return (
      <Page
        service={[
          'agile-service.issue-link-type.updateIssueLinkType',
          'agile-service.issue-link-type.deleteIssueLinkType',
          'agile-service.issue-link-type.createIssueLinkType',
        ]}
        className="c7n-issue-link"
      >
        <Header title="问题链接">
          <Permission
            type={type}
            projectId={projectId}
            organizationId={orgId}
            service={['agile-service.issue-link-type.createIssueLinkType']}
          >
            <Button funcType="flat" onClick={() => this.setState({ createLinkShow: true })}>
              <Icon type="playlist_add icon" />
              <span>创建链接</span>
            </Button>
          </Permission>
          <Button funcType="flat" onClick={() => this.loadLinks(pagination.current - 1, pagination.pageSize)}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title="问题链接"
          description="通过自定义问题链接，可以帮助您更好的对多个问题进行关联，不再局限于父子任务。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/issue-link/"
        >
          <div>
            <Spin spinning={loading}>
              <Table
                pagination={pagination}
                rowKey={record => record.linkTypeId}
                columns={column}
                dataSource={links}
                filterBarPlaceholder="过滤表"
                scroll={{ x: true }}
                onChange={this.handleTableChange}
              />
            </Spin>
            {createLinkShow ? (
              <CreateLink
                onOk={() => {
                  this.setState({ createLinkShow: false });
                  this.loadLinks();
                }}
                onCancel={() => this.setState({ createLinkShow: false })}
              />
            ) : null}
            {editLinkShow ? (
              <EditLink
                linkTypeId={currentLinkTypeId}
                onOk={() => {
                  this.setState({ editLinkShow: false });
                  this.loadLinks();
                }}
                onCancel={() => this.setState({ editLinkShow: false })}
              />
            ) : null}
            {confirmShow ? (
              <DeleteLink
                visible={confirmShow}
                link={link}
                onCancel={() => this.setState({ confirmShow: false })}
                onOk={this.deleteLink.bind(this)}
              />
            ) : null}
          </div>
        </Content>
      </Page>
    );
  }
}

export default Link;
