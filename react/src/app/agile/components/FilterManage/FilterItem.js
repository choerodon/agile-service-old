import React, { Component, Fragment } from 'react';
import { stores } from 'choerodon-front-boot';
import PropTypes from 'prop-types';
import {
  Input, Form, Icon, Popconfirm, 
} from 'choerodon-ui';
import { checkMyFilterName, updateMyFilter, deleteMyFilter } from '../../api/NewIssueApi';
import './FilterItem.scss';

const FormItem = Form.Item;
const { AppState } = stores;
const propTypes = {
  filter: PropTypes.shape({}).isRequired,
  onUpdate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
};
class FilterItem extends Component {
  state = {
    editing: false,
  }

  checkMyFilterNameRepeat = (rule, value, callback) => {
    const { filter: { name } } = this.props;
    if (value === name) {
      callback();
    } else {
      checkMyFilterName(value).then((res) => {
        if (res) {
          callback('名称重复');
        } else {
          callback();
        }
      });
    }
  }

  handleSubmit = () => {
    const { form, onUpdate, filter: { filterId, objectVersionNumber } } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const { name } = values;
        const data = {
          filterId,
          objectVersionNumber,
          name,
          projectId: AppState.currentMenuType.id,
          userId: AppState.userInfo.id,
        };
        updateMyFilter(filterId, data).then((res) => {
          this.setState({
            editing: false,
          });
          onUpdate(name);
        });
      }
    });
  }

  handleDelete = () => {
    const { onDelete, filter: { filterId } } = this.props;
    deleteMyFilter(filterId).then(() => {
      onDelete();
    });
  }

  handleCancel = () => {
    this.setState({
      editing: false,
    });
  }

  handleEdit = () => {
    this.setState({
      editing: true,
    });
  }

  render() {
    const { editing } = this.state;
    const { filter: { name }, form: { getFieldDecorator } } = this.props;
    return (
      <div className="c7nagile-FilterItem">
        {editing
          ? (
            <FormItem>
              {getFieldDecorator('name', {
                rules: [{
                  required: true, message: '名称必填',
                }, {
                  validator: this.checkMyFilterNameRepeat,
                }],
                initialValue: name,
              })(
                <Input style={{ flex: 1 }} autoFocus maxLength="10" />,
              )}
            </FormItem>
          )

          : <div className="c7nagile-FilterItem-text">{name}</div>
        }
        {
          editing ? (
            <Fragment>
              <Icon type="check" onClick={this.handleSubmit} />
              <Icon type="close" onClick={this.handleCancel} />
            </Fragment>
          )
            : (
              <Fragment>
                <Icon type="mode_edit" onClick={this.handleEdit} />
                <Popconfirm placement="leftBottom" title="确认要删除筛选?" onConfirm={this.handleDelete} okText="确定" cancelText="取消">
                  <Icon type="delete_forever" />
                </Popconfirm>               
              </Fragment>
            )
        }
      </div>
    );
  }
}

FilterItem.propTypes = {

};

export default Form.create({})(FilterItem);
