import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Modal, Form, Select, DatePicker } from 'choerodon-ui';
import { Page, Header, Content, stores } from 'choerodon-front-boot';
import moment from 'moment';
import _ from 'lodash';
import AccumulationStore from '../../../../../../stores/project/accumulation/AccumulationStore';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const Option = Select.Option;

@observer
class AccumulationFilter extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }
  setStoreCheckData(data, id, params, array) {
    const newData = _.clone(data);
    for (let index = 0, len = newData.length; index < len; index += 1) {
      if (array) {
        if (id.indexOf(String(newData[index][params])) !== -1) {
          newData[index].check = true;
        } else {
          newData[index].check = false;
        }
      } else if (String(newData[index][params]) === String(id)) {
        newData[index].check = true;
      } else {
        newData[index].check = false;
      }
    }
    return newData;
  }
  changeStartDate(id) {
    if (id === 1) {
      this.props.form.setFieldsValue({
        startDate: moment().subtract(1, 'weeks'),
      });
      this.props.form.setFieldsValue({
        endDate: moment(),
      });
    }
    if (id === 2) {
      this.props.form.setFieldsValue({
        startDate: moment().subtract(2, 'weeks'),
      });
      this.props.form.setFieldsValue({
        endDate: moment(),
      });
    }
    if (id === 3) {
      this.props.form.setFieldsValue({
        startDate: moment().subtract(1, 'months'),
      });
      this.props.form.setFieldsValue({
        endDate: moment(),
      });
    }
    if (id === 4) {
      this.props.form.setFieldsValue({
        startDate: moment().subtract(3, 'months'),
      });
      this.props.form.setFieldsValue({
        endDate: moment(),
      });
    }
    if (id === 5) {
      this.props.form.setFieldsValue({
        startDate: moment().subtract(6, 'months'),
      });
      this.props.form.setFieldsValue({
        endDate: moment(),
      });
    }
    if (id === 6) {
      this.props.form.setFieldsValue({
        startDate: moment('2018-5-23'),
      });
      this.props.form.setFieldsValue({
        endDate: moment(),
      });
    }
  }

  handleOk(e) {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        AccumulationStore.setTimeData(this.setStoreCheckData(AccumulationStore.getTimeData, value.circle, 'id'));
        AccumulationStore.setStartDate(value.startDate);
        AccumulationStore.setEndDate(value.endDate);
        AccumulationStore.setBoardList(this.setStoreCheckData(AccumulationStore.getBoardList, value.board, 'boardId'));
        AccumulationStore.setColumnData(this.setStoreCheckData(AccumulationStore.getColumnData, value.column, 'columnId', 'array'));
        AccumulationStore.setFilterList(this.setStoreCheckData(AccumulationStore.getFilterList, value.fast, 'filterId', 'array'));
        this.props.getData();
      }
    });
  }
  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Sidebar
        title="设置"
        visible={this.props.visible}
        cancelText="取消"
        onCancel={this.props.onCancel}
        okText="保存"
        onOk={this.handleOk.bind(this)}
      >
        <Content
          title="修改迭代冲刺“xxxx”的累积流量图"
          description="请在下面输入模块名称、模块概要、负责人和默认经办人策略，创建新模版。"
          style={{
            padding: 0,
          }}
        >
          <Form style={{ width: 512 }}>
            <p className="c7n-accumulation-sidebarP">报告时间</p>
            <FormItem>
              {
                getFieldDecorator('circle', {
                  rules: [{
                    required: true,
                    message: '周期是必填的',
                  }],
                  initialValue: this.props.getTimeType(AccumulationStore.getTimeData, 'id'),
                })(
                  <Select
                    label="周期"
                    onChange={this.changeStartDate.bind(this)}
                  >
                    {
                      AccumulationStore.getTimeData.map(item => (
                        <Option value={item.id}>{item.name}</Option>
                      ))
                    }
                  </Select>,
                )
              }
            </FormItem>
            <FormItem>
              {
                getFieldDecorator('startDate', {
                  rules: [{
                    required: true,
                    message: '开始日期是必填的',
                  }],
                  initialValue: AccumulationStore.getStartDate,
                })(
                  <DatePicker
                    label="开始日期"
                    onChange={() => {
                      this.props.form.setFieldsValue({
                        circle: 7,
                      });
                    }}
                  />,
                )
              }
            </FormItem>
            <FormItem>
              {
                getFieldDecorator('endDate', {
                  rules: [{
                    required: true,
                    message: '结束日期是必填的',
                  }],
                  initialValue: AccumulationStore.getEndDate,
                })(
                  <DatePicker
                    label="结束日期"
                    onChange={() => {
                      this.props.form.setFieldsValue({
                        circle: 7,
                      });
                    }}
                  />,
                )
              }
            </FormItem>
            <p className="c7n-accumulation-sidebarP">报告筛选</p>
            <FormItem>
              {
                getFieldDecorator('board', {
                  rules: [{
                    required: true,
                    message: '看板是必填的',
                  }],
                  initialValue: this.props.getTimeType(AccumulationStore.getBoardList, 'boardId'),
                })(
                  <Select
                    label="看板"
                    onChange={(value) => {
                      this.props.getColumnData(value);
                    }}
                  >
                    {
                      AccumulationStore.getBoardList.map(item => (
                        <Option value={item.boardId}>{item.name}</Option>
                      ))
                    }
                  </Select>,
                )
              }
            </FormItem>
            <FormItem>
              {
                getFieldDecorator('column', {
                  initialValue: this.props.getTimeType(AccumulationStore.getColumnData, 'columnId', 'array'),
                })(
                  <Select
                    label="列"
                    mode="tags"
                  >
                    {
                      AccumulationStore.getColumnData.map(item => (
                        <Option value={String(item.columnId)}>{item.name}</Option>
                      ))
                    }
                  </Select>,
                )
              }
            </FormItem>
            <FormItem>
              {
                getFieldDecorator('fast', {
                  initialValue: this.props.getTimeType(AccumulationStore.getFilterList, 'filterId', 'array'),
                })(
                  <Select
                    label="快速搜索"
                    mode="tags"
                  >
                    {
                      AccumulationStore.getFilterList.map(item => (
                        <Option value={String(item.filterId)}>{item.name}</Option>
                      ))
                    }
                  </Select>,
                )
              }
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(AccumulationFilter);
