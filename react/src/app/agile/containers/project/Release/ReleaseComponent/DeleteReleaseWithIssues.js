import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Page, Header, Content, stores, axios,
} from 'choerodon-front-boot';
import {
  Button, Table, Menu, Dropdown, Icon, Modal, Radio, Select,
} from 'choerodon-ui';
import _ from 'lodash';
import ReleaseStore from '../../../../stores/project/release/ReleaseStore';

const { AppState } = stores;
const RadioGroup = Radio.Group;
const { Option } = Select;

@observer
class DeleteReleaseWithIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      distributed: true,
      targetVersionId: '',
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.versionDelInfo.versionNames) {
      if (nextProps.versionDelInfo.versionNames.length > 0) {
        this.setState({
          targetVersionId: nextProps.versionDelInfo.versionNames[0].versionId,
          distributed: true,
        });
      } else {
        this.setState({
          targetVersionId: '',
          distributed: false,
        });
      }
    }
  }

  handleOk() {
    const {
      versionDelInfo, onCancel, refresh, 
    } = this.props;
    const {
      distributed, targetVersionId,
    } = this.state;
    const data2 = {
      projectId: AppState.currentMenuType.id,
      versionId: versionDelInfo.versionId,
    };
    if (versionDelInfo.agileIssueCount) {
      if (distributed) {
        data2.targetVersionId = targetVersionId;
      }
    }
    ReleaseStore.axiosDeleteVersion(data2).then((data) => {
      onCancel();
      refresh();
    }).catch((error) => {
    });
  }

  render() {
    const {
      visible, versionDelInfo, onCancel, 
    } = this.props;
    return (
      <Modal
        title="删除版本"
        visible={visible}
        okText="删除"
        cancelText="取消"
        onCancel={onCancel.bind(this)}
        onOk={this.handleOk.bind(this)}
      >
        <p style={{ marginTop: 20, marginBottom: 0 }}>
          {`您正在删除 ${Object.keys(versionDelInfo).length ? versionDelInfo.versionName : ''} 版本`}
        </p>
        <div style={{ marginTop: 10 }}>
          {
            versionDelInfo.agileIssueCount > 0 || versionDelInfo.testCaseCount > 0 ? (
              <div style={{ marginBottom: 0 }}>
                <p style={{ flex: 1, marginBottom: 10 }}>
                  <Icon
                    type="error"
                    style={{
                      display: 'inline-block', marginRight: 10, marginTop: -3, color: 'red', 
                    }}
                  />
                  {'此版本有'}
                  {
                    versionDelInfo.agileIssueCount ? (
                      <span>
                        <span style={{ color: 'red' }}>{` ${versionDelInfo.agileIssueCount} `}</span>
                        {'个问题'}
                      </span>
                    ) : ''
                  }
                  {
                    versionDelInfo.testCaseCount ? (
                      <span>
                        {','}
                        <span style={{ color: 'red' }}>{` ${versionDelInfo.testCaseCount} `}</span>
                        {'个测试用例'}
                      </span>
                    ) : ''
                  }
                  {'。'}
                </p>
              </div>
            ) : ''
          }
          {
            versionDelInfo.testCaseCount ? (
              <div>
                <p>
                  {'注意：删除后与版本相关的测试用例会一并删除。相关的问题请移动到其他版本中。'}
                </p>
              </div>
            ) : ''
          }
          {
            Object.keys(versionDelInfo).length && versionDelInfo.versionNames.length && versionDelInfo.agileIssueCount ? (
              <div>
                {
                  <div style={{ flex: 4 }}>
                    <Select
                      style={{
                        width: '100%',
                      }}
                      label="版本"
                      onChange={(value) => {
                        this.setState({
                          targetVersionId: value,
                        });
                        if (value === -1) {
                          this.setState({
                            distributed: false,
                          });
                        } else {
                          this.setState({
                            distributed: true,
                          });
                        }
                      }}
                      defaultValue={versionDelInfo.versionNames[0].versionId}
                    >
                      {
                          [...versionDelInfo.versionNames, { versionId: -1, name: '无' }].map(item => (
                            <Option value={item.versionId}>{item.name}</Option>
                          ))
                        }
                    </Select>
                  </div>
                }
              </div>
            ) : ''
          }
        </div>
      </Modal>
    );
  }
}

export default DeleteReleaseWithIssue;
