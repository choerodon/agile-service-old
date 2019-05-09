import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Table, Spin, Tooltip, Button,
} from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import './ObjectSchemeHome.scss';

const { AppState } = stores;

@observer
class ObjectSchemeHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      pagination: {
        current: 1,
        pageSize: 10,
        total: undefined,
      },
      filterName: '',
    };
  }

  componentDidMount() {
    const { pagination } = this.state;
    this.initCurrentMenuType();
    this.loadObjectScheme(pagination.current - 1, pagination.pageSize);
  }

  initCurrentMenuType = () => {
    const { ObjectSchemeStore } = this.props;
    ObjectSchemeStore.initCurrentMenuType(AppState.currentMenuType);
  };

  loadObjectScheme = (page, size) => {
    const { ObjectSchemeStore } = this.props;
    const { filterName } = this.state;
    this.setState({
      loading: true,
    });
    ObjectSchemeStore.loadObjectScheme(page, size, { param: filterName }).then(() => {
      this.setState({
        loading: false,
      });
    });
  };

  showDetail = (item) => {
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    history.push(`/agile/objectScheme/detail/${item.schemeCode}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

  getColume = () => [
    {
      title: '方案名称',
      dataIndex: 'name',
      width: '45%',
    },
    {
      title: '方案类型',
      dataIndex: 'schemeCodeName',
      width: '45%',
    },
    {
      align: 'right',
      width: 104,
      key: 'action',
      render: (componentId, record) => (
        <div>
          <Tooltip placement="top" title="详情">
            <Button shape="circle" size="small" onClick={this.showDetail.bind(this, record)}>
              <i className="icon icon-mode_edit" />
            </Button>
          </Tooltip>
        </div>
      ),
    },
  ];

  handleTableChange = (pagination, filters, sorter, barFilters) => {
    this.setState({
      pagination,
      filterName: filters,
    });
  };

  render() {
    const {
      loading, pagination,
    } = this.state;

    const { ObjectSchemeStore } = this.props;
    const objectScheme = ObjectSchemeStore.getObjectScheme;

    return (
      <Page
        className="c7n-object-scheme"
      >
        <Header title="字段定义" />
        <Content
          description="如果你需要添加自定义字段，你可以选择一个类型方案，在创建字段的时候选择对应的问题类型进行创建。"
        >
          <div>
            <Spin spinning={loading}>
              <Table
                pagination={pagination}
                rowKey={record => record.id}
                columns={this.getColume()}
                dataSource={objectScheme}
                filterBarPlaceholder="过滤表"
                scroll={{ x: true }}
                onChange={this.handleTableChange}
              />
            </Spin>
          </div>
        </Content>
      </Page>
    );
  }
}

export default ObjectSchemeHome;
