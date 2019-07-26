import React from 'react';

import {
  Select, Input, InputNumber,
  Checkbox, TimePicker, Row, Col, Radio, DatePicker, 
} from 'choerodon-ui';
import moment from 'moment';
import SelectFocusLoad from '../SelectFocusLoad';

const { TextArea } = Input;
const { Option } = Select;
export default function renderField(field) {
  const {
    fieldOptions, fieldType, required, fieldName,
  } = field;
  if (fieldType === 'radio') {
    if (fieldOptions && fieldOptions.length > 0) {
      return (
        <Radio.Group
          label={fieldName}
        >
          {fieldOptions && fieldOptions.length > 0
            && fieldOptions.filter(option => option.enabled).map(item => (
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
          label={fieldName}
        >
          <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
        </Radio.Group>
      );
    }
  } else if (field.fieldType === 'checkbox') {
    if (fieldOptions && fieldOptions.length > 0) {
      return (
        <Checkbox.Group
          label={fieldName}
        >
          <Row>
            {fieldOptions && fieldOptions.length > 0
              && fieldOptions.filter(option => option.enabled).map(item => (
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
          label={fieldName}
        >
          <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
        </Checkbox.Group>
      );
    }
  } else if (field.fieldType === 'time') {
    return (
      <TimePicker
        label={fieldName}
        style={{ display: 'block' }}
        defaultOpenValue={moment('00:00:00', 'HH:mm:ss')}
        allowEmpty={!required}
      />
    );
  } else if (field.fieldType === 'datetime') {
    return (
      <DatePicker
        showTime
        label={fieldName}
        format="YYYY-MM-DD HH:mm:ss"
        style={{ display: 'block' }}
        allowClear={!required}
      />
    );
  } else if (field.fieldType === 'date') {
    return (
      <DatePicker
        label={fieldName}
        format="YYYY-MM-DD"
        style={{ display: 'block' }}
        allowClear={!required}
      />
    );
  } else if (field.fieldType === 'single') {
    return (
      <Select
        label={fieldName}   
        allowClear={!required}
      >
        {field.fieldOptions && field.fieldOptions.length > 0
          && field.fieldOptions.filter(option => option.enabled).map(item => (
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
        label={fieldName}
        mode="multiple"
      >
        {field.fieldOptions && field.fieldOptions.length > 0
          && field.fieldOptions.filter(option => option.enabled).map(item => (
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
        label={fieldName}
        step={field.extraConfig === '1' ? 0.1 : 1}
        maxLength={8}
      />
    );
  } else if (field.fieldType === 'text') {
    return (
      <TextArea
        autosize
        label={fieldName}
        maxLength={255}
      />
    );
  } else if (field.fieldType === 'member') {
    return (
      <SelectFocusLoad
        label={fieldName}            
        allowClear
        type="user"
        loadWhenMount
      />
    );
  } else {
    return (
      <Input
        label={fieldName}
        maxLength={100}
      />
    );
  }
}
