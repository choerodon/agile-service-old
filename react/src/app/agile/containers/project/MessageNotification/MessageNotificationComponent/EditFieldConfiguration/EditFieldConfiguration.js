import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Page, Header, Content, stores, Permission, axios,
} from 'choerodon-front-boot';
import {
  Table, Icon, 
} from 'choerodon-ui';
import './EditFieldConfiguration.scss';

const { AppState } = stores;
const { 
  type, id, name, organizationId, 
} = AppState.currentMenuType;
@observer
class EditFieldConfiguration extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      users: [[], [], []],
      dataSource: [],
    };
  }

  componentDidMount() {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/notice`)
      .then((res) => {
        const createType = [...(res.filter(item => item.event === 'issue_created' && item.enable === true).map(o => o.noticeName))];
        const distributionType = [...(res.filter(item => item.event === 'issue_assigneed' && item.enable === true).map(o => o.noticeName))];
        const solvedType = [...(res.filter(item => item.event === 'issue_solved' && item.enable === true).map(o => o.noticeName))];
        const createUser = res.filter(item => item.event === 'issue_created' && item.noticeName === '用户')[0];
        const distributionUser = res.filter(item => item.event === 'issue_assigneed' && item.noticeName === '用户')[0];
        const solvedUser = res.filter(item => item.event === 'issue_solved' && item.noticeName === '用户')[0];
        this.setState({
          loading: false,
          users: [
            createUser && createUser.user && createUser.user !== 'null' ? createUser.idWithNameDTOList : [],
            distributionUser && distributionUser.user && distributionUser.user !== 'null' ? distributionUser.idWithNameDTOList : [],
            solvedUser && solvedUser.user && solvedUser.user !== 'null' ? solvedUser.idWithNameDTOList : [],
          ],
          dataSource: [{
            key: 'issue_created',
            event: '问题创建',
            notificationType: createType,
          }, {
            key: 'issue_assigneed',
            event: '问题分配',
            notificationType: distributionType,
          }, {
            key: 'issue_solved',
            event: '问题已解决',
            notificationType: solvedType,
          }],
        });
      })
      .catch(() => {
        this.setState({
          loading: false,
        });
        Choerodon.prompt('获取信息失败');
      });
  }

  getColumn() {
    const { users } = this.state;
    const columns = [
      {
        title: '事件',
        dataIndex: 'event',
        key: 'event',
        width: '30%',
      },
      {
        title: '通知对象',
        dataIndex: 'notificationType',
        key: 'notificationType',
        width: '62%',
        render: (text, record, index) => (
          (text && text.length > 0 ? (
            <ul className="notificationTypeList">
              {
                  text.slice(0, 20).map((item) => {
                    if (item !== '用户') {
                      if (item === '当前处理人') {
                        return <li>经办人</li>;
                      }
                      return (
                        <li>{item}</li>
                      );
                    } else if (item === '用户') {
                      return (
                        <li>
                          {`用户: ${users && users.length && users[index].length > 0 ? users[index].map(o => o.name).join(', ') : '-'}`}
                        </li>
                      );
                    }
                  })
               }
            </ul>
          ) : '-')
        )
        ,
      },
      {
        render: text => (
          <Permission type={type} projectId={id} organizationId={organizationId} service={['agile-service.notice.updateNotice']}>
            <Icon
              style={{ cursor: 'pointer' }}
              type="mode_edit"
              onClick={() => {
                const { history } = this.props;
                history.push(`/agile/messageNotification/editnotificationtype?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}&event=${text.key}`);
              }}
            />
          </Permission>
        ),
      },
    ];
    return columns;
  }

  render() {
    const { loading, dataSource } = this.state;
    return (
      <Permission type={type} projectId={id} organizationId={organizationId} service={['agile-service.notice.queryByProjectId']}>
        <Page>
          <Header
            title="通知设置"
          />
          <Content
            title="通知设置"
            className="c7n-editFieldConfiguration"
            description="通过设置事件发生时所通知的对象，被通知者可以在右上角铃铛处及时查看消息通知，提高个人以及团队的工作效率。"
            link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/message-notification_set/"
          >
            <Table
              dataSource={dataSource}
              columns={this.getColumn()}
              rowKey={record => record.key}
              pagination={false}
              filterBar={false}
              loading={loading}
            />
          </Content>
        </Page>
      </Permission>
    );
  }
}

export default withRouter(EditFieldConfiguration);
