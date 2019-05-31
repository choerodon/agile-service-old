import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Table } from 'choerodon-ui';

const InnerTable = ({ dataSource }) => {
  const innerColumns = [
    {
      title: 'PI目标名称',
      dataIndex: 'name',
    },
    {
      title: '计划商业价值',
      dataIndex: 'planBv',  
      render: planBv => planBv || '-',
    },
    {
      title: '实际商业价值',
      dataIndex: 'actualBv',
      render: actualBv => actualBv || '-',
    },
  ];
  return (
    <Table
      filterBar={false}
      columns={innerColumns}
      dataSource={dataSource}
      pagination={false}
    />
  );
};
const expandedRowRender = (record) => {
  const { piAims } = record;
  return (
    <div style={{ marginLeft: -8 }}>
      <InnerTable dataSource={piAims} />
    </div>
  );
};
class ProjectTable extends Component {
  render() {
    const { dataSource } = this.props;   
    
    const columns = [
      { title: '团队名称', dataIndex: 'name', key: 'name' },     
    ];

    return (
      <div style={{ marginTop: 20 }}>
        <Table        
          filterBar={false}
          columns={columns}
          expandedRowRender={expandedRowRender}
          dataSource={dataSource}
          pagination={false}      
        />
      </div>
    );
  }
}

ProjectTable.propTypes = {

};

export default ProjectTable;
