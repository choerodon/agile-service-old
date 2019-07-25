import React from 'react';
import { Form, Button } from 'choerodon-ui';
import SelectFocusLoad from '../SelectFocusLoad';

let id = 1;

const FormItem = Form.Item;
function FieldIssueLinks({ form }) {
  const remove = (k) => {
    // can use data-binding to get
    const keys = form.getFieldValue('keys');
    // We need at least one passenger
    if (keys.length === 1) {
      return;
    }

    // can use data-binding to set
    form.setFieldsValue({
      keys: keys.filter(key => key !== k),
    });
  };

  const add = () => {
    // can use data-binding to get
    const keys = form.getFieldValue('keys');
    const nextKeys = keys.concat(id += 1);
    // can use data-binding to set
    // important! notify form to detect changes
    form.setFieldsValue({
      keys: nextKeys,
    });
  };
  const { getFieldDecorator, getFieldValue } = form;
  getFieldDecorator('keys', { initialValue: [0] });
  const keys = getFieldValue('keys');

  return keys.map(k => (
    <div style={{ display: 'flex' }}>
      <div style={{ flex: 1, display: 'flex' }}>
        <FormItem label="关系" style={{ width: '30%' }}>
          {getFieldDecorator(`linkTypes[${k}]`, {
          })(
            <SelectFocusLoad
              label="关系"
              type="issue_link"
            />,
          )}
        </FormItem>
        <FormItem label="问题" style={{ marginLeft: 20, width: 'calc(70% - 20px)' }}>
          {getFieldDecorator(`linkIssues[${k}]`, {
          })(
            <SelectFocusLoad
              label="问题"
              type="issues_in_link"
            />,
          )}
        </FormItem>
      </div>
      <div style={{ marginTop: 10, width: 70, marginLeft: 20 }}>
        <Button
          shape="circle"
          icon="add"
          onClick={add}
        />
        {
          keys.length > 1 ? (
            <Button
              shape="circle"
              style={{ marginLeft: 10 }}
              icon="delete"
              onClick={() => remove(k)}
            />
          ) : null
        }
      </div>
    </div>
  ));
}
export default FieldIssueLinks;
