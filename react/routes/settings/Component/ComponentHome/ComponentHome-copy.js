/* eslint-disable prefer-destructuring */
/* eslint-disable react/destructuring-assignment */
import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Button, Table, Spin, Popover, Tooltip, Icon,
} from 'choerodon-ui/pro';
import {
  Page, Content, stores, Permission,
} from '@choerodon/boot';
import './ComponentHome.less';
import pic from '../../../../assets/image/模块管理－空.png';
import { loadComponents } from '../../../../api/ComponentApi';
import CreateComponent from '../ComponentComponent/AddComponent';
import EditComponent from '../ComponentComponent/EditComponent';
import DeleteComponent from '../ComponentComponent/DeleteComponent';
import EmptyBlock from '../../../../components/EmptyBlock';
import UserHead from '../../../../components/UserHead';
import Store from './stores';

const { Column } = Table;

export default observer(() => {
  const {
    dataSet, intl,
    AppState: {
      currentMenuType: {
        type, id, organizationId,
      },
    },
  } = useContext(Store);

  const handleFilterChange = (pagination, filters, sorter, barFilters) => {
    // const searchArgs = {};
    // if (filters && filters.name && filters.name.length > 0) {
    //   searchArgs.name = filters.name[0];
    // }
    // if (filters && filters.description && filters.description.length > 0) {
    //   searchArgs.description = filters.description[0];
    // }
    // if (filters && filters.managerId && filters.managerId.length > 0) {
    //   searchArgs.manager = filters.managerId[0];
    // }

    // const filtersPost = {
    //   advancedSearchArgs: {
    //     defaultAssigneeRole: filters && filters.defaultAssigneeRole && filters.defaultAssigneeRole.length > 0 ? filters.defaultAssigneeRole : [],
    //   },
    //   searchArgs,
    //   contents: barFilters,
    // };
    // this.setState({
    //   filters: filtersPost,
    // });
    // this.loadComponents({ pagination, filters: filtersPost });
  };

  return (
    <Page
      className="c7n-component"
      service={[
        'agile-service.issue-component.updateComponent',
        'agile-service.issue-component.deleteComponent',
        'agile-service.issue-component.createComponent',
        'agile-service.issue-component.listByProjectId',
      ]}
    >
      <Content>
        <Table
          dataSet={dataSet}
          scroll={{ x: true }}
          filterBarPlaceholder="筛选条件"
          onChange={handleFilterChange}
        // loading={this.state.loading}
        >
          <Column name="name" />
          <Column name="issueCount" />
          <Column name="managerId" />
          <Column name="description" />
          <Column name="defaultAssigneeRole" />
        </Table>
      </Content>
    </Page>
  );
});
