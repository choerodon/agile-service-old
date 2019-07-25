/**
 * props:
 * type
 * defaultValue
 * enterOrBlur
 * disabledDate
 * onChange
 * style
 * disabled
 * byHand
 * editIf
 * time
 */

import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  DatePicker, Input, Button, Select, Icon, Tooltip, Popover, Modal, Table,
} from 'choerodon-ui';

let isClick = false;
@inject('AppState')
@observer
class EasyEdit extends Component {
  constructor(props) {
    super(props);
    this.state = {
      edit: false,
      hoverIf: false,
    };
  }

  handleOnOk(e) {
    if (e && e.stopPropagation) {
      e.stopPropagation();
    }
    const { onChange } = this.props;
    const { date, dateString } = this.state;
    /* eslint-disable */
    onChange(date, dateString || e._i);
    /* eslint-enable */
    isClick = true;
    this.setState({
      edit: false,
    });
  }

  renderEdit() {
    const that = this;
    const {
      type, width: propWidth, maxLength, defaultValue, enterOrBlur, disabledDate, time,
    } = this.props;
    const { edit } = this.state;
    if (type === 'input') {
      return (
        <Input
          style={{ width: propWidth || '' }}
          maxLength={maxLength}
          defaultValue={defaultValue}
          autoFocus
          onPressEnter={(e) => {
            enterOrBlur(e.target.value);
            this.setState({
              edit: false,
              hoverIf: false,
            });
          }}
          onBlur={(e) => {
            enterOrBlur(e.target.value);
            this.setState({
              edit: false,
              hoverIf: false,
            });
          }}
        />
      );
    } else {
      return (
        <DatePicker
          autoFocus
          open={edit}
          defaultValue={defaultValue}
          disabledDate={disabledDate}
          format="YYYY-MM-DD HH:mm:ss"
          showTime={time}
          onOpenChange={(status) => {
            if (!status) {
              this.setState({
                edit: false,
                hoverIf: false,
              });
            }
          }}
          onChange={(date, dateString) => {
            this.setState({
              edit: false,
              hoverIf: false,
              date,
              dateString,
            }, () => {
              if (!time) {
                this.handleOnOk();
              }
            });
          }}
          onOk={this.handleOnOk.bind(this)}
        />
      );
    }
  }

  render() {
    const {
      className, style, disabled, byHand, editIf, children,
    } = this.props;
    const { edit, hoverIf } = this.state;
    return (
      <div
        className={className}
        style={{
          position: 'relative',
          cursor: 'pointer',
          minHeight: 20,
          ...style,
        }}
        role="none"
        onClick={() => {
          if (!disabled) {
            if (!byHand && !isClick) {
              this.setState({
                edit: true,
              });
            }
          }
          isClick = false;
        }}
        onMouseEnter={() => {
          if (!disabled) {
            if (!byHand) {
              this.setState({
                hoverIf: true,
              });
            }
          }
        }}
        onMouseLeave={() => {
          if (!disabled) {
            if (!byHand) {
              this.setState({
                hoverIf: false,
              });
            }
          }
        }}
      >
        {
          edit || editIf ? this.renderEdit() : (
            <div style={{ minWidth: 70 }}>
              {children}
              <div
                style={{
                  display: hoverIf ? 'flex' : 'none',
                  width: '100%',
                  position: 'absolute',
                  height: 'calc(100% + 10px)',
                  top: -5,
                  border: '1px solid gainsboro',
                  justifyContent: 'flex-end',
                  borderRadius: 3,
                }}
              >
                <div
                  style={{
                    background: 'gainsboro',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    padding: '0 3px',
                    width: '31px',
                  }}
                >
                  <Icon style={{ fontSize: 15 }} type="mode_edit" />
                </div>
              </div>
            </div>
          )
        }
      </div>
    );
  }
}

export default EasyEdit;
