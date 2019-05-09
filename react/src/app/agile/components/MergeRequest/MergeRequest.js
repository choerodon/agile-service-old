import React, { Component } from 'react';
import { Modal, Table, Tooltip, Popover, Button, Icon } from 'choerodon-ui';
import { stores, Content, axios } from 'choerodon-front-boot';
import TimeAgo from 'timeago-react';
import UserHead from '../UserHead';

const { AppState } = stores;
const Sidebar = Modal.Sidebar;
const STATUS_SHOW = {
  opened: '开放',
  merged: '已合并',
  closed: '关闭',
};

class MergeRequest extends Component {
  constructor(props) {
    super(props);
    this.state = {
      mergeRequests: [],
      loading: false,
    };
  }

  componentDidMount() {
    this.loadMergeRequest();
  }

  loadMergeRequest() {
    const { issueId } = this.props;
    this.setState({ loading: true });
    axios.get(`/devops/v1/project/${AppState.currentMenuType.id}/issue/${issueId}/merge_request/list`)
      .then((res) => {
        this.setState({
          mergeRequests: res,
          loading: false,
        });
      });
  }

  createMergeRequest(record) {
    const win = window.open('');
    const projectId = AppState.currentMenuType.id;
    const { applicationId, gitlabMergeRequestId } = record;
    axios.get(`/devops/v1/projects/${projectId}/apps/${applicationId}/git/url`)
      .then((res) => {
        const url = `${res}/merge_requests/${gitlabMergeRequestId}`;
        win.location.href = url;
      })
      .catch((error) => {
      });
  }

  render() {
    const { issueId, issueNum, num, visible, onCancel } = this.props;
    const column = [
      {
        title: '编码',
        dataIndex: 'id',
        width: '10%',
        render: id => (
          <div style={{ width: '100%', overflow: 'hidden' }}>
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={id}>
              <p style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0 }}>
                # {id}
              </p>
            </Tooltip>
          </div>
        ),
      },
      {
        title: '名称',
        dataIndex: 'title',
        width: '35%',
        render: title => (
          <div style={{ width: '100%', overflow: 'hidden', flexShrink: 0 }}>
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={title}>
              <p style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0 }}>
                {title}
              </p>
            </Tooltip>
          </div>
        ),
      },
      {
        title: '状态',
        dataIndex: 'state',
        width: '10%',
        render: state => (
          <div style={{ width: '100%', overflow: 'hidden', flexShrink: 0 }}>
            {['opened', 'merged', 'closed'].includes(state) ? STATUS_SHOW[state] : ''}       
          </div>
        ),
      },
      {
        title: '审查人',
        dataIndex: 'authorId',
        width: '20%',
        render: (authorId, record) => (
          <div style={{ width: '100%', overflow: 'hidden', flexShrink: 0, justifyContent: 'flex-start' }}>
            <UserHead
              user={{
                id: authorId,
                realName: record.authorName,
                avatar: record.imageUrl,
              }}
            />
          </div>
        ),
      },
      {
        title: '更新时间',
        dataIndex: 'updatedAt',
        width: '15%',
        render: updatedAt => (
          <div style={{ width: '100%', overflow: 'hidden', flexShrink: 0 }}>
            <Popover
              title="更新时间"
              content={updatedAt}
              placement="left"
            >
              <TimeAgo
                datetime={updatedAt}
                locale={Choerodon.getMessage('zh_CN', 'en')}
              />
            </Popover> 
          </div>
        ),
      },
      {
        title: '',
        dataIndex: 'gitlabMergeRequestId',
        width: '10%',
        render: (gitlabMergeRequestId, record) => (
          <div style={{ flexShrink: 0 }}>
            <Popover placement="bottom" mouseEnterDelay={0.5} content={<div><span>合并请求</span></div>}>
              <Button shape="circle" onClick={this.createMergeRequest.bind(this, record)}>
                <Icon type="device_hub" />
              </Button>
            </Popover>
          </div>
        ),
      },
    ];
    return (
      <Sidebar
        className="c7n-commits"
        title="关联合并请求"
        visible={visible || false}
        okText="关闭"
        okCancel={false}
        onOk={onCancel}
      >
        <Content
          style={{
            paddingLeft: 0,
            paddingRight: 0,
            paddingTop: 0,
          }}
          title={`查看问题“${this.props.issueNum}”关联的合并请求`}
          description="您可以在此查看该问题关联的所有合并请求相关信息，及查看合并请求详情。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/issue/manage-branch/"
        >
          <Table
            filterBar={false}
            rowKey={record => record.id}
            columns={column}
            dataSource={this.state.mergeRequests}
            loading={this.state.loading}
            scroll={{ x: true }}
          />
        </Content>
      </Sidebar>
    );
  }
}
export default MergeRequest;
