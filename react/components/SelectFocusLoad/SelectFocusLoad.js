/* eslint-disable no-shadow */
import React, { useState, useEffect } from 'react';
import { Select, Button } from 'choerodon-ui';
import { debounce, uniqBy } from 'lodash';
import PropTypes from 'prop-types';
import Types from './Types';
import './SelectFocusLoad.less';

const { Option } = Select;
function dataConverter(data) {
  if (data instanceof Array) {
    return {
      list: data,
      hasNextPage: false,
    };
  }
  return data;
}
const propTypes = {
  type: PropTypes.string.isRequired,
};
const SelectFocusLoad = (props) => {
  const SelectRef = React.createRef();
  const { type } = props;
  const getType = () => {
    const Type = { ...Types[type], ...props };
    return Type;
  };
  const Type = getType();
  const {
    request, props: TypeProps, getDefaultValue, render,
  } = Type;
  const totalProps = { ...props, ...TypeProps };
  const {
    loadWhenMount, afterLoad, value, saveList, children, defaultOpen, defaultOption,
  } = totalProps;
  const [loading, setLoading] = useState(false);
  const [List, setList] = useState(defaultOption ? [defaultOption] : []);
  const [extraList, setExtraList] = useState(false);
  const [page, setPage] = useState(1);
  const [filter, setFilter] = useState(1);
  const [canLoadMore, setCanLoadMore] = useState(false);

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
  const loadData = ({ filter = '', page = 1, isLoadMore = false } = {}) => new Promise((resolve) => {
    setLoading(true);
    request({ filter, page }).then((data) => {
      const { list, hasNextPage } = dataConverter(data);
      const TotalList = isLoadMore ? [...List, ...list] : list;
      setPage(page);
      setFilter(filter);
      setCanLoadMore(hasNextPage);
      avoidShowError(TotalList);
      setList(TotalList);
      setLoading(false);
      resolve(TotalList);
    });
  });
  const LoadWhenMount = () => {
    if (props.loadWhenMount || loadWhenMount) {
      loadData().then((list) => {
        let defaultValue;
        if (getDefaultValue) {
          defaultValue = getDefaultValue(list);
        }
        if (afterLoad) {
          afterLoad(list, defaultValue);
        }
      });
    }
  };

  useEffect(() => {
    LoadWhenMount();
  }, []);
  useEffect(() => {
    avoidShowError();
  }, [value]);
  useEffect(() => {
    if (defaultOpen) {
      SelectRef.current.rcSelect.onDropdownVisibleChange(true);
    }
  }, []);

  const handleFilterChange = debounce((Filter) => {
    setLoading(true);
    loadData({ filter: Filter });
  }, 300);
  const loadMore = () => {
    setLoading(true);
    loadData({ filter, page: page + 1, isLoadMore: true });
  };
  const totalList = [...List, ...extraList];
  if (saveList) {
    saveList(totalList);
  }
  // 渲染去掉重复项
  const Options = uniqBy(totalList.map(render).concat(React.Children.toArray(children)), option => option.props.value);
  return (
    <Select
      filter
      filterOption={false}
      loading={loading}
      ref={SelectRef}
      // style={{ width: 200 }}      
      onFilterChange={handleFilterChange}
      {...TypeProps}
      {...props}
    >
      {Options}
      {canLoadMore && (
        <Option key="SelectFocusLoad-loadMore" className="SelectFocusLoad-loadMore" disabled>
          <Button type="primary" style={{ textAlign: 'left', width: '100%', background: 'transparent' }} onClick={loadMore}>更多</Button>
        </Option>
      )}
    </Select>
  );
};


SelectFocusLoad.propTypes = propTypes;
export default SelectFocusLoad;
