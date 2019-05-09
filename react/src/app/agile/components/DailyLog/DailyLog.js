import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import moment from 'moment';
import {
  Select, DatePicker, Button, Modal, Radio, message, Icon, InputNumber,
} from 'choerodon-ui';
import { NumericInput } from '../CommonComponent';
import { beforeTextUpload } from '../../common/utils';
import { createWorklog } from '../../api/NewIssueApi';
import WYSIWYGEditor from '../WYSIWYGEditor';
import FullEditor from '../FullEditor';
import './DailyLog.scss';

const DATA_FORMAT = 'YYYY-MM-DD HH:mm:ss';
const { Sidebar } = Modal;
const { Option } = Select;
const { AppState } = stores;
const RadioGroup = Radio.Group;
const TYPE = {
  1: 'self_adjustment',
  2: 'no_set_prediction_time',
  3: 'set_to',
  4: 'reduce',
};
const storyPointList = ['0.5', '1', '2', '3', '4', '5', '8', '13'];

class DailyLog extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dissipate: undefined,
      dissipateUnit: '小时',
      startTime: null,
      radio: 1,
      time: undefined,
      timeUnit: '小时',
      reduce: undefined,
      reduceUnit: '小时',
      delta: '',
      createLoading: false,
      dissipateNull: false,
      startTimeNull: false,
    };
  }

  onRadioChange = (e) => {
    this.setState({ radio: e.target.value });
  }

  handleFullEdit = (delta) => {
    this.setState({
      delta,
      edit: false,
    });
  }

  handleCreateDailyLog = () => {
    const {
      dissipate, startTime, radio, delta,
    } = this.state;
    const { issueId } = this.props;
    if (!dissipate || !startTime) {
      this.setState({
        dissipateNull: !dissipate,
        startTimeNull: !startTime,
      });
      return;
    }
    this.setState({ createLoading: true });
    let num;
    if (radio === '3' || radio === 3) {
      num = this.transformTime('time', 'timeUnit');
    }
    if (radio === '4' || radio === 4) {
      num = this.transformTime('reduce', 'reduceUnit');
    }
    const extra = {
      issueId,
      projectId: AppState.currentMenuType.id,
      startDate: startTime.format('YYYY-MM-DD HH:mm:ss'),
      workTime: this.transformTime('dissipate', 'dissipateUnit'),
      residualPrediction: TYPE[radio],
      predictionTime: [3, 4].indexOf(radio) === -1 ? undefined : num,
    };
    const deltaOps = delta;
    if (deltaOps) {
      beforeTextUpload(deltaOps, extra, this.handleSave);
    } else {
      extra.description = '';
      this.handleSave(extra);
    }
  };

  handleSave = (data) => {
    const { onOk } = this.props;
    createWorklog(data)
      .then((res) => {
        onOk();
      });
  };

  handleDissipateChange = (e) => {
    this.setState({ dissipate: e || '' });
  }

  handleDissipateUnitChange = (value) => {
    this.setState({ dissipateUnit: value });
  }

  handleTimeChange = (e) => {
    this.setState({ time: e || '' });
  }

  handleTimeUnitChange = (value) => {
    this.setState({ timeUnit: value });
  }

  handleReduceChange = (e) => {
    this.setState({ reduce: e || '' });
  }

  handleReduceUnitChange = (value) => {
    this.setState({ reduceUnit: value });
  }

  changeEndTime = (value) => {
    const startTime = this.transTime(value);
    this.setState({ startTime: value, startTimeNull: !value });
  }

  handleChangeDissipate = (value) => {
    const { dissipate } = this.state;
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.setState({
        dissipate: '0.5',
        dissipateNull: false,
      });
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.setState({
        dissipate: String(value).slice(0, 3), // 限制最长三位,
        dissipateNull: !value,
      });
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.setState({
        dissipate: value.slice(0, -1),
        dissipateNull: !value.slice(0, -1),
      });
    } else {
      this.setState({
        dissipate,
        dissipateNull: !dissipate,
      });
    }
  };

  handleChangeTime = (value) => {
    const { time } = this.state;
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.setState({
        time: '0.5',
      });
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.setState({
        time: String(value).slice(0, 3), // 限制最长三位,
      });
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.setState({
        time: value.slice(0, -1),
      });
    } else {
      this.setState({
        time,
      });
    }
  };

  handleChangeReduce = (value) => {
    const { reduce } = this.state;
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.setState({
        reduce: '0.5',
      });
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.setState({
        reduce: String(value).slice(0, 3), // 限制最长三位,
      });
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.setState({
        reduce: value.slice(0, -1),
      });
    } else {
      this.setState({
        reduce,
      });
    }
  };

  isEmpty(data) {
    return data === '' || data === undefined || data === null;
  }

  transformTime(pro, unit) {
    const { state } = this;
    const TIME = new Map([
      ['小时', 1],
      ['天', 8],
      ['周', 40],
    ]);
    if (!state[pro]) {
      return 0;
    } else {
      return state[pro] * TIME.get(state[unit]);
    }
  }

  formDate(data) {
    const temp = data ? new Date(data) : new Date();
    return `${temp.getFullYear()}-${temp.getMonth() + 1}-${temp.getDate()}`;
  }

  transTime(data) {
    if (this.isEmpty(data)) {
      return undefined;
    } else if (typeof data === 'string') {
      return moment(this.formDate(data), DATA_FORMAT);
    } else {
      return data.format('YYYY-MM-DD HH:mm:ss');
    }
  }

  render() {
    const {
      initValue, visible, onCancel, onOk, issueNum,
    } = this.props;
    const {
      createLoading, dissipate, dissipateUnit,
      startTime, radio, time, timeUnit, reduce,
      reduceUnit, delta, edit, startTimeNull, dissipateNull,
    } = this.state;
    const radioStyle = {
      display: 'block',
      height: '30px',
      lineHeight: '30px',
      marginBottom: '20px',
    };
    const tempAlignStyle = {
      lineHeight: '21px',
      marginBottom: -3,
      width: 100,
    };
    const callback = (value) => {
      this.setState({
        delta: value,
        edit: false,
      });
    };

    return (
      <Sidebar
        className="c7n-dailyLog"
        title="登记工作日志"
        visible={visible || false}
        onOk={this.handleCreateDailyLog}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <div>
          <h2>{`登记"${issueNum}"的工作日志`}</h2>
          <p style={{ width: 520 }}>
            您可以在这里记录您的工作，花费的时间会在关联问题中预估时间进行扣减，以便更精确地计算问题进度和提升工作效率。
          </p>
          <section className="info">
            <div className="line-info">
              <Select
                label="耗费时间*"
                value={dissipate && dissipate.toString()}
                mode="combobox"
                ref={(e) => {
                  this.componentRef = e;
                }}
                onPopupFocus={(e) => {
                  this.componentRef.rcSelect.focus();
                }}
                tokenSeparators={[',']}
                style={{ marginTop: 0, paddingTop: 0 }}
                onChange={value => this.handleChangeDissipate(value)}
              >
                {storyPointList.map(sp => (
                  <Option key={sp.toString()} value={sp}>
                    {sp}
                  </Option>
                ))}
              </Select>
              <Select
                value={dissipateUnit}
                style={{ width: 100, marginLeft: 18 }}
                onChange={this.handleDissipateUnitChange.bind(this)}
              >
                {['小时', '天', '周'].map(type => (
                  <Option key={type} value={type}>{type}</Option>))}
              </Select>
            </div>
            {dissipateNull
              ? <div className="error-text">耗费时间必填</div>
              : ''
            }
            <div
              className="dataPicker"
              style={{
                width: 218, margin: '32px 0', display: 'flex', flexDirection: 'column', position: 'relative',
              }}
            >
              <DatePicker
                label="工作日期*"
                value={startTime}
                format={DATA_FORMAT}
                onChange={this.changeEndTime}
              />
              {startTimeNull
                ? <div className="error-text">工作日期必填</div>
                : ''
              }
            </div>
            <div className="line-info">
              <RadioGroup label="剩余的估计" onChange={this.onRadioChange} value={radio}>
                <Radio style={radioStyle} value={1}>自动调整</Radio>
                <Radio style={radioStyle} value={2}>不设置预估时间</Radio>
                <Radio
                  style={{
                    ...radioStyle,
                    marginBottom: 20,
                  }}
                  value={3}
                >
                  <span style={{ display: 'inline-block', width: 52 }}>设置为</span>
                  <Select
                    disabled={radio !== 3}
                    value={time && time.toString()}
                    mode="combobox"
                    ref={(e) => {
                      this.componentRef = e;
                    }}
                    onPopupFocus={(e) => {
                      this.componentRef.rcSelect.focus();
                    }}
                    tokenSeparators={[',']}
                    style={{ marginTop: 0, paddingTop: 0 }}
                    onChange={value => this.handleChangeTime(value)}
                  >
                    {storyPointList.map(sp => (
                      <Option key={sp.toString()} value={sp}>
                        {sp}
                      </Option>
                    ))}
                  </Select>
                  <Select
                    disabled={radio !== 3}
                    style={{ width: 100, marginLeft: 18 }}
                    value={timeUnit}
                    onChange={this.handleTimeUnitChange.bind(this)}
                  >
                    {['小时', '天', '周'].map(type => (
                      <Option key={`${type}`} value={`${type}`}>{type}</Option>))}
                  </Select>
                </Radio>
                <Radio
                  style={{
                    ...radioStyle,
                    marginBottom: 20,
                  }}
                  value={4}
                >
                  <span style={{ display: 'inline-block', width: 52 }}>缩减</span>
                  <Select
                    disabled={radio !== 4}
                    value={reduce && reduce.toString()}
                    mode="combobox"
                    ref={(e) => {
                      this.componentRef = e;
                    }}
                    onPopupFocus={(e) => {
                      this.componentRef.rcSelect.focus();
                    }}
                    tokenSeparators={[',']}
                    style={{ marginTop: 0, paddingTop: 0 }}
                    onChange={value => this.handleChangeReduce(value)}
                  >
                    {storyPointList.map(sp => (
                      <Option key={sp.toString()} value={sp}>
                        {sp}
                      </Option>
                    ))}
                  </Select>
                  <Select
                    disabled={radio !== 4}
                    style={{ width: 100, marginLeft: 18 }}
                    value={reduceUnit}
                    onChange={this.handleReduceUnitChange.bind(this)}
                  >
                    {['小时', '天', '周'].map(type => (
                      <Option key={`${type}`} value={`${type}`}>{type}</Option>))}
                  </Select>
                </Radio>
              </RadioGroup>
            </div>

            <div className="c7n-sidebar-info">
              <div style={{ display: 'flex', marginBottom: '13px', alignItems: 'center' }}>
                <div style={{ fontWeight: 'bold' }}>工作说明</div>
                <div style={{ marginLeft: '80px' }}>
                  <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ edit: true })} style={{ display: 'flex', alignItems: 'center' }}>
                    <Icon type="zoom_out_map" style={{ color: '#3f51b5', fontSize: '18px', marginRight: '12px' }} />
                    <span style={{ color: '#3f51b5' }}>全屏编辑</span>
                  </Button>
                </div>
              </div>
              {
                !edit && (
                  <div className="clear-p-mw">
                    <WYSIWYGEditor
                      value={delta}
                      style={{ height: 200, width: '100%' }}
                      onChange={(value) => {
                        this.setState({ delta: value });
                      }}
                    />
                  </div>
                )
              }
            </div>
          </section>
        </div>
        {
          edit ? (
            <FullEditor
              initValue={delta}
              visible={edit}
              onCancel={() => this.setState({ edit: false })}
              onOk={callback}
            />
          ) : null
        }
      </Sidebar>
    );
  }
}
export default DailyLog;
