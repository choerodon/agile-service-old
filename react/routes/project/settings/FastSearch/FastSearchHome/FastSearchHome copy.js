import React, { Component, useState, useEffect } from 'react';
import { observer } from 'mobx-react';
import {
  Spin, Popover, Tooltip, Modal,
} from 'choerodon-ui';
import { DataSet, Table, Button, Icon } from 'choerodon-ui/pro';
import {
  Page, Header, Content, stores, axios,
} from '@choerodon/boot';
import Filter from './Component/Filter';
import EditFilter from './Component/EditFilter';
import DeleteFilter from './Component/DeleteFilter';
import SortTable from './Component/SortTable';
import './FastSearchHome.scss';

const { AppState } = stores;
const { Column } = Table;

function Search(props) {
  const [filters, setFilters] = useState([]);
  const [createFileterShow, setCreateFileterShow] = useState(false);
  const [currentFilterId, setCurrentFilterId] = useState(undefined);
  const [filter, setFilter] = useState({});
  const [loading, setLoading] = useState(false);
  const [barFilters, setBarFilters] = useState([]);
  const [filterName, setFilterName] = useState('');
  const [deleteFilterShow, setDeleteFilterShow] = useState(false);
  const [editFilterShow, setEditFilterShow] = useState(false);

  const column = [
    {
      title: '名称',
      dataIndex: 'name',
      // width: '20%',
      render: name => (
        <div style={{ width: '100%', overflow: 'hidden' }}>
          <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={name}>
            <p
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                marginBottom: 0,
              }}
            >
              {name}
            </p>
          </Tooltip>
        </div>
      ),
      filters: [],
    },
    {
      title: '筛选器',
      dataIndex: 'expressQuery',
      // width: '50%',
      render: expressQuery => (
        <div style={{
          maxWidth: '422px',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
        }}
        >
          <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={this.transformOperation(expressQuery)}>
            <span
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                marginBottom: 0,
              }}
            >
              {/* {expressQuery} */}
              {this.transformOperation(expressQuery)}
            </span>
          </Tooltip>
        </div>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      // width: '25%',
      render: description => (
        <div style={{
          maxWidth: '288px',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
        }}
        >
          <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={description.split('+++')[0]}>
            <span
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                marginBottom: 0,
              }}
            >
              {description.split('+++')[0] || ''}
            </span>
          </Tooltip>
        </div>
      ),
    },
    {
      title: '',
      dataIndex: 'filterId',
      width: '96',
      align: 'right',
      render: (filterId, record) => (
        <div>
          <Popover
            placement="bottom"
            mouseEnterDelay={0.5}
            content={(
              <div>
                <span>详情</span>
              </div>
            )}
          >
            {/* <Button shape="circle" onClick={this.showFilter.bind(this, record)}> */}
            <Icon type="mode_edit" onClick={this.showFilter.bind(this, record)} />
            {/* </Button> */}
          </Popover>
          <Popover
            placement="bottom"
            mouseEnterDelay={0.5}
            content={(
              <div>
                <span>删除</span>
              </div>
            )}
          >
            {/* <Button shape="circle" onClick={this.clickDeleteFilter.bind(this, record)}> */}
            <Icon type="delete_forever" onClick={this.clickDeleteFilter.bind(this, record)} />
            {/* </Button> */}
          </Popover>
        </div>
      ),
    },
  ];

  const ds = new DataSet({
    primaryKey: 'userid',
    name: 'user',
    // autoQuery: true,
    pageSize: 5,
    queryFields: [
      { name: 'name', type: 'string', label: '姓名' },
      { name: 'age', type: 'number', label: '年龄' },
      { name: 'code', type: 'object', label: '代码描述', lovCode: 'LOV_CODE' },
      { name: 'sex', type: 'string', label: '性别', lookupCode: 'HR.EMPLOYEE_GENDER' },
      { name: 'date.startDate', type: 'date', label: '开始日期' },
      { name: 'sexMultiple', type: 'string', label: '性别（多值）', lookupCode: 'HR.EMPLOYEE_GENDER', multiple: true },
    ],
    fields: [
      {
        title: '名称',
        dataIndex: 'name',
        // width: '20%',
        render: name => (
          <div style={{ width: '100%', overflow: 'hidden' }}>
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={name}>
              <p
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  marginBottom: 0,
                }}
              >
                {name}
              </p>
            </Tooltip>
          </div>
        ),
        filters: [],
      },
      {
        title: '筛选器',
        dataIndex: 'expressQuery',
        // width: '50%',
        render: expressQuery => (
          <div style={{
            maxWidth: '422px',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
          >
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={this.transformOperation(expressQuery)}>
              <span
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  marginBottom: 0,
                }}
              >
                {/* {expressQuery} */}
                {this.transformOperation(expressQuery)}
              </span>
            </Tooltip>
          </div>
        ),
      },
      {
        title: '描述',
        dataIndex: 'description',
        // width: '25%',
        render: description => (
          <div style={{
            maxWidth: '288px',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
          >
            <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={description.split('+++')[0]}>
              <span
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  marginBottom: 0,
                }}
              >
                {description.split('+++')[0] || ''}
              </span>
            </Tooltip>
          </div>
        ),
      },
      {
        title: '',
        dataIndex: 'filterId',
        width: '96',
        align: 'right',
        render: (filterId, record) => (
          <div>
            <Popover
              placement="bottom"
              mouseEnterDelay={0.5}
              content={(
                <div>
                  <span>详情</span>
                </div>
              )}
            >
              {/* <Button shape="circle" onClick={this.showFilter.bind(this, record)}> */}
              <Icon type="mode_edit" onClick={this.showFilter.bind(this, record)} />
              {/* </Button> */}
            </Popover>
            <Popover
              placement="bottom"
              mouseEnterDelay={0.5}
              content={(
                <div>
                  <span>删除</span>
                </div>
              )}
            >
              {/* <Button shape="circle" onClick={this.clickDeleteFilter.bind(this, record)}> */}
              <Icon type="delete_forever" onClick={this.clickDeleteFilter.bind(this, record)} />
              {/* </Button> */}
            </Popover>
          </div>
        ),
      },
    ],
    events: {
      query: ({ params }) => console.log('filterbar query parameter', params),
    },
  });

  useEffect((page = 0, size = 10) => {
    setLoading(true);
    axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
      contents: barFilters,
      filterName,
    })
      .then((res) => {
        setFilters(res);
        setLoading(false);
      })
      .catch((error) => { });
  }, []);

  const transformOperation = (str) => {
    // 注意该对象key的顺序
    const OPERATION = {
      '!=': '不等于',
      'not in': '不包含',
      in: '包含',
      'is not': '不是',
      is: '是',
      '<=': '小于或等于',
      '<': '小于',
      '>=': '大于或等于',
      '>': '大于',
      '=': '等于',
      OR: '或',
      AND: '与',
    };

    let transformKey = str;
    Object.keys(OPERATION).forEach((v) => {
      transformKey = transformKey.replace(new RegExp(` ${v} `, 'g'), ` ${OPERATION[v]} `);
    });
    return transformKey;
  };

  const handleDrag = (data, postData) => {
    setFilters(data);
    axios
      .put(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/drag`, postData)
      .then(() => {
        axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
          contents: [
          ],
          filterName: '',
        }).then((res) => {
          setFilters(res);
        });
      })
      .catch(() => {
        axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
          contents: [
          ],
          filterName: '',
        }).then((res) => {
          setFilters(res);
        });
      });
  };

  const loadFilters = (page = 0, size = 10) => {
    setLoading(true);
    axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
      contents: barFilters,
      filterName,
    })
      .then((res) => {
        setFilters(res);
        setLoading(false);
      })
      .catch((error) => {});
  };

  const handleTableChange = (pagination, filters, sorter, barFilters) => {
    setFilterName(filters.name && filters.name[0]);
    setBarFilters(barFilters);
    loadFilters();
  };

  const clickDeleteFilter = (record) => {
    setFilter(record);
    setDeleteFilterShow(true);
  };

  // const deleteComponent = () => {
  //   loadComponents();
  // };

  const showFilter = (record) => {
    setEditFilterShow(true);
    setCurrentFilterId(record.filterId);
  };

  return (
    <Page className="c7n-fast-search">
      {/* <Header title="快速搜索">
        <Button funcType="flat" onClick={() => this.setState({ createFileterShow: true })}>
          <Icon type="playlist_add icon" />
          <span>创建快速搜索</span>
        </Button>
        <Button funcType="flat" onClick={() => this.loadFilters()}>
          <Icon type="refresh icon" />
          <span>刷新</span>
        </Button>
      </Header> */}
      <Content
      // title="快速搜索"
      // description="通过定义快速搜索，可以在待办事项和活跃冲刺的快速搜索工具栏生效，帮助您更好的筛选过滤问题面板。"
      // link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/quick-search/"
      >
        <div>
          <Spin spinning={loading}>
            <SortTable
              onChange={handleTableChange}
              handleDrag={handleDrag}
              rowKey={record => record.filterId}
              columns={column}
              dataSource={filters}
              scroll={{ x: true }}
            />
          </Spin>
          {createFileterShow ? (
            <Filter
              onOk={() => {
                setCreateFileterShow(false);
                loadFilters();
              }}
              onCancel={() => setCreateFileterShow(false)}
            />
          ) : null}
          {editFilterShow ? (
            <EditFilter
              filterId={currentFilterId}
              onOk={() => {
                setEditFilterShow(false);
                loadFilters();
              }}
              onCancel={() => setEditFilterShow(false)}
            />
          ) : null}
          {deleteFilterShow ? (
            <DeleteFilter
              filter={filter}
              onOk={() => {
                setDeleteFilterShow(false);
                loadFilters();
              }}
              onCancel={() => setDeleteFilterShow(false)}
            />
          ) : null}
        </div>
      </Content>
    </Page>
  );
}

export default Search;
