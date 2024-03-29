import React, {
  Fragment, useState, useEffect, useContext, 
} from 'react';
import { observer } from 'mobx-react-lite';
import {
  Table, Spin, Tooltip, Button, Checkbox, Modal, Icon, Tag,
} from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import CreateField from '../components/create-field';
import TypeTag from '../../../components/TypeTag';
import Store from '../stores';

const { confirm } = Modal;
const showIcons = {
  史诗: {
    icon: 'agile_epic',
    colour: '#743be7',
    typeCode: 'issue_epic',
    name: '史诗',
  },
  故事: {
    icon: 'agile_story',
    colour: '#00bfa5',
    typeCode: 'story',
    name: '故事',
  },
  特性: {
    icon: 'agile-feature',
    colour: '#29B6F6',
    typeCode: 'feature',
    name: '特性',
  },
  缺陷: {
    icon: 'agile_fault',
    colour: '#f44336',
    typeCode: 'bug',
    name: '缺陷',
  },
  任务: {
    icon: 'agile_task',
    colour: '#4d90fe',
    typeCode: 'task',
    name: '任务',
  },
  子任务: {
    icon: 'agile_subtask',
    colour: '#4d90fe',
    typeCode: 'sub_task',
    name: '子任务',
  },
};


function ObjectSchemeDetail(props) {
  const context = useContext(Store);
  // const { AppState, pageStore } = props.value;
  const { AppState, objectSchemeStore } = context;
  const [loading, setLoading] = useState(false);
  const [addVisible, setAddVisible] = useState(false);


  const initCurrentMenuType = () => {
    objectSchemeStore.initCurrentMenuType(AppState.currentMenuType);
  };

  const loadScheme = () => {
    const { match } = context;

    setLoading(true);
    objectSchemeStore.loadSchemeDetail(match.params.code).then(() => {
      setLoading(false);
    });
  };
  const onRequiredChange = (item) => {
    if (item.system) {
      return;
    }
    if (!item.required && !item.defaultValue) {
      Choerodon.prompt('必填字段请设置默认值！');
    }
    const field = {
      required: !item.required,
      objectVersionNumber: item.objectVersionNumber,
    };
    objectSchemeStore.updateField(item.id, field);
  };

  const editField = (item) => {
    const { history } = context;
    const urlParams = AppState.currentMenuType;
    history.push(`/agile/objectScheme/field/${item.id}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };
  const deleteField = (item) => {
    objectSchemeStore.deleteField(item.id).then(() => {
      loadScheme();
    });
  };

  const handleDelete = (item) => {
    if (item.system) {
      return;
    }
    // const that = this;
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
        deleteField(item);
      },
      onCancel() { },
      okText: '删除',
      okType: 'danger',
      width: 512,
    });
  };


  const onClose = () => {
    setAddVisible(false);
  };

  const onOk = () => {
    loadScheme();

    setAddVisible(false);
  };
  const getColume = () => [
    {
      title: '字段',
      dataIndex: 'name',
      // width: '15%',
    },
    {
      title: '显示范围',
      dataIndex: 'contextName',
      // width: '25%',
      render: contextName => (
        <Fragment>
          {contextName.split(',').map(name => (
            showIcons[name] ? <div><TypeTag data={showIcons[name]} showName /></div> : name
          ))}
        </Fragment>
      ),
    },
    {
      title: '字段来源',
      render: ({ projectId, system }) => (
        // eslint-disable-next-line no-nested-ternary
        system
          ? <Tag style={{ color: 'rgba(0,0,0,0.65)', borderColor: '#d9d9d9', background: '#fafafa' }}>系统</Tag>
          : projectId
            ? <Tag color="orange">项目</Tag>
            : <Tag color="geekblue">组织</Tag>
      ),
    },
    {
      title: '字段类型',
      dataIndex: 'fieldTypeName',
      // width: '25%',
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
            onChange={() => onRequiredChange(record)}
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
                  <Button shape="circle" size="small" onClick={() => editField(record)}>
                    <i className="icon icon-mode_edit" />
                  </Button>
                </Tooltip>
                <Tooltip placement="top" title="删除">
                  <Button shape="circle" size="small" onClick={() => handleDelete(record)}>
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


  useEffect(() => {
    initCurrentMenuType();
    loadScheme();
  }, []);

  useEffect(() => () => {
    objectSchemeStore.setSchemeDetail({
      content: [],
    });
  }, []);

  const render = () => {
    const menu = AppState.currentMenuType;
    const {
      type, id, organizationId, name: orgName,
    } = menu;

    const { match } = context;
    const scheme = objectSchemeStore.getSchemeDetail;
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
              setAddVisible(true);
            }}
          >
            <Icon type="playlist_add icon" />
            <span>添加字段</span>
          </Button>
          <Button funcType="flat" onClick={loadScheme}>
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
              columns={getColume()}
              dataSource={content}
              filterBar={false}
              scroll={{ x: true }}
            // onChange={this.handleTableChange}
            />
          </Spin>
          {addVisible
            ? (
              <CreateField
                store={objectSchemeStore}
                onClose={onClose}
                onOk={onOk}
                visible={addVisible}
                schemeCode={match.params.code}
              />
            )
            : null
          }
        </Content>
      </Page>
    );
  };
  return render();
}

export default observer(ObjectSchemeDetail);
