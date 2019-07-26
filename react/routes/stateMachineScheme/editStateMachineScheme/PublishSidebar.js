import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import {
  Select, Icon, Modal, Button, Table, Spin, message,
} from 'choerodon-ui';
import { FormattedMessage, injectIntl } from 'react-intl';
import { stores } from '@choerodon/boot';
import TypeTag from '../../../../components/TypeTag/TypeTag';
import StatusTag from '../../../../components/StatusTag/StatusTag';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
@observer
class PublishSidebar extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      transform: {},
      loading: false,
    };
  }

  handleCancel = () => {
    const { store } = this.props;
    store.setIsPublishVisible(false);
  };

  handlePublish = () => {
    const {
      store, scheme, refresh, intl,
    } = this.props;
    const { transform } = this.state;
    const { organizationId } = AppState.currentMenuType;
    const publishData = store.getPublishData;
    const data = [];
    this.setState({
      loading: true,
    });
    publishData.forEach((typeData) => {
      const changeItems = [];
      typeData.statusChangeItems.forEach((stateData) => {
        const oldStatusId = stateData.oldStatus.id;
        let newStatusId = stateData.newStatus.id;
        if (transform[oldStatusId]) {
          newStatusId = transform[oldStatusId];
        }
        changeItems.push({
          oldStatus: {
            id: oldStatusId,
          },
          newStatus: {
            id: newStatusId,
          },
        });
      });
      data.push({
        statusChangeItems: changeItems,
        issueTypeId: typeData.issueTypeId,
        newStateMachineId: typeData.newStateMachineId,
        oldStateMachineId: typeData.oldStateMachineId,
      });
    });
    store.publishStateMachine(
      organizationId, scheme.id, scheme.objectVersionNumber, data,
    ).then((res) => {
      if (res) {
        if (res.failed) {
          message.warn(intl.formatMessage({
            id: 'stateMachineScheme.publish.warn',
          }));
        }
        this.setState({
          transform: [],
        });
        store.setIsPublishVisible(false);
        refresh();
      }
      this.setState({
        loading: false,
      });
    });
  };

  handleChange = (statusId, e) => {
    const { transform } = this.state;
    this.setState({
      transform: {
        ...transform,
        [statusId]: e,
      },
    });
  };

  renderFooter = () => (
    <Fragment>
      <Button
        key="finish"
        type="primary"
        funcType="raised"
        onClick={this.handlePublish}
        loading={this.state.loading}
      >
        {<FormattedMessage id="stateMachineScheme.publish" />}
      </Button>

      <Button
        key="cancel"
        funcType="raised"
        onClick={this.handleCancel}
      >
        {<FormattedMessage id="stateMachineScheme.cancel" />}
      </Button>
    </Fragment>
  );

  getColumn = () => ([{
    title: <FormattedMessage id="stateMachineScheme.issueType" />,
    dataIndex: 'issueType',
    key: 'issueType',
    width: 200,
    render: (text, record) => (
      <Fragment>
        <TypeTag
          data={record.issueTypeVO}
          showName
        />
        <span
          style={{
            display: 'inline-block',
            verticalAlign: 'top',
            marginLeft: '10px',
          }}
        >
          {`(${record.issueCount})`}
        </span>
      </Fragment>
    ),
  }, {
    align: 'right',
    title: <FormattedMessage id="stateMachineScheme.sourceStatus" />,
    dataIndex: 'sourceStatus',
    key: 'sourceStatus',
    width: 350,
    render: (text, record) => {
      const list = [];
      list.push(record.statusChangeItems.map(data => (
        <div
          style={{
            marginBottom: 5,
            display: 'flex',
            justifyContent: 'flex-end',
          }}
        >
          <StatusTag
            data={data.oldStatus}
            style={{ width: 'fit-content' }}
          />
        </div>
      )));
      return (
        <Fragment>
          <div style={{ marginBottom: 5 }}>{record.oldStateMachine.name}</div>
          {list}
        </Fragment>
      );
    },
  }, {
    title: '',
    dataIndex: 'arrow',
    key: 'arrow',
    width: 60,
    render: (text, record) => {
      const list = [];
      list.push(record.statusChangeItems.map(data => (
        <div style={{ marginBottom: 6 }}>
          <Icon
            type="arrow_forward"
            style={{ verticalAlign: 'top', marginLeft: 10 }}
          />
        </div>
      )));
      return (
        <Fragment>
          <div style={{ height: 25 }}> </div>
          {list}
        </Fragment>
      );
    },
  }, {
    title: <FormattedMessage id="stateMachineScheme.targetStatus" />,
    dataIndex: 'targetStatus',
    key: 'targetStatus',
    width: 350,
    render: (text, record) => {
      const options = [];
      const list = [];
      options.push(record.newStateMachine.statusVOS.map(data => (
        <Option value={data.id}>{data.name}</Option>
      )));
      list.push(record.statusChangeItems.map(data => (
        <div>
          <Select
            defaultValue={record.newStateMachine.statusVOS[0].id}
            style={{ width: 200 }}
            onChange={e => this.handleChange(data.oldStatus.id, e)}
          >
            {options}
          </Select>
        </div>
      )));
      return (
        <Fragment>
          <div style={{ marginBottom: 5 }}>{record.newStateMachine.name}</div>
          {list}
        </Fragment>
      );
    },
  }]);

  renderPublishForm = () => {
    const { store } = this.props;
    const publishData = store.getPublishData;
    if (publishData && publishData.length) {
      return (
        <div style={{ width: 900 }}>
          <Table
            dataSource={publishData}
            rowClassName="publishSidebar-table-col"
            columns={this.getColumn()}
            loading={store.getIsLoading}
            rowKey={record => record.id}
            pagination={false}
            filterBar={false}
          />
        </div>
      );
    } else {
      return (
        <Fragment>
          <Icon type="check_circle" style={{ color: '#52C41A', marginRight: 10, verticalAlign: 'top' }} />
          <FormattedMessage id="stateMachineScheme.publish.noMatch" />
        </Fragment>
      );
    }
  };

  render() {
    const { store } = this.props;
    return (
      <Sidebar
        title={<FormattedMessage id="stateMachineScheme.title.publish" />}
        visible={store.getIsPublishVisible}
        footer={this.renderFooter()}
        destroyOnClose
      >
        {
          <div style={{ marginBottom: 20 }}>
            <FormattedMessage id="stateMachineScheme.publish.des" />
          </div>
        }
        {store.getPublishLoading ? <Spin /> : this.renderPublishForm()}
      </Sidebar>
    );
  }
}

export default injectIntl(PublishSidebar);
