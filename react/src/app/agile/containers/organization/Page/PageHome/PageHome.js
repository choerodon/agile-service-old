import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Table, Spin, Tooltip, Button,
} from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import './PageHome.scss';

const { AppState } = stores;

@observer
class PageHome extends Component {
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
    this.initCurrentMenuType();
    const { pagination } = this.state;
    this.loadPage(pagination.current - 1, pagination.pageSize);
  }

  initCurrentMenuType = () => {
    const { PageStore } = this.props;
    PageStore.initCurrentMenuType(AppState.currentMenuType);
  };

  loadPage = (page, size) => {
    const { PageStore } = this.props;
    const { filterName } = this.state;
    this.setState({
      loading: true,
    });
    PageStore.loadPage(page, size, { param: filterName }).then(() => {
      this.setState({
        loading: false,
      });
    });
  };

  showDetail = (item) => {
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    history.push(`/agile/page/detail/${item.pageCode}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

  getColume = () => [
    {
      title: '页面名称',
      dataIndex: 'name',
      width: '45%',
    },
    {
      title: '关联字段方案',
      dataIndex: 'schemeName',
      width: '45%',
    },
    {
      title: '',
      dataIndex: 'id',
      width: '10%',
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

    const { PageStore } = this.props;
    const page = PageStore.getPage;

    return (
      <Page
        className="c7n-page"
      >
        <Header title="页面" />
        <Content
          description="你可以在页面中选择你需要显示的字段，并且上下拖动进行字段排序。"
        >
          <div>
            <Spin spinning={loading}>
              <Table
                pagination={pagination}
                rowKey={record => record.id}
                columns={this.getColume()}
                dataSource={page}
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

export default PageHome;
