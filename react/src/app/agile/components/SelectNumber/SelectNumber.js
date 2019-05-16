import React, { Component } from 'react';
import {
  Select,
} from 'choerodon-ui';

const selectValues = ['0.5', '1', '2', '3', '4', '5', '8', '13'];
const { Option } = Select;
class SelectNumber extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: props.value || undefined,
    };
  }

  static getDerivedStateFromProps(nextProps) {
    if ('value' in nextProps) {
      return {
        value: nextProps.value,
      };
    }
    return null;
  }

  handleChange = (value) => {
    const { value: preValue } = this.state;    
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.triggerChange('0.5');      
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.triggerChange(String(value).slice(0, 3));
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.triggerChange(value.slice(0, -1));    
    } else {
      this.triggerChange(preValue);         
    }
  };

  triggerChange = (value) => {
    const { onChange } = this.props;
    if (!('value' in this.props)) {
      this.setState({ value });
    }
    if (onChange) {
      onChange(value);
    }
  }

  render() {
    const { value } = this.state;
    return (
      <Select
        {...this.props}
        label="故事点"
        value={value}
        mode="combobox"        
        tokenSeparators={[',']}        
        onChange={this.handleChange}
      >
        {selectValues.map(sp => (
          <Option key={sp.toString()} value={sp}>
            {sp}
          </Option>
        ))}
      </Select>
    );
  }
}
export default SelectNumber;
