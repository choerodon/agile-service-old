import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { axios } from 'choerodon-front-boot';
import { Select } from 'choerodon-ui';
import EventEmitter from 'wolfy87-eventemitter';

import './QuickSearch.scss';
import BacklogStore from '../../stores/project/backlog/BacklogStore';

const { Option, OptGroup } = Select;
const QuickSearchEvent = new EventEmitter();
@inject('AppState')
@observer
class QuickSearch extends Component {
  constructor(props) {
    super(props);
    this.state = {
      userDataArray: [],
      quickSearchArray: [],
      selectQuickSearch: [],
    };
  }

  /**
   * DidMount =>
   * 1. 请求快速搜索数据
   * 2. 请求项目经办人信息
   */
  componentDidMount() {
    QuickSearchEvent.addListener('clearQuickSearchSelect', this.clearQuickSearch);
    QuickSearchEvent.addListener('setSelectQuickSearch', this.setSelectQuickSearch);
    const { AppState } = this.props;
    const axiosGetFilter = axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
      contents: [
      ],
      filterName: '',
    });
    const axiosGetUser = axios.get(`/iam/v1/projects/${AppState.currentMenuType.id}/users?size=40`);
    Promise.all([axiosGetFilter, axiosGetUser]).then((res = []) => {
      const resFilterData = res[0].map(item => ({
        label: item.name,
        value: item.filterId,
      }));
      // 非停用角色
      const resUserData = res[1].content.filter(item => item.enabled).map(item => ({
        id: item.id,
        realName: item.realName,
      }));
      this.setState({
        userDataArray: resUserData,
        quickSearchArray: resFilterData,
      });
    }).catch((error) => {
      Choerodon.prompt(error);
    });
  }

  componentWillUnmount() {
    this.setState({
      userDataArray: [],
      quickSearchArray: [],
      selectQuickSearch: [],
    });
  }

  /**
   *
   * @param value（Array） => 选中的快速搜索 ID 组成的数组
   * @props onQuickSearchChange
   */
  handleQuickSearchChange = (value, key) => {
    const { onQuickSearchChange } = this.props;
    const flattenValue = value.map(item => item.key);
    const otherSearchId = flattenValue.filter(item => item >= 0);
    this.setState({
      selectQuickSearch: value,
    });
    // -1 仅我的问题
    // -2 仅故事
    onQuickSearchChange(flattenValue.includes(-1), flattenValue.includes(-2), otherSearchId);
  };

  /**
   *
   * @param value（Array）=> 选中的经办人 ID 组成的数组
   */
  handleAssigneeChange = (value) => {
    const { onAssigneeChange } = this.props;
    const flattenValue = value.map(item => item.key);
    onAssigneeChange(flattenValue);
  };

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

  clearQuickSearch = () => {
    this.setState({
      selectQuickSearch: [],
    });
  }

  setSelectQuickSearch=(selectQuickSearch) => {
    this.setState({
      selectQuickSearch,
    });
    this.handleQuickSearchChange(selectQuickSearch);
  }

  render() {
    // 防抖函数
    const debounceCallback = this.deBounce(500);
    const {
      style, AppState, onAssigneeChange, quickSearchAllowClear, 
    } = this.props;
    const { showRealQuickSearch } = BacklogStore;
    const {
      userDataArray,
      quickSearchArray,
      selectQuickSearch,
    } = this.state;

    // showRealQuickSearch 用于在待办事项中销毁组件
    // 具体查看 Backlog/BacklogComponent/SprintComponent/SprintItem.js 中 clearFilter 方法
    return (
      <div className="c7n-agile-quickSearch" style={style}>
        <p>搜索:</p>
        {showRealQuickSearch ? (
          <React.Fragment>
            <Select
              key="quickSearchSelect"
              className="quickSearchSelect"
              dropdownClassName="quickSearchSelect-dropdown"
              mode="multiple"
              labelInValue
              placeholder="快速搜索"
              maxTagCount={0}
              maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item.label).join(', ')}`}
              onChange={this.handleQuickSearchChange}
              getPopupContainer={triggerNode => triggerNode.parentNode}
              allowClear={!!quickSearchAllowClear}
              value={selectQuickSearch}
            >
              {
                <OptGroup key="quickSearch" label="常用选项">
                  <Option key={-1} value={-1}>仅我的问题</Option>
                  <Option key={-2} value={-2}>仅故事</Option>
                </OptGroup>
          }
              <OptGroup key="more" label="更多">
                {
              quickSearchArray.map(item => (
                <Option key={item.value} value={item.value} title={item.label}>{item.label}</Option>
              ))
            }
              </OptGroup>
            </Select>
            {
              onAssigneeChange && (
                <Select
                  key="assigneeSelect"
                  className="assigneeSelect"
                  mode="multiple"
                  dropdownClassName="assigneeSelect-dropdown"
                  placeholder="经办人"
                  labelInValue
                  maxTagCount={0}
                  maxTagPlaceholder={ommittedValues => `${ommittedValues.map(item => item.label).join(', ')}`}
                  filter
                  optionFilterProp="children"
                  onFilterChange={(value) => {
                    if (value) {
                      debounceCallback(() => {
                        axios.get(`/iam/v1/projects/${AppState.currentMenuType.id}/users?size=40&param=${value}`).then((res) => {
                        // Set 用于查询是否有 id 重复的，没有重复才往里加
                          const temp = new Set(userDataArray.map(item => item.id));
                          res.content.filter(item => item.enabled).forEach((item) => {
                            if (!temp.has(item.id)) {
                              userDataArray.push({
                                id: item.id,
                                realName: item.realName,
                              });
                            }
                          });
                          this.setState({
                            userDataArray,
                          });
                        });
                      }, this);
                    }
                  }}
                  onChange={this.handleAssigneeChange}
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                >
                  {
                    userDataArray.length && userDataArray.map(item => (
                      <Option key={item.id} value={item.id} title={item.realName}>{item.realName}</Option>
                    ))
                  }
                </Select>
              )
            }
          </React.Fragment>
        ) : (
          <React.Fragment>
            <Select placeholder="快速搜索" className="quickSearchSelect" />
            <Select placeholder="经办人" className="assigneeSelect" />
          </React.Fragment>
        )}
      </div>
    );
  }
}

export { QuickSearchEvent };
export default QuickSearch;
