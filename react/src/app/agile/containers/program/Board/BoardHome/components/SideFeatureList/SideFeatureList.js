import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Input, Icon, Checkbox, Popover, Spin,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';
import FeatureItem from './FeatureItem';
import './SideFeatureList.scss';

@observer
class SideFeatureList extends Component {
  state = {
    filter: '',
  }

  componentDidMount() {
    BoardStore.loadFeatureList();
  }


  handleCollapseClick = () => {
    BoardStore.setFeatureListCollapse(!BoardStore.featureListCollapse);
  }

  handleFilter = (feature) => {
    const { filter } = this.state;
    return feature.issueNum.indexOf(filter) > -1 || feature.summary.indexOf(filter) > -1;
  }

  handleFilterChange = (e) => {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {
      featureList, activePi, featureListCollapse, featureListLoading, 
    } = BoardStore;
    const { filter } = this.state;
    return (
      <div className="c7nagile-SideFeatureList">
        <div className="c7nagile-SideFeatureList-header">
          <div className="c7nagile-SideFeatureList-input">
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
          </Popover> */}
        </div>
        <Spin spinning={featureListLoading}>
          <div className="c7nagile-SideFeatureList-content">
            <div className="c7nagile-SideFeatureList-content-pi">
              <span>{activePi.piCode}</span>
              <Icon type={featureListCollapse ? 'expand_more' : 'expand_less'} onClick={this.handleCollapseClick} />
            </div>
            <div className="c7nagile-SideFeatureList-content-list">
              {
              !featureListCollapse && featureList.filter(this.handleFilter).map(feature => <FeatureItem feature={feature} />)
            }
            </div>
          </div>
        </Spin>
      </div>
    );
  }
}

SideFeatureList.propTypes = {

};

export default SideFeatureList;
