import React, { Component } from 'react';
import _ from 'lodash';
import { stores } from 'choerodon-front-boot';
import {
  Modal, Form, Select, Button,
} from 'choerodon-ui';
import { getSelf, getUsers, getUser } from '../../api/CommonApi';
import { updateIssue } from '../../api/NewIssueApi';
import UserHead from '../UserHead';
import './Assignee.scss';

const { AppState } = stores;
const { Option } = Select;
const FormItem = Form.Item;
let sign = false;

class Assignee extends Component {
  debounceFilterIssues = _.debounce((input) => {
    this.setState({ selectLoading: true });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.content.filter(u => u.enabled),
        selectLoading: false,
      });
    });
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      selectLoading: false,
      assigneeId: undefined,
      originUsers: [],
    };
  }

  componentDidMount() {
    this.loadUser();
  }

  handleClickAssignee = () => {
    const {
      form, issueId, objectVersionNumber, onOk,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const projectId = AppState.currentMenuType.id;
        const { assigneeId } = values;
        const obj = {
          issueId,
          objectVersionNumber,
          assigneeId: assigneeId || null,
        };
        this.setState({ loading: true });
        updateIssue(obj)
          .then((res) => {
            this.setState({ loading: false });
            onOk();
          });
      }
    });
  };

  handleFilterChange(input) {
    if (!sign) {
      this.setState({ selectLoading: true });
      getUsers(input).then((res) => {
        this.setState({
          originUsers: res.content.filter(u => u.enabled),
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  handleSelectChange(assigneeId) {
    this.setState({ assigneeId });
  }

  handleClickAssigneeToMe() {
    const { assigneeId } = this.state;
    const { form } = this.props;
    getSelf().then((res) => {
      if (res.id !== assigneeId) {
        this.setState({
          assigneeId: res.id,
          originUsers: [res],
        });
        form.setFieldsValue({
          assigneeId: res.id,
        });
      }
    });
  }

  /**
   * first come in, if assigneeId is not null, load and set into origin
   */
  loadUser() {
    const { assigneeId } = this.props;
    if (!assigneeId) return;
    getUser(assigneeId).then((res) => {
      this.setState({
        assigneeId: res.content[0].id,
        originUsers: res.content.length ? [res.content[0]] : [],
      });
    });
  }

  render() {
    const {
      visible, onCancel, issueNum, form: { getFieldDecorator },
    } = this.props;
    const {
      loading, selectLoading, assigneeId, originUsers,
    } = this.state;

    return (
      <Modal
        className="c7n-agile-assignee"
        title={`分配问题${issueNum}`}
        visible={visible || false}
        onOk={this.handleClickAssignee}
        onCancel={onCancel}
        okText="分配"
        cancelText="取消"
        confirmLoading={loading}
      >
        <div className="body">
          <Form layout="vertical" style={{ width: 400 }}>
            <FormItem>
              {getFieldDecorator('assigneeId', {
                initialValue: assigneeId,
              })(
                <Select
                  label="分配给"
                  loading={selectLoading}
                  allowClear
                  filter
                  filterOption={false}
                  onChange={this.handleSelectChange.bind(this)}
                  onFilterChange={this.handleFilterChange.bind(this)}
                >
                  {originUsers.map(user => (
                    <Option key={user.id} value={user.id}>
                      <div className="wrap">
                        <UserHead
                          user={{
                            id: user.id,
                            loginName: user.loginName,
                            realName: user.realName,
                            avatar: user.imageUrl,
                          }}
                        />
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
          </Form>
          <Button
            className="btn"
            funcType="raised"
            onClick={this.handleClickAssigneeToMe.bind(this)}
          >
            {'分配给我'}
          </Button>
        </div>
      </Modal>
    );
  }
}
export default Form.create({})(Assignee);
