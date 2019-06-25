import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Input, Icon, Checkbox, Popover, Spin,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import Loading from '../../../../../../components/Loading';
import IssueItem from './IssueItem';
import './SideIssueList.scss';

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

  render() {
    const {
      issueList, issueListCollapse, issueListLoading, 
    } = StoryMapStore;
    const { filter } = this.state;
    const issues = issueList.filter(this.handleFilter);
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
