import React, { memo, Component } from 'react';
import {
  Table, Tooltip,
} from 'choerodon-ui';
import FiltersProvider from '../../../../../components/FiltersProvider';
import {
  IssueNum, Summary, StatusName, LastUpdateTime, Sprint, Epic,
} from '../../../../project/Issue/IssueTable/IssueTableComponent';
import TypeTag from '../../../../../components/TypeTag';
import { QuickCreateFeatureWithProvider } from '../../../../../components/QuickCreateFeature';
import './FeatureTable.scss';

const shouldColumnShow = (tableShowColumns, column) => {
  if (column.title === '' || !column.key) {
    return true;
  }
  return tableShowColumns.length === 0 ? true : tableShowColumns.includes(column.key);
};

const manageVisible = (tableShowColumns, columns) => columns.map(column => (shouldColumnShow(tableShowColumns, column) ? { ...column, hidden: false } : { ...column, hidden: true }));

const getColumns = (filters, getFilteredValue) => ([
  {
    title: '编号',
    dataIndex: 'issueNum',
    key: 'issueNum',
    className: 'issueId',
    sorterId: 'issueId',
    width: 100,  
    // filters: [],
    // filteredValue: getFilteredValue('issueNum'),
    render: text => <IssueNum text={text} />,
  },
  {
    title: '类型',
    key: 'featureType',
    dataIndex: 'featureType',
    className: 'featureType',
    sorterId: 'featureType',
    width: 100,
    render: (featureType, record) => {
      const { typeCode, issueTypeDTO } = record;
      return (
        <div style={{ lineHeight: 0 }}>
          {typeCode === 'feature' ? (
            <TypeTag
              showName
              data={{
                colour: featureType === 'business' ? '#29B6F6' : '#FFCA28',
                icon: 'agile-feature',
                name: featureType === 'business' ? '特性' : '使能',
              }}
            />
          ) : (
            <TypeTag
              showName
              data={issueTypeDTO}
            />
          )}
        </div>
      );
    },
  },
  {
    title: '概要',
    dataIndex: 'summary',
    className: 'summary',
    key: 'summary',
    width: 240, 
    // filters: [],
    // filteredValue: getFilteredValue('summary'),
    render: text => <Summary text={text} />,
  },
  {
    title: '状态',
    key: 'statusList',
    className: 'status',
    sorterId: 'statusList',    
    // filters: filters.issueStatus,
    // filterMultiple: true,
    width: 134,
    render: record => <StatusName record={record} />,
  },
  {
    title: '史诗',
    key: 'epicList',
    className: 'epic',
    sorterId: 'epic',
    filters: filters.epic,
    filterMultiple: true,
    filteredValue: getFilteredValue('epicList'),
    width: 134,
    render: (epic, record) => <Epic color={record.epicColor} name={record.epicName} />,
  },
  {
    title: 'PI',
    key: 'piList',
    className: 'piNameDTOList',
    dataIndex: 'piNameDTOList',    
    filters: filters.pi,
    filteredValue: getFilteredValue('piList'),
    filterMultiple: true,
    width: 134,
    render: piNameDTOList => (
      <Tooltip placement="top" title={piNameDTOList.length ? piNameDTOList.map(o => `${o.code}-${o.name};`) : ''}>
        {Sprint({ objArray: piNameDTOList, name: piNameDTOList.length > 0 && `${piNameDTOList[0].code}-${piNameDTOList[0].name}` })}
      </Tooltip>
    ),
  },
  {
    title: '最后更新时间',
    dataIndex: 'lastUpdateDate',
    className: 'lastUpdateDate',
    key: 'lastUpdateDate',
    sorterId: 'lastUpdateDate',
    width: 134,    
    render: text => <LastUpdateTime text={text} />,
  },
  {
    title: '创建时间',
    dataIndex: 'creationDate',
    className: 'creationDate',
    key: 'creationDate',
    sorterId: 'creationDate',
    width: 134,   
    hidden: true,
    render: text => <LastUpdateTime text={text} />,
  },
  {
    title: '故事点',
    dataIndex: 'storyPoints',
    className: 'storyPoints',
    key: 'storyPoints',
    sorterId: 'storyPoints',
    width: 134,
    hidden: true,
  },
  {
    title: '特性价值',
    dataIndex: 'benfitHypothesis',
    className: 'benfitHypothesis',
    key: 'benfitHypothesis',
    sorterId: 'benfitHypothesis',
    width: 134,   
    render: benfitHypothesis => <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{benfitHypothesis}</div>,
    hidden: true, 
  },
  {
    title: '验收标准',
    dataIndex: 'acceptanceCritera',
    className: 'acceptanceCritera',
    key: 'acceptanceCritera',
    sorterId: 'acceptanceCritera',
    width: 134,   
    render: acceptanceCritera => <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{acceptanceCritera}</div>,
    hidden: true, 
  },
]);

class FeatureTable extends Component {
  getFilteredValue=(key) => {
    const { searchDTO } = this.props;
    let field = '';
    switch (key) {
      case 'assigneeIds':
      case 'statusList':
      case 'issueTypeList':
      case 'reporterList':
      case 'epicList':
        field = 'advancedSearchArgs';
        break;
      case 'piList':
        field = 'otherArgs';        
        break;
      default:
        field = 'searchArgs';
        break;
    }
    const value = searchDTO[field][key];
    if (value && value.length > 0) {
      return value instanceof Array ? value : [value];
    } else {
      return [];
    }
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      tableShowColumns,
      onColumnFilterChange,
      onChange,
      onRow,
      onCreateFeature, 
    } = this.props; 
    return (
      <FiltersProvider fields={['epic', 'pi', 'priority']}>
        {
        filters => (
          <div className="c7nagile-FeatureTable">
            <Table
              loading={loading}
              columns={manageVisible(tableShowColumns, getColumns(filters, this.getFilteredValue))}
              pagination={pagination}
              dataSource={dataSource}
              onChange={onChange}
              onColumnFilterChange={onColumnFilterChange}
              onRow={onRow}
              footer={() => (<QuickCreateFeatureWithProvider onCreate={onCreateFeature} />)}
              scroll={{
                x: true,
              }}
              rowClassName={record => (record.selected ? 'c7nagile-FeatureTable-row-select' : '')}
            />
          </div>
        )}
      </FiltersProvider>
    );
  }
}
FeatureTable.propTypes = {

};

export default memo(FeatureTable);
