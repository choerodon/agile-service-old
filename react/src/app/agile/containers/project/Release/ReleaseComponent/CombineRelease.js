import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
 Modal, Form, Select, message
} from 'choerodon-ui';
import { stores, Content } from 'choerodon-front-boot';
import _ from 'lodash';
import ReleaseStore from '../../../../stores/project/release/ReleaseStore';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const Option = Select.Option;

@observer
class CombineRelease extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sourceList: [],
    };
  }

  // componentWillMount() {
  //   ReleaseStore.axiosGetVersionListWithoutPage().then((res) => {
  //     this.setState({
  //       sourceList: res,
  //     });
  //   }).catch((error) => {
  //   });
  // }
  componentDidMount() {
    this.props.onRef();
  }

  changeState(data) {
    this.setState({
      sourceList: data,
    });
  }

  handleCombine(e) {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        if (value.source.length === 1) {
          if (value.source[0] === value.destination) {
            Choerodon.prompt('合并版本不能一样');
            return;
          }
        }
        const data = {
          sourceVersionIds: _.clone(value.source).map(Number),
          targetVersionId: parseInt(_.clone(value.destination), 10),
        };
        if (data.sourceVersionIds.indexOf(data.targetVersionId) !== -1) {
          data.sourceVersionIds.splice(data.sourceVersionIds.indexOf(data.targetVersionId), 1);
        }
        ReleaseStore.axiosMergeVersion(data).then((res) => {
          this.props.form.resetFields();
          this.props.onCancel();
          this.props.refresh();
        }).catch((error) => {
        });
      }
    });
  }

  judgeSelectDisabled(item) {
    if (this.props.form.getFieldValue('source')) {
      if (this.props.form.getFieldValue('source').length === 1 && this.props.form.getFieldValue('source')[0] === item.versionId) {
        return true;
      }
    }
    return false;
  }

  handleCancel = () => {
    this.props.form.resetFields();
    this.props.onCancel();
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Sidebar
        title="合并版本"
        okText="合并"
        cancelText="取消"
        visible={this.props.visible}
        onCancel={this.handleCancel}
        onOk={this.handleCombine.bind(this)}
      >
        <Content
          style={{
            padding: 0,
          }}
          title={`在项目“${AppState.currentMenuType.name}”中合并版本`}
          description="您可以通过合并版本的功能将多个版本的issue合并到一个版本中，被合并的版本将被删除。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/release/merge-version/"
        >
          <p style={{ display: 'flex', alignItems: 'center' }}>
<span className="c7n-release-icon">!</span>
一旦版本合并后，您就无法还原。
</p>
          <Form style={{ width: '512px' }}>
            <FormItem>
              {getFieldDecorator('source', {
                rules: [{
                  required: true,
                  message: '合并版本是必填的',
                }],
              })(
                <Select
                  mode="tags"
                  label="合并版本"
                >
                  {this.props.sourceList.length > 0 ? this.props.sourceList.map(item => (
                    <Option value={String(item.versionId)} key={item.versionId}>{item.name}</Option>
                  )) : ''}
                </Select>,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('destination', {
                rules: [{
                  required: true,
                  message: '合并至版本是必填的',
                }],
              })(
                <Select
                  label="合并至版本"
                >
                  {this.props.sourceList.length > 0 ? this.props.sourceList.map(item => (
                    <Option
                      key={item.versionId}
                      value={String(item.versionId)}
                    >
{item.name}

                    </Option>
                  )) : ''}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(CombineRelease);
