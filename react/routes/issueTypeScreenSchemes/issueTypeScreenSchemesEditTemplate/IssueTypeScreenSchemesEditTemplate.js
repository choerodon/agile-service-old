import {
  Content, Header, Page, stores,
} from '@choerodon/boot';
import {
  Button, Card, Form, Icon, Input, message, Modal, Spin, Table, Tooltip, Popconfirm,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import React, { Component } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import AssociationCreate from '../associationCreate/AssociationCreate';
import './IssueTypeScreenSchemesEditTemplate.scss';

import Tips from '../../../../components/Tips';

const { AppState } = stores;
const FormItem = Form.Item;
const { confirm } = Modal;

@Form.create({})
@injectIntl
@observer
class IssueTypeScreenSchemesEditTemplate extends Component {
  constructor(props) {
    super(props);
    const { match } = this.props;
    this.state = {
      spinning: false,
      id: match.params.id,
      associations: [],
      usedIssueTypes: [],
      association: false,
    };
  }

  componentDidMount() {
    this.loadData();
  }

  back = () => {
    const { IssueTypeScreenSchemesStore } = this.props;
    const menu = AppState.currentMenuType;
    const {
      type, id, organizationId, name,
    } = menu;
    return `/agile/issue-type-screen-schemes?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`;
  };

  onAssociationChange = (data, key) => {
    const { associations } = this.state;
    let newAssociations = associations;
    if (key) {
      newAssociations = associations
        .filter(association => association.tempKey !== key && association.id !== key);
    }
    newAssociations.unshift(data);
    this.setState({
      associations: newAssociations,
      usedIssueTypes: newAssociations.map(item => item.issueTypeId),
    });
  };

  getColumns = () => ([
    {
      title: <FormattedMessage id="issueTypeScreenSchemes.association.issueType" />,
      dataIndex: 'issueType',
      key: 'issueType',
      render: (text, record) => (
        <React.Fragment>
          <Icon type={record.issueTypeIcon} className="issue-type-page-scheme-create-icon" />
          {record.issueTypeName}
        </React.Fragment>
      ),
    },
    {
      title: <FormattedMessage id="issueTypeScreenSchemes.association.pageScheme" />,
      dataIndex: 'pageSchemeName',
      key: 'pageSchemeName',
    },
    {
      title: <FormattedMessage id="issueTypeScreenSchemes.association.operation" />,
      dataIndex: 'operation',
      key: 'operation',
      render: (text, record) => (
        <React.Fragment>
          <Tooltip
            placement="bottom"
            title={<FormattedMessage id="edit" />}
          >
            <Button size="small" shape="circle" onClick={this.showSidebar.bind(this, record)}>
              <i className="icon icon-mode_edit" />
            </Button>
          </Tooltip>
          {!record.default
            ? (
              <Tooltip
                placement="bottom"
                title={<FormattedMessage id="delete" />}
              >
                <Button size="small" shape="circle" onClick={this.handleDeleteAssociation.bind(this, record.tempKey || record.id)}>
                  <i className="icon icon-delete" />
                </Button>
              </Tooltip>
            ) : <div className="issue-customFields-space" />
          }
        </React.Fragment>
      ),
    },
  ]);

  loadData = () => {
    const { IssueTypeScreenSchemesStore, type } = this.props;
    const { id } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    IssueTypeScreenSchemesStore.loadIssueTypes(orgId);
    IssueTypeScreenSchemesStore.loadScreens(orgId);
    if (type === 'edit') {
      IssueTypeScreenSchemesStore.loadSchemeById(orgId, id).then((data) => {
        if (data) {
          this.setState({
            associations: data.lineVOS,
            usedIssueTypes: data.lineVOS.map(item => item.issueTypeId),
          });
        }
      });
    }
  };

  showSidebar = (association) => {
    const { IssueTypeScreenSchemesStore } = this.props;
    if (association) {
      this.setState({
        association,
      });
    }
    IssueTypeScreenSchemesStore.setCreateShow(true);
  };

  handleDeleteAssociation = (key) => {
    let { associations } = this.state;
    associations = associations
      .filter(association => association.tempKey !== key && association.id !== key);
    this.setState({
      associations,
      usedIssueTypes: associations.map(item => item.issueTypeId),
    });
  };

  handleSubmit = (e) => {
    e.preventDefault();
    const {
      IssueTypeScreenSchemesStore, type, form, intl,
    } = this.props;
    const { associations } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    const scheme = IssueTypeScreenSchemesStore.getScheme;
    form.validateFieldsAndScroll(async (err, data) => {
      if (!err) {
        this.setState({
          spinning: true,
        });
        data.organizationId = orgId;
        data.lineVOS = associations;
        if (type === 'create') {
          try {
            await IssueTypeScreenSchemesStore.createScheme(orgId, data);
            message.success(intl.formatMessage({ id: 'createSuccess' }));
            this.handleCreateCancel();
          } catch (error) {
            message.error(intl.formatMessage({ id: 'createFailed' }));
          }
        } else if (type === 'edit') {
          data.objectVersionNumber = scheme.objectVersionNumber;
          data.id = scheme.id;
          try {
            await IssueTypeScreenSchemesStore.updateScheme(orgId, scheme.id, data);
            message.success(intl.formatMessage({ id: 'editSuccess' }));
            this.handleCreateCancel();
          } catch (error) {
            message.success(intl.formatMessage({ id: 'editFailed' }));
          }
        }

        this.setState({
          spinning: false,
        });
      }
    });
  };

  handleCreateCancel = () => {
    const { history } = this.props;
    history.push(this.back());
  };

  checkName = (rule, value, callback) => {
    const { IssueTypeScreenSchemesStore, intl, id } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const name = IssueTypeScreenSchemesStore.scheme
      ? IssueTypeScreenSchemesStore.scheme.name : false;
    if ((name && value === name) || !value) {
      callback();
    } else {
      IssueTypeScreenSchemesStore.checkName(orgId, value, id)
        .then((data) => {
          if (data) {
            callback();
          } else {
            callback(intl.formatMessage({ id: 'issueTypeScreenSchemes.name.check.exist' }));
          }
        }).catch((error) => {
          callback();
        });
    }
  };

  renderTips() {
    const { intl } = this.props;
    const tip1 = intl.formatMessage({ id: 'issueTypeScreenSchemes.create.tip1' });
    const tip2 = intl.formatMessage({ id: 'issueTypeScreenSchemes.create.tip2' });
    return (
      <React.Fragment>
        <Tips tips={[tip1, tip2]} />
      </React.Fragment>
    );
  }

  render() {
    const {
      form, IssueTypeScreenSchemesStore, intl, type,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      spinning, associations, usedIssueTypes, association,
    } = this.state;

    const scheme = IssueTypeScreenSchemesStore.getScheme;

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
      <Page>
        <Header
          title={<FormattedMessage id={`issueTypeScreenSchemes.${type}`} />}
          backPath={this.back()}
        />
        <Spin spinning={spinning}>
          <Content>
            { this.renderTips() }
            <Form className="scheme-form c7nagile-form" onSubmit={this.handleSubmit}>
              <FormItem
                {...formItemLayout}
                label="schemeName"
              >
                {
                  getFieldDecorator('name', {
                    rules: [{
                      required: true,
                      message: intl.formatMessage({ id: 'issueTypeScreenSchemes.create.nameWarning' }),
                    }, {
                      validator: this.checkName,
                    }],
                    initialValue: scheme ? scheme.name : '',
                  })(
                    <Input
                      maxLength={15}
                      label={<FormattedMessage id="issueTypeScreenSchemes.create.nameLabel" />}
                      type="text"
                      placeholder={intl.formatMessage({ id: 'issueTypeScreenSchemes.create.namePlaceholder' })}
                    />,
                  )
                }
              </FormItem>
              <FormItem
                {...formItemLayout}
                label="schemeDes"
              >
                {
                  getFieldDecorator('description', {
                    initialValue: scheme ? scheme.description : '',
                  })(
                    <Input.TextArea
                      maxLength={45}
                      placeholder={intl.formatMessage({ id: 'issueTypeScreenSchemes.create.desPlaceholder' })}
                      label={<FormattedMessage id="issueTypeScreenSchemes.create.desLabel" />}
                    />,
                  )
                }
              </FormItem>

              <div className="associations-wrapper">
                <Card
                  className="associations-card"
                  title={<FormattedMessage id="issueTypeScreenSchemes.association.title" />}
                  extra={(
                    <Button className="add-btn" onClick={() => this.showSidebar(false)}>
                      <Icon type="add" />
                      <FormattedMessage id="issueTypeScreenSchemes.association.addBtn" />
                    </Button>
                  )}
                >
                  <Table
                    className="associations-table"
                    columns={this.getColumns()}
                    rowKey={record => record.tempKey || record.id}
                    dataSource={associations}
                    filterBar={false}
                    pagination={false}
                  />
                </Card>
              </div>

              <FormItem>
                <Button type="primary" funcType="raised" htmlType="submit">
                  <FormattedMessage id="save" />
                </Button>
                <Button
                  funcType="raised"
                  style={{ marginLeft: '10px' }}
                  onClick={this.handleCreateCancel}
                >
                  <FormattedMessage id="cancel" />
                </Button>
              </FormItem>
            </Form>

            <AssociationCreate
              IssueTypeScreenSchemesStore={IssueTypeScreenSchemesStore}
              usedIssueTypes={usedIssueTypes}
              association={association}
              onChange={this.onAssociationChange}
            />
          </Content>
        </Spin>
      </Page>
    );
  }
}

export default IssueTypeScreenSchemesEditTemplate;
