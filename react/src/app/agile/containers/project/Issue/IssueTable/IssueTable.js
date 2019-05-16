import React, { Component } from 'react';
import { Table, Tooltip } from 'choerodon-ui';
import { observer } from 'mobx-react';
import { trace } from 'mobx';
import { findIndex, map } from 'lodash';
import IssueStore from '../../../../stores/project/sprint/IssueStore';
import IssueFilterControler from '../IssueFilterControler';
import {
  IssueNum, TypeCode, Summary, StatusName, Priority, Assignee, LastUpdateTime, Sprint, Epic,
} from './IssueTableComponent';
import EmptyBlock from '../../../../components/EmptyBlock';
import pic from '../../../../assets/image/emptyIssue.svg';
import QuickCreateIssue from '../QuickCreateIssue/QuickCraeteIssue';

let previousClick = false;
@observer
class IssueTable extends Component {
  constructor(props) {
    super(props);
    this.filterControler = new IssueFilterControler();
  }

  componentDidMount() {    
    this.getAssigneeDistributed();   
    this.setSelectRow(); 
  }
  

  componentWillUnmount() {
    IssueStore.setClickedRow({
      selectedIssue: {},
      expand: false,
    });
  }
  
  setSelectRow=() => {
    const selectedIssue = IssueStore.getSelectedIssue;   
    if (selectedIssue && selectedIssue.issueId) {
      const issues = IssueStore.getIssues;
      const index = findIndex(issues, { issueId: selectedIssue.issueId });
      if (index > -1) {
        const target = document.getElementsByClassName('ant-table-row')[index];
        if (target) {
          target.click();
        }
      }
    }
  }

  /**
   * @param filters => Object => Table 传入的 filter
   * @param setArgs => function => 设置参数时需要调用的闭包函数
   */
  filterConvert = (filters, setArgs) => {
    // 循环遍历 Object 中的每个键
    Object.keys(filters).forEach((key) => {
      // 根据对应的 key 传入对应的 mode
      switch (key) {
        case 'issueNum':
          setArgs('searchArgs', {
            [key]: filters[key][0],
          });
          // 地址栏有id和名称时，会同时发送id和名称，在筛选变动时，清掉id，防止影响后续筛选
          setArgs('otherArgs', { issueIds: [] });
          break;
        // case 'priorityId':
        // case 'issueTypeId':
        //   setArgs('advArgs', filters);
        //   break;
        case 'label':
        case 'component':
        case 'version':
        case 'epic':
        case 'sprint':
          // eslint-disable-next-line no-case-declarations
          const { fieldSelected, fieldInput } = this.convertSelectOrInput(filters);
          setArgs('otherArgs', fieldSelected);
          setArgs('searchArgs', fieldInput);
          break;
        default:
          setArgs('searchArgs', {
            [key]: filters[key][0],
          });
          break;
      }
    });
  };

  convertSelectOrInput = (filters) => {
    const fieldSelected = {}; // {sprint: [1,2]}
    const fieldInput = {}; // {sprint: 'shjh'}
    Object.keys(filters).forEach((key) => {
      fieldSelected[key] = []; // 选中
      fieldInput[key] = ''; // 输入
      filters[key].forEach((fieldValue) => {
        try {
          const selected = JSON.parse(fieldValue);
          if (selected.id) {
            fieldSelected[key].push(selected.id);
          } else {
            throw new Error('没有id');
          }
        } catch (e) {
          if (fieldValue === '未分配') {
            fieldSelected[key].push('0');
          } else {
            fieldInput[key] = fieldValue;
          }

          // fieldInput[key] = fieldValue;
        }
      });
    });
    return { fieldSelected, fieldInput };
  }

  /**
   *
   * @param barFilters => Array => Table Filter 生成的 barFilter，模糊搜索和 filter 受控会使用到
   * @param setArgs => function => 设置参数时会调用到的闭包函数
   */
  barFilterConvert = (barFilters, setArgs) => {
    IssueStore.setBarFilter(barFilters);
    // 复制 Array
    const temp = barFilters.slice();
    // 如果 paramFilter 在当前 barFilter 中能找到，则不调用模糊搜索
    if (barFilters.indexOf(IssueStore.getParamFilter) !== -1) {
      temp.shift();
    }
    setArgs('contents', {
      contents: temp,
    });
  };

