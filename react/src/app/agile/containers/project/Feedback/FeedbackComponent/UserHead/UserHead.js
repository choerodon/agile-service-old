import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';
import './UserHead.scss';

class UserHead extends Component {
  getFirst(str) {
    if (!str) {
      return '';
    }
    const matchStr = str.match(/([\u4E00-\u9FA5])/g);
    return matchStr ? matchStr[0] : str[0];
  }

  render() {
    const {
      user, size, hiddenText, type, tooltip = true,
    } = this.props;
    const s = size || 18;
    return (
      <Tooltip title={tooltip ? `${user.loginName || ''}${user.realName || ''}` : ''} mouseEnterDelay={0.5}>
        <div
          className="c7n-userHead"
          style={{
            display: 'flex',
            maxWidth: 108,
            alignItems: 'center',
          }}
        >
          {
            type === 'datalog' ? (
              <div
                style={{
                  width: 40,
                  height: 40,
                  background: '#b3bac5',
                  color: '#fff',
                  overflow: 'hidden',
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  textAlign: 'center',
                  borderRadius: 4,
                  flexShrink: 0,
                }}
              >
                {
                  user.avatar ? (
                    <img src={user.avatar} alt="" style={{ width: '100%' }} />
                  ) : (
                    <span
                      style={{
                        width: 40, height: 40, lineHeight: '40px', textAlign: 'center', color: '#fff', fontSize: '12px',
                      }}
                      className="user-Head-Title"
                    >
                      {this.getFirst(user.realName)}
                    </span>
                  )
                }
              </div>
            ) : (
              <div
                style={{
                  width: s,
                  height: s,
                  background: '#c5cbe8',
                  color: '#6473c3',
                  overflow: 'hidden',
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  marginRight: 5,
                  textAlign: 'center',
                  borderRadius: '50%',
                  flexShrink: 0,
                }}
              >
                {
                  user.avatar ? (
                    <img src={user.avatar} alt="" style={{ width: '100%' }} />
                  ) : (
                    <span style={{
                      width: s, height: s, lineHeight: `${s}px`, textAlign: 'center', color: '#6473c3',
                    }}
                    >
                      {this.getFirst(user.realName)}
                    </span>
                  )
                }
              </div>
            )
          }
          {
            hiddenText ? null : (
              <span
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  fontSize: '13px',
                  lineHeight: '20px',
                  color: 'rgba(0, 0, 0, 0.65)',
                }}
              >
                {`${user.realName || user.loginName}`}
              </span>
            )
          }
        </div>
      </Tooltip>
    );
  }
}
export default UserHead;
