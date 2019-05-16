import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Button, Icon, Popconfirm, 
} from 'choerodon-ui';
import _ from 'lodash';
import { createPI } from '../../../../../../../api/ArtApi';
import PIListTable from './PIListTable';
import CreatePIModal from './CreatePIModal';
import StatusTag from '../../../../../../../components/StatusTag';

const STATUS = {
  todo: '未开启',
  doing: '进行中',
  done: '已完成',
};
@observer
class PIList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createPIModalVisible: false,
    };
  }

  handleCreatePIClick = () => {
    this.setState({
      createPIModalVisible: true,
    });
    this.form.resetFields();
  }

  handleCreatePICancel = () => {
    this.setState({
      createPIModalVisible: false,
    });
  }

  handleCreatePIOK = (startDate) => {
    const { artId, onGetPIList, onGetArtInfo } = this.props;
    createPI(artId, startDate).then(() => {
      onGetPIList(artId);
      onGetArtInfo();
      this.setState({
        createPIModalVisible: false,
      });
    });
  }

  render() {
    const { createPIModalVisible } = this.state;
    // eslint-disable-next-line no-shadow
    const {
      name, PiList, data, onDeletePI, 
    } = this.props;
    const columns = [
      {
        title: 'PI名称',
        dataIndex: 'name',
        render: (text, record) => (<a role="none">{`${record.code}-${record.name}`}</a>),
      },
      {
        title: '状态',
        dataIndex: 'statusCode',
        render: statusCode => <StatusTag categoryCode={statusCode} name={STATUS[statusCode]} />,
      },
      {
        title: '剩余天数',
        dataIndex: 'remainDays',
      },
      {
        title: '开始日期',
        dataIndex: 'startDate',
      },
      {
        title: '结束日期',
        dataIndex: 'endDate',
      },
      // {
      //   title: '',
      //   render: (record) => {
      //     const { statusCode, id } = record;
      //     return statusCode === 'todo' 
      //       ? (
      //         <Popconfirm title="确定删除这个PI吗?" onConfirm={() => { onDeletePI(id); }} okText="确定" cancelText="取消">
      //           <Button shape="circle" icon="delete_forever" />
      //         </Popconfirm>
      //       ) : <Button shape="circle" icon="delete_forever" style={{ visibility: 'hidden' }} />;
      //   },
      // },
    ];
    return (
      <React.Fragment>
        {/* <Button funcType="flat" type="primary" style={{ marginBottom: 15 }} disabled={data.statusCode === 'stop'} onClick={this.handleCreatePIClick}>
          <Icon type="playlist_add" />
          <span>创建下一批PI</span>
        </Button> */}
        <PIListTable 
          columns={columns}
          dataSource={PiList}
        />
        <CreatePIModal
          ref={(form) => { this.form = form; }}
          name={name} 
          visible={createPIModalVisible}
          defaultStartDate={PiList && PiList[0] && PiList[0].endDate}
          onCreatePIOk={this.handleCreatePIOK}
          onCreatePICancel={this.handleCreatePICancel}
        />
      </React.Fragment>
    );
  }
}

export default PIList;
