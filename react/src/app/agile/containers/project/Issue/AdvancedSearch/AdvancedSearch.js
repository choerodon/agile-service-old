import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Select, DatePicker, Button, Modal, Tooltip, 
} from 'choerodon-ui';
import { stores, axios } from 'choerodon-front-boot';
import moment from 'moment';
import {
  find, map, filter, unionBy, 
} from 'lodash';
import IssueStore from '../../../../stores/project/sprint/IssueStore';
import IssueFilterControler from '../IssueFilterControler';
import SelectFocusLoad from '../../../../components/SelectFocusLoad';

const { Option } = Select;
const { AppState } = stores;
const { RangePicker } = DatePicker;

@observer
class AdvancedSearch extends Component {
    getSearchFilter = (filterId) => {
      this.filterControler = new IssueFilterControler();
      const projectInfo = IssueStore.getProjectInfo;
      const myFilters = IssueStore.getMyFilters;
      
      if (filterId) {
        IssueStore.setIsExistFilter(true);
        IssueStore.setEmptyBtnVisible(true);
        const searchFilterInfo = myFilters.find(item => item.filterId === filterId);
        const {
          advancedSearchArgs, searchArgs, otherArgs, contents, 
        } = searchFilterInfo.personalFilterSearchDTO;
        if (otherArgs.assigneeId && otherArgs.assigneeId.includes(0)) {
          otherArgs.assigneeId = otherArgs.assigneeId.map(item => (item === 0 ? '0' : item));
        }

        // 从统计图链接过来如果未分配，传入的0应该改为"0"
        ['component', 'epic', 'version', 'label', 'sprint'].forEach((key, index) => {
          if (otherArgs[key] && otherArgs[key].includes(0)) {
            otherArgs[key] = otherArgs[key].map((value) => {
              if (value === 0) {
                return '0';
              } else {
                return value;
              }
            }); 
          }
        });

        IssueStore.setSelectedMyFilterInfo(searchFilterInfo);
        IssueStore.setSelectedIssueType(advancedSearchArgs.issueTypeId || []);
        IssueStore.setSelectedStatus(advancedSearchArgs.statusId || []);
        IssueStore.setSelectedPriority(advancedSearchArgs.priorityId || []);
        IssueStore.setSelectedAssignee(advancedSearchArgs.assigneeIds.concat(otherArgs.assigneeId && otherArgs.assigneeId.length > 0 ? ['none'] : []) || []);
        IssueStore.setCreateStartDate(searchArgs.createStartDate ? moment(searchArgs.createStartDate).format('YYYY-MM-DD HH:mm:ss') : '');
        IssueStore.setCreateEndDate(searchArgs.createEndDate ? moment(searchArgs.createEndDate).format('YYYY-MM-DD HH:mm:ss') : '');
        IssueStore.setBarFilter(contents || []);
        this.filterControler.searchArgsFilterUpdate(IssueStore.setCreateStartDate, IssueStore.getCreateEndDate);
        this.filterControler.myFilterUpdate(otherArgs, contents, searchArgs);
        this.filterControler.advancedSearchArgsFilterUpdate(IssueStore.getSelectedIssueType, IssueStore.getSelectedStatus, IssueStore.getSelectedPriority);
        this.filterControler.assigneeFilterUpdate(IssueStore.getSelectedAssignee.filter(assigneeId => assigneeId !== 'none'));
        IssueStore.updateIssues(this.filterControler, contents);
      } else {
        IssueStore.resetFilterSelect(this.filterControler);
      }
    }
  
    
    handleMyFilterSelectChange = (value) => {
      IssueStore.setSelectedFilterId((value && value.key) || undefined);
      this.getSearchFilter((value && value.key) || undefined);
    }
  
    handleIssueTypeSelectChange = (value) => {
      const selectedStatus = IssueStore.getSelectedStatus;
      const selectedPriority = IssueStore.getSelectedPriority;
      IssueStore.setSelectedIssueType(map(value, 'key'));
      this.filterControler = new IssueFilterControler();
      this.filterControler.advancedSearchArgsFilterUpdate(map(value, 'key'), selectedStatus, selectedPriority);
      IssueStore.judgeConditionWithFilter();
      IssueStore.judgeFilterConditionIsEmpty();
      IssueStore.updateIssues(this.filterControler);
    }
  
    handleStatusSelectChange = (value) => {
      const selectedIssueType = IssueStore.getSelectedIssueType;
      const selectedPriority = IssueStore.getSelectedPriority;
      IssueStore.setSelectedStatus(map(value, 'key'));
      this.filterControler = new IssueFilterControler();
      this.filterControler.advancedSearchArgsFilterUpdate(selectedIssueType, map(value, 'key'), selectedPriority);
      IssueStore.judgeConditionWithFilter();
      IssueStore.judgeFilterConditionIsEmpty();
      IssueStore.updateIssues(this.filterControler);
    }
  
