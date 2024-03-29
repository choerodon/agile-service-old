import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import moment from 'moment';
import _ from 'lodash';
import {
  Checkbox, Select, Input, TimePicker, Row, Col, Radio, DatePicker, InputNumber,
} from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import SelectFocusLoad from '../../../../SelectFocusLoad';
import { updateFieldValue } from '../../../../../api/NewIssueApi';
import UserHead from '../../../../UserHead';
import { getUsers } from '../../../../../api/CommonApi';
import './Field.scss';


const { TextArea } = Input;
const { Option } = Select;
const { Text, Edit } = TextEditToggle;
let sign = false;

@inject('AppState')
@observer class IssueField extends Component {
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
      });
    }
  };

  updateIssueField = () => {
    const { newValue } = this.state;
    const {
      store, onUpdate, reloadIssue, field,
    } = this.props;
    const issue = store.getIssue;
    const {
      fieldId, fieldType, value, required, fieldName,
    } = field;
    const { issueId } = issue;
    if (required) {
      if ((fieldType === 'number' && newValue === undefined) || (fieldType !== 'number' && !newValue)) {
        Choerodon.prompt(`${fieldName}必填！`);
        return;
      } else if (newValue instanceof Array && newValue.length === 0) {
        Choerodon.prompt(`${fieldName}必填！`);
        return;
      }
    }
    if (JSON.stringify(value) !== JSON.stringify(newValue)) {
      const obj = {
        fieldType,
        value: newValue,
      };
      updateFieldValue(issueId, fieldId, 'agile_issue', obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
        });
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
          allowClear={!required}
          onChange={e => this.handleChange(e)}
        >
          {field.fieldOptions && field.fieldOptions.length > 0
            && field.fieldOptions.filter(option => option.enabled || option.id === value).map(item => (
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
        <SelectFocusLoad
          type="user"
          loading={selectLoading}
          filter
          filterOption={false}
          defaultOption={field.valueStr || undefined}
          defaultOpen
          allowClear          
          onChange={e => this.handleChange(e)}
        />
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

  transform = (fieldType, value) => {
    if (fieldType === 'time' || fieldType === 'datetime' || fieldType === 'date') {
      return value ? moment(value) : undefined;
    } else if (value instanceof Array) {
      return value.slice();
    } else {
      return value;
    }
  };

  render() {
    const { field, disabled } = this.props;
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
            disabled={disabled}            
            formKey={fieldCode}
            onSubmit={this.updateIssueField}
            originData={this.transform(fieldType, value)}
          // rules={[
          //   { required, message: `${fieldName}必填！` },
          // ]}
          >
            <Text key="text">
              <div style={{ maxWidth: 200, wordBreak: 'break-all', whiteSpace: 'pre-line' }}>
                {fieldType === 'member' && valueStr
                  ? (
                    <UserHead
                      user={{
                        id: valueStr.id,
                        loginName: valueStr.loginName,
                        realName: valueStr.realName,
                        avatar: valueStr.imageUrl,
                      }}
                    />
                  ) : (valueStr || '无')}
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

export default withRouter(injectIntl(IssueField));
