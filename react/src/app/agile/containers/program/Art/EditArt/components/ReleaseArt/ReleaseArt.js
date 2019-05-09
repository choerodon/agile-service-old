import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Select } from 'choerodon-ui';

const { Option } = Select;

class ReleaseArt extends PureComponent {
  state = {
    PINum: 4,
  }

  handlePINumChange = (value) => {
    this.setState({
      PINum: value,
    });
  };

  handleOk = () => {
    const { onOk } = this.props;
    const { PINum } = this.state;
    onOk(PINum);
  };

  render() {
    const {
      visible,
      onOk,
      onCancel,
      loading,
    } = this.props;
    const { PINum } = this.state;
    return (
      <Modal
        title="发布ART"
        confirmLoading={loading}
        visible={visible}
        onOk={this.handleOk}
        onCancel={onCancel}
      >
        <div style={{ fontSize: '14px', margin: '10px 0' }}>请选择你即将发布的ART的PI个数</div>
        <Select defaultValue={PINum} onChange={this.handlePINumChange} style={{ width: '100%', marginBottom: 15 }}>
          {
            [3, 4, 5, 6, 7, 8].map(value => <Option value={value}>{value}</Option>)
          }
        </Select>
      </Modal>
    );
  }
}

ReleaseArt.propTypes = {

};

export default ReleaseArt;
