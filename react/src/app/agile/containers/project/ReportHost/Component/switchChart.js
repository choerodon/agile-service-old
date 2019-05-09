import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import {
  Page, Header, Content, stores, 
} from 'choerodon-front-boot';
import {
  Dropdown, Button, Menu, Icon, 
} from 'choerodon-ui';
import list from '../Home/list';

const { AppState } = stores;

class SwitchChart extends Component {
  constructor(props) {
    super(props);
    this.state = {
      linkFromParamUrl: undefined,
    };
  }

  componentWillReceiveProps(nextProps, nextState) {
    const { location: { search }, current } = this.props;
    const defaultParm = current ? `reporthost/${current}` : undefined;
    const linkFromParamUrl = _.last(search.split('&')).split('=')[0] === 'paramUrl' ? _.last(search.split('&')).split('=')[1] : defaultParm;
    this.setState({
      linkFromParamUrl,
    });
  }


  handleClick(e) {
    const { history } = this.props;
    const { linkFromParamUrl } = this.state;
    const urlParams = AppState.currentMenuType;
    const {
      type, id, name, organizationId, 
    } = urlParams;
    const { key } = e;
    const obj = list.find(v => v.key.toString() === key);
    const { history: { search } } = this.props;
    if (obj) {
      history.push(`${obj.link}?type=${type}&id=${id}&name=${name}&organizationId=${organizationId}&paramUrl=${linkFromParamUrl}`);
    }
  }
  
  render() {
    const { current } = this.props;
    const menu = (
      <Menu onClick={this.handleClick.bind(this)}>
        {
          list.filter(chart => chart.key !== current).map(chart => (
            <Menu.Item key={chart.key}>
              {chart.title}
            </Menu.Item>
          ))
        }
      </Menu>
    );
    return (
      <Dropdown placement="bottomCenter" trigger={['click']} overlay={menu}>
        <Button funcType="flat">
          <span>切换报表</span>
          <Icon type="arrow_drop_down" />
        </Button>
      </Dropdown>
    );
  }
}

export default withRouter(SwitchChart);
