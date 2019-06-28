import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Input, Icon, Checkbox, Popover, Spin, Select,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import FiltersProvider from '../../../../../../components/FiltersProvider';
import Loading from '../../../../../../components/Loading';
import IssueItem from './IssueItem';
import './SideIssueList.scss';

const { Option } = Select;
@FiltersProvider(['issueStatus', 'version'])
@observer
class SideIssueList extends Component {
  state = {
    filter: '',
  }

  componentDidMount() {
    StoryMapStore.loadIssueList();
  }


  handleCollapseClick = () => {
    StoryMapStore.setIssueListCollapse(!StoryMapStore.issueListCollapse);
  }

  handleFilter = (issue) => {
    const { filter } = this.state;
    return issue.issueNum.indexOf(filter) > -1 || issue.summary.indexOf(filter) > -1;
  }

  handleClickFilter=() => {
    
  }

  handleFilterChange = (e) => {
    this.setState({
      filter: e.target.value,
    });
  }

  setFilter=(field, values) => {
    StoryMapStore.handleSideFilterChange(field, values.map(Number));
  }

  render() {
    const {
      issueList, issueListCollapse, issueListLoading, 
    } = StoryMapStore;
    const { filter } = this.state;
    const issues = issueList.filter(this.handleFilter);
    const { filters: { issueStatus, version: versionList } } = this.props;
    return (
      <div className="c7nagile-SideIssueList">
        <div className="c7nagile-SideIssueList-header">
          <div className="c7nagile-SideIssueList-input">
            <Input
              placeholder="按照名称搜索"
              prefix={<Icon type="search" />}
              value={filter}
              onChange={this.handleFilterChange}
            />
          </div>  
          
          {/* <Popover
            trigger="click"
            placement="bottomRight"
            overlayClassName="c7nagile-SideFeatureList-popover"
            content={(
              <div
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                }}
              >
                {
                  [
                    {
                      name: '特性',
                      id: 'business',
                    },
                    {
                      name: '史诗',
                      id: 'enabler',
                    },
                  ].map(item => (
                    <Checkbox
                      onChange={this.handleClickFilter.bind(this, item.id)}
                    >
                      {item.name}
                    </Checkbox>
                  ))
                }
              </div>
            )}
          >
            <div style={{ color: '#3F51B5', cursor: 'pointer' }}>
              <span>快速搜索</span>
              <Icon type="baseline-arrow_drop_down" className="icon" />
            </div>
          </Popover>         */}
        </div>   
        <div style={{ display: 'flex' }}>
          <Select              
            allowClear
            mode="multiple"
            style={{ width: 150 }}
            onChange={this.setFilter.bind(this, 'statusList')}
            placeholder="状态"
          >
            {issueStatus.map(({ text, value }) => <Option value={value}>{text}</Option>)}
          </Select>
          <Select             
            allowClear
            mode="multiple"
            style={{ width: 150 }}
            onChange={this.setFilter.bind(this, 'versionList')}
            placeholder="版本"
          >
            {versionList.map(({ text, value }) => <Option value={value}>{text}</Option>)}
          </Select>
        </div>      
        <div className="c7nagile-SideIssueList-content">
          {/* <Loading loading={issueListLoading} /> */}
          <div className="c7nagile-SideIssueList-content-pi">
            {/* <span>{activePi.piCode}</span> */}
            {/* <Icon type={issueListCollapse ? 'expand_more' : 'expand_less'} onClick={this.handleCollapseClick} /> */}
          </div>        
          {issues.length > 0 ? (
            <div className="c7nagile-SideIssueList-content-list">
              {issues.map(issue => <IssueItem issue={issue} />)}  
            </div>
          ) : <div style={{ textAlign: 'center', color: 'rgba(0, 0, 0, 0.54)' }}>暂无数据</div> }      
        </div>        
      </div>
    );
  }
}

SideIssueList.propTypes = {

};
const SideIssueListContainer = observer(({ ...props }) => (StoryMapStore.sideIssueListVisible && !StoryMapStore.isFullScreen ? <SideIssueList {...props} /> : null));
export default SideIssueListContainer;
