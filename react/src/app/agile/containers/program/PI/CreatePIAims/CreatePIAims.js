import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Modal, Form, Select, Input, Checkbox,
} from 'choerodon-ui';
import {
  stores,
} from 'choerodon-front-boot';
import moment from 'moment';
import PIStore from '../../../../stores/program/PI/PIStore';
import { createPIAims, getPIList, getPIAims } from '../../../../api/PIApi';

const formatter = 'YYYY-MM-DD';
const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { Option } = Select;
const BV = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
@observer
class CreatePIAims extends Component {
  handleCheckboxChange = (e) => {
    PIStore.setCreateStretch(e.target.checked);
  }

  handleOnOk = () => {
    const { form, piId, artId } = this.props;
    const { createStretch } = PIStore;
    form.validateFields((err, values) => {
      if (!err) {
        PIStore.setPIAimsLoading(true);
        const piObjectiveDTO = {
          levelCode: 'program',
          programId: AppState.currentMenuType.id,
          projectId: AppState.currentMenuType.id,
          piId: values.piId || piId,
          name: values.name,
          planBv: values.planBv,
          actualBv: values.actualBv,
          stretch: createStretch,
        };
        createPIAims(piObjectiveDTO).then(() => {
          Promise.all([getPIList(artId), getPIAims(values.piId || piId)]).then(([PIList, piAims]) => {
            PIStore.setPIList(PIList.content.map(item => (
              Object.assign(item, {
                startDate: moment(item.startDate).format(formatter),
                endDate: moment(item.endDate).format(formatter),
                remainDays: moment(item.endDate).diff(moment(), 'days') > 0 ? moment(item.endDate).diff(moment(), 'days') : 0,
              })
            )));
            PIStore.setPIAims(piAims);
            PIStore.setEditPiAimsCtrl(piAims.program.map((item, index) => (
              {
                isEditing: false,
                editingId: item.id,
                editingIndex: index,  
              }
            )));
            PIStore.setPIAimsLoading(false);
            form.resetFields();
            PIStore.setCreatePIVisible(false);
            PIStore.setCreateStretch(false);
          });
        });
      }
    }); 
  }

  handleOnCancel = () => {
    const { form } = this.props;
    form.resetFields();
    PIStore.setCreatePIVisible(false);
    PIStore.setCreateStretch(false);
  }

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    // eslint-disable-next-line no-unused-vars
    const { PIList, createPIVisible, createStretch } = PIStore;
    return (
      <Sidebar
        className="c7n-pi-createPISideBar"
        title="创建PI目标"
        visible={createPIVisible}
        cancelText="取消"
        okText="创建"
        onOk={this.handleOnOk}
        onCancel={this.handleOnCancel}
      >
        <Form>
          {/* <FormItem label="PI" style={{ width: 520 }}>
            {getFieldDecorator('piId', {
              rules: [{ required: true, message: 'PI为必选项' }],
            })(
              <Select
                label="PI"
                allowClear
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {PIList.map(
                  pi => (
                    <Option
                      key={pi.id}
                      value={pi.id}
                    >
                      {`${pi.code}-${pi.name}`}
                    </Option>
                  ),
                )}
              </Select>,
            )}
          </FormItem> */}
          <FormItem style={{ width: 520 }}>
            {getFieldDecorator('name', {
              rules: [{ required: true, message: 'PI目标名称为必输项' }],
            })(
              <Input label="PI目标名称" placeholder="请输入PI目标名称" maxLength={44} />,
            )}
          </FormItem>
          <FormItem style={{ width: 520 }}>
            {
                getFieldDecorator('planBv')(
                  <Select label="计划商业价值">
                    {
                      BV.map(value => (<Option key={value} value={value}>{value}</Option>))
                    }
                  </Select>,
                )
              }
          </FormItem>
          <FormItem style={{ width: 520 }}>
            {
               getFieldDecorator('actualBv')(
                 <Select label="实际商业价值">
                   {
                    BV.map(value => (<Option key={value} value={value}>{value}</Option>))
                  }
                 </Select>,
               )
              }
          </FormItem>
          <Checkbox checked={createStretch} onChange={this.handleCheckboxChange}>延伸目标</Checkbox>
        </Form>
      </Sidebar>
    );
  }
}

export default Form.create()(CreatePIAims);
