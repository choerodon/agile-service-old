import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import {
  Modal, DatePicker, Form,
} from 'choerodon-ui';

const FormItem = Form.Item;


const handleCreatePIOk = (props) => {
  const { form, onCreatePIOk } = props;
  form.validateFields((err, values) => {
    if (!err) {
      // onCreatePIOk(new Date(moment(values.PIStartDate)));
      onCreatePIOk(moment(values.PIStartDate).format('YYYY-MM-DD HH:mm:ss'));
    }
  });
};

const handleCreatePICancel = (props) => {
  const { onCreatePICancel } = props;
  onCreatePICancel();
};

const CreatePIModal = (props) => {
  const {
    visible, name, form, defaultStartDate,
  } = props;
  const { getFieldDecorator } = form; 
  return (
    <Modal
      visible={visible}
      onOk={() => { handleCreatePIOk(props); }}
      onCancel={() => { handleCreatePICancel(props); }}
      title="创建下一批PI"
    >
      <p style={{ marginTop: 20 }}>{`您正在为 ${name} 创建下一批PI, 默认开始时间为上一个PI的结束时间。`}</p>
      <Form>
        <FormItem>
          {
              getFieldDecorator('PIStartDate', {
                rules: [{
                  required: true, message: '请选择PI开始时间!',
                }],
                initialValue: moment(defaultStartDate),
              })(<DatePicker
                style={{ width: '100%' }}
                label="PI开始时间"
                allowClear
                format="YYYY-MM-DD"
                disabledDate={current => current < moment(defaultStartDate).endOf('day').subtract(1, 'days')}
              />)
          }
        </FormItem>
      </Form>
       
    </Modal>
  );
};

CreatePIModal.prototype = {
  visible: PropTypes.bool.isRequired,
  onCreatePIOk: PropTypes.func.isRequired,
  onCreatePICancel: PropTypes.func.isRequired,
};

export default Form.create()(CreatePIModal);
