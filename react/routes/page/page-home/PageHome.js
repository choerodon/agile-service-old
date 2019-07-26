import React, { useState, useEffect, useContext } from 'react';
// import { observer } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import {
  Table, Spin, Tooltip, Button,
} from 'choerodon-ui';
import { Page, Header, Content } from '@choerodon/boot';
import Store from '../stores';

function PageHome(props) {
  const context = useContext(Store);
  // const { AppState, pageStore } = props.value;
  const { AppState, pageStore } = context;
  const page = pageStore.getPage;
  const [loading, setLoading] = useState(false);
  const [filterName, setFilterName] = useState('');
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: undefined,
  });

  const initCurrentMenuType = () => {
    pageStore.initCurrentMenuType(AppState.currentMenuType);
  };

  const loadPage = (newPage, size) => {
    setLoading(true);

    pageStore.loadPage(newPage, size, { param: filterName }).then(() => {
      setLoading(false);
    });
  };


  const showDetail = (item) => {
    const { history } = context;
    const urlParams = AppState.currentMenuType;
    history.push(`/issue/page/detail/${item.pageCode}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

  const getColume = () => [
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
            <Button shape="circle" size="small" onClick={showDetail.bind(this, record)}>
              <i className="icon icon-mode_edit" />
            </Button>
          </Tooltip>
        </div>
      ),
    },
  ];

  const handleTableChange = (newPagination, filters, sorter, barFilters) => {
    setPagination(newPagination);
    setFilterName(filters);
  };
  
  useEffect(() => {
    initCurrentMenuType();
    loadPage(pagination.current, pagination.pageSize);
  }, []);

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
              columns={getColume()}
              dataSource={page}
              filterBarPlaceholder="过滤表"
              scroll={{ x: true }}
              onChange={handleTableChange}
            />
          </Spin>
        </div>
      </Content>
    </Page>
  );
}

export default observer(PageHome);
