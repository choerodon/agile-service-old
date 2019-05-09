import React, { Component } from 'react';
import { Modal, Icon } from 'choerodon-ui';
import { Content, stores, axios } from 'choerodon-front-boot';

const confirm = Modal.confirm;
const { AppState } = stores;

class DeleteComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      filter: {},
      loading: false,
      confirmShow: false,
    };
  }

  componentDidMount() {
    this.init();
  }

  init() {
    this.setState({
      filter: this.props.filter || {},
    });
  }

  deleteFilter() {
    this.setState({
      loading: true,
    });
    axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/${this.state.filter.filterId}`)
      .then((res) => {
        this.setState({
          loading: false,
        });
        this.props.onOk();
      })
      .catch((error) => {
        this.setState({
          loading: false,
        });
      });
  }

  render() {
    return (
      <Modal
        title={`删除快速搜索：${this.state.filter.name}`}
        visible
        confirmLoading={this.state.loading}
        onOk={this.deleteFilter.bind(this)}
        onCancel={this.props.onCancel.bind(this)}
        okText="删除"
        okType="danger"
      >
        <div style={{ margin: '20px 0', position: 'relative' }}>
          删除后将无法使用该快速搜索，如果只是想要改变某些条件可以修改快速搜索。
        </div>
      </Modal>
    );
  }
}

export default DeleteComponent;
