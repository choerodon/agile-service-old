import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Page, Header, Content, stores, Permission,
} from 'choerodon-front-boot';
import _ from 'lodash';
import {
  Button, Tabs, Table, Popover, Form, Icon, Spin, Avatar, Tooltip,
} from 'choerodon-ui';
import ReleaseStore from '../../../../stores/project/release/ReleaseStore';
import './ReleaseDetail.scss';
import PublicRelease from '../ReleaseComponent/PublicRelease';
import TypeTag from '../../../../components/TypeTag';
import StatusTag from '../../../../components/StatusTag';
import PriorityTag from '../../../../components/PriorityTag';
import UserHead from '../../../../components/UserHead';

const { TabPane } = Tabs;
const { AppState } = stores;

@observer
class ReleaseDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      publicVersion: false,
      tab: null,
      release: false,
    };
  }

  componentWillMount() {
    document.getElementById('autoRouter').style.overflow = 'scroll';
    try {
      ReleaseStore.getSettings();
    } catch (e) {
      throw e;
    }
    this.refresh();
  }

  componentWillUnmount() {
    ReleaseStore.setVersionStatusIssues([]);
    document.getElementById('autoRouter').style.overflow = 'unset';
  }

  handleFilterChange = (pagination, filters, sorter, barFilters) => {
    const { tab } = this.state;
    Object.keys(filters).forEach((key) => {
      if (key === 'statusId' || key === 'priorityId' || key === 'issueTypeId') {
        ReleaseStore.setAdvArg(filters);
      } else if (key === 'issueNum') {
        // 根据接口进行对象调整
        ReleaseStore.setArg({ issueNum: filters[key][0] });
      } else if (key === 'assigneeName') {
        // 同上
        ReleaseStore.setArg({ assignee: filters[key][0] });
      } else if (key === 'summary') {
        ReleaseStore.setArg({ summary: filters[key][0] });
      } else {
        ReleaseStore.setArg({ comment: filters[key][0] });
      }
    });
    ReleaseStore.setSearchContent(barFilters);
    this.refresh(tab, ReleaseStore.getFilter);
  };

  handleChangeTab(key) {
    const { match } = this.props;
    this.setState({
      tab: key,
    });
    ReleaseStore.axiosGetVersionStatusIssues(
      match.params.id,
      ReleaseStore.getFilterMap.get(key), key,
    ).then((res2) => {
      ReleaseStore.setVersionStatusIssues(res2);
    }).catch((error2) => {
    });
  }

  refresh(key, filter = {}) {
    const { match } = this.props;
    ReleaseStore.setFilterMap(key || '0');
    ReleaseStore.clearArg();
    this.setState({
      loading: true,
    });
    ReleaseStore.axiosGetVersionDetail(match.params.id).then((res) => {
      ReleaseStore.setVersionDetail(res);
      ReleaseStore.setIssueCountDetail({
        todoCount: res.todoIssueCount,
        todoStatus: res.todoStatuses,
        doingCount: res.doingIssueCount,
        todoStatusCount: res.todoStatuses.length,
        doingStatusCount: res.doingStatuses.length,
        doneStatusCount: res.doneStatuses.length,
        doingStatus: res.doingStatuses,
        doneCount: res.doneIssueCount,
        doneStatus: res.doneStatuses,
        count: res.issueCount,
      });
      this.setState({
        loading: false,
      });
    }).catch((error) => {
    });
    ReleaseStore.axiosGetVersionStatusIssues(match.params.id, filter, key).then((res2) => {
      ReleaseStore.setVersionStatusIssues(res2);
      ReleaseStore.setOriginIssue(res2);
      this.setState({
        loading: false,
      });
    }).catch((error2) => {
    });
  }

  renderBorderRadius(position) {
    let radius = {};
    if (position === 'done') {
      if (ReleaseStore.getIssueCountDetail.doneCount) {
        radius = {
          borderTopLeftRadius: '10px',
          borderBottomLeftRadius: '10px',
        };
      }
      if (!(ReleaseStore.getIssueCountDetail.doingCount
          || ReleaseStore.getIssueCountDetail.todoCount)) {
        radius = {
          ...radius,
          borderTopRightRadius: '10px',
          borderBottomRightRadius: '10px',
        };
      }
    } else if (position === 'doing') {
      if (!ReleaseStore.getIssueCountDetail.doneCount) {
        radius = {
          borderTopLeftRadius: '10px',
          borderBottomLeftRadius: '10px',
        };
      }
      if (!ReleaseStore.getIssueCountDetail.todoCount) {
        radius = {
          ...radius,
          borderTopRightRadius: '10px',
          borderBottomRightRadius: '10px',
        };
      }
    } else {
      if (!(ReleaseStore.getIssueCountDetail.doneCount
          || ReleaseStore.getIssueCountDetail.doingCount)) {
        radius = {
          borderTopLeftRadius: '10px',
          borderBottomLeftRadius: '10px',
        };
      }
      if (ReleaseStore.getIssueCountDetail.todoCount) {
        radius = {
          ...radius,
          borderTopRightRadius: '10px',
          borderBottomRightRadius: '10px',
        };
      }
    }
    return radius;
  }

  renderTabTables(columns) {
    const urlParams = AppState.currentMenuType;
    return (
      <div>
        <div style={{
          padding: '16px 0', display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        }}
        >
          <p
            style={{
              color: '#3F51B5',
              cursor: 'pointer',
            }}
            role="none"
            onClick={() => {
              const { history } = this.props;
              history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramType=fixVersion&paramId=${ReleaseStore.getVersionDetail.versionId}&paramName=${encodeURIComponent(`${ReleaseStore.getVersionDetail.name}下的问题`)}&paramUrl=release/detail/${ReleaseStore.getVersionDetail.versionId}`);
            }}
          >
            {'在“问题管理中”查看'}
            <Icon style={{ fontSize: 13 }} type="open_in_new" />
          </p>
        </div>
        <Table
          ref={(node) => { this.Table = node; }}
          pagination={ReleaseStore.getVersionStatusIssues.length > 10}
          onChange={this.handleFilterChange}
          dataSource={ReleaseStore.getVersionStatusIssues}
          columns={columns}
          rowKey="issueId"
        />
      </div>
    );
  }

  renderPopContent(type) {
    let name;
    let data;
    let count;
    let background;
    if (type === 'done') {
      name = '已完成';
      count = ReleaseStore.getIssueCountDetail.doneStatusCount;
      data = ReleaseStore.getIssueCountDetail.doneStatus;
      background = 'rgb(0, 191, 165)';
    } else if (type === 'doing') {
      name = '处理中';
      data = ReleaseStore.getIssueCountDetail.doingStatus;
      count = ReleaseStore.getIssueCountDetail.doingStatusCount;
      background = 'rgb(77, 144, 254)';
    } else {
      name = '待处理';
      data = ReleaseStore.getIssueCountDetail.todoStatus;
      count = ReleaseStore.getIssueCountDetail.todoStatusCount;
      background = 'rgb(255, 177, 0)';
    }
    return (
      <div>
        <p>{name}</p>
        <p style={{ marginTop: 3 }}>
          {'类别'}
          <span style={{ color: '#3575DF' }}>{name}</span>
          {'中有'}
          {count || 0}
          {'种状态'}
        </p>
        {
          data && data.length ? data.map(item => (
            <div key={item.id} style={{ margin: '14px 0' }}>
              <span
                style={{
                  background,
                  color: 'white',
                  padding: '1px 4px',
                  marginRight: 16,
                }}
              >
                {item.name}
              </span>
              <span>
                {
                  ReleaseStore.getOriginIssue.filter(
                    issues => issues.statusMapDTO && issues.statusMapDTO.id === item && item.id,
                  ).length
                }
                {'个'}
              </span>
            </div>
          ))
            : ''
        }
      </div>
    );
  }

  render() {
    const urlParams = AppState.currentMenuType;
    const filterMap = new Map([
      ['issueNum', []],
      ['issueTypeId', ReleaseStore.getIssueTypes.map(item => ({
        text: item.name,
        value: item.id.toString(),
      }))],
      ['summary', []],
      ['assigneeName', []],
      ['priorityId', ReleaseStore.getIssuePriority.map(item => ({
        text: item.name,
        value: item.id.toString(),
      }))],
      ['statusId', ReleaseStore.getIssueStatus.map(item => ({
        text: item.name,
        value: item.id.toString(),
      }))],
    ]);
    const { history, match } = this.props;
    const { loading, publicVersion, release } = this.state;
    const columns = [
      {
        width: '10%',
        title: '问题编号',
        dataIndex: 'issueNum',
        key: 'issueNum',
        render: (text, record) => (
          <Tooltip mouseEnterDelay={0.5} title={`问题编号： ${text}`}>
            <a
              role="none"
              onClick={() => {
                if (record.parentIssueId) {
                  history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${record.parentIssueNum}&paramIssueId=${record.parentIssueId}&paramOpenIssueId=${record.issueId}&paramUrl=release/detail/${ReleaseStore.getVersionDetail.versionId}`);
                } else {
                  history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${record.issueNum}&paramIssueId=${record.issueId}&paramOpenIssueId=${record.issueId}&paramUrl=release/detail/${ReleaseStore.getVersionDetail.versionId}`);
                }
              }}
            >
              <span className="textDisplayOneColumn" style={{ minWidth: 85 }}>{text}</span>
            </a>
          </Tooltip>
        ),
        filters: filterMap.get('issueNum'),
      },
      {
        width: '10%',
        title: '问题类型',
        dataIndex: 'issueTypeId',
        key: 'issueTypeId',
        render: (text, record) => (
          <TypeTag
            style={{ minWidth: 90 }}
            data={record.issueTypeDTO}
            showName
          />
        ),
        filters: filterMap.get('issueTypeId'),
        filterMultiple: true,
      },
      {
        width: '40%',
        title: '概要',
        dataIndex: 'summary',
        key: 'summary',
        render: text => (
          <Tooltip mouseEnterDelay={0.5} title={`问题概要：${text}`}>
            <span className="textDisplayOneColumn" style={{ minWidth: 40 }}>{text}</span>
          </Tooltip>
        ),
        filters: filterMap.get('summary'),
      },
      {
        width: '15%',
        title: '模块',
        dataIndex: 'issueComponentBriefDTOS',
        key: 'issueComponentBriefDTOS',
        render: (text, record) => (
          <Tooltip mouseEnterDelay={0.5} title={`模块：${_.map(record.issueComponentBriefDTOS, 'name').join(',')}`}>
            <span className="textDisplayOneColumn" style={{ minWidth: 100 }}>{_.map(record.issueComponentBriefDTOS, 'name').join(',')}</span>
          </Tooltip>
        ),
        hidden: true,
      },
      {
        width: '15%',
        title: '经办人',
        dataIndex: 'assigneeName',
        key: 'assigneeName',
        render: (text, record) => (text ? (
          <UserHead
            user={{
              id: record.assigneeId,
              loginName: record.assigneeLoginName,
              realName: record.assigneeRealName,
              avatar: record.assigneeImageUrl,
            }}
          />

        ) : ''),
        filters: filterMap.get('assigneeName'),
      },
      {
        width: '10%',
        title: '优先级',
        dataIndex: 'priorityName',
        key: 'priorityId',
        render: (text, record) => (
          <PriorityTag
            style={{ minWidth: 55 }}
            priority={record.priorityDTO}
          />
        ),
        filters: filterMap.get('priorityId'),
        filterMultiple: true,
      }, {
        width: '15%',
        title: '状态',
        dataIndex: 'statusName',
        key: 'statusId',
        render: (text, record) => (
          <StatusTag
            data={record.statusMapDTO}
          />
        ),
        filters: filterMap.get('statusId'),
        filterMultiple: true,
      },
    ];
    return (
      <Page>
        <Header
          title={(
            <Tooltip title={`版本${ReleaseStore.getVersionDetail.name}`}>
              <div
                style={{
                  display: 'inline-block',
                  maxWidth: '141px',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  marginTop: '23px',
                }}
              >
                {`版本 ${ReleaseStore.getVersionDetail.name}`}
              </div>
            </Tooltip>
          )}
          backPath={`/agile/release?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}
        >

          <div style={{
            marginLeft: 12, fontSize: 13, color: '#FFB100', padding: '1px 10px', background: 'rgba(255,177,0,0.08)', height: 20, lineHeight: '20px',
          }}
          >
            {ReleaseStore.getVersionDetail.statusName}
          </div>
          {
            ReleaseStore.getVersionDetail.statusCode === 'archived' ? '' : (
              <Permission service={ReleaseStore.getVersionDetail.statusCode === 'version_planning' ? ['agile-service.product-version.releaseVersion'] : ['agile-service.product-version.revokeReleaseVersion']}>
                <Button
                  funcType="flat"
                  style={{
                    marginLeft: 8,
                  }}
                  onClick={() => {
                    if (ReleaseStore.getVersionDetail.statusCode === 'version_planning') {
                      ReleaseStore.axiosGetPublicVersionDetail(
                        ReleaseStore.getVersionDetail.versionId,
                      )
                        .then((res) => {
                          ReleaseStore.setPublicVersionDetail(res);
                          this.setState({ publicVersion: true, release: res });
                        }).catch((error) => {
                        });
                    } else {
                      ReleaseStore.axiosUnPublicRelease(
                        ReleaseStore.getVersionDetail.versionId,
                      ).then((res2) => {
                        this.refresh();
                      }).catch((error) => {
                      });
                    }
                  }}
                >
                  <Icon type="publish2" />
                  <span>{ReleaseStore.getVersionDetail.statusCode === 'version_planning' ? '发布' : '撤销发布'}</span>
                </Button>
              </Permission>
            )
          }
          <Button
            funcType="flat"
            style={{
              marginLeft: 8,
            }}
            onClick={() => {
              history.push(`/agile/release/logs/${match.params.id}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
            }}
          >
            <Icon type="find_in_page" />
            <span>版本日志</span>
          </Button>
        </Header>
        <Content className="c7n-versionDetail">
          <Spin spinning={loading}>
            <div style={{ display: 'flex', color: 'rgba(0,0,0,0.54)' }}>
              <div className="c7n-versionTime">
                <Icon style={{ fontSize: 20 }} type="date_range" />
                {'开始日期:'}
                <span className="c7n-version-timemoment">{ReleaseStore.getVersionDetail.startDate ? ReleaseStore.getVersionDetail.startDate.slice(0, 10) : '无'}</span>
              </div>
              <div className="c7n-versionTime" style={{ marginLeft: 80 }}>
                <Icon style={{ fontSize: 20 }} type="date_range" />
                {'实际发布日期:'}
                <span className="c7n-version-timemoment">{ReleaseStore.getVersionDetail.releaseDate ? ReleaseStore.getVersionDetail.releaseDate.slice(0, 10) : '无'}</span>
              </div>
            </div>
            <div className="c7n-release-issueClassify">
              <Popover
                placement="bottom"
                content={this.renderPopContent('done')}
              >
                <div
                  style={{
                    flex: ReleaseStore.getIssueCountDetail.doneCount,
                    background: '#00BFA5',
                    ...this.renderBorderRadius('done'),
                  }}
                  className="c7n-release-issueDone"
                />
              </Popover>
              <Popover
                placement="bottom"
                content={this.renderPopContent('doing')}
              >
                <div
                  style={{
                    flex: ReleaseStore.getIssueCountDetail.doingCount,
                    background: '#4D90FE',
                    ...this.renderBorderRadius('doing'),
                  }}
                  className="c7n-release-issueDoing"
                />
              </Popover>
              <Popover
                placement="bottom"
                content={this.renderPopContent('todo')}
              >
                <div
                  style={{
                    flex: ReleaseStore.getIssueCountDetail.todoCount,
                    background: '#FFB100',
                    ...this.renderBorderRadius('todo'),
                  }}
                  className="c7n-release-issueTodo"
                />
              </Popover>
            </div>
            <div>
              <Tabs
                animated={false}
                onChange={this.handleChangeTab.bind(this)}
                style={{ marginTop: 28 }}
              >
                <TabPane
                  tab={(
                    <div className="c7n-release-tabTitle">
                      <span className="c7n-release-titleNum">{ReleaseStore.getIssueCountDetail.count}</span>
                      <span>
                        {'当前版本'}
                        <br />
                        {'个问题'}
                      </span>
                    </div>
                  )}
                  key="0"
                >
                  {this.renderTabTables(columns)}
                </TabPane>
                <TabPane
                  tab={(
                    <div className="c7n-release-tabTitle">
                      <span
                        style={{ color: 'rgb(0, 191, 165)' }}
                        className="c7n-release-titleNum"
                      >
                        {ReleaseStore.getIssueCountDetail.doneCount}
                      </span>
                      <span>
                        {'问题'}
                        <br />
                        {'已完成'}
                      </span>
                    </div>
                    )}
                  key="done"
                >
                  {this.renderTabTables(columns)}
                </TabPane>
                <TabPane
                  tab={(
                    <div className="c7n-release-tabTitle">
                      <span
                        style={{ color: 'rgb(77, 144, 254)' }}
                        className="c7n-release-titleNum"
                      >
                        {ReleaseStore.getIssueCountDetail.doingCount}
                      </span>
                      <span>
                        {'问题'}
                        <br />
                        {'正在处理'}
                      </span>
                    </div>
                  )}
                  key="doing"
                >
                  {this.renderTabTables(columns)}
                </TabPane>
                <TabPane
                  tab={(
                    <div className="c7n-release-tabTitle">
                      <span
                        style={{ color: 'rgb(255, 177, 0)' }}
                        className="c7n-release-titleNum"
                      >
                        {ReleaseStore.getIssueCountDetail.todoCount}
                      </span>
                      <span>
                        {'问题'}
                        <br />
                        {'待处理'}
                      </span>
                    </div>
                  )}
                  key="todo"
                >
                  {this.renderTabTables(columns)}
                </TabPane>
              </Tabs>
            </div>

            <PublicRelease
              release={release}
              visible={publicVersion}
              onCancel={() => {
                this.setState({
                  publicVersion: false,
                });
              }}
              refresh={this.refresh.bind(this)}
            />
          </Spin>

        </Content>
      </Page>
    );
  }
}

export default Form.create()(withRouter(ReleaseDetail));
