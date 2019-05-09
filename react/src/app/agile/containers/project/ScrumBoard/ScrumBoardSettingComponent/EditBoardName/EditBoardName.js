import React, { Component } from 'react';
import {
  stores, axios, Page, Header, Content, Permission,
} from 'choerodon-front-boot';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import {
  Form, Input, Button, Icon, Select, Radio, Spin, message,
} from 'choerodon-ui';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';

const { AppState } = stores;
const { Option } = Select;
const FormItem = Form.Item;

class EditBoardName extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      initialBoardName: '',
      boardName: '',
      lastBoardName: '',
    };
  }

  componentDidMount() {
    const initialBoardName = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard).name;
    this.setState({
      initialBoardName,
      lastBoardName: initialBoardName,
    });
  }

  checkBoardNameRepeat = (rule, value, callback) => {
    const { initialBoardName } = this.state;
    const proId = AppState.currentMenuType.id;
    if (initialBoardName === value) {
      callback();
    }
    ScrumBoardStore.checkBoardNameRepeat(proId, value)
      .then((res) => {
        if (res) {
          callback('看板名称重复');
        } else {
          this.setState(
            {
              boardName: value,
            },
          );
          callback();
        }
      });
  };

  handleUpdateBoardName = () => {
    const { boardName } = this.state;
    const { form, history } = this.props;
    const currentEditBoard = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard);
    const { objectVersionNumber, boardId, projectId } = currentEditBoard;
    const data = {
      objectVersionNumber,
      boardId,
      name: boardName,
      projectId,
    };

    form.validateFields((err, value, modify) => {
      if (!err && modify) {
        this.setState({
          loading: true,
          lastBoardName: value.boardName,
        });
        axios.put(`/agile/v1/projects/${data.projectId}/board/${data.boardId}?boardName=${encodeURIComponent(data.name)}`, data)
          .then((res) => {
            this.setState({
              loading: false,
            });
            ScrumBoardStore.setBoardList(ScrumBoardStore.getSelectedBoard, res);
            Choerodon.prompt('保存成功');
            // history.push(`/agile/scrumboard?type=project&id=${data.projectId}&name=${encodeURIComponent(AppState.currentMenuType.name)}&organizationId=${AppState.currentMenuType.organizationId}`);
          });
      }
    });
  }

  render() {
    const { form: { getFieldDecorator, setFieldsValue } } = this.props;
    const {
      initialBoardName, loading, boardName, lastBoardName,
    } = this.state;
    const { editBoardNameDisabled } = this.props;
    return (
      <Content
        description={`您正在编辑项目“${AppState.currentMenuType.name}”的看板名称`}
        style={{
          padding: 0,
          height: '100%',
        }}
      >
        <Spin spinning={loading}>
          {
            editBoardNameDisabled ? (
              <Input
                style={{ width: 512 }}
                label="看板名称"
                maxLength={10}
                defaultValue={initialBoardName}
                disabled={editBoardNameDisabled}
              />
            ) : (
              <div>
                <Form layout="vertical">
                  <FormItem label="boardName" style={{ width: 512 }}>
                    {getFieldDecorator('boardName', {
                      rules: [{ required: true, message: '看板名称必填' }, {
                        validator: this.checkBoardNameRepeat,
                      }],
                      initialValue: initialBoardName,
                    })(
                      <Input
                        label="看板名称"
                        maxLength={10}
                      />,
                    )}
                  </FormItem>
                </Form>
                <div style={{ padding: '12px 0', borderTop: '1px solid rgba(0, 0, 0, 0.12)' }}>
                  <Button
                    type="primary"
                    funcType="raised"
                    loading={loading}
                    onClick={this.handleUpdateBoardName}
                  >
                    {'保存'}
                  </Button>
                  <Button
                    funcType="raised"
                    style={{ marginLeft: 12 }}
                    onClick={() => {
                      setFieldsValue({
                        boardName: lastBoardName,
                      });
                      this.setState({
                        boardName: initialBoardName,
                      });
                    }}
                  >
                    {'取消'}
                  </Button>
                </div>
              </div>
            )
          }

        </Spin>

      </Content>
    );
  }
}
export default withRouter(Form.create()(EditBoardName));
