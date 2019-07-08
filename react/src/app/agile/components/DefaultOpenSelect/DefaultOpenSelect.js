import React, { useEffect } from 'react';
import { Select } from 'choerodon-ui';

const { Option } = Select;

const DefaultOpenSelect = ({ ...otherProps }) => {
  const textInput = React.createRef();
  useEffect(() => {
    textInput.current.rcSelect.onDropdownVisibleChange(true);
  }, []);
  return (
    <Select ref={textInput} {...otherProps} />
  );
};

DefaultOpenSelect.Option = Option;
export default DefaultOpenSelect;
