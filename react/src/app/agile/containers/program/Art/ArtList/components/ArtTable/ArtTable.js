import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { Table } from 'choerodon-ui';
import moment from 'moment';
import StatusTag from '../../../../../../components/StatusTag';
import Empty from '../../../../../../components/Empty';
import pic from '../../../../../../assets/image/emptyArtList.svg';

const propTypes = {
  dataSource: PropTypes.shape({}).isRequired,
  onEditArtClick: PropTypes.func.isRequired,
};
const STATUS = {
  todo: '未开启',
  doing: '进行中',
  stop: '停止',
};
const ArtTable = ({
  dataSource,
  onEditArtClick,
}) => {
  const columns = [
  //   {
  //   title: '编号',
  //   dataIndex: 'code',
  //   key: 'code',
  //   render: (code, record) => `#${code}-${record.id}`,
  // }, 
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      render: (name, record) => (
        <a
          role="none"
          onClick={() => {
            onEditArtClick(record);
          }}
        >
          {name}
        </a>
      ),
    }, {
      title: '开始日期',
      dataIndex: 'startDate',
      key: 'startDate',
      render: startDate => moment(startDate).format('YYYY-MM-DD'),
    }, {
      title: '状态',
      dataIndex: 'statusCode',
      key: 'statusCode',
      render: statusCode => (<StatusTag categoryCode={statusCode} name={STATUS[statusCode]} />),
    }, {
      title: '创建日期',
      dataIndex: 'creationDate',
      key: 'creationDate',
      render: creationDate => moment(creationDate).format('YYYY-MM-DD'),
    }, {
      title: '最后更新日期',
      dataIndex: 'lastUpdateDate',
      key: 'lastUpdateDate',
      render: lastUpdateDate => moment(lastUpdateDate).format('YYYY-MM-DD'),
    }];

  return (
    <Table
      rowKey="id"
      filterBar={false}
      pagination={false}
      columns={columns}
      dataSource={dataSource}
      empty={(
        <Empty
          border
          pic={pic}
          title="创建属于您的ART"
          description="这是您的ART列表。您可以创建属于您的ART，并且设置您需要的PI节奏，开启敏捷发布火车。"    
        />
      )}
    />
  );
};

ArtTable.propTypes = propTypes;

export default memo(ArtTable);
