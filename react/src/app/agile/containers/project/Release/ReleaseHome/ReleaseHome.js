import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Page, Header, Content, stores, Permission,
} from 'choerodon-front-boot';
import {
  Button, Table, Menu, Dropdown, Icon, Modal, Radio, Select, Spin, Tooltip,
} from 'choerodon-ui';
import { Action, axios } from 'choerodon-front-boot';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import DragSortingTable from '../ReleaseComponent/DragSortingTable';
import AddRelease from '../ReleaseComponent/AddRelease';
import ReleaseStore from '../../../../stores/project/release/ReleaseStore';
import './ReleaseHome.scss';
import EditRelease from '../ReleaseComponent/EditRelease';
import PublicRelease from '../ReleaseComponent/PublicRelease';
import emptyVersion from '../../../../assets/image/emptyVersion.svg';
import DeleteReleaseWithIssues from '../ReleaseComponent/DeleteReleaseWithIssues';
import CombineRelease from '../ReleaseComponent/CombineRelease';

const confirm = Modal.confirm;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const { Sidebar } = Modal;
const { AppState } = stores;
const COLOR_MAP = {
  规划中: '#ffb100',
  已发布: '#00bfa5',
  归档: 'rgba(0, 0, 0, 0.3)',
};

@observer
class ReleaseHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      editRelease: false,
      addRelease: false,
      pagination: {
        current: 1,
        total: 0,
        pageSize: 10,
      },
      selectItem: {},
      versionDelInfo: {},
      publicVersion: false,
      radioChose: null,
      selectChose: null,
      combineVisible: false,
      loading: false,
      sourceList: [],
      release: false,
    };
  }

  componentDidMount() {
    this.refresh(this.state.pagination);
  }

  componentWillUnmount() {
    ReleaseStore.setVersionList([]);
  }

  refresh(pagination) {
    this.setState({
      loading: true,
    });
    ReleaseStore.axiosGetVersionList({
      page: pagination.current - 1,
      size: pagination.pageSize,
    }).then((data) => {
      ReleaseStore.setVersionList(data.content);
      this.setState({
        loading: false,
        pagination: {
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: data.totalElements,
        },
      });
    }).catch((error) => {
    });
  }

  MyTable = (props) => {
    if (ReleaseStore.getVersionList.length === 0 && !this.state.loading) {
      // fixed 会渲染两张表，所以要判断子元素有没有这个属性
      // 如果有的话禁止渲染，防止 Empty 重复渲染
      if (!props.children[0].props.fixed) {
        return (
          <EmptyBlock
            style={{ marginTop: 60 }}
            border
            pic={pic}
            title="您还没有为此项目添加任何版本"
            des="尝试修改您
            的过滤选项或者在下面创建新的问题"
          />
        );
      } else {
        return null;
      }
    }
    const renderNarrow = (
      <div style={props.style} className={props.className}>
        {props.children[1]}
        {props.children[2]}
      </div>
    );
    return expand ? renderNarrow : (<table {...props} />);
  };

  handleClickMenu(record, e) {
    const that = this;
    if (e.key.indexOf('0') !== -1) {
      if (record.statusCode === 'version_planning') {
        ReleaseStore.axiosGetPublicVersionDetail(record.versionId)
          .then((res) => {
            ReleaseStore.setPublicVersionDetail(res);
            ReleaseStore.setVersionDetail(record);
            this.setState({ publicVersion: true, release: record });
          }).catch((error) => {
          });
      } else {
        ReleaseStore.axiosUnPublicRelease(
          record.versionId,
        ).then((res2) => {
          this.refresh(this.state.pagination);
        }).catch((error) => {
        });
      }
    }
    if (e.key.indexOf('4') !== -1) {
      ReleaseStore.axiosVersionIssueStatistics(record.versionId).then((res) => {
        this.setState({
          versionDelInfo: {
            versionName: record.name,
            versionId: record.versionId,
            ...res,
          },
        }, () => {
          ReleaseStore.setDeleteReleaseVisible(true);
        });
      }).catch((error) => {
      });
    }
    if (e.key.indexOf('5') !== -1) {
      ReleaseStore.axiosGetVersionDetail(record.versionId).then((res) => {
        ReleaseStore.setVersionDetail(res);
        this.setState({
          selectItem: record,
          editRelease: true,
        });
      }).catch((error) => {
      });
    }
    if (e.key.indexOf('3') !== -1) {
      if (record.statusCode === 'archived') {
        // 撤销归档
        ReleaseStore.axiosUnFileVersion(record.versionId).then((res) => {
          this.refresh(this.state.pagination);
        }).catch((error) => {
        });
      } else {
        // 归档
        ReleaseStore.axiosFileVersion(record.versionId).then((res) => {
          this.refresh(this.state.pagination);
        }).catch((error) => {
        });
      }
    }
  }

  handleChangeTable(pagination, filters, sorter, barFilters) {
    const searchArgs = {};
    if (filters && filters.name && filters.name.length > 0) {
      searchArgs.name = filters.name[0];
    }
    if (filters && filters.description && filters.description.length > 0) {
      searchArgs.description = filters.description[0];
    }
    ReleaseStore.setFilters({
      advancedSearchArgs: { statusCodes: filters && filters.key && filters.key.length > 0 ? filters.key : [] },
      searchArgs,
      contents: barFilters,
    });
    this.refresh({
      current: pagination.current,
      pageSize: pagination.pageSize,
    });
  }

  handleCombineRelease() {
    ReleaseStore.axiosGetVersionListWithoutPage().then((res) => {
      this.setState({
        combineVisible: true,
        sourceList: res,
      });
    }).catch((error) => {
    });
  }

  handleDrag =(res, postData) => {
    const { pagination } = this.state;
    ReleaseStore.setVersionList(res);
    ReleaseStore.handleDataDrag(AppState.currentMenuType.id, postData)
      .then(() => {
        this.refresh(pagination);
      }).catch((error) => {
        this.refresh(pagination);
      });
  };

  render() {
    const {
      loading,
      pagination,
      addRelease,
      editRelease,
      sourceList,
      combineVisible,
      versionDelInfo,
      selectItem,
      publicVersion,
      release,
    } = this.state;
    const deleteReleaseVisible = ReleaseStore.getDeleteReleaseVisible;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const versionData = ReleaseStore.getVersionList.length > 0 ? ReleaseStore.getVersionList : [];
    const versionColumn = [{
      title: '版本',
      dataIndex: 'name',
      key: 'name',
      width: '94px',
      render: (text, record) => (
        <Tooltip title={`版本名称：${text}`}>
          <div
            role="none"
            style={{
              maxWidth: '120px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
            }}
          >
            <a
              role="none"
              onClick={() => {
                const { history } = this.props;
                const urlParams = AppState.currentMenuType;
                history.push(`/agile/release/detail/${record.versionId}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
              }}
            >
              {text}
            </a>
          </div>
        </Tooltip>
      ),
      filters: [],
    }, {
      title: '版本状态',
      dataIndex: 'status',
      key: 'key',
      render: text => (
        <p style={{ marginBottom: 0, minWidth: 60 }}>
          <span
            style={{
              color: '#fff',
              background: COLOR_MAP[text],
              display: 'inline-block',
              lineHeight: '20px',
              borderRadius: '3px',
              padding: '0 10px',
            }}
          >
            {text === '归档' ? '已归档' : text}
          </span>
        </p>
      ),
      filters: [
        {
          text: '已归档',
          value: 'archived',
        },
        {
          text: '已发布',
          value: 'released',
        },
        {
          text: '规划中',
          value: 'version_planning',
        },
      ],
      filterMultiple: true,
    }, {
      title: '开始日期',
      dataIndex: 'startDate',
      key: 'startDate',
      render: text => (text ? <p style={{ marginBottom: 0, minWidth: 75 }}>{text.split(' ')[0]}</p> : ''),
    }, {
      title: '预计发布日期',
      dataIndex: 'expectReleaseDate',
      key: 'expectReleaseDate',
      render: text => (text ? <p style={{ marginBottom: 0, minWidth: 75 }}>{text.split(' ')[0]}</p> : ''),
    }, {
      title: '实际发布日期',
      dataIndex: 'releaseDate',
      key: 'releaseDate',
      render: text => (text ? <p style={{ marginBottom: 0, minWidth: 75 }}>{text.split(' ')[0]}</p> : ''),
    }, {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      render: text => (
        <Tooltip mouseEnterDelay={0.5} title={`描述：${text}`}>
          <p style={{
            marginBottom: 0, maxWidth: '120px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', 
          }}
          >
            {text}
          </p>
        </Tooltip>
      ),
      filters: [],
    }, {
      title: '',
      dataIndex: 'option',
      key: 'option',
      render: (text, record) => (
        <Action
          data={record.statusCode === 'archived' ? [
            {
              service: record.statusCode === 'archived' ? ['agile-service.product-version.revokeArchivedVersion'] : ['agile-service.product-version.archivedVersion'],
              text: record.statusCode === 'archived' ? '撤销归档' : '归档',
              action: this.handleClickMenu.bind(this, record, { key: '3' }),
            },
            {
              service: ['agile-service.product-version.updateVersion'],
              text: '编辑',
              action: this.handleClickMenu.bind(this, record, { key: '5' }),
            }] : [
            {
              service: record.statusCode === 'version_planning' ? ['agile-service.product-version.releaseVersion'] : ['agile-service.product-version.revokeReleaseVersion'],
              text: record.statusCode === 'version_planning' ? '发布' : '撤销发布',
              action: this.handleClickMenu.bind(this, record, { key: '0' }),
            },
            {
              service: record.statusCode === 'archived' ? ['agile-service.product-version.revokeArchivedVersion'] : ['agile-service.product-version.archivedVersion'],
              text: record.statusCode === 'archived' ? '撤销归档' : '归档',
              action: this.handleClickMenu.bind(this, record, { key: '3' }),
            },
            {
              service: ['agile-service.product-version.deleteVersion'],
              text: '删除',
              action: this.handleClickMenu.bind(this, record, { key: '4' }),
            },
            {
              service: ['agile-service.product-version.updateVersion'],
              text: '编辑',
              action: this.handleClickMenu.bind(this, record, { key: '5' }),
            }]}
        />
      ),
    }];
    return (
      <Page
        service={[
          'agile-service.product-version.releaseVersion',
          'agile-service.product-version.revokeReleaseVersion',
          'agile-service.product-version.revokeArchivedVersion',
          'agile-service.product-version.archivedVersion',
          'agile-service.product-version.deleteVersion',
          'agile-service.product-version.updateVersion',
          'agile-service.product-version.createVersion',
          'agile-service.product-version.mergeVersion',
          'agile-service.product-version.listByProjectId',
        ]}
      >
        <Header title="发布版本">
          <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.product-version.createVersion']}>
            <Button
              onClick={() => {
                this.setState({
                  addRelease: true,
                });
              }}
              className="leftBtn"
              funcType="flat"
            >
              <Icon type="playlist_add" />
              {'创建发布版本'}
            </Button>
          </Permission>
          <Permission service={['agile-service.product-version.mergeVersion']} type={type} projectId={projectId} organizationId={orgId}>
            <Button
              className="leftBtn2"
              funcType="flat"
              onClick={this.handleCombineRelease.bind(this)}
            >
              <Icon type="merge_type" />
              {'版本合并'}
            </Button>
          </Permission>
          <Button className="leftBtn2" funcType="flat" onClick={this.refresh.bind(this, this.state.pagination)}>
            <Icon type="refresh" />
            {'刷新'}
          </Button>
        </Header>
        <Content
          title={`项目“${AppState.currentMenuType.name}”的发布版本`}
          description="根据项目周期，可以对软件项目追踪不同的版本，同时可以将对应的问题分配到版本中。例如：v1.0.0、v0.5.0等。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/release/"
        >
          <Spin spinning={loading}>
            {
              <DragSortingTable
                handleDrag={this.handleDrag}
                columns={versionColumn}
                dataSource={versionData}
                pagination={pagination}
                onChange={this.handleChangeTable.bind(this)}
              />
            }
          </Spin>
          {addRelease
            ? (
              <AddRelease
                visible={addRelease}
                onCancel={() => {
                  this.setState({
                    addRelease: false,
                  });
                }}
                refresh={this.refresh.bind(this, pagination)}
              />
            ) : ''
          }
          <CombineRelease
            onRef={(ref) => {
              this.combineRelease = ref;
            }}
            sourceList={sourceList}
            visible={combineVisible}
            onCancel={() => {
              this.setState({
                combineVisible: false,
              });
            }}
            refresh={this.refresh.bind(this, pagination)}
          />
          <DeleteReleaseWithIssues
            visible={deleteReleaseVisible}
            versionDelInfo={versionDelInfo}
            onCancel={() => {
              this.setState({
                versionDelInfo: {},
              });
              ReleaseStore.setDeleteReleaseVisible(false);
            }}
            refresh={this.refresh.bind(this, pagination)}
            changeState={(k, v) => {
              this.setState({
                [k]: v,
              });
            }}
          />
          {editRelease ? (
            <EditRelease
              visible={editRelease}
              onCancel={() => {
                this.setState({
                  editRelease: false,
                  selectItem: {},
                });
              }}
              refresh={this.refresh.bind(this, pagination)}
              data={selectItem}
            />
          ) : ''}
          <PublicRelease
            visible={publicVersion}
            release={release}
            onCancel={() => {
              this.setState({
                publicVersion: false,
              });
            }}
            refresh={this.refresh.bind(this, pagination)}
          />
        </Content>
      </Page>
    );
  }
}

export default withRouter(ReleaseHome);