  /**
   * Table 默认的 filter 处理函数
   * @param pagination => Object => 分页对象
   * @param filters => Object => Table 筛选对象
   * @param sorter => Object => 排序对象
   * @param barFilters => Object => filter 受控对象
   */
  handleFilterChange = (pagination, filters, sorter, barFilters) => {
    const setArgs = this.filterControler.initArgsFilter();
    this.filterConvert(filters, setArgs);
    this.barFilterConvert(barFilters, setArgs);
    IssueStore.judgeConditionWithFilter();
    IssueStore.judgeFilterConditionIsEmpty();
    IssueStore.setLoading(true);
    // 更新函数
    this.filterControler.update(
      pagination.current - 1,
      pagination.pageSize,
      sorter,
      barFilters,
    ).then(
      (res) => {
        IssueStore.updateFiltedIssue({
          current: res.number + 1,
          pageSize: res.size,
          total: res.totalElements,
        }, res.content, barFilters);
      },
    );
  };

  // table列选择时触发
  handleColumnFilterChange = (info) => {
    const { selectedKeys } = info;
    IssueStore.setTableShowColumns(selectedKeys);
    // console.log(info);
  }

  /**
   * 其他页面链入问题管理页面经办人为未分配
   *
   * @memberof IssueTable
   */
  getAssigneeDistributed = () => {
    const userFilter = IssueStore.getFilterMap.get('userFilter');
    const paramFilter = IssueStore.getFilterMap.get('paramFilter');
    const userFilterOrParamFilter = Object.keys(paramFilter).length ? paramFilter : userFilter;
    let fieldValue;
    if (Object.keys(userFilterOrParamFilter).length >= 0 && userFilterOrParamFilter.otherArgs) {
      fieldValue = userFilterOrParamFilter.otherArgs.assigneeId || [];
      if (fieldValue.length === 1 && fieldValue[0] === '0') {
        // IssueStore.setBarFilter(['经办人未分配']);
        IssueStore.setSelectedAssignee(['none']);
      }
    }
  }

  getFieldFilteredValue = (field) => {
    const userFilter = IssueStore.getFilterMap.get('userFilter');
    const paramFilter = IssueStore.getFilterMap.get('paramFilter');
    const userFilterOrParamFilter = Object.keys(paramFilter).length ? paramFilter : userFilter;
    const userFilterAndParamFilterIsEmptyObj = Object.keys(userFilterOrParamFilter).length === 0;
    let fieldValue;
    if (Object.keys(userFilterOrParamFilter).length >= 0 && userFilterOrParamFilter.otherArgs) {
      fieldValue = userFilterOrParamFilter.otherArgs[field] || [];
    }

    let fieldFilteredValue = [];
    if (!userFilterAndParamFilterIsEmptyObj) {
      const searchArgsField = userFilterOrParamFilter.searchArgs[field];
      if (['sprint', 'version', 'component', 'epic', 'label'].find(item => item === field)) {
        if (fieldValue.length === 0 && !searchArgsField) {
          fieldFilteredValue = [];
        } else if (fieldValue.length === 1 && fieldValue[0] === '0') { // otherArgs[filed]为['0']
          fieldFilteredValue = ['未分配'];
        } else {
          const FieldColumnFilter = IssueStore.getColumnFilter.get(field);
          const otherFieldFilteredValue = FieldColumnFilter && map(FieldColumnFilter.filter(item => fieldValue.find(id => JSON.parse(item.value).id === id.toString())), 'value').concat(fieldValue.find(value => value === '0') ? ['未分配'] : []);
          fieldFilteredValue = !searchArgsField ? otherFieldFilteredValue : [...otherFieldFilteredValue, searchArgsField];
        }
      } else {
        fieldFilteredValue = !searchArgsField ? [] : [searchArgsField];
      }
    } else {
      fieldFilteredValue = [];
    }

    return fieldFilteredValue;
  }

  onHideIssue = () => {
    if (previousClick) {
      previousClick.style.background = '';
      previousClick.style.borderLeft = '';
    }
  };

  handleRowClick = (record, e) => {
    const editFilterInfo = IssueStore.getEditFilterInfo;
    // 点击时设置当前点击元素 style
    if (previousClick) {
      // 如果上一次点击过，就清空 previousClick 中保存的 style
      previousClick.style.background = '';
      previousClick.style.borderLeft = '';
    } else {
      e.currentTarget.scrollIntoView(true);
    }
    e.currentTarget.style.background = 'rgba(140, 158, 255, 0.08)';
    e.currentTarget.style.borderLeft = '3px solid #3f51b5';
    // 将这次的点击元素设置为 previousClick 供下次使用
    previousClick = e.currentTarget;
    IssueStore.setClickedRow({
      selectedIssue: record,
      expand: true,
    });
    IssueStore.setFilterListVisible(false);
    IssueStore.setEditFilterInfo(map(editFilterInfo, item => Object.assign(item, { isEditing: false })));
  }

