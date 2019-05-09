import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Modal, Form, Input,
} from 'choerodon-ui';
import { stores, axios } from 'choerodon-front-boot';
import _ from 'lodash';
import IssueStore from '../../../../stores/project/sprint/IssueStore';

const { AppState } = stores;
const FormItem = Form.Item;
@observer
class SaveFilterModal extends Component {
    checkMyFilterNameRepeat = filterName => axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/personal_filter/check_name?userId=${AppState.userInfo.id}&name=${filterName}`)

    checkMyFilterNameRepeatCreating = (rule, value, callback) => {
      this.checkMyFilterNameRepeat(value).then((res) => {
        if (res) {
        // Choerodon.prompt('筛选名称重复');
          callback('筛选名称重复');
        } else {
          callback();
        }
      });
    }

    handleSaveFilterOk = () => {
      const selectedIssueType = IssueStore.getSelectedIssueType;
      const selectedStatus = IssueStore.getSelectedStatus;
      const selectedPriority = IssueStore.getSelectedPriority;
      const selectedAssignee = IssueStore.getSelectedAssignee;
      const createStartDate = IssueStore.getCreateStartDate;
      const createEndDate = IssueStore.getCreateEndDate;
      const userFilter = IssueStore.getFilterMap.get('userFilter');
      const { form } = this.props;
      
    
      form.validateFields(['filterName'], (err, value, modify) => {
        if (!err && userFilter) {
          const createFilterData = IssueStore.getCreateFilterData;
          const personalFilterSearchDTO = IssueStore.setCFDArgs({
            issueTypeId: selectedIssueType,
            statusId: selectedStatus,
            assigneeIds: selectedAssignee.filter(item => item !== 'none'),
            priorityId: selectedPriority,
          }, Object.assign(userFilter.searchArgs, {
            createEndDate,
            createStartDate,
          }), _.pick(userFilter.otherArgs, ['assigneeId', 'component', 'sprint', 'epic', 'label', 'version']), userFilter.contents);
          IssueStore.setCreateFilterData(createFilterData, { name: value.filterName, personalFilterSearchDTO, filterJson: JSON.stringify(personalFilterSearchDTO) });
          IssueStore.setLoading(true);   
          axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/personal_filter`, IssueStore.getCreateFilterData)
            .then((res) => {
              IssueStore.axiosGetMyFilterList();
              IssueStore.setSaveFilterVisible(false);
              IssueStore.setSelectedFilterId(res.filterId);
              IssueStore.setIsExistFilter(true);
              form.setFieldsValue({ filterName: '' });
              Choerodon.prompt('保存成功');
            }).catch(() => {
              IssueStore.setLoading(false);
              Choerodon.prompt('保存失败');
            });
        }
      });
    }

    render() {
      const saveFilterVisible = IssueStore.getSaveFilterVisible; 
      const { form } = this.props;
      const { getFieldDecorator } = form;
      return (
        <Modal
          title="保存筛选"
          visible={saveFilterVisible}
          onOk={this.handleSaveFilterOk}
          onCancel={() => {
            form.setFieldsValue({ filterName: '' });
            IssueStore.setSaveFilterVisible(false);
          }}
        >
          <Form className="c7n-filterNameForm">
            <FormItem>
              {getFieldDecorator('filterName', {
                rules: [{
                  required: true, message: '请输入筛选名称',
                }, { validator: this.checkMyFilterNameRepeatCreating }],
                validateTrigger: 'onChange',
              })(
                <Input
                  label="筛选名称"
                  maxLength={10}
                />,
              )}
            </FormItem>
          </Form>
        </Modal>
      );
    }
}

export default Form.create()(SaveFilterModal);
