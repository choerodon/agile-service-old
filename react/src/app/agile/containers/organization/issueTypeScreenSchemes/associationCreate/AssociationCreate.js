import {
 Form, Modal, Select, Icon 
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import React, { Component } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import './AssociationCreate.scss';
import { randomString } from '../../../../common/utils';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { Option } = Select;

@Form.create({})
@injectIntl
@observer
class AssociationCreate extends Component {
  handleCreatingOk = () => {
    const {
      form, type, association, onChange, IssueTypeScreenSchemesStore,
    } = this.props;
    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        const issueType = IssueTypeScreenSchemesStore.getIssueTypeList.filter(
          item => data.issueTypeId === item.id,
        );
        const pageScheme = IssueTypeScreenSchemesStore.getScreenList.filter(
          item => data.pageSchemeId === item.id,
        );
        const newData = {
          ...data,
          issueTypeIcon: issueType[0] && issueType[0].icon,
          issueTypeName: issueType[0] && issueType[0].name,
          pageSchemeName: pageScheme[0] && pageScheme[0].pageName,
        };
        if (association) {
          if (association.id) {
            newData.id = association.id;
            newData.objectVersionNumber = association.objectVersionNumber;
          } else {
            newData.tempKey = association.tempKey;
          }
          onChange(newData, association.tempKey || association.id);
        } else {
          newData.tempKey = randomString(5);
          onChange(newData);
        }
        this.handleCreatingCancel();
      }
    });
  };

  handleCreatingCancel = () => {
    const { form, IssueTypeScreenSchemesStore } = this.props;
    form.resetFields();
    IssueTypeScreenSchemesStore.setCreateShow(false);
  };

  render() {
    const {
      IssueTypeScreenSchemesStore,
      form,
      usedIssueTypes,
      association,
    } = this.props;
    const issueTypes = IssueTypeScreenSchemesStore.getIssueTypeList.filter(
      item => usedIssueTypes.indexOf(item.id) === -1 || item.id === association.issueTypeId,
    );
    const pageSchemes = IssueTypeScreenSchemesStore.getScreenList;
    const { getFieldDecorator } = form;

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };

    return (
      <Sidebar
        title={<FormattedMessage id={`issueTypeScreenSchemes.association.${association ? 'edit' : 'create'}.sidebarTitle`} />}
        visible={IssueTypeScreenSchemesStore.getCreateShow}
        okText={<FormattedMessage id="add" />}
        cancelText={<FormattedMessage id="cancel" />}
        onOk={this.handleCreatingOk}
        onCancel={this.handleCreatingCancel}
      >
        <Form className="issue-association-form c7nagile-form">
          <FormItem
            {...formItemLayout}
          >
            {
              getFieldDecorator('issueTypeId', {
                rules: [{
                  required: true,
                  message: <FormattedMessage id="issueTypeScreenSchemes.association.create.issueTypeWarning" />,
                }],
                initialValue: association ? association.issueTypeId : [],
              })(
                <Select
                  label={<FormattedMessage id="issueTypeScreenSchemes.association.issueType" />}
                  onChange={this.handleSelectChange}
                >
                  {
                    issueTypes.map(item => (
                      <Option value={item.id} key={item.id}>
                        <Icon type={item.icon} className="issue-association-icon" />
                        {item.name}
                      </Option>
                    ))
                  }
                </Select>,
              )
            }
          </FormItem>
          <FormItem
            {...formItemLayout}
            label="pageScheme"
          >
            {
              getFieldDecorator('pageSchemeId', {
                rules: [{
                  required: true,
                  message: <FormattedMessage id="issueTypeScreenSchemes.association.create.pageSchemeWarning" />,
                }],
                initialValue: association ? association.pageSchemeId : [],
              })(
                <Select
                  label={<FormattedMessage id="issueTypeScreenSchemes.association.pageScheme" />}
                  onChange={this.handleSelectChange}
                >
                  {
                    pageSchemes.map(pageScheme => (
                      <Option value={pageScheme.id} key={pageScheme.id}>
                        {pageScheme.pageName}
                      </Option>
                    ))
                  }
                </Select>,
              )
            }
          </FormItem>
        </Form>
      </Sidebar>
    );
  }
}

export default AssociationCreate;
