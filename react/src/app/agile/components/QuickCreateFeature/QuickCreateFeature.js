import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Button, Icon, Dropdown, Input, Menu,
} from 'choerodon-ui';
import TypeTag from '../TypeTag';
import { deBounce } from './Utils';
import { getProjectId } from '../../common/utils';
import { createIssue, createIssueField } from '../../api/NewIssueApi';
import './QuickCreateFeature.scss';

const debounceCallback = deBounce(500);

const propTypes = {
  defaultPriority: PropTypes.number,
  featureTypeDTO: PropTypes.shape({}),
  piId: PropTypes.number,
  onCreate: PropTypes.func,
};
class QuickCreateFeature extends Component {
  state = {
    create: false,
    loading: false,
    currentType: 'business',
    createIssueValue: '',
  }

  handleChange = (e) => {
    this.setState({
      createIssueValue: e.target.value,
    });
  }

  handleChangeType = ({ key }) => {
    this.setState({
      currentType: key,
    });
  };

  handleCreateFeature = () => {
    const { createIssueValue, currentType } = this.state;
    const {
      defaultPriority, piId, epicId, onCreate, featureTypeDTO,
    } = this.props;
    debounceCallback(() => {
      if (createIssueValue.trim() !== '') {
        const feature = {
          priorityCode: `priority-${defaultPriority.id}`,
          priorityId: defaultPriority.id,
          projectId: getProjectId(),
          programId: getProjectId(),
          featureDTO: {
            featureType: currentType,
          },
          piId,
          epicId,
          summary: createIssueValue,
          issueTypeId: featureTypeDTO.id,
          typeCode: featureTypeDTO.typeCode,
          parentIssueId: 0,
        };
        this.setState({
          loading: true,
        });
        createIssue(feature, 'program').then((res) => {
          this.setState({
            loading: false,
            create: false,
          });
          const dto = {
            schemeCode: 'agile_issue',
            context: res.typeCode,
            pageCode: 'agile_issue_create',
          };
          const { store } = this.props;
          createIssueField(res.issueId, dto).then(() => {
            store.clickedOnce(String(piId), res);
          });
          if (onCreate) {
            onCreate();
          }
        }).catch(() => {
          this.setState({
            loading: false,
          });
        });
      }
    }, this);
  };

  render() {
    const {
      create, loading, currentType, createIssueValue,
    } = this.state;
    const { featureTypeDTO } = this.props;
    let featureTypeList = [];
    if (featureTypeDTO) {
      featureTypeList = [
        {
          ...featureTypeDTO,
          colour: '#29B6F6',
          featureType: 'business',
          name: '特性',
        }, {
          ...featureTypeDTO,
          colour: '#FFCA28',
          featureType: 'enabler',
          name: '使能',
        },
      ];
    }
    const currentFeature = featureTypeList.find(feature => feature.featureType === currentType);
    const typeList = (
      <Menu
        style={{
          background: '#fff',
          boxShadow: '0 5px 5px -3px rgba(0, 0, 0, 0.20), 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12)',
          borderRadius: '2px',
        }}
        onClick={this.handleChangeType}
      >
        {
          featureTypeList.map(type => (
            <Menu.Item key={type.featureType}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <TypeTag
                  data={type}
                  showName
                />
              </div>
            </Menu.Item>
          ))
        }
      </Menu>
    );
    return (
      <div
        className="c7nagile-QuickCreateFeature"
        style={{
          userSelect: 'none',
          background: 'white',
          fontSize: 13,
          display: 'flex',
          alignItems: 'center',
        }}
      >
        {
          create ? (
            <div style={{ display: 'block', width: '100%', marginTop: 8 }}>
              <div style={{ display: 'flex' }}>
                <Dropdown overlay={typeList} trigger={['click']}>
                  <div style={{ display: 'flex', alignItem: 'center' }}>
                    <TypeTag
                      data={currentFeature}
                    />
                    <Icon
                      type="arrow_drop_down"
                      style={{ fontSize: 16 }}
                    />
                  </div>
                </Dropdown>
                <div style={{ marginLeft: 8, flexGrow: 1 }}>
                  <Input
                    autoFocus
                    value={createIssueValue}
                    placeholder="需要做什么？"
                    maxLength={44}
                    onChange={this.handleChange}
                    onPressEnter={this.handleCreateFeature}
                  />
                </div>
              </div>
              <div
                style={{
                  marginTop: 10,
                  display: 'flex',
                  marginLeft: 32,
                }}
              >
                <Button
                  type="primary"
                  onClick={() => {
                    this.setState({
                      create: false,
                    });
                  }}
                >
                  取消
                </Button>
                <Button
                  type="primary"
                  loading={loading}
                  onClick={this.handleCreateFeature}
                >
                  {'确定'}
                </Button>
              </div>
            </div>
          ) : (
            <Button
              type="primary"
              icon="playlist_add"
              onClick={() => {
                this.setState({
                  create: true,
                  createIssueValue: '',
                });
              }}
            >
                创建特性
            </Button>
          )
        }
      </div>
    );
  }
}

QuickCreateFeature.propTypes = propTypes;

export default QuickCreateFeature;
