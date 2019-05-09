import React, { Component, Fragment } from 'react';
import { Select } from 'choerodon-ui';
import PropTypes from 'prop-types';
import Types from './Types';

const propTypes = {
  type: PropTypes.string.isRequired,
};
class SelectFocusLoad extends Component {
  state = {
    loading: false,
    List: [],
    extraList: [],
  }

  componentDidMount() {      
    this.loadData(); 
  }
  
  componentDidUpdate(prevProps, prevState) {
    // eslint-disable-next-line react/destructuring-assignment
    if (prevProps.value !== this.props.value) {
      this.avoidShowError();
    }
  }

  getType=() => {
    const { type } = this.props;  
    const Type = { ...Types[type], ...this.props };
    return Type;
  }

  // 防止取值不在option列表中，比如user
  // eslint-disable-next-line react/destructuring-assignment
  avoidShowError=(List = this.state.List) => {
    const Type = this.getType();
    if (Type.avoidShowError) {     
      Type.avoidShowError(this.props, List).then((extraList) => {        
        if (extraList) {
          this.setState({
            extraList,
          });
        }
      });
    }
  }

  loadData=() => {
    const {
      afterLoad, loadWhenMount, 
    } = this.props;  
    const Type = this.getType();
    const {
      request,   
    } = Type;
    if (loadWhenMount) {
      this.setState({
        loading: true,
      });
      request().then((Data) => {    
        this.avoidShowError(Data);
        this.setState({
          List: Data,
          loading: false,
        });
        if (afterLoad) {
          afterLoad(Data);
        }
      });
    }
  }

  render() {
    const { loading, List, extraList } = this.state;
    const { saveList, children } = this.props;  
    const Type = this.getType();
    const { render, request, props } = Type;
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
        onFilterChange={(value) => {
          this.setState({
            loading: true,
          });
          request(value).then((Data) => {
            this.avoidShowError(Data);
            this.setState({
              List: Data,
              loading: false,
            });
          });    
        }}
        {...props}
        {...this.props}
      >
        {Options}        
      </Select>
    );
  }
}

SelectFocusLoad.propTypes = propTypes;
export default SelectFocusLoad;
