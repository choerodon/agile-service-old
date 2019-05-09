/* eslint-disable no-restricted-globals */
/* eslint-disable react/no-access-state-in-setstate */
/* eslint-disable react/destructuring-assignment */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import FeatureTable from '../FeatureTable';
import SearchArea from '../SearchArea';
import ExportIssue from '../ExportIssue';
import { getFeatures } from '../../../../../api/FeatureApi';
import FeatureStore from '../../../../../stores/program/Feature/FeatureStore';
import { getMyFilters } from '../../../../../api/NewIssueApi';

const getDefaultSearchDTO = () => ({
  advancedSearchArgs: {
    featureTypeList: [],
    assigneeIds: [],
    statusList: [],
    issueTypeList: [],
    reporterList: [],
    epicList: [],
  },
  // content: '',
  otherArgs: {
    piList: [],
  },
  searchArgs: {
  //   assignee: '',
  //   component: '',
  //   epic: '',
    issueNum: '',
    //   sprint: '',
    summary: '',
  //   version: '',
  },
});
const filterConvert = (filters, originSearchDTO = getDefaultSearchDTO()) => {
  const searchDTO = { ...originSearchDTO };
  const setArgs = (field, filter) => {
    Object.assign(searchDTO[field], filter);
  };
  // 循环遍历 Object 中的每个键
  Object.keys(filters).forEach((key) => {
    // 根据对应的 key 传入对应的 mode
    switch (key) {
      case 'assigneeIds':
      case 'statusList':
      case 'issueTypeList':
      case 'featureTypeList':      
      case 'reporterList':
      case 'epicList':
        setArgs('advancedSearchArgs', { [key]: filters[key] });
        break;
      case 'piList':
        setArgs('otherArgs', { [key]: filters[key] });
        break;
      default:
        setArgs('searchArgs', {
          [key]: filters[key][0],
        });
        break;
    }
  });
  return searchDTO;
};
@observer
class QueryMode extends Component {
  state = {
    loading: false,
    pagination: {
      current: 1,
      total: 0,
      pageSize: 10,
    },
    tableShowColumns: [
      'issueNum',
      'featureType',
      'summary',
      'statusList',
      'epicList',
      'piList',
      'lastUpdateDate',
    ],
    searchDTO: getDefaultSearchDTO(),
    issues: [],
    myFilters: [],
    createMyFilterVisible: false,
    filterManageVisible: false,
    filterManageLoading: false,
    selectedFilter: undefined,
    exportIssueVisible: false,
  }

  filters = {}

  componentDidMount() {
    this.refresh();
  }

  componentWillUnmount() {
    FeatureStore.setClickIssueDetail({});
  }

  refresh = () => {
    this.loadFeatures();
    this.loadMyFilters();
  }

  loadMyFilters = () => {
    const { filterManageVisible } = this.state;
    if (filterManageVisible) {
      this.setState({
        filterManageLoading: true,
      });
    }
    getMyFilters().then((myFilters) => {
      this.setState({
        myFilters,
        filterManageLoading: false,
      });
    });
  }

  exportFeatures = () => {
    this.setState({
      exportIssueVisible: true,
    });
  }

  handleCancelExport=() => {
    this.setState({
      exportIssueVisible: false,
    });
  }

  handleCreateMyFilterCancel = () => {
    this.setState({
      createMyFilterVisible: false,
    });
  }

  handleSaveClick = () => {
    this.setState({
      createMyFilterVisible: true,
    });
  }

  handleCreateMyFilter = () => {
    this.setState({
      createMyFilterVisible: false,
    });
    this.loadMyFilters();
  }

  handleSelectMyFilter = (filterId) => {
    const { myFilters } = this.state;
    const targetFilter = myFilters.find(filter => filter.filterId === filterId);

    if (targetFilter) {
      const { filterJson } = targetFilter;
      this.setState({
        searchDTO: JSON.parse(filterJson) || {},
        selectedFilter: filterId,
      },
      this.loadFeatures);
    } else {
      this.setState({
        searchDTO: getDefaultSearchDTO(),
        selectedFilter: filterId,
      },
      this.loadFeatures);
    }
  }

  handleManageClick = () => {
    this.setState({
      filterManageVisible: true,
    });
  }

  handleManageClose = () => {
    this.setState({
      filterManageVisible: false,
    });
  }

