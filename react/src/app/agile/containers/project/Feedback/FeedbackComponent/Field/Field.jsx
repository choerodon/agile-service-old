import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import moment from 'moment';
import _ from 'lodash';
import {
  Checkbox, Select, Input, TimePicker, Row, Col, Radio, DatePicker, InputNumber,
} from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../../components/TextEditToggle';
import UserHead from '../UserHead';
import { getUsers } from '../../../../../api/CommonApi';
import './Field.less';

const { TextArea } = Input;
const { Option } = Select;
const { Text, Edit } = TextEditToggle;
let sign = false;

const STATUS = {
  todo: '#ffb100',
  doing: '#4d90fe',
  done: '#00bfa5',
  cancel: '#393E46',
};
  

@observer class Field extends Component {
  debounceFilterIssues = _.debounce((input) => {
    this.setState({ selectLoading: true });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.list,
        selectLoading: false,
      });
    });
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      newValue: '',
      originUsers: [],
      selectLoading: false,
    };
  }

  componentDidMount() {

  }

  handleChange = (e) => {
    const { field } = this.props;
    const { fieldType } = field;
    if (fieldType === 'time' || fieldType === 'datetime' || fieldType === 'date') {
      this.setState({
        newValue: e && e.format('YYYY-MM-DD HH:mm:ss'),
      });
    } else if (e && e.target) {
      this.setState({
        newValue: e.target.value,
      });
    } else {
      this.setState({
        newValue: e,
      }, () => {
        // console.log(this.state.newValue);
      });
    }
  };

    updateIssueField = () => {
      const { newValue } = this.state;
      const {
        field, handleUpdate,
      } = this.props;
      const {
        fieldType, value, required, fieldName,
      } = field;
      if (required) {
        if ((fieldType === 'number' && newValue === undefined) || (fieldType !== 'number' && !newValue)) {
          Choerodon.prompt(`${fieldName}必填！`);
          return;
        } else if (newValue instanceof Array && newValue.length === 0) {
          Choerodon.prompt(`${fieldName}必填！`);
          return;
        }
      }
      if (fieldName === '经办人') {
        if ((!value && newValue) || (value && JSON.stringify(value.id) !== JSON.stringify(newValue))) {
          handleUpdate(fieldName, newValue);
        }
      } else if (JSON.stringify(value) !== JSON.stringify(newValue)) {
        handleUpdate(fieldName, newValue);
      }
    };

    onFilterChange(input) {
      if (!sign) {
        this.setState({
          selectLoading: true,
        });
        getUsers(input).then((res) => {
          this.setState({
            originUsers: res.list,
            selectLoading: false,
          });
        });
        sign = true;
      } else {
        this.debounceFilterIssues(input);
      }
    }

  renderField = () => {
    const { selectLoading, originUsers } = this.state;
    const { field } = this.props;
    const {
      fieldOptions, fieldType, required, value,
    } = field;
    if (fieldType === 'radio') {
      if (fieldOptions && fieldOptions.length > 0) {
        return (
          <Radio.Group
            className="fieldWith"
            onChange={e => this.handleChange(e)}
          >
            {fieldOptions && fieldOptions.length > 0
              && fieldOptions.filter(option => option.enabled || option.id === value).map(item => (
                <Radio
                  className="radioStyle"
                  value={item.id}
                  key={item.id}
                >
                  {item.value}
                </Radio>
              ))}
          </Radio.Group>
        );
      } else {
        return (
          <Radio.Group
            className="fieldWith"
            onChange={e => this.handleChange(e)}
          >
            <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
          </Radio.Group>
        );
      }
    } else if (field.fieldType === 'checkbox') {
      if (fieldOptions && fieldOptions.length > 0) {
        return (
          <Checkbox.Group
            className="fieldWith"
            onChange={e => this.handleChange(e)}
          >
            <Row>
              {fieldOptions && fieldOptions.length > 0
                && fieldOptions.filter(option => option.enabled || value.indexOf(option.id) !== -1).map(item => (
                  <Col
                    span={24}
                    key={item.id}
                  >
                    <Checkbox
                      value={item.id}
                      key={item.id}
                      className="checkboxStyle"
                    >
                      {item.value}
                    </Checkbox>
                  </Col>
                ))}
            </Row>
          </Checkbox.Group>
        );
      } else {
        return (
          <Checkbox.Group
            className="fieldWith"
            onChange={e => this.handleChange(e)}
          >
            <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
          </Checkbox.Group>
        );
      }
    } else if (field.fieldType === 'time') {
      return (
        <TimePicker
          className="fieldWith"
          defaultOpenValue={moment('00:00:00', 'HH:mm:ss')}
          allowEmpty={!required}
          onChange={e => this.handleChange(e)}
        />
      );
    } else if (field.fieldType === 'datetime') {
      return (
        <DatePicker
          showTime
          format="YYYY-MM-DD HH:mm:ss"
          className="fieldWith"
          allowClear={!required}
          onChange={e => this.handleChange(e)}
        />
      );
    } else if (field.fieldType === 'date') {
      return (
        <DatePicker
          format="YYYY-MM-DD"
          className="fieldWith"
          allowClear={!required}
          onChange={e => this.handleChange(e)}
        />
      );
    } else if (field.fieldType === 'single') {
      return (
        <Select
          dropdownMatchSelectWidth
          className="fieldWith"
          onChange={e => this.handleChange(e)}
        >
          {field.fieldOptions && field.fieldOptions.length > 0
            && field.fieldOptions.map(item => (
              <Option
                value={item.id}
                key={item.id}
              >
                {item.value}
              </Option>
            ))}
        </Select>
      );
    } else if (field.fieldType === 'multiple') {
      return (
        <Select
          dropdownMatchSelectWidth
          mode="multiple"
          className="fieldWith"
          onChange={e => this.handleChange(e)}
        >
          {field.fieldOptions && field.fieldOptions.length > 0
            && field.fieldOptions.filter(option => option.enabled || (value && value.indexOf(option.id) !== -1)).map(item => (
              <Option
                value={item.id}
                key={item.id}
              >
                {item.value}
              </Option>
            ))}
        </Select>
      );
    } else if (field.fieldType === 'number') {
      return (
        <InputNumber
          autoFocus
          onChange={e => this.handleChange(e)}
          className="fieldWith"
          step={field.extraConfig === '1' ? 0.1 : 1}
          maxLength={8}
        />
      );
    } else if (field.fieldType === 'text') {
      return (
        <TextArea
          autoFocus
          autosize
          className="fieldWith"
          onChange={e => this.handleChange(e)}
          maxLength={255}
        />
      );
    } else if (field.fieldType === 'member') {
      return (
        <Select
          loading={selectLoading}
          getPopupContainer={triggerNode => triggerNode.parentNode}
          filter
          filterOption={false}
          allowClear
          onFilterChange={this.onFilterChange.bind(this)}
          onChange={e => this.handleChange(e)}
        >
          {originUsers.filter(user => (field.valueStr ? user.id !== field.valueStr.id : true) && user.enabled).concat(field.valueStr || []).map(user => (
            <Option key={user.id} value={user.id}>
              <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
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
        </Select>
      );
    } else {
      return (
        <Input
          autoFocus
          maxLength={100}
          className="fieldWith"
          onChange={e => this.handleChange(e)}
        />
      );
    }
  };

  renderFieldText = () => {
    const { field } = this.props;
    const { fieldCode, valueStr, value } = field;
    if (!valueStr) {
      return '无';
    } else if (fieldCode === 'member') {
      return (
        <UserHead
          user={{
            id: valueStr.id,
            loginName: valueStr.loginName,
            realName: valueStr.realName,
            avatar: valueStr.imageUrl,
          }}
        />
      );
    } else if (fieldCode === 'status') {
      return (
        <div
          style={{
            background: STATUS[value],
            color: '#fff',
            borderRadius: '2px',
            padding: '0 8px',
            display: 'inline-block',
            margin: '2px auto 2px 0',
          }}
        >
          {valueStr}
        </div>
      );
    } else {
      return <span>{valueStr}</span>;
    }
  }

  transform = (fieldType, value) => {
    if (fieldType === 'time' || fieldType === 'datetime' || fieldType === 'date') {
      return value ? moment(value) : undefined;
    } else if (fieldType === 'member') {
      return value ? value.id : null;
    } else if (value instanceof Array) {
      return value.slice();
    } else {
      return value;
    }
  };

  render() {
    const { field } = this.props;
    const {
      fieldCode, fieldName, value, fieldType, valueStr,
    } = field;

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {`${fieldName}：`}
          </span>
        </div>
        <div className="c7n-value-wrapper" style={{ width: 'auto' }}>
          <TextEditToggle
            formKey={fieldCode}
            onSubmit={this.updateIssueField}
            originData={this.transform(fieldType, value)}
          // rules={[
          //   { required, message: `${fieldName}必填！` },
          // ]}
          >
            <Text key="text">
              <div style={{ maxWidth: 200, wordBreak: 'break-all', whiteSpace: 'pre-line' }}>
                {
                    this.renderFieldText()
                }
                
              </div>
            </Text>
            <Edit key="edit">
              {this.renderField()}
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(Field));
