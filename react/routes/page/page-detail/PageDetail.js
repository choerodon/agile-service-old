import React, {
  Component, useState, useEffect, useContext,
} from 'react';
// import { observer } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import {
  Spin, Checkbox, Button, Icon,
} from 'choerodon-ui';
import {
  Page, Header, Content,
} from '@choerodon/boot';
import SortTable from '../Components/SortTable';
import Store from '../stores';
/**
 * 函数组件
 * 使用HOOKS相关内容
 */
function PageDetail(props) {
  const context = useContext(Store);
  const { pageStore, AppState, match } = context;
  const [loading, setLoading] = useState(false);

  const initCurrentMenuType = () => {
    pageStore.initCurrentMenuType(AppState.currentMenuType);
  };

  const loadPageDetail = () => {
    setLoading(true);
    pageStore.loadPageDetail(match.params.code).then(() => {
      setLoading(false);
    });
  };

  // componentDidMount
  useEffect(() => {
    initCurrentMenuType();
    loadPageDetail();
  }, []);

  // componentWillUnmount
  useEffect(() => () => {
    pageStore.setPage([]);
  }, []);


  const handleDrag = (result, postData) => {
    const page = pageStore.getPageDetail;
    const { name = '' } = page;
    pageStore.updateFieldOrder(match.params.code, postData).then((data) => {
      if (data) {
        pageStore.setPageDetail({
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

  const onDisplayChange = (item) => {
    if (item.system && item.required) {
      return;
    }
    const field = {
      display: !item.display,
      objectVersionNumber: item.objectVersionNumber,
    };
    pageStore.updateField(item.fieldId, match.params.code, field);
  };
  const getColume = () => [
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
            disabled={record.system && record.required}
            onChange={() => onDisplayChange(record)}
          />
        </div>
      ),
    },
  ];
  const menu = AppState.currentMenuType;
  const {
    type, id, organizationId, name: orgName,
  } = menu;
  const page = pageStore.getPageDetail;
  const { name = '', content = [] } = page;
  return (
    <Page
      className="c7n-page-detail"
    >
      <Header
        title="编辑页面"
        backPath={`/agile/page?type=${type}&id=${id}&name=${encodeURIComponent(orgName)}&organizationId=${organizationId}`}
      >
        <Button funcType="flat" onClick={loadPageDetail}>
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
            columns={getColume()}
            dataSource={content.slice()}
            filterBar={false}
            handleDrag={handleDrag}
          />
        </Spin>
      </Content>
    </Page>
  );
}

export default observer(PageDetail);