    handlePrioritySelectChange = (value) => {
      const selectedIssueType = IssueStore.getSelectedIssueType;
      const selectedStatus = IssueStore.getSelectedStatus;
      IssueStore.setSelectedPriority(map(value, 'key'));
      this.filterControler = new IssueFilterControler();
      this.filterControler.advancedSearchArgsFilterUpdate(selectedIssueType, selectedStatus, map(value, 'key'));
      IssueStore.judgeConditionWithFilter();
      IssueStore.judgeFilterConditionIsEmpty();
      IssueStore.updateIssues(this.filterControler);
    }
  
    handleAssigneeSelectChange = (value) => {
      this.filterControler = new IssueFilterControler();
      IssueStore.setSelectedAssignee(value);
      if (value.find(item => item === 'none')) {
        this.filterControler.assigneeFilterUpdate([]);
        this.filterControler.cache.get('userFilter').otherArgs.assigneeId = ['0'].concat(filter(value, item => item !== 'none'));
        // IssueStore.setFilterMap(this.filterControler.cache);
      } else {
        if (!this.filterControler.cache.get('userFilter').otherArgs) {
          this.filterControler.cache.get('userFilter').otherArgs = {};
        }
        this.filterControler.cache.get('userFilter').otherArgs.assigneeId = [];
        // IssueStore.setFilterMap(this.filterControler.cache);
        this.filterControler.assigneeFilterUpdate(value);
      }
      IssueStore.judgeFilterConditionIsEmpty();
      IssueStore.judgeConditionWithFilter();
      IssueStore.updateIssues(this.filterControler);
    }
    
    handleCreateDateRangeChange = (dates) => {
      if (dates.length) {
        const createStartDate = `${moment(dates[0]).format('YYYY-MM-DD')} 00:00:00`;
        const createEndDate = `${moment(dates[1]).format('YYYY-MM-DD')} 23:59:59`;
        IssueStore.setCreateStartDate(createStartDate);
        IssueStore.setCreateEndDate(createEndDate);
      } else {
        const projectInfo = IssueStore.getProjectInfo;
        IssueStore.setCreateStartDate('');
        IssueStore.setCreateEndDate('');
      }
      IssueStore.setSaveFilterVisible(false);
      this.filterControler = new IssueFilterControler();
      this.filterControler.searchArgsFilterUpdate(IssueStore.getCreateStartDate, IssueStore.getCreateEndDate);
      IssueStore.judgeConditionWithFilter();
      IssueStore.judgeFilterConditionIsEmpty();
      IssueStore.updateIssues(this.filterControler);
    }
    

    deBounce = (delay) => {
      let timeout;
      return (fn, that) => {
        if (timeout) {
          clearTimeout(timeout);
          timeout = null;
        }
        timeout = setTimeout(fn, delay, that);// (自定义函数，延迟时间，自定义函数参数1，参数2)
      };
    };
   
