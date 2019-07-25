import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Select, Button, Divider } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import './RelateIssueTypeScheme.scss';

const { Option } = Select;
const { AppState } = stores;
const { 
  type, id, name, organizationId, 
} = AppState.currentMenuType; 


@observer 
class RelateIssueTypeScheme extends Component {
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

  render() {
    return (
        <Page className="issue-region">
            <Header 
                title={<FormattedMessage id="relateIssueTypeScheme.title"/>}
                backPath={`/agile/issue-type-schemes?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
             />
            <Content>
                <p className="issue-relateIssueTypeScheme-tip">
                    <FormattedMessage id='relateIssueTypeScheme.tip' />
                </p>
                <div className="issue-relateIssueTypeScheme-content">
                    <span className="issueType-name">{'问题类型方案：test-问题类型方案-1'}</span>
                    <p className="issueType-des">{'描述：Cloopm Service Desk的测试问题类型方案'}</p>
                    <Select className="project-Select" label="项目" placeholder="无" allowClear style={{ width: 520 }}>
                        <Option key="jack">Jack</Option>
                        <Option key="lucy">Lucy</Option>
                        <Option key="disabled">Disabled</Option>
                        <Option key="Yiminghe">yiminghe</Option>
                    </Select>
                    <Divider className="divider"/>
                    <div>
                        <Button type="primary" funcType="raised" style={{ marginRight: 10 }} >保存</Button>
                        <Button funcType="raised" >取消</Button>
                    </div>
                </div>
            </Content>
        </Page>
    );
  }
}

export default injectIntl(RelateIssueTypeScheme);
