import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Select, Button, Divider, Table, 
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import './RelateMergeMatchTrd.scss';

const { Option } = Select;
const { AppState } = stores;
const { 
  type, id, name, organizationId, 
} = AppState.currentMenuType; 


@observer 
class RelateMergeMatchTrd extends Component {
  constructor(props) {
    super(props);
    this.state = {
      SelectedProjectId: undefined,
    };
  }

  //   handleSelectChange = (value) => {
  //     this.setState({
  //       SelectedProjectId: value.key, 
  //     });
  //   }
  getColumns = () => {
    const columns = [
      {
        title: <FormattedMessage id="relateMerge.project" />,
        dataIndex: 'name',
        key: 'name',
        // render: (text, record) => {

        // }
      },
      {
        title: <FormattedMessage id="relateMerge.currentType" />,
        dataIndex: 'currentIssueType',
        key: 'currentIssueType',
        // render: (text, record) => {

        // }
      },
      {
        title: <FormattedMessage id="relateMerge.targetType" />,
        dataIndex: 'targetIssueType',
        key: 'targetIssueType',
        // render: (text, record) => {

        // }
      },
      {
        title: <FormattedMessage id="relateMerge.effectCount" />,
        dataIndex: 'effectIssueCount',
        key: 'effectIssueCount',
        // render: (text, record) => {

        // }
      },

    ];
    return columns;
  }

  render() {
    const tableDataSource = [
      {
        id: 1,
        name: '张君雅小妹妹服务项目',
        currentIssueType: '任务',
        targetIssueType: '故事',
        effectIssueCount: 32,
      },
      {
        id: 2,
        name: 'lwf的项目',
        currentIssueType: '故事',
        targetIssueType: '史诗',
        effectIssueCount: 2,
      },
    ];

    return (
        <Page className="issue-region">
            <Header 
                title={<FormattedMessage id="relateMerge.title"/>}
                backPath={`/agile/issue-type-schemes/relateMergeMatchSed?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
             />
            <Content className="mergeMatch-content">
                <div className="mergeMatch-subTitle">
                    <FormattedMessage id='relateMergeMatchTrd.subTitle' />
                </div>
                <p className="mergeMatch-des">
                    <FormattedMessage 
                      id='relateMergeMatchTrd.des' 
                      values={{
                        currentType: '任务',
                        targetType: '故事',
                      }} 
                    />
                </p>
                <Table 
                  // dataSource={IssueTypeStore.getIssueTypes}
                  dataSource={tableDataSource}
                  columns={this.getColumns()}
                  // loading={IssueTypeStore.getIsLoading}
                  // rowKey={record => record.id}
                  pagination={false}
                  // onChange={this.handleTableChange}
                  filterBar={false}
                />
                <Divider className="divider" />
                <div>
                    <Button type="primary" style={{ marginRight: 10 }}>上一步</Button>
                    <Button type="primary" funcType="raised" style={{ marginRight: 10 }} >确认</Button>
                    <Button funcType="raised" >取消</Button>
                </div>
            </Content>
        </Page>
    );
  }
}

export default injectIntl(RelateMergeMatchTrd);
