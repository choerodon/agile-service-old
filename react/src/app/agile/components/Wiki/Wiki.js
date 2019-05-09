import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import {
  Modal,
  Table,
  message,
} from 'choerodon-ui';
import './Wiki.scss';

const { AppState } = stores;
const { Sidebar } = Modal;

class Wiki extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      data: [],
      expendIds: [],
      idAddress: {},
      selectedRows: [],
      selectedRowKeys: props.checkIds || [],
      loading: false,
    };
  }

  componentDidMount() {
    this.loadWiki();
  }

  loadWiki = async (id) => {
    const menu = AppState.currentMenuType;
    const { loginName } = AppState.userInfo;
    const { name, id: proId, organizationId } = menu;
    const { data, idAddress } = this.state;
    this.setState({
      loading: true,
    });
    const postData = {
      organizationId,
      projectName: name,
      username: loginName,
    };
    if (id) {
      postData.menuId = id;
    }
    const dataSource = [];
    const newData = await axios.post(`/wiki/v1/projects/${proId}/space/menus`, postData);
    if (newData && !newData.failed) {
      let idIndex = idAddress;
      newData.forEach((item, index) => {
        idIndex = {
          ...idIndex,
          [item.id]: idAddress[id] ? [...idAddress[id], index] : [index],
        };
        dataSource.push({
          id: item.id,
          children: item.children ? [] : false,
          href: item.a_attr ? item.a_attr.href : '',
          name: item.text,
        });
      });
      if (id) {
        let goalData = data;
        if (idIndex[id] && idIndex[id].length) {
          idIndex[id].forEach((i, index) => {
            if (index === 0) {
              goalData = goalData[i];
            } else {
              goalData = goalData.children[i];
            }
          });
        }
        goalData.children = dataSource;
        this.setState({
          data,
          idAddress: idIndex,
          loading: false,
        });
      } else {
        this.setState({
          data: dataSource,
          idAddress: idIndex,
          loading: false,
        });
      }
    } else {
      this.setState({
        loading: false,
      });
    }
  };

  getColumn = () => [
    {
      title: '文档名称',
      dataIndex: 'name',
      key: 'name',
    },
  ];

  onSelectChange = (selectedRowKeys, selectedRows) => {
    this.setState({
      selectedRows,
      selectedRowKeys,
    });
  };

  getCheckboxProps = (record) => {
    const { checkIds } = this.props;
    return ({
      disabled: checkIds.indexOf(record.href) !== -1,
      name: record.name,
    });
  };

  onExpand = (expand, data) => {
    const { expendIds } = this.state;
    if (expand && expendIds.indexOf(data.id) === -1) {
      this.setState({
        expendIds: [...expendIds, data.id],
      });
      this.loadWiki(data.id);
    }
  };

  handleCreateWiki = () => {
    const menu = AppState.currentMenuType;
    const { id: proId } = menu;
    const { selectedRows } = this.state;
    const { issueId, onOk, checkIds } = this.props;
    this.setState({
      createLoading: true,
    });
    if (selectedRows && selectedRows.length) {
      const postData = [];
      selectedRows.forEach((row) => {
        if (checkIds.indexOf(row.href) === -1) {
          postData.push({
            issueId,
            wikiName: row.name,
            wikiUrl: row.href,
            projectId: proId,
          });
        }
      });
      axios.post(`/agile/v1/projects/${proId}/wiki_relation`, postData).then(() => {
        this.setState({
          createLoading: false,
        });
        onOk();
      }).catch(() => {
        Choerodon.prompt('关联wiki文档失败');
        this.setState({
          createLoading: false,
        });
      });
    } else {
      this.setState({
        createLoading: false,
      });
    }
  };

  render() {
    const {
      onCancel,
      visible,
    } = this.props;
    const {
      createLoading,
      selectedRowKeys,
      data,
      loading,
    } = this.state;

    const { name } = AppState.currentMenuType;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
      getCheckboxProps: this.getCheckboxProps,
    };

    return (
      <Sidebar
        className="c7n-wikiDoc"
        title="添加wiki文档"
        visible={visible || false}
        onOk={this.handleCreateWiki}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <div>
          <p>{`你当前项目为"${name}"，wiki文档的内容所属为当前项目。`}</p>
          <Table
            dataSource={data}
            columns={this.getColumn()}
            rowSelection={rowSelection}
            onExpand={this.onExpand}
            rowKey={record => record.href}
            pagination={false}
            loading={loading}
          />
        </div>
      </Sidebar>
    );
  }
}
export default Wiki;
