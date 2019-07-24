import React, { useState, useEffect } from 'react';
import { Select } from 'choerodon-ui';
import { debounce } from 'lodash';
import PropTypes from 'prop-types';
import Types from './Types';

const propTypes = {
  type: PropTypes.string.isRequired,
};
const SelectFocusLoad = (props) => {
  const {
    type, afterLoad, loadWhenMount, value, saveList, children,
  } = props;
  const [loading, setLoading] = useState(false);
  const [List, setList] = useState(false);
  const [extraList, setExtraList] = useState(false);

  const getType = () => {
    const Type = { ...Types[type], ...props };
    return Type;
  };
  const Type = getType();
  const {
    request, props: TypeProps, getDefaultValue, render,
  } = Type;
  // 防止取值不在option列表中，比如user
  const avoidShowError = (ListDate = List) => {
    if (Type.avoidShowError) {
      Type.avoidShowError(props, ListDate).then((extra) => {
        if (extraList) {
          setExtraList(extra);
        }
      });
    }
  };
  const loadData = () => {
    if (props.loadWhenMount || loadWhenMount) {
      setLoading(true);
      request().then((Data) => {
        avoidShowError(Data);
        setList(Data);
        setLoading(false);
        let defaultValue;
        if (getDefaultValue) {
          defaultValue = getDefaultValue(Data);
        }
        if (afterLoad) {
          afterLoad(Data, defaultValue);
        }
      });
    }
  };
  useEffect(() => {
    loadData();
  }, []);
  useEffect(() => {
    avoidShowError();
  }, [value]);


  const handleFilterChange = debounce((filter) => {
    setLoading(true);
    request(filter).then((Data) => {
      avoidShowError(Data);
      setList(Data);
      setLoading(false);
    });
  }, 300);
  const totalList = [...List, ...extraList];
  if (saveList) {
    saveList(totalList);
  }
  const Options = totalList.map(render).concat(React.Children.toArray(children));
  return (
    <Select
      filter
      filterOption={false}
      loading={loading}
        // style={{ width: 200 }}      
      onFilterChange={handleFilterChange}
      {...TypeProps}
      {...props}
    >
      {Options}
    </Select>
  );
};


SelectFocusLoad.propTypes = propTypes;
export default SelectFocusLoad;
