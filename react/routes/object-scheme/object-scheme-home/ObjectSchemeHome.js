import React, { useState, useEffect, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Table, Spin, Tooltip, Button,
} from 'choerodon-ui';
import {
  Page, Header, Content,
} from '@choerodon/boot';
import Store from '../stores';

function ObjectSchemeHome(props) {
  const context = useContext(Store);
  // const { AppState, pageStore } = props.value;
  const { AppState, objectSchemeStore } = context;
  const objectScheme = objectSchemeStore.getObjectScheme;
  const [loading, setLoading] = useState(false);
  const [filterName, setFilterName] = useState('');
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: undefined,
  });

  const initCurrentMenuType = () => {
    objectSchemeStore.initCurrentMenuType(AppState.currentMenuType);
  };

  const loadObjectScheme = (page, size) => {
    setLoading(true);
    objectSchemeStore.loadObjectScheme(page, size, { param: filterName }).then(() => {
      setLoading(false);
    });
  };

  const showDetail = (item) => {
    const { history } = context;
    const urlParams = AppState.currentMenuType;
    history.push(`/issue/objectScheme/detail/${item.schemeCode}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

  const getColume = () => [
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
    loadObjectScheme(pagination.current, pagination.pageSize);
  }, []);


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
              columns={getColume()}
              dataSource={objectScheme}
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

export default observer(ObjectSchemeHome);
