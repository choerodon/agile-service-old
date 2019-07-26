import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Select, Button, Divider, Table, 
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import './RelateMergeMatchFst.scss';

const { Option } = Select;
const { AppState } = stores;
const { 
  type, id, name, organizationId, 
} = AppState.currentMenuType; 


@observer 
class RelateMergeMatchFst extends Component {
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
        effectIssueCount: 32,
      },
      {
        id: 2,
        name: 'lwf的项目',
        currentIssueType: '故事',
        effectIssueCount: 2,
      },
    ];

    return (
        <Page className="issue-region">
            <Header 
                title={<FormattedMessage id="relateMerge.title"/>}
                backPath={`/agile/issue-type-schemes?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
             />
            <Content className="mergeMatch-content">
                <div className="mergeMatch-subTitle">
                    <FormattedMessage id='relateMergeMatchFst.subTitle' />
                </div>
                <p className="mergeMatch-des">
                    <FormattedMessage id='relateMergeMatchFst.des' />
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
                    <Button type="primary" funcType="raised" style={{ marginRight: 10 }} >下一步</Button>
                    <Button funcType="raised" >取消</Button>
                </div>
            </Content>
        </Page>
    );
  }
}

export default injectIntl(RelateMergeMatchFst);