    render() {
      const editFilterInfo = IssueStore.getEditFilterInfo;
      const projectInfo = IssueStore.getProjectInfo;
      const issueTypes = AppState.currentMenuType.category === 'PROGRAM' ? IssueStore.getIssueTypes : IssueStore.getIssueTypes.filter(item => item.typeCode !== 'feature');
      const statusLists = IssueStore.getIssueStatus;
      const prioritys = IssueStore.getIssuePriority;      
      const selectedIssueType = IssueStore.getSelectedIssueType;
      const selectedStatus = IssueStore.getSelectedStatus;
      const selectedPriority = IssueStore.getSelectedPriority;
      const selectedAssignee = IssueStore.getSelectedAssignee;
      const createStartDate = IssueStore.getCreateStartDate || '';
      const createEndDate = IssueStore.getCreateEndDate || '';
      const selectedMyFilterInfo = IssueStore.getSelectedMyFilterInfo;
      const selectedFilterId = IssueStore.getSelectedFilterId;
      const isExistFilter = IssueStore.getIsExistFilter;
      const myFilters = IssueStore.getMyFilters;
      const filterListVisible = IssueStore.getFilterListVisible;
      const emptyBtnVisible = IssueStore.getEmptyBtnVisible;
      const filterControler = new IssueFilterControler();

      const debounceCallback = this.deBounce(500);
      return (
        <div className="c7n-mySearch">
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Select
              key="myFilterSelect"
              className="myFilterSelect"
              allowClear
              dropdownClassName="myFilterSelect-dropdown"
              dropdownMatchSelectWidth={false}
              placeholder="我的筛选"
              labelInValue
              maxTagCount={0}
              maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item.label).join(', ')}`}
              filter
              optionFilterProp="children"
              onChange={this.handleMyFilterSelectChange}
              value={selectedFilterId ? { key: selectedFilterId, label: selectedMyFilterInfo.name } : undefined}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {
                myFilters.length && myFilters.map(item => (
                  <Option key={item.filterId} value={item.filterId} title={item.name}>{item.name}</Option>
                ))
              }
            </Select>

            <Select
              key="issueTypeSelect"
              className="issueTypeSelect"
              mode="multiple"
              allowClear
              dropdownClassName="issueTypeSelect-dropdown"
              dropdownMatchSelectWidth={false}
              placeholder="问题类型"
              labelInValue
              maxTagCount={0}
              maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item.label).join(', ')}`}
              onChange={this.handleIssueTypeSelectChange}
              value={map(selectedIssueType, key => (
                {
                  key,
                  name: map(issueTypes, item => item.id === key).name,
                }
              ))}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {
                issueTypes.length && issueTypes.map(item => (
                  <Option key={item.id} value={item.id} title={item.name}>{item.name}</Option>
                ))
              }
            </Select>

            <Select
              key="statusSelect"
              className="statusSelect"
              mode="multiple"
              allowClear
              dropdownClassName="statusSelect-dropdown"
              dropdownMatchSelectWidth={false}
              placeholder="状态"
              labelInValue
              maxTagCount={0}
              maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item.label).join(', ')}`}
              onChange={this.handleStatusSelectChange}
              value={map(selectedStatus, key => (
                {
                  key,
                  name: map(statusLists, item => item.id === key).name,
                }
              ))}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {
                statusLists.length && statusLists.map(item => (
                  <Option key={item.id} value={item.id} title={item.name}>{item.name}</Option>
                ))
              }
            </Select>

            <Select
              key="prioritySelect"
              className="prioritySelect"
              mode="multiple"
              dropdownClassName="prioritySelect-dropdown"
              dropdownMatchSelectWidth={false}
              allowClear
              placeholder="优先级"
              labelInValue
              maxTagCount={0}
              maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item.label).join(', ')}`}
              onChange={this.handlePrioritySelectChange}
              value={map(selectedPriority, key => (
                {
                  key,
                  name: map(prioritys, item => item.id === key).name,
                }
              ))}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {
                prioritys.length && prioritys.map(item => (
                  <Option key={item.id} value={item.id} title={item.name}>{item.name}</Option>
                ))
              }
            </Select>

            <SelectFocusLoad
              type="user"
              loadWhenMount
              key="assigneeSelect"
              className="assigneeSelect"
              mode="multiple"
              allowClear         
              dropdownClassName="assigneeSelect-dropdown"
              dropdownMatchSelectWidth={false}
              placeholder="经办人"              
              saveList={(users) => { this.users = unionBy(this.users, users, 'id'); }}
              maxTagCount={0}
              maxTagPlaceholder={ommittedValues => `${ommittedValues.map(value => find(this.users, { id: value }) && find(this.users, { id: value }).realName).join(', ')}`}
              filter
              optionFilterProp="children"      
              onChange={this.handleAssigneeSelectChange}
              value={selectedAssignee}
              getPopupContainer={triggerNode => triggerNode.parentNode}
              render={user => <Option value={user.id}>{user.realName}</Option>}
            >
              <Option value="none">未分配</Option>
            </SelectFocusLoad>
            
            {
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
            }
            
          </div>
          <div className="c7n-mySearchManage">
            {
              emptyBtnVisible && (
              <Button 
                funcType="raised" 
                style={{ color: '#fff', background: '#3F51B5', marginRight: 10 }}
                onClick={() => {
                  IssueStore.setSaveFilterVisible(false);
                  IssueStore.setFilterListVisible(false);
                  IssueStore.setEditFilterInfo(map(editFilterInfo, item => Object.assign(item, { isEditing: false })));
                  IssueStore.resetFilterSelect(filterControler);
                  IssueStore.setClickedRow({
                    expand: false,
                    selectedIssue: {},
                    checkCreateIssue: false,
                  });
                }}
              >
                {'清空筛选'}
              </Button>
              )
            }
            {
              !isExistFilter && (
              <Button 
                funcType="raised" 
                style={{ color: '#fff', background: '#3F51B5', marginRight: 10 }}
                onClick={() => {
                  IssueStore.setSaveFilterVisible(true);
                  IssueStore.setFilterListVisible(false);
                  IssueStore.setEditFilterInfo(map(editFilterInfo, item => Object.assign(item, { isEditing: false })));
                }}
              >
                {'保存筛选'}
              </Button>
              )
            }
            {myFilters && myFilters.length > 0 && (
              <Button 
                funcType="flat" 
                style={{ color: '#3F51B5' }}
                onClick={() => {
                  IssueStore.setSaveFilterVisible(false);
                  IssueStore.setFilterListVisible(!filterListVisible);
                  IssueStore.setEditFilterInfo(map(editFilterInfo, item => Object.assign(item, { isEditing: false })));
                }}
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
