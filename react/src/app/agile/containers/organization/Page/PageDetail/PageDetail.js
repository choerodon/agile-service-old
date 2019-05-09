import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Spin, Checkbox, Button, Icon,
} from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import SortTable from '../Components/SortTable';

const { AppState } = stores;

@observer
class PageDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  componentDidMount() {
    this.initCurrentMenuType();
    this.loadPageDetail();
  }

  componentWillUnmount() {
    const { PageStore } = this.props;
    PageStore.setPage([]);
  }

  initCurrentMenuType = () => {
    const { PageStore } = this.props;
    PageStore.initCurrentMenuType(AppState.currentMenuType);
  };

  loadPageDetail = () => {
    const { match } = this.props;
    const { PageStore } = this.props;
    this.setState({
      loading: true,
    });
    PageStore.loadPageDetail(match.params.code).then(() => {
      this.setState({
        loading: false,
      });
    });
  };

  getColume = () => [
    {
      title: '字段',
      dataIndex: 'fieldName',
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
      title: '显示',
      dataIndex: 'display',
      width: '15',
      render: (display, record) => (
        <div>
          <Checkbox
            checked={record.display}
            disabled={record.system}
            onChange={() => this.onDisplayChange(record)}
          />
        </div>
      ),
    },
  ];

  handleDrag = (result, postData) => {
    const { PageStore, match } = this.props;
    const page = PageStore.getPageDetail;
    const { name = '' } = page;
    PageStore.updateFieldOrder(match.params.code, postData).then((data) => {
      if (data) {
        PageStore.setPageDetail({
          name,
          content: result.map((item) => {
            if (data.fieldId === item.fieldId) {
              return {
                ...item,
                objectVersionNumber: data.objectVersionNumber,
                display: data.display,
              };
            } else {
              return item;
            }
          }),
        });
      }
    });
  };

  onDisplayChange = (item) => {
    if (item.system) {
      return;
    }
    const { PageStore, match } = this.props;
    const field = {
      display: !item.display,
      objectVersionNumber: item.objectVersionNumber,
    };
    PageStore.updateField(item.fieldId, match.params.code, field);
  };

  render() {
    const {
      loading,
    } = this.state;

    const menu = AppState.currentMenuType;
    const {
      type, id, organizationId, name: orgName,
    } = menu;
    const { PageStore } = this.props;
    const page = PageStore.getPageDetail;
    const { name = '', content = [] } = page;

    return (
      <Page
        className="c7n-page-detail"
      >
        <Header
          title="编辑页面"
          backPath={`/agile/page?type=${type}&id=${id}&name=${encodeURIComponent(orgName)}&organizationId=${organizationId}`}
        >
          <Button funcType="flat" onClick={this.loadPageDetail}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title={name}
          description="你可以通过上下拖动进行字段排序。"
        >
          <Spin spinning={loading}>
            <SortTable
              pagination={false}
              columns={this.getColume()}
              dataSource={content.slice()}
              filterBar={false}
              handleDrag={this.handleDrag}
            />
          </Spin>
        </Content>
      </Page>
    );
  }
}

export default PageDetail;
