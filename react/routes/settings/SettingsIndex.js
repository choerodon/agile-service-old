import React from 'react';
import { Tabs } from 'choerodon-ui';
import ComponentIndex from './Component/ComponentHome';
import FastSearchIndex from './FastSearch/FastSearchHome/FastSearchHome';
import IssueLinkIndex from './IssueLink/IssueLinkHome/IssueLinkHome';
import MessageNotificationIndex from './MessageNotification/MessageNotificationHome/MessageNotificationHome';
import Breadcrumb from './components/breadcrumb';
import OperationBarIndex from './components/operation-bar';
import './style/index.less';

const { TabPane } = Tabs;

class SettingsIndex extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      operationData: {
        type: 'add',
        name: '创建',
      },
      breadcrumbData: {
        list: [
          {
            id: 1,
            name: 'new123',
          },
          {
            id: 2,
            name: '设置',
            href: '#',
          },
          {
            id: 3,
            name: '模块管理',
          },
        ],
        separator: '>',
      },
    };
  }


  callback = (key) => {
    // console.log(key);
    const { list } = this.state.breadcrumbData;
    list[list.length - 1].name = key;
    this.setState({
      breadcrumbData: this.state.breadcrumbData,
    });
  }

  render() {
    // const { match, location } = this.props;
    return (
      <div className="c7n-settings">
        <OperationBarIndex data={this.state.operationData} />
        <hr className="hr" />
        <Breadcrumb data={this.state.breadcrumbData} />
        <Tabs defaultActiveKey="模块管理" onChange={this.callback}>
          <TabPane tab="模块管理" key="模块管理">
            <div className="tabPane-box">
              <ComponentIndex />
            </div>
          </TabPane>
          <TabPane tab="快速搜索" key="快速搜索">
            <div className="tabPane-box">
              <FastSearchIndex />
            </div>
          </TabPane>
          <TabPane tab="通知设置" key="通知设置">
            <div className="tabPane-box">
              <MessageNotificationIndex />
            </div>
          </TabPane>
          <TabPane tab="问题链接" key="问题链接">
            <div className="tabPane-box">
              <IssueLinkIndex />
            </div>
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

export default SettingsIndex;