  shouldColumnShow = (column) => {
    if (column.title === '' || !column.key) {
      return true;
    }
    const filteredColumns = IssueStore.tableShowColumns.slice();
    // console.log(filteredColumns);
    return filteredColumns.length === 0 ? true : filteredColumns.includes(column.key);
  }

  manageVisible = columns => columns.map(column => (this.shouldColumnShow(column) ? { ...column, hidden: false } : { ...column, hidden: true }))

  render() {
    // Table 列配置
    const issueNumFieldValue = this.getFieldFilteredValue('issueNum');
    const summaryFilterValue = this.getFieldFilteredValue('summary');
    const reporterFilterValue = this.getFieldFilteredValue('reporter');
    const sprintFilterValue = this.getFieldFilteredValue('sprint');
    const versionFilterValue = this.getFieldFilteredValue('version');
    const componentFilterValue = this.getFieldFilteredValue('component');
    const epicFilterValue = this.getFieldFilteredValue('epic');
    const labelFilterValue = this.getFieldFilteredValue('label');

    const columns = this.manageVisible([
      {
        title: '问题编号',
        dataIndex: 'issueNum',
        key: 'issueNum',
        className: 'issueId',
        sorterId: 'issueId',
        width: 100,
        sorter: true,
        filters: [],
        filteredValue: issueNumFieldValue,
        // fixed: true,
        render: text => <IssueNum text={text} />,
      },
      {
        title: '问题类型',
        key: 'issueTypeId',
        className: 'issueType',
        sorterId: 'issueTypeId',
        width: 100,
        sorter: true,
        // filters: IssueStore.getColumnFilter.get('typeId'),
        // filterMultiple: true,
        // fixed: true,
        render: (text, record) => (
          <div style={{ lineHeight: 0 }}>
            <TypeCode record={record} />
          </div>
        ),
      },
      {
        title: '概要',
        dataIndex: 'summary',
        className: 'summary',
        key: 'summary',
        width: 240,
        filters: [],
        filteredValue: summaryFilterValue,
        // fixed: true,
        render: text => <Summary text={text} />,
      },
      {
        title: '状态',
        key: 'statusId',
        className: 'status',
        sorterId: 'statusId',
        width: 100,
        sorter: true,
        // filters: IssueStore.getColumnFilter.get('statusId'),
        // filterMultiple: true,
        render: (text, record) => <StatusName record={record} />,
      },
      {
        title: '优先级',
        key: 'priorityId',
        className: 'priority',
        sorterId: 'priorityId',
        sorter: true,
        width: 100,
        // filters: IssueStore.getColumnFilter.get('priorityId'),
        // filterMultiple: true,
        render: (text, record) => <Priority record={record} />,
      },
      {
        title: '经办人',
        dataIndex: 'assigneeName',
        className: 'assignee',
        width: 135,
        key: 'assignee',
        sorterId: 'assigneeId',
        sorter: true,
        // filteredValue: assigneeFilterValue,
        render: (text, record) => (
          <Assignee
            loginName={record.assigneeLoginName}
            realName={record.assigneeRealName}
            id={record.assigneeId}
            img={record.assigneeImageUrl}
          />
        ),
      },
      {
        title: '冲刺',
        key: 'sprint',
        className: 'sprint',
        width: 128,
        filters: IssueStore.getColumnFilter.get('sprint'),
        // filteredValue: ['1315'],
        // filteredValue: ['{"id":"1044"}', '{"id":"1016"}'],
        filteredValue: sprintFilterValue,
        filterMultiple: true,
        render: record => (
          <Sprint
            objArray={record.issueSprintDTOS}
            name={
              record.issueSprintDTOS && record.issueSprintDTOS.length
                ? record.issueSprintDTOS[0].sprintName
                : null
            }
          />
        ),
      },
      {
        title: '最后更新时间',
        dataIndex: 'lastUpdateDate',
        className: 'lastUpdateDate',
        key: 'lastUpdateDate',
        sorterId: 'lastUpdateDate',
        width: 134,
        sorter: true,
        render: text => <LastUpdateTime text={text} />,
      },
      {
        title: '报告人',
        dataIndex: 'reporterName',
        key: 'reporter',
        className: 'reporter',
        width: 135,
        filters: [],
        filteredValue: reporterFilterValue,
        hidden: true,
        render: (text, record) => (
          <Assignee
            loginName={record.reporterLoginName}
            realName={record.reporterRealName}
            id={record.reporterId}
            img={record.reporterImageUrl}
          />
        ),
      },
      {
        title: '版本',
        key: 'version',
        className: 'version',
        width: 128,
        filters: IssueStore.getColumnFilter.get('version'),
        filterMultiple: true,
        filteredValue: versionFilterValue,
        hidden: true,
        render: record => (
          record.versionIssueRelDTOS && record.versionIssueRelDTOS.length > 0 && (
            <Tooltip title={() => record.versionIssueRelDTOS.map(item => item.name).join(',')}>
              <div>
                <Sprint
                  objArray={record.versionIssueRelDTOS}
                  name={
                    record.versionIssueRelDTOS && record.versionIssueRelDTOS.length
                      ? record.versionIssueRelDTOS[0].name
                      : null}
                />
              </div>
            </Tooltip>
          )
        ),
      },
      {
        title: '模块',
        key: 'component',
        className: 'component',
        width: 128,
        filters: IssueStore.getColumnFilter.get('component'),
        filterMultiple: true,
        filteredValue: componentFilterValue,
        hidden: true,
        render: record => (
          record.issueComponentBriefDTOS && record.issueComponentBriefDTOS.length > 0 && (
            <Tooltip title={() => record.issueComponentBriefDTOS.map(item => item.name).join(',')}>
              <div>
                <Sprint
                  objArray={record.issueComponentBriefDTOS}
                  name={
                    record.issueComponentBriefDTOS && record.issueComponentBriefDTOS.length
                      ? record.issueComponentBriefDTOS[0].name
                      : null
                  }
                />
              </div>
            </Tooltip>
          )
        ),
      },
      {
        title: '史诗',
        dataIndex: 'epicName',
        key: 'epic',
        className: 'epic',
        width: 128,
        filters: IssueStore.getColumnFilter.get('epic'),
        filterMultiple: true,
        filteredValue: epicFilterValue,
        hidden: true,
        render: (text, record) => <Epic name={record.epicName} color={record.epicColor} />,
      },
      {
        title: '标签',
        key: 'label',
        className: 'label',
        width: 128,
        filters: IssueStore.getColumnFilter.get('label'),
        filteredValue: labelFilterValue,
        filterMultiple: true,
        hidden: true,
        render: record => record.labelIssueRelDTOS && record.labelIssueRelDTOS.length > 0 && (
          <Tooltip title={() => record.labelIssueRelDTOS.map(item => item.labelName).join(',')}>
            <div>
              <Sprint
                objArray={record.labelIssueRelDTOS}
                name={
                  record.labelIssueRelDTOS && record.labelIssueRelDTOS.length
                    ? record.labelIssueRelDTOS[0].labelName
                    : null
                }
              />
            </div>
          </Tooltip>
        ),
      },
      {
        title: '故事点',
        key: 'storyPoints',
        className: 'storyPoints',
        width: 50,
        hidden: true,
        render: (text, record) => (<span>{record.storyPoints ? record.storyPoints : '-'}</span>),
      },
    ]);
    // 表格列配置
    return (
      <Table
        rowKey={record => record.issueId}
        {...this.props}
        columns={columns}
        dataSource={IssueStore.getIssues}
        // scroll={{ x: 1600 }}
        empty={(
          <EmptyBlock
            style={{ marginTop: 60, marginBottom: 60 }}
            border
            pic={pic}
            title="根据当前搜索条件没有查询到问题"
            des="尝试修改您的过滤选项或者在下面创建新的问题"
          />
        )}
        filterBarPlaceholder="过滤表"
        noFilter
        filters={IssueStore.getBarFilter}
        loading={IssueStore.getLoading}
        pagination={IssueStore.getPagination}
        footer={() => (<QuickCreateIssue />)}
        onChange={this.handleFilterChange}
        className="c7n-Issue-table"
        scroll={{ x: true }}
        onColumnFilterChange={this.handleColumnFilterChange}
        onRow={record => ({
          onClick: this.handleRowClick.bind(this, record),
          onBlur: (e) => {
            // 点击隐藏详情时无法触发 onClick，所以需要利用 onBlur 触发
            // e.currentTarget.style.background = '';
            // e.currentTarget.style.borderLeft = '';
          },
        })
        }
      />
    );
  }
}

export default IssueTable;
