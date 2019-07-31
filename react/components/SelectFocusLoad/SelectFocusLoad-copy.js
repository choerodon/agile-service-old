import React, { Component } from 'react';
import { Select, Button } from 'choerodon-ui';
import { debounce, uniqBy } from 'lodash';
import PropTypes from 'prop-types';
import Types from './Types';

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

const SelectRef = React.createRef();

class SelectFocusLoad extends Component {
  constructor(props) {
    super(props);
    const { type } = this.props;
    const getType = () => {
      const Type = { ...Types[type], ...props };
      return Type;
    };
    const Type = getType();
    const {
      props: TypeProps,
    } = Type;
    const totalProps = { ...this.props, ...TypeProps };
    const {
      defaultOption,
    } = totalProps;
    this.state = {
      loading: false,
      List: defaultOption ? [defaultOption] : [],
      extraList: false,
      page: 1,
      filter: 1,
      canLoadMore: false,
    };
  }

  // 防止取值不在option列表中，比如user
  avoidShowError = (ListDate = this.state.List) => {
    const { type } = this.props;
    const getType = () => {
      const Type = { ...Types[type], ...this.props };
      return Type;
    };
    const Type = getType();
    if (Type.avoidShowError) {
      Type.avoidShowError(this.props, ListDate).then((extra) => {
        if (this.state.extraList) {
          this.setState({
            extraList: extra,
          });
        }
      });
    }
  };

  loadData = ({ filter = '', page = 1, isLoadMore = false } = {}) => new Promise((resolve) => {
    const { type } = this.props;
    const getType = () => {
      const Type = { ...Types[type], ...this.props };
      return Type;
    };
    const Type = getType();
    const {
      request,
    } = Type;
    this.setState({
      loading: true,
    });
    request({ filter, page }).then((data) => {
      const { list, hasNextPage } = dataConverter(data);
      const { List } = this.state;
      const TotalList = isLoadMore ? [...List, ...list] : list;
      this.setState({
        page,
        filter,
        canLoadMore: hasNextPage,
        List: TotalList,
        loading: false,
      });
      this.avoidShowError(TotalList);
      // console.log('totalList: ', TotalList);
      resolve(TotalList);
    });
  });

  LoadWhenMount = () => {
    const { type } = this.props;
    const getType = () => {
      const Type = { ...Types[type], ...this.props };
      return Type;
    };
    const Type = getType();
    const {
      props: TypeProps, getDefaultValue,
    } = Type;
    const totalProps = { ...this.props, ...TypeProps };
    const {
      loadWhenMount, afterLoad,
    } = totalProps;
    if (this.props.loadWhenMount || loadWhenMount) {
      this.loadData().then((list) => {
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

  componentDidMount() {
    this.LoadWhenMount();
    this.avoidShowError();
    const { type } = this.props;
    const getType = () => {
      const Type = { ...Types[type], ...this.props };
      return Type;
    };
    const Type = getType();
    const {
      props: TypeProps,
    } = Type;
    const totalProps = { ...this.props, ...TypeProps };
    const {
      defaultOpen,
    } = totalProps;
    if (defaultOpen) {
      SelectRef.current.rcSelect.onDropdownVisibleChange(true);
    }
  }

  handleFilterChange = debounce((Filter) => {
    this.setState({
      loading: true,
    });
    this.loadData({ filter: Filter });
  }, 300);

  loadMore = () => {
    this.setState({
      loading: true,
    });
    const { filter, page } = this.state;
    this.loadData({ filter, page: page + 1, isLoadMore: true });
  };

  render() {
    const { type } = this.props;
    const getType = () => {
      const Type = { ...Types[type], ...this.props };
      return Type;
    };
    const Type = getType();
    const {
      props: TypeProps, render,
    } = Type;
    const totalProps = { ...this.props, ...TypeProps };
    const {
      saveList, children,
    } = totalProps;

    const totalList = [...this.state.List, ...this.state.extraList];
    // console.log('totalList: ', totalList);
    if (saveList) {
      saveList(totalList);
    }
    // 渲染去掉重复项
    const Options = uniqBy(totalList.map(render).concat(React.Children.toArray(children)), option => option.props.value);

    return (
      <Select
        filter
        filterOption={false}
        loading={this.state.loading}
        ref={SelectRef}
        // style={{ width: 200 }}      
        onFilterChange={this.handleFilterChange}
        {...TypeProps}
        {...this.props}
      >
        {Options}
        {/* {this.state.canLoadMore && (
          <Option key="SelectFocusLoad-loadMore" disabled style={{ cursor: 'auto' }}>
            <Button type="primary" style={{ textAlign: 'left', width: '100%', background: 'transparent' }} onClick={this.loadMore}>更多</Button>
          </Option>
        )} */}
        <Option style={{ display: this.state.canLoadMore ? 'block' : 'none' }} key="SelectFocusLoad-loadMore" className="SelectFocusLoad-loadMore" disabled>
          <Button type="primary" style={{ textAlign: 'left', width: '100%', background: 'transparent' }} onClick={this.loadMore}>更多</Button>
        </Option>
      </Select>
    );
  }
}

export default SelectFocusLoad;