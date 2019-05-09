import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Button, Icon, Dropdown, Input, Menu, Form,
} from 'choerodon-ui';
import TypeTag from '../../../../../../components/TypeTag';

const FormItem = Form.Item;

@Form.create({})
@inject('AppState', 'HeaderStore')
@observer class SprintHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      expand: false,
      currentType: props.defaultType,
    };
  }

  handleChangeType = ({ key }) => {
    const { issueType } = this.props;
    const currentSelect = issueType.find(type => type.typeCode === key);
    this.setState({
      currentType: currentSelect,
    });
  };

  handleBlurCreateIssue = (e) => {
    e.preventDefault();
    const { form, handleCreateIssue } = this.props;
    const { currentType } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        this.setState({
          loading: true,
        });
        handleCreateIssue.bind(this, currentType, values.summary)();
      }
    });
  };

  render() {
    const {
      issueType, form, defaultType,
    } = this.props;
    const { getFieldDecorator } = form;
    const { expand, currentType, loading } = this.state;
    const typeList = (
      <Menu
        style={{
          background: '#fff',
          boxShadow: '0 5px 5px -3px rgba(0, 0, 0, 0.20), 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12)',
          borderRadius: '2px',
        }}
        onClick={this.handleChangeType}
      >
        {
          issueType.map(type => (
            <Menu.Item key={type.typeCode}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <TypeTag
                  data={type}
                  showName
                />
              </div>
            </Menu.Item>
          ))
        }
      </Menu>
    );
    return (
      <div className="c7n-backlog-sprintIssue">
        <div
          style={{
            userSelect: 'none',
            padding: '10px 0 10px 33px',
            fontSize: 13,
            display: 'flex',
            alignItems: 'center',
          }}
        >
          {expand ? (
            <div className="c7n-backlog-sprintIssueSide" style={{ display: 'block', width: '100%' }}>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Form onSubmit={this.handleBlurCreateIssue} style={{ width: 640 }}>
                  <div style={{ display: 'flex' }}>
                    <Dropdown overlay={typeList} trigger={['click']} getPopupContainer={trigger => trigger.parentNode}>
                      <div style={{ display: 'flex', alignItem: 'center' }}>
                        <TypeTag
                          data={currentType || defaultType}
                        />
                        <Icon
                          type="arrow_drop_down"
                          style={{ fontSize: 16 }}
                        />
                      </div>
                    </Dropdown>
                    <FormItem label="summary" style={{ flex: 1 }}>
                      {getFieldDecorator('summary', {
                        rules: [{ required: true, message: '请输入问题概要！' }],
                      })(
                        <Input
                          autoFocus
                          maxLength={44}
                          placeholder="需要做什么"
                        />,
                      )}
                    </FormItem>
                  </div>
                  <div style={{
                    margin: '10px 0 5px',
                    display: 'flex',
                    justifyContent: 'flex-start',
                    paddingRight: 70,
                  }}
                  >
                    <FormItem>
                      <Button
                        type="primary"
                        onClick={() => {
                          this.setState({
                            expand: false,
                            loading: false,
                          });
                        }}
                      >
                        {'取消'}
                      </Button>
                    </FormItem>
                    <FormItem>
                      <Button
                        type="primary"
                        htmlType="submit"
                        loading={loading}
                      >
                        {'确定'}
                      </Button>
                    </FormItem>
                  </div>
                </Form>
              </div>
            </div>
          ) : (
            <div className="c7n-backlog-sprintIssueSide">
              <Button
                className="leftBtn"
                functyp="flat"
                style={{
                  color: '#3f51b5',
                }}
                onClick={() => {
                  this.setState({
                    expand: true,
                  });
                }}
              >
                <Icon type="playlist_add" />
                {'创建问题'}
              </Button>
            </div>
          )}
        </div>
      </div>
    );
  }
}

export default SprintHeader;
