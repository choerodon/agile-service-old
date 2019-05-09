import React, { PureComponent } from 'react';

import {
  Select, DatePicker, Button, Modal, Tooltip,
} from 'choerodon-ui';
import { stores, axios } from 'choerodon-front-boot';
import moment from 'moment';
import { find, difference, unionBy } from 'lodash';
import SelectFocusLoad from '../../../../../components/SelectFocusLoad';
import './AdvancedSearch.scss';

const { Option } = Select;
const { AppState } = stores;
const { RangePicker } = DatePicker;
const SelectStyle = {
  width: 120,
};

class AdvancedSearch extends PureComponent {
  saveList = (type, idField, List) => {
    this[type] = unionBy(this[type], List, idField);
  }

  checkFilterEmpty = () => {
    const { searchDTO } = this.props;
    const { advancedSearchArgs, otherArgs, searchArgs } = searchDTO;
    const searches = { ...advancedSearchArgs, ...otherArgs, ...searchArgs };
    return !Object.keys(searches).some((key) => {
      const item = searches[key];
      return item && item.length !== 0;
    });
  }

  getValueFields = (searchDTO) => {
    const { advancedSearchArgs, otherArgs, searchArgs } = searchDTO;
    const searches = { ...advancedSearchArgs, ...otherArgs, ...searchArgs };
    const keys = Object.keys(searches).filter((key) => {
      const item = searches[key];
      return item && item.length !== 0;
    });
    const obj = {};
    keys.forEach((key) => {
      obj[key] = searches[key];
    });
    return obj;
  }

  filterSame = (obj, obj2) => {
    const keys1 = Object.keys(obj);
    const keys2 = Object.keys(obj2);

    if (keys1.length !== keys2.length || difference(keys1, keys2).length > 0) {
      return false;
    } else {
      for (let i = 0; i < keys1.length; i += 1) {
        if (typeof obj[keys1[i]] === 'string' && obj[keys1[i]] !== obj2[keys1[i]]) {
          return false;
        } else if (difference(obj[keys1[i]], obj2[keys1[i]]).length > 0) {
          return false;
        }
      }
    }
    return true;
  }

  findSameFilter = () => {
    const { myFilters, searchDTO } = this.props;
    const hasValueFields = this.getValueFields(searchDTO);
    return myFilters.find((filter) => {
      const { filterJson } = filter;
      const filterHasValueFields = this.getValueFields(JSON.parse(filterJson));
      // console.log(filterHasValueFields, hasValueFields);    
      return this.filterSame(filterHasValueFields, hasValueFields);
    });
  }

  handleSelectChange = (type, values) => {
    const { onAdvancedSearchChange } = this.props;
    onAdvancedSearchChange(type, values);
    // switch
  }

  // 将searchDTO提取出选择框的值
  searchConvert = () => {
    const { searchDTO } = this.props;
    const { advancedSearchArgs } = searchDTO;
    const {
      assigneeIds, issueTypeId, priorityId, statusId,
    } = advancedSearchArgs;
    return advancedSearchArgs;
  }

  renderPlaceHolder=(type, props, ommittedValues) => {
    const values = [];
    for (const value of ommittedValues) {
      const target = find(this[type], { [props[0]]: value })[props[1]];
      if (target) {
        values.push(target);
      }
    }
    return values.join(', ');
  }

