import React, { memo } from 'react';
import ProTypes from 'prop-types';
import { Tooltip, Table, Icon } from 'choerodon-ui';

const ProgramAimsTable = ({ 
  amisColumns, dataSource, onEditPiAims, onDeletePiAims, 
}) => {
  const columns = [
    ...amisColumns,
    {
      key: 'action',
      render: (text, record) => (
        <div className="c7n-pi-action">
          <Tooltip title="修改PI目标">
            <Icon
              role="none" 
              type="mode_edit"
              onClick={() => { onEditPiAims(record); }}
            />
          </Tooltip>
          <Tooltip title="删除PI目标">
            <Icon 
              role="none" 
              type="delete"
              onClick={() => { onDeletePiAims(record); }}
            />
          </Tooltip>
        </div>
      ),
      width: 120,
    },
  ];
  return (
    <div style={{ magin: '0 24px' }}>
      <Table
        filterBar={false}
        rowKey={record => record.id}
        columns={columns}
        dataSource={dataSource}
        pagination={false}
      />
    </div>
  );
};

ProgramAimsTable.propTypes = {
  // eslint-disable-next-line react/forbid-prop-types
  amisColumns: ProTypes.array.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  dataSource: ProTypes.array.isRequired,
  onEditPiAims: ProTypes.func.isRequired,
  onDeletePiAims: ProTypes.func.isRequired,
};

export default memo(ProgramAimsTable);
