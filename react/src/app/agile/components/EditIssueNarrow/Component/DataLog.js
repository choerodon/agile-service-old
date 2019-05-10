import React, { Component } from 'react';
import { Icon, Popover } from 'choerodon-ui';
import UserHead from '../../UserHead';
import { DatetimeAgo } from '../../CommonComponent';
import './DataLog.scss';

const PROP = {
  Sprint: '冲刺',
  status: '状态',
  'Story Points': '故事点',
  timeestimate: '剩余时间',
  summary: '问题概要',
  'Epic Name': '史诗名',
  priority: '优先级',
  // Component: '模块',
  labels: '标签',
  'Epic Link': '史诗',
  assignee: '经办人',
  reporter: '报告人',
  Pi: 'PI',
};
const PROP_SIMPLE = {
  Component: '模块',
  'Fix Version': '版本',
  'Epic Child': '史诗关联任务',
  issue_epic: '史诗',
  story: '故事',
  bug: '缺陷',
  task: '任务',
  sub_task: '子任务',
  feature: '特性',
  description: '描述',
  Attachment: '附件',
  timespent: '花费时间',
  WorklogId: '工作日志',
  issuetype: '类型',
  Rank: '排序',
  resolution: '解决状态',
  Comment: '评论',
};

class DataLog extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
    };
  }

  getMode1(datalog) {
    const {
      field, oldString, oldValue, newString, newValue, categoryCode,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      if (['labels', 'Component', 'Fix Version', 'Epic Child', 'WorklogId', 'Epic Child', 'issue_epic', 'story', 'bug', 'task', 'sub_task', 'feature'].includes(field)) {
        return '创建';
      }
      if (['Attachment'].includes(field)) {
        return '上传';
      }
      return '更新';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['Epic Link', 'issuetype', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'priority', 'assignee', 'reporter'].includes(field)) {
        return '将';
      }
      if (['description', 'WorklogId', 'Comment', 'timespent'].includes(field)) {
        return '更新';
      }
      if (field === 'status') {
        if (categoryCode === 'doing') {
          return '开始处理';
        }
        if (categoryCode === 'done') {
          return '完成任务';
        }
        if (categoryCode === 'todo') {
          return '置为待办';
        }
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (['Epic Link', 'Sprint', 'Pi', 'assignee', 'reporter', 'labels', 'Component', 'Fix Version', 'Epic Child', 'resolution'].includes(field)) {
        return '移除';
      }
      if (['Story Points', 'timeestimate'].includes(field)) {
        return '将';
      }
      if (['timespent'].includes(field)) {
        return '更新';
      }
      if (['Attachment', 'WorklogId', 'Comment'].includes(field)) {
        if (oldString && !newString) {
          return '删除';
        }
      }
    } else {
      // null -> null
      if (field === 'description') {
        if (oldString && !newString) {
          return '移除';
        }
        return '更新';
      }
      if (field === 'priority') {
        return '将';
      }
      if (field === 'Rank') {
        return '更新';
      }
      if (field === 'issuetype') {
        return '将';
      }
      if (field === 'resolution') {
        if (oldString && !newString) {
          return '移除';
        } else if (!oldString && newString) {
          return '更新';
        }
      }
      if (field === 'summary' || field === 'Epic Name') {
        return '将';
      }
      if (field === 'Story Points') {
        return '将';
      }
      if (field === 'labels') {
        if (!oldString && newString) {
          return '创建';
        }
        return '移除';
      }
      return '';
    }
  }

  getMode2(datalog) {
    const { field } = datalog;
    if (field === 'status') {
      return '';
    }
    return ` 【${PROP[field] || PROP_SIMPLE[field]}】 `;
  }

  // ['由', '']
  getMode3(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      return '';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['Epic Link', 'issuetype', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'priority', 'assignee', 'reporter'].includes(field)) {
        return '由';
      } else {
        return '';
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (['Story Points', 'timeestimate'].includes(field)) {
        return '由';
      } else {
        return '';
      }
    } else {
      if (field === 'priority') {
        return '由';
      }
      if (field === 'Rank') {
        return '';
      }
      if (field === 'issuetype') {
        return '由';
      }
      if (field === 'resolution') {
        return '';
      }
      if (field === 'summary' || field === 'Epic Name') {
        return '由';
      }
      if (field === 'Story Points') {
        return '由';
      }
      if (field === 'labels') {
        return '';
      }
      return '';
    }
  }

  // 原值，只有移除和修改可能出现
  getMode4(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      return '';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['Epic Link', 'issuetype', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'priority', 'assignee', 'reporter'].includes(field)) {
        return ` 【${oldString}】 `;
      }
      if (['description', 'WorklogId', 'Rank', 'Comment'].includes(field)) {
        return '';
      }

      if (field === 'status') {
        return '';
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (['Story Points', 'timeestimate'].includes(field)) {
        return ` 【${oldString}】 `;
      } else if (['timespent', 'Comment'].includes(field)) {
        return '';
      } else if (field === 'Attachment') {
        const attachnewArr = oldString.split('_');
        return ` 【${decodeURI(attachnewArr.slice(2, attachnewArr.length).join('_'))}】 `;
      } else {
        return ` 【${oldString}】 `;
      }
    } else {
      if (field === 'priority') {
        return ` 【${oldString}】 `;
      }
      if (field === 'Rank') {
        return '';
      }
      if (field === 'issuetype') {
        return ` 【${oldString}】 `;
      }
      if (field === 'resolution') {
        return '';
      }
      if (field === 'summary' || field === 'Epic Name') {
        return ` 【${oldString}】 `;
      }
      if (field === 'Story Points') {
        if (!oldString) {
          return ' 【未预估】 ';
        }
        return ` 【${oldString}】 `;
      }
      if (field === 'labels') {
        if (!oldString && newString) {
          return '';
        }
        return ` 【${oldString}】 `;
      }
      return '';
    }
  }

  // ['改变为', '为', '']
  getMode5(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      if (['Epic Link', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'assignee', 'reporter'].includes(field)) {
        return '为';
      }
      return '';
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['Epic Link', 'issuetype', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'priority', 'assignee', 'reporter'].includes(field)) {
        return '改变为';
      }
      return '';
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (['Story Points', 'timeestimate'].includes(field)) {
        return '改变为';
      }
      return '';
    } else {
      if (field === 'priority') {
        return '改变为';
      }
      if (field === 'issuetype') {
        return '改变为';
      }
      if (field === 'summary' || field === 'Epic Name') {
        return '改变为';
      }
      if (field === 'Story Points') {
        return '改变为';
      }
      return '';
    }
  }

  // 新值，只有新增和修改可能出现
  getMode6(datalog) {
    const {
      field, oldString, oldValue, newString, newValue,
    } = datalog;
    const { typeCode } = this.props;
    const TYPEARR = {
      bug: '缺陷',
      story: '故事',
      task: '任务',
      sub_task: '子任务',
    };
    if ((!oldValue && oldValue !== 0) && (newValue || newValue === 0)) {
      // null -> xxx
      if (['Epic Link', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'assignee', 'reporter'].includes(field)) {
        return ` 【${newString}】 `;
      }
      if (['description', 'WorklogId', 'Rank', 'Comment', 'timespent'].includes(field)) {
        return '';
      }
      if (['labels', 'Component', 'Fix Version', 'Epic Child'].includes(field)) {
        return ` 【${newString}】 `;
      }
      // if (field === 'Sprint') {
      //   return ` 【${TYPEARR[typeCode]}】 `;
      // }
      if (field === 'Attachment') {
        const attachnewArr = newString.split('_');
        return ` 【${decodeURI(attachnewArr.slice(2, attachnewArr.length).join('_'))}】 `;
      }
    } else if ((oldValue || oldValue === 0) && (newValue || newValue === 0)) {
      // xxx -> yyy
      if (['Epic Link', 'issuetype', 'Sprint', 'Pi', 'Story Points', 'timeestimate', 'summary', 'Epic Name', 'priority', 'assignee', 'reporter', 'labels', 'Component', 'Fix Version', 'Epic Child'].includes(field)) {
        return ` 【${newString}】 `;
      }
      if (['description', 'Attachment', 'WorklogId', 'Rank', 'Comment', 'timespent'].includes(field)) {
        return '';
      }
      if (field === 'status') {
        return '';
      }
    } else if ((oldValue || oldValue === 0) && (!newValue && newValue !== 0)) {
      // yyy -> null
      if (['Story Points', 'timeestimate'].includes(field)) {
        return ' 【未预估】 ';
      } else {
        return '';
      }
    } else {
      if (field === 'priority') {
        return ` 【${newString}】 `;
      }
      if (field === 'Rank' || field === 'resolution') {
        return '';
      }
      if (field === 'issuetype') {
        return ` 【${newString}】 `;
      }
      if (field === 'summary' || field === 'Epic Name') {
        return ` 【${newString}】 `;
      }
      if (field === 'Story Points') {
        if (!newString) {
          return ' 【未预估】 ';
        }
        return ` 【${newString}】 `;
      }
      if (field === 'labels') {
        if (!oldString && newString) {
          return ` 【${newString}】 `;
        }
        return '';
      }
    }
  }

  getFirst(str) {
    if (!str) {
      return '';
    }
    const re = /[\u4E00-\u9FA5]/g;
    for (let i = 0, len = str.length; i < len; i += 1) {
      if (re.test(str[i])) {
        return str[i];
      }
    }
    return str[0];
  }

  render() {
    const {
      datalog, i, origin, expand,
    } = this.props;
    return (
      <div>
        {
          i > 4 && !expand ? null : (
            <div className="c7n-datalog" key={datalog.logId}>
              <div className="line-justify">
                <div className="c7n-title-log" style={{ flexShrink: 0 }}>
                  <div
                    style={{
                      width: 40,
                      height: 40,
                      marginRight: 15,
                    }}
                  >
                    {
                      i && origin[i].lastUpdatedBy === origin[i - 1].lastUpdatedBy ? null : (
                        <UserHead
                          user={{
                            id: datalog.lastUpdatedBy,
                            loginName: '',
                            realName: datalog.name,
                            avatar: datalog.imageUrl,
                          }}
                          hiddenText
                          type="datalog"
                        />
                      )
                    }

                  </div>
                </div>
                <div style={{ flex: 1, borderBottom: '1px solid rgba(0, 0, 0, 0.12)', padding: '8.5px 0' }}>
                  <div>
                    <Popover
                      placement="bottomLeft"
                      content={(
                        <div style={{ padding: '5px 2px 0' }}>
                          <div
                            style={{
                              width: 62,
                              height: 62,
                              background: '#c5cbe8',
                              color: '#6473c3',
                              overflow: 'hidden',
                              display: 'flex',
                              justifyContent: 'center',
                              alignItems: 'center',
                              textAlign: 'center',
                              borderRadius: '50%',
                              fontSize: '28px',
                              margin: '0 auto',
                            }}
                          >
                            {
                              datalog.imageUrl ? (
                                <img src={datalog.imageUrl} alt="" style={{ width: '100%' }} />
                              ) : (
                                <span style={{
                                  width: 62, height: 62, lineHeight: '62px', textAlign: 'center', color: '#6473c3',
                                }}
                                >
                                  {this.getFirst(datalog.name)}
                                </span>
                              )
                            }
                          </div>
                          <h1 style={{
                            margin: '8px auto 18px', fontSize: '13px', lineHeight: '20px', textAlign: 'center',
                          }}
                          >
                            {datalog.name}
                          </h1>
                          <div style={{
                            color: 'rgba(0, 0, 0, 0.65)', fontSize: '13px', textAlign: 'center', display: 'flex',
                          }}
                          >
                            <Icon type="markunread" style={{ lineHeight: '20px' }} />
                            <span style={{
                              marginLeft: 6, lineHeight: '20px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                            }}
                            >
                              {datalog.email}
                            </span>
                          </div>
                        </div>
                      )}
                    >
                      <span style={{ color: '#303f9f' }}>
                        {`${datalog.name} `}
                      </span>
                    </Popover>

                    <div style={{ display: 'inline' }}>
                      <span>
                        {this.getMode1(datalog)}
                      </span>
                      <span style={{ color: '#303f9f' }}>
                        {this.getMode2(datalog)}
                      </span>
                      <span>
                        {this.getMode3(datalog)}
                      </span>
                      <span style={{ color: '#303f9f' }}>
                        {this.getMode4(datalog)}
                      </span>
                      <span>
                        {this.getMode5(datalog)}
                      </span>
                      <span style={{ color: '#303f9f' }}>
                        {this.getMode6(datalog)}
                      </span>
                    </div>

                  </div>
                  <div style={{ marginTop: 5, fontSize: '12px' }}>
                    <DatetimeAgo
                      date={datalog.lastUpdateDate}
                    />
                  </div>
                </div>
              </div>
            </div>
          )
        }
      </div>
    );
  }
}

export default DataLog;
