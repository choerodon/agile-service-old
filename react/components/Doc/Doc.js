import React, { Component } from 'react';
import { stores, axios } from '@choerodon/boot';
import {
  Modal,
  Table,
  message,
} from 'choerodon-ui';
import './Doc.scss';
import { getOrganizationId } from '../../common/utils';

const { AppState } = stores;
const { Sidebar } = Modal;

class Doc extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      data: [],
      selectedRows: [],
      selectedRowKeys: props.checkIds || [],
      loading: false,
    };
  }

  componentDidMount() {
    this.loadDoc();
  }

  loadDoc = async () => {
    const menu = AppState.currentMenuType;
    const { id: proId } = menu;
    this.setState({
      loading: true,
    });
    const newData = await axios.get(`/knowledge/v1/projects/${proId}/work_space?organizationId=${getOrganizationId()}`);
    if (newData && !newData.failed) {
      this.setState({
        data: newData,
        loading: false,
      });
    } else {
      this.setState({
        data: [],
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

  handleCreateDoc = () => {
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
        if (checkIds.indexOf(row.id) === -1) {
          postData.push({
            issueId,
            wikiName: row.name,
            wikiUrl: '',
            projectId: proId,
            spaceId: row.id,
          });
        }
      });
      axios.post(`/agile/v1/projects/${proId}/knowledge_relation`, postData).then(() => {
        this.setState({
          createLoading: false,
        });
        onOk();
      }).catch(() => {
        Choerodon.prompt('关联文档失败');
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
        className="c7n-agile-doc"
        title="添加文档"
        visible={visible || false}
        onOk={this.handleCreateDoc}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <div>
          <p>{`你当前项目为"${name}"，文档的内容所属为当前项目。`}</p>
          <Table
            dataSource={data}
            columns={this.getColumn()}
            rowSelection={rowSelection}
            // onExpand={this.onExpand}
            rowKey={record => record.id}
            pagination={false}
            loading={loading}
          />
        </div>
      </Sidebar>
    );
  }
}
export default Doc;
