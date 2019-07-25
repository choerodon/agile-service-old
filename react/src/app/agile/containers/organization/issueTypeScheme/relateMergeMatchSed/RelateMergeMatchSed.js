import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Select, Button, Divider, Checkbox, 
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import './RelateMergeMatchSed.scss';

const { Option } = Select;
const { AppState } = stores;
const { 
  type, id, name, organizationId, 
} = AppState.currentMenuType; 


@observer 
class RelateMergeMatchSed extends Component {
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
  getColumn() {

  }

  render() {
    return (
        <Page className="issue-region">
            <Header 
                title={<FormattedMessage id="relateMerge.title"/>}
                backPath={`/agile/issue-type-schemes/relateMergeMatchFst?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
             />
            <Content className="mergeMatch-content">
                <div className="mergeMatch-subTitle">
                    <FormattedMessage id='relateMergeMatchSed.subTitle' />
                </div>
                <p className="mergeMatch-des">
                    <FormattedMessage id='relateMergeMatchSed.des' />
                </p>
                <Select className="targetIssueType-Select" label="目标问题类型" placeholder="" allowClear style={{ width: 520 }}>
                    <Option key="jack">Jack</Option>
                    <Option key="lucy">Lucy</Option>
                    <Option key="disabled">Disabled</Option>
                    <Option key="Yiminghe">yiminghe</Option>
                </Select>
                <div>
                  <Checkbox>保留原有问题类型中的字段值</Checkbox>
                </div>
                <Divider className="divider" />
                <div>
                    <Button type="primary" style={{ marginRight: 10 }}>上一步</Button>
                    <Button type="primary" funcType="raised" style={{ marginRight: 10 }} >下一步</Button>
                    <Button funcType="raised" >取消</Button>
                </div>
            </Content>
        </Page>
    );
  }
}

export default injectIntl(RelateMergeMatchSed);
