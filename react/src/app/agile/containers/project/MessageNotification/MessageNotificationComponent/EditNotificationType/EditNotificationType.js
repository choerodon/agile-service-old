import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Page, Header, Content, stores, axios,
} from 'choerodon-front-boot';
import _ from 'lodash';
import {
  Button, Table, Checkbox, Select, Tooltip,
} from 'choerodon-ui';
import './EditNotificationType.scss';

const { Option } = Select;
const { AppState } = stores;
const {
  type, id, name, organizationId,
} = AppState.currentMenuType;

let userOptionsWithUserId = [];
let initialNoticeTypeData = [];
@observer
class EditNotificationType extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      checkeds: [false, false, false, false],
      userOptions: [],
      userOptionsLoading: false,
      updateData: [],
      selectedValue: [],
      dataSource: [],
    };
  }

  componentDidMount() {
    const { location: { search } } = this.props;
    const noticeType = _.last(search.split('&')).split('=')[1];
    axios.all([
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/notice`),
      axios.get(`/iam/v1/projects/${AppState.currentMenuType.id}/users`),
    ])
      .then(axios.spread((notice, users) => {
        const noticeTypeData = notice.filter(item => item.event === noticeType);
        initialNoticeTypeData = _.map(noticeTypeData, item => _.pick(item, ['id', 'event', 'noticeType', 'noticeName', 'enable', 'user', 'objectVersionNumber']));
        const noticeTypeUsers = noticeTypeData.filter(item => item.noticeType === 'users')[0];
        this.setState({
          loading: false,
          checkeds: _.map(noticeTypeData, 'enable'),
          userOptions: [...users.content.filter(item => item.enabled), ...noticeTypeData[3].idWithNameDTOList.filter(item => !users.content.find(o => o.id === item.userId))], // 如果后端返回的idWithNameDTOList中的用户不在20条数据之内，就拼接在后面
          updateData: _.map(noticeTypeData, (item) => {
            const pickItem = _.pick(item, ['id', 'event', 'noticeType', 'noticeName', 'enable', 'user', 'objectVersionNumber']); // 去除对象中的idWithNameDTOList字段，更新时不需要
            return ({ ...pickItem, objectVersionNumber: pickItem.id ? pickItem.objectVersionNumber : null });// 如果之前没有更新过,pickItem.id为null, 此时后端接受的objectVersionNumber为null
          }),
          selectedValue: noticeTypeUsers && noticeTypeUsers.user && noticeTypeData.filter(item => item.noticeType === 'users')[0].user.split(',').map(item => Number(item)), // user字段为id拼接字符串
          dataSource: [
            {
              key: 'currentProcess',
              checked: noticeTypeData[0].enable,
              typeName: '经办人',
            },
            {
              key: 'reportor',
              checked: noticeTypeData[1].enable,
              typeName: '报告人',
            },
            {
              key: 'projectManger',
              checked: noticeTypeData[2].enable,
              typeName: '项目所有者',
            },
            {
              key: 'user',
              checked: noticeTypeData[3].enable,
              typeName: '用户 (可多选)',
            },
          ],
        });
      }))
      .catch((error) => {
        this.setState({
          loading: false,
        });
        Choerodon.prompt('获取信息失败');
      });
  }

  getColumns() {
    const {
      userOptions, userOptionsLoading, checkeds, selectedValue,
    } = this.state;
    userOptionsWithUserId = (() => _.map(_.union(_.map(userOptionsWithUserId, JSON.stringify), _.map(_.filter(userOptions, 'userId'), JSON.stringify)), JSON.parse)
    )(); // userOptions中所有有userId字段的user筛选出来，和原来的拼接，注意去重
    const columns = [
      {
        dataIndex: 'checked',
        key: 'checked',
        render: (text, record, index) => (
          <Checkbox
            style={{ marginTop: 5, marginBottom: 5 }}
            checked={checkeds[index]}
            onChange={e => this.handleCheckboxChange(e, index)}
          />
        ),
        width: '5%',
      },
      {
        title: '通知对象',
        dataIndex: 'typeName',
        key: 'typeName',
        width: '30%',
      },
      {
        render: (text, record, index) => (index > 2 ? (
          <Select
            style={{ width: 520 }}
            value={selectedValue}
            // value={}
            onChange={value => this.handleSelectChange(value, index)}
            onFilterChange={(param) => {
              this.setState({
                userOptionsLoading: true,
              });
              axios.get(`/iam/v1/projects/${AppState.currentMenuType.id}/users?param=${param}`)
                .then((res) => {
                  this.setState({
                    userOptionsLoading: false,
                    userOptions: param ? res.content : [...res.content, ...userOptionsWithUserId],
                    // 如果搜索条件为空，就把为空时搜出来的20条与之前有userId的数据进行拼接，保证已被选中但不在20条之内的数据显示出来
                  });
                })
                .catch((e) => {
                  Choerodon.prompt(e);
                });
            }}
            mode="multiple"
            label="请选择"
            optionFilterProp="children"
            loading={userOptionsLoading}
            filter
            allowClear
            autoFocus
          >
            {
              userOptions && _.map(userOptions, item => (
                <Option
                  key={item.id ? item.id : item.userId}
                  value={item.id ? item.id : item.userId}
                >
                  <Tooltip title={item.realName ? `${item.loginName || ''}${item.realName || ''}` : item.name} mouseEnterDelay={0.5}>
                    {item.realName ? `${item.realName}` : item.name}
                  </Tooltip>
                </Option>
              ))
            }
          </Select>
        ) : ''),
      },
    ];
    return columns;
  }

  handleCheckboxChange = (e, index) => {
    const { checkeds, updateData } = this.state;
    this.setState({
      checkeds: _.map(checkeds, (item, i) => (i === index ? e.target.checked : item)),
      updateData: _.map(updateData, (item, i) => (i === index ? { ...item, enable: e.target.checked } : item)),
    });
  };


  handleSelectChange = (value, index) => {
    const { updateData } = this.state;
    this.setState({
      selectedValue: value,
      updateData: _.map(updateData, (item, i) => (i === index ? { ...item, user: value.length > 0 ? value.join(',') : 'null' } : item)),
    });
  }

 handleSaveBtnClick = () => {
   const { history } = this.props;
   const { updateData } = this.state;
   const postData = _.map(_.difference(_.map(updateData, JSON.stringify), _.map(initialNoticeTypeData, JSON.stringify)), JSON.parse);
   axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/notice`, postData)
     .then((res) => {
       Choerodon.prompt('更新成功');
       history.push(`/agile/messageNotification?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
     })
     .catch((error) => {
       Choerodon.prompt('更新失败');
     });
 }

 render() {
   const { dataSource, loading } = this.state;
   return (
     <Page>
       <Header
         title="编辑通知对象"
         backPath={`/agile/messageNotification?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
       />
       <Content
         className="c7n-editNotificationType"
       >
         <Table
           loading={loading}
           columns={this.getColumns()}
           dataSource={dataSource}
           filterBar={false}
           pagination={false}
           rowKey={record => record.key}
         />
         <div className="saveOrCancel">
           <Button type="primary" funcType="raised" style={{ marginRight: 10 }} onClick={this.handleSaveBtnClick}>保存</Button>
           <Button
             funcType="raised"
             onClick={() => {
               const { history } = this.props;
               history.push(`/agile/messageNotification?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
             }}
           >
             {'取消'}
           </Button>
         </div>
       </Content>
     </Page>
   );
 }
}

export default withRouter(EditNotificationType);