  render() {
    const {
      myFilters, onSaveClick, onManageClick, onSelectMyFilter, onClearFilter, selectedFilter,
    } = this.props;
    const SelectValue = this.searchConvert();
    const isFilterEmpty = this.checkFilterEmpty();
    const filterExist = isFilterEmpty ? undefined : this.findSameFilter();
    return (
      <div className="c7nagile-AdvancedSearch">
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Select
            allowClear
            className="SelectTheme primary"
            style={SelectStyle}
            dropdownMatchSelectWidth={false}
            placeholder="我的筛选"
            filter
            optionFilterProp="children"
            onChange={onSelectMyFilter}
            value={filterExist ? filterExist.filterId : selectedFilter}
            getPopupContainer={triggerNode => triggerNode.parentNode}
          >
            {
              myFilters.length && myFilters.map(item => (
                <Option key={item.filterId} value={item.filterId} title={item.name}>{item.name}</Option>
              ))
            }
          </Select>

          <SelectFocusLoad
            type="issue_type_program_feature_epic"
            className="SelectTheme"
            mode="multiple"
            allowClear
            filter={false}
            loadWhenMount
            style={SelectStyle}
            dropdownMatchSelectWidth={false}
            placeholder="问题类型"
            saveList={this.saveList.bind(this, 'issueTypeList', 'id')}
            maxTagCount={0}
            maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'issueTypeList', ['id', 'name'])}
            onChange={this.handleSelectChange.bind(this, 'issueTypeList')}
            value={SelectValue.issueTypeList.concat(SelectValue.featureTypeList)}
            getPopupContainer={triggerNode => triggerNode.parentNode}
          />
          <SelectFocusLoad
            className="SelectTheme"
            mode="multiple"
            type="status_program"
            allowClear
            filter={false}
            loadWhenMount
            style={SelectStyle}
            dropdownMatchSelectWidth={false}
            placeholder="状态"
            saveList={this.saveList.bind(this, 'statusList', 'id')}
            maxTagCount={0}
            optionLabelProp="name"
            maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'statusList', ['id', 'name'])}
            onChange={this.handleSelectChange.bind(this, 'statusList')}
            value={SelectValue.statusList}
            render={status => <Option value={status.id}>{status.name}</Option>}
            getPopupContainer={triggerNode => triggerNode.parentNode}
          />

          {/* <SelectFocusLoad
            className="SelectTheme"
            type="user"
            loadWhenMount
            mode="multiple"
            allowClear
            style={SelectStyle}
            dropdownMatchSelectWidth={false}
            placeholder="经办人"
            saveList={this.saveList.bind(this, 'assigneeList', 'id')}
            maxTagCount={0}
            maxTagPlaceholder={ommittedValues => `${ommittedValues.map(value => find(this.assigneeList, { id: value }).realName).join(', ')}`}
            onChange={this.handleSelectChange.bind(this, 'assigneeIds')}
            value={SelectValue.assigneeIds}
            getPopupContainer={triggerNode => triggerNode.parentNode}
            render={user => <Option value={user.id}>{user.realName}</Option>}
          /> */}

          {/* {
              (moment(createStartDate).format('YYYY-MM-DD') === moment(projectInfo.creationDate).format('YYYY-MM-DD') || createStartDate === '') && (moment(createEndDate).format('YYYY-MM-DD') === moment().format('YYYY-MM-DD') || createEndDate === '') ? (
                <div className="c7n-createRange">
                  <RangePicker
                    format="YYYY-MM-DD hh:mm:ss"
                    defaultPickerValue={[moment().subtract(1, 'months'), moment()]}
                    disabledDate={current => current && (current > moment().endOf('day') || current < moment(projectInfo.creationDate).startOf('day'))}
                    allowClear
                    onChange={this.handleCreateDateRangeChange}
                    placeholder={['创建时间', '']}
                  />
                </div>
              ) : (
                <Tooltip title={`创建问题时间范围为${moment(createStartDate).format('YYYY-MM-DD')} ~ ${moment(createEndDate).format('YYYY-MM-DD')}`}>
                  <div className="c7n-createRange">
                    <RangePicker
                      value={[createStartDate && moment(createStartDate), createEndDate && moment(createEndDate)]}
                      format="YYYY-MM-DD hh:mm:ss"
                      disabledDate={current => current && (current > moment().endOf('day') || current < moment(projectInfo.creationDate).startOf('day'))}
                      allowClear
                      onChange={this.handleCreateDateRangeChange}
                      placeholder={['创建时间', '']}
                    />
                  </div>
                </Tooltip>
              )
            } */}

        </div>
        <div className="c7n-mySearchManage">
          {
            !isFilterEmpty && (
              <Button
                funcType="raised"
                style={{ color: '#fff', background: '#3F51B5', marginRight: 10 }}
                onClick={onClearFilter}
              >
                {'清空筛选'}
              </Button>
            )
          }
          {
            !isFilterEmpty && !filterExist && (
              <Button
                funcType="raised"
                style={{ color: '#fff', background: '#3F51B5', marginRight: 10 }}
                onClick={onSaveClick}
              >
                {'保存筛选'}
              </Button>
            )
          }
          {myFilters && myFilters.length > 0 && (
            <Button
              funcType="flat"
              style={{ color: '#3F51B5' }}
              onClick={onManageClick}
            >
              {'筛选管理'}
            </Button>
          )}
        </div>
      </div>

    );
  }
}

export default AdvancedSearch;