  // eslint-disable-next-line react/destructuring-assignment
  loadFeatures = ({ pagination = this.state.pagination, searchDTO = this.state.searchDTO } = {}) => {
    const { current, pageSize } = pagination;
    this.setState({
      loading: true,
    });
    getFeatures({
      page: current - 1,
      size: pageSize,
    }, searchDTO).then((res) => {
      if (res.failed) {
        return;
      }
      const {
        content: issues, size, number, totalElements,
      } = res;
      this.setState({
        pagination: {
          current: number + 1,
          total: totalElements,
          pageSize: size,
        },
        issues,
        loading: false,
      });
    });
  }


  handleAdvancedSearchChange = (type, values) => {
    let searchDTO;
    // 对类型单独处理，因为特性的区分是用business,enabler , 但史诗是用id
    if (type === 'issueTypeList') {
      const issueTypeList = values.filter(value => !isNaN(value));
      const featureTypeList = values.filter(value => isNaN(value));      
      searchDTO = filterConvert({ issueTypeList, featureTypeList }, this.state.searchDTO);
    } else {
      searchDTO = filterConvert({ [type]: values }, this.state.searchDTO);
    }
   
    this.loadFeatures({ searchDTO });
    this.setState({
      searchDTO,
      selectedFilter: undefined,
    });
  }

  handleTableChange = (pagination, filters) => {
    this.filters = { ...this.filters, ...filters };  
    const searchDTO = filterConvert(filters, this.state.searchDTO); 
    this.loadFeatures({ searchDTO, pagination });
    this.setState({
      searchDTO,
      selectedFilter: undefined,
    });
  }


  handleRow = record => ({
    onClick: (event) => { this.handleTableRowClick(record); },
  })

  handleTableRowClick = (record) => {
    FeatureStore.setClickIssueDetail(record);
  }

  handleCreateFeature = () => {
    this.refresh();
  }


  handleClearFilter = () => {   
    this.filters = {};
    this.loadFeatures({ searchDTO: getDefaultSearchDTO() });
    this.setState({
      searchDTO: getDefaultSearchDTO(),
      selectedFilter: undefined,
    });
  }

  handleMyFilterUpdate=() => {
    this.loadMyFilters();
  }

  handleMyFilterDelete=() => {
    this.loadMyFilters();
  }

  // table列选择时触发
  handleColumnFilterChange = (info) => {
    const { selectedKeys } = info;
    this.setState({
      tableShowColumns: selectedKeys,
    });
  }

  render() {
    const {
      pagination, loading, issues, searchDTO, myFilters, selectedFilter, 
      createMyFilterVisible, filterManageVisible, filterManageLoading,
      tableShowColumns, exportIssueVisible,
    } = this.state;
    return (
      <div style={{ flex: 1, height: '100%', overflow: 'auto' }}>
        <SearchArea
          createMyFilterVisible={createMyFilterVisible}
          filterManageVisible={filterManageVisible}
          filterManageLoading={filterManageLoading}
          myFilters={myFilters}
          searchDTO={searchDTO}
          selectedFilter={selectedFilter}
          onAdvancedSearchChange={this.handleAdvancedSearchChange}
          onSelectMyFilter={this.handleSelectMyFilter}
          onClearFilter={this.handleClearFilter}
          onCancel={this.handleCreateMyFilterCancel}
          onCreate={this.handleCreateMyFilter}
          onSaveClick={this.handleSaveClick}
          onManageClick={this.handleManageClick}
          onClose={this.handleManageClose}
          onUpdate={this.handleMyFilterUpdate}
          onDelete={this.handleMyFilterDelete}          
        />
        <FeatureTable
          loading={loading}
          dataSource={issues.map(issue => (FeatureStore.getClickIssueDetail.issueId === issue.issueId ? { ...issue, selected: true } : { ...issue, selected: false }))}
          pagination={pagination}
          searchDTO={searchDTO}
          tableShowColumns={tableShowColumns}
          onColumnFilterChange={this.handleColumnFilterChange}
          onChange={this.handleTableChange}
          onRow={this.handleRow}
          onCreateFeature={this.handleCreateFeature}
        />
        <ExportIssue 
          visible={exportIssueVisible}
          searchDTO={searchDTO}
          tableShowColumns={tableShowColumns}
          onCancel={this.handleCancelExport}
        />
      </div>
    );
  }
}

QueryMode.propTypes = {

};

export default QueryMode;
