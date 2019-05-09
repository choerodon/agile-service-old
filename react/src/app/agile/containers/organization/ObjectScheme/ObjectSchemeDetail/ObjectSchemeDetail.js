import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Table, Spin, Tooltip, Button, Checkbox, Modal, Icon,
} from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import './ObjectSchemeDetail.scss';
import CreateField from '../Components/CreateField';

const { confirm } = Modal;
const { AppState } = stores;

@observer
class ObjectSchemeDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      addVisible: false,
    };
  }

  componentDidMount() {
    this.initCurrentMenuType();
    this.loadScheme();
  }

  componentWillUnmount() {
    const { ObjectSchemeStore } = this.props;
    ObjectSchemeStore.setSchemeDetail({
      content: [],
    });
  }

  initCurrentMenuType = () => {
    const { ObjectSchemeStore } = this.props;
    ObjectSchemeStore.initCurrentMenuType(AppState.currentMenuType);
  };

  loadScheme = () => {
    const { match } = this.props;
    const { ObjectSchemeStore } = this.props;
    this.setState({
      loading: true,
    });
    ObjectSchemeStore.loadSchemeDetail(match.params.code).then(() => {
      this.setState({
        loading: false,
      });
    });
  };

  getColume = () => [
    {
      title: '字段',
      dataIndex: 'name',
      width: '25%',
    },
    {
      title: '显示范围',
      dataIndex: 'contextName',
      width: '25%',
    },
    {
      title: '字段类型',
      dataIndex: 'fieldTypeName',
      width: '25%',
    },
    {
      title: '必填项',
      dataIndex: 'required',
      width: '15',
      render: (required, record) => (
        <div>
          <Checkbox
            checked={record.required}
            disabled={record.system || (AppState.currentMenuType.type === 'project' && !record.projectId)}
            onChange={() => this.onRequiredChange(record)}
          />
        </div>
      ),
    },
    {
      title: '',
      dataIndex: 'id',
      width: '10%',
      render: (componentId, record) => (
        <div>
          {record.system || (AppState.currentMenuType.type === 'project' && !record.projectId)
            ? ''
            : (
              <React.Fragment>
                <Tooltip placement="top" title="详情">
                  <Button shape="circle" size="small" onClick={() => this.editField(record)}>
                    <i className="icon icon-mode_edit" />
                  </Button>
                </Tooltip>
                <Tooltip placement="top" title="删除">
                  <Button shape="circle" size="small" onClick={() => this.handleDelete(record)}>
                    <i className="icon icon-delete" />
                  </Button>
                </Tooltip>
              </React.Fragment>
            )
          }
        </div>
      ),
    },
  ];

  onRequiredChange = (item) => {
    const { ObjectSchemeStore } = this.props;
    if (item.system) {
      return;
    }
    const field = {
      required: !item.required,
      objectVersionNumber: item.objectVersionNumber,
    };
    ObjectSchemeStore.updateField(item.id, field);
  };

  editField =(item) => {
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    history.push(`/agile/objectScheme/field/${item.id}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

  handleDelete = (item) => {
    if (item.system) {
      return;
    }
    const that = this;
    confirm({
      title: '删除字段',
      content: (
        <div>
          <p style={{ marginBottom: 10 }}>
            <div style={{ marginBottom: 10 }}>{`删除自定义字段：${item.name}`}</div>
            <div>注意：将会从所有使用的问题中删除此字段，并且字段数据会清空。你确定要删除此字段吗？</div>
          </p>
        </div>
      ),
      onOk() {
        that.deleteField(item);
      },
      onCancel() {},
      okText: '删除',
      okType: 'danger',
      width: 512,
    });
  };

  deleteField = (item) => {
    const { ObjectSchemeStore } = this.props;
    ObjectSchemeStore.deleteField(item.id).then(() => {
      this.loadScheme();
    });
  };

  onClose = () => {
    this.setState({
      addVisible: false,
    });
  };

  onOk = () => {
    this.loadScheme();
    this.setState({
      addVisible: false,
    });
  };

  render() {
    const {
      loading, addVisible,
    } = this.state;
    const menu = AppState.currentMenuType;
    const {
      type, id, organizationId, name: orgName,
    } = menu;

    const { ObjectSchemeStore, match } = this.props;
    const scheme = ObjectSchemeStore.getSchemeDetail;
    const { name = '', content = [] } = scheme;

    return (
      <Page
        className="c7n-object-scheme"
      >
        <Header
          title="编辑方案"
          backPath={`/agile/objectScheme?type=${type}&id=${id}&name=${encodeURIComponent(orgName)}&organizationId=${organizationId}`}
        >
          <Button
            className="leftBtn"
            funcType="flat"
            onClick={() => {
              this.setState({
                addVisible: true,
              });
            }}
          >
            <Icon type="playlist_add icon" />
            <span>添加字段</span>
          </Button>
          <Button funcType="flat" onClick={this.loadScheme}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title={name}
        >
          <Spin spinning={loading}>
            <Table
              pagination={false}
              rowKey={record => record.id}
              columns={this.getColume()}
              dataSource={content}
              filterBar={false}
              scroll={{ x: true }}
              onChange={this.handleTableChange}
            />
          </Spin>
          {addVisible
            ? (
              <CreateField
                store={ObjectSchemeStore}
                onClose={this.onClose}
                onOk={this.onOk}
                visible={addVisible}
                schemeCode={match.params.code}
              />
            )
            : null
          }
        </Content>
      </Page>
    );
  }
}

export default ObjectSchemeDetail;
