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
            overlayClassName="c7nagile-SideIssueList-popover"
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
          </Popover> */}
        </div>
        
        <div className="c7nagile-SideIssueList-content">
          {/* <Loading loading={issueListLoading} /> */}
          <div className="c7nagile-SideIssueList-content-pi">
            {/* <span>{activePi.piCode}</span> */}
            {/* <Icon type={issueListCollapse ? 'expand_more' : 'expand_less'} onClick={this.handleCollapseClick} /> */}
          </div>
          <div className="c7nagile-SideIssueList-content-list">
            {issueList.filter(this.handleFilter).map(issue => <IssueItem issue={issue} />) }  
          </div>
        </div>
        
      </div>
    );
  }
}

SideIssueList.propTypes = {

};
const SideIssueListContainer = observer(({ ...props }) => (StoryMapStore.sideIssueListVisible ? <SideIssueList {...props} /> : null));
export default SideIssueListContainer;
