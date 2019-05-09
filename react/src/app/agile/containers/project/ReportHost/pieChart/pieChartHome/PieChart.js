import React, { Component } from 'react';
import { observer } from 'mobx-react';
import echarts from 'echarts/lib/echarts';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import 'echarts/lib/chart/pie';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import {
  Page, Header, Content, stores, axios,
} from 'choerodon-front-boot';
import {
  Button, Select, Icon, Spin, Tooltip, DatePicker,
} from 'choerodon-ui';
import _ from 'lodash';
import './pie.scss';
import { reduce } from 'zrender/lib/core/util';
import util from 'util';
import moment from 'moment';
import SwitchChart from '../../Component/switchChart';
import VersionReportStore from '../../../../../stores/project/versionReport/VersionReport';
import NoDataComponent from '../../Component/noData';
import pic from '../../../../../assets/image/emptyChart.svg';
import ReleaseStore from '../../../../../stores/project/release/ReleaseStore';
import { loadSprints, loadVersions } from '../../../../../api/NewIssueApi';

const Option = Select.Option;
const { AppState } = stores;
const { RangePicker } = DatePicker;
let backUrl;
// const chooseDimension = [
//   {
//     key: 'sprint',
//     name: '迭代冲刺',
//   }, {
//     key: 'version',
//     name: '版本',
//   }, {
//     key: 'timeRange',
//     name: '时间',
//   },
// ];

@observer
class ReleaseDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      colors: [],
      type: '',
      value: '',
      showOtherTooltip: false,
      linkFromParamUrl: undefined,
      sprintAndVersion: {
        sprint: [],
        version: [],
      },
      chooseDimensionType: [
        {
          key: 'sprint',
          name: '冲刺',
        }, {
          key: 'version',
          name: '版本',
        }, 
        // {
        //   key: 'timeRange',
        //   name: '时间',
        // },
      ],
      currentChooseDimension: '',
      currentSprintChoose: '',
      currentVersionChoose: '',
      startDate: '',
      endDate: '',
    };
  }

   componentDidMount = async () => {
     const Request = this.GetRequest(this.props.location.search);
     backUrl = Request.paramUrl || 'reporthost';
     const value = this.getSelectDefaultValue();
     await VersionReportStore.getPieDatas(AppState.currentMenuType.id, value);
     await axios.all([
       axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/names`, ['started', 'closed']),
       axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/names`),
     ])
       .then(axios.spread((sprints, versions) => {
         this.setState({
           sprintAndVersion: {
             //  sprint: _.map(sprints, 'sprintName'),
             //  version: _.map(versions, 'name'),
             sprint: sprints,
             version: versions,
           },
           currentSprintChoose: sprints[0] && sprints[0].sprintId,
           currentVersionChoose: versions[0] && versions[0].versionId,
         });
       }));


     const pieChart = this.pie.getEchartsInstance();
     pieChart.on('mouseout', (params) => {
       if (params.data.name === '其它') {
         this.setState({
           showOtherTooltip: false,
         });
       }
     });
   }

   GetRequest(url) {
     const theRequest = {};
     if (url.indexOf('?') !== -1) {
       const str = url.split('?')[1];
       const strs = str.split('&');
       for (let i = 0; i < strs.length; i += 1) {
         theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
       }
     }
     return theRequest;
   }

  getFirstName = (str) => {
    if (!str) {
      return '';
    }
    const re = /[\u4E00-\u9FA5]/g;
    for (let i = 0, len = str.length; i < len; i += 1) {
      if (re.test(str[i])) {
        return str[i];
      }
    }
    return str[0].toUpperCase();
  };

  isShowOtherToolTip(e) {
    this.setState({
      showOtherTooltip: true,
    });
    const otherTooptipItem = document.getElementsByClassName('pie-otherTooptip-item-percent');
    let opacity = 0.9;
    for (let i = 0; i < otherTooptipItem.length; i += 1) {
      opacity = 1 - i * 0.1 > 0 ? 1 - i * 0.1 : 0.9;
      otherTooptipItem[i].style.backgroundColor = `rgba(250,211,82,${opacity})`;
    }
    // e.stopPropageation();
  }

  getSelectDefaultValue = () => {
    const { location: { pathname } } = this.props;
    const quaryLinks = [
      { title: '经办人', value: 'assignee' },
      { title: '问题类型', value: 'typeCode' },
      { title: '优先级', value: 'priority' },
      { title: '状态', value: 'status' },
      { title: '史诗', value: 'epic' },
    ];
    const quaryLink = pathname.slice(pathname.lastIndexOf('/') + 1, pathname.length);
    if (quaryLinks.filter(item => item.value === quaryLink).length === 0) {
      this.setState({
        type: '经办人',
        value: 'assignee',
      });
      return 'assignee';
    } else {
      this.setState({
        type: quaryLinks.filter(item => item.value === quaryLink)[0].title,
        value: quaryLink,
      });
      return quaryLink;
    }
  }

  getOption() {
    const colors = VersionReportStore.colors;
    const datas = VersionReportStore.pieData;
    return {
      // title : {
      //   text: '某站点用户访问来源',
      //   subtext: '统计图',
      //   x:'center'
      // },
      // color:['#9665E2','#F7667F','#FAD352', '#45A3FC','#56CA77'],
      color: colors,
      tooltip: {
        trigger: 'item',
        // formatter: '问题: {c} {a} <br/>{b} : {d}%',
        // formatter: value => (
        //   `<div>
        //       <span>问题：${value.data.value}</span><br/>
        //       <span>百分比：${(value.data.percent.toFixed(2))}%</span>
        //     </div>`
        // ),
        formatter: (value) => {
          if (value.data.name !== '其它') {
            this.setState({ showOtherTooltip: false });
            return `<div><span>问题：${value.data.value} 个</span><br/><span>百分比：${(value.data.percent.toFixed(2))}%</span></div>`;
          } else {
            this.isShowOtherToolTip();
            return '';
          }
        },
        padding: 10,
        textStyle: {
          color: '#000',
          fontSize: 12,
          lineHeight: 20,
        },
        extraCssText: 'background: #FFFFFF;\n'
        + 'border: 1px solid #DDDDDD;\n'
        + 'box-shadow: 0 2px 4px 0 rgba(0,0,0,0.20);\n'
        + 'border-radius: 0',
      },
      series: [
        {
          name: '',
          type: 'pie',
          // radius: '55%',
          // hoverAnimation: false,
          startAngle: 245,
          center: ['50%', '47%'],
          data: datas,
          // labelLine: {
          //   length: 100,
          //   length2: 200,
          // },
          label: {
            // fontSize: '13px',
            color: 'rgba(0,0,0,0.65)',
            position: 'outside',

            formatter: (value) => {
              if (value.data.name === null) {
                return '未分配';
              }
              // if (value.data.name === '其它') {
              //   return '';
              // }
            },
          },
          itemStyle: {
            normal: {
              // borderRadius: 18,
              borderWidth: 2,
              borderColor: '#ffffff',
            },
            // color: (data) => {
            //   return this.state.colors[data.dataIndex];
            // }
          },

        },
      ],
    };
  }

  handelRefresh = () => {
    this.setState({
      value: 'assignee',
      currentChooseDimension: '',
      currentSprintChoose: '',
      currentVersionChoose: '',
      startDate: '',
      endDate: '',
    }, () => {
      VersionReportStore.getPieDatas(AppState.currentMenuType.id, this.state.value);
    });
  };

  changeType =(value, option) => {
    // VersionReportStore.setPieData([]);
    this.setState({
      type: option.key,
      value,
      currentChooseDimension: '',

    });
    if (value === 'sprint') {
      this.setState({
        chooseDimensionType: [
          {
            key: 'version',
            name: '版本',
          }, 
        ],
      });
    } else if (value === 'version') {
      this.setState({
        chooseDimensionType: [
          {
            key: 'sprint',
            name: '冲刺',
          }, 
        ],
      });
    } else {
      this.setState({
        chooseDimensionType: [
          {
            key: 'sprint',
            name: '冲刺',
          }, {
            key: 'version',
            name: '版本',
          }, 
        ],
      });
    }

    VersionReportStore.getPieDatas(AppState.currentMenuType.id, value);
  };

  compare(pro) {
    return function (obj1, obj2) {
      const val1 = obj1[pro];
      const val2 = obj2[pro];
      if (val1 < val2) {
        return 1;
      } else if (val1 > val2) {
        return -1;
      } else {
        return 0;
      }
    };
  }

  getQueryString(type, value) {
    const QUERY = {
      assignee: 'paramType=assigneeId&paramId=',
      component: 'paramType=component&paramId=',
      typeCode: 'paramType=typeCode&paramId=',
      version: 'paramType=fixVersion&paramId=',
      priority: 'paramType=priority&paramId=',
      status: 'paramType=statusId&paramId=',
      sprint: 'paramType=sprint&paramId=',
      epic: 'paramType=epic&paramId=',
      label: 'paramType=label&paramId=',
    };
    if (!QUERY[type]) return null;
    return `${QUERY[type]}${value === null ? '0' : value}`;
  }

  getCurrentChoose() {
    const {
      currentChooseDimension, currentSprintChoose, currentVersionChoose, startDate, endDate,
    } = this.state;
    const CHOOSEQUERY = {
      sprint: `&paramChoose=sprint&paramCurrentSprint=${currentSprintChoose}`,
      version: `&paramChoose=version&paramCurrentVersion=${currentVersionChoose}`,
      timeRange: startDate && endDate && `&paramChoose=timeRange&paramStartDate=${startDate.format().substring(0, 10)}&paramEndDate=${endDate.format().substring(0, 10)}`,
    };
    return (currentChooseDimension && CHOOSEQUERY[currentChooseDimension]) ? CHOOSEQUERY[currentChooseDimension] : '';
  }

  handleLinkToIssue(item) {
    const urlParams = AppState.currentMenuType;
    const {
      type, id, organizationId,
    } = urlParams;
    const { history } = this.props;
    const {
      value, sprintAndVersion, currentChooseDimension, currentSprintChoose, currentVersionChoose, startDate, endDate,
    } = this.state;
    const { typeName, name } = item;
    const queryString = this.getQueryString(value, typeName);
    const chooseQueryString = this.getCurrentChoose();
    let paramName = name || '未分配';
    if (currentChooseDimension === 'sprint') {
      paramName += `、冲刺为${sprintAndVersion.sprint.find(sprintItem => sprintItem.sprintId === currentSprintChoose).sprintName}`;
    }

    if (currentChooseDimension === 'version') {
      paramName += `、版本为${sprintAndVersion.version.find(versionItem => versionItem.versionId === currentVersionChoose).name}`;
    }

    paramName += '下的问题';

    if (!queryString) return;
    history.push(
      encodeURI(`/agile/issue?type=${type}&id=${id}&name=${urlParams.name}&organizationId=${organizationId}&${queryString}${`${chooseQueryString || ''}`}&paramName=${paramName}&paramUrl=reporthost/pieReport`),
    );
  }

  renderOtherTooltip = () => {
    const sourceData = VersionReportStore.getSourceData;
    const otherDates = sourceData.filter(item => item.percent < 2).sort(this.compare('percent'));
    if (otherDates && otherDates.length > 0) {
      if (otherDates.length <= 6) {
        return (
          otherDates.map(item => (
            <div className="pie-otherTooptip-item">
              <p className="pie-otherTooptip-item-percent">
                <span>{`${item.percent.toFixed(2)}%`}</span>
              </p>
              <p>
                <Tooltip title={item.name} placement="bottom">
                  <span>{item.realName ? item.realName : item.name}</span>
                </Tooltip>
              </p>
            </div>
          ))
        );
      } else {
        return (
          <React.Fragment>
            { otherDates.slice(0, 6).map(item => (
              <div className="pie-otherTooptip-item">
                <p className="pie-otherTooptip-item-percent">
                  <span>{`${item.percent.toFixed(2)}%`}</span>
                </p>
                <p>
                  <Tooltip title={item.name} placement="bottom">
                    <span>{item.realName ? item.realName : item.name}</span>
                  </Tooltip>
                </p>
              </div>
            ))}
            <div className="pie-otherTooptip-item">
              <span className="pie-otherTooptip-item-ignore">...</span>
            </div>
          </React.Fragment>
        );
      }
    }
  }

  renderChooseDimension = () => {
    const {
      value, showOtherTooltip, sprintAndVersion, currentChooseDimension, currentSprintChoose, currentVersionChoose, startDate, endDate,
    } = this.state;
    return (
      <div>
        {
          // currentChooseDimension === 'timeRange' ? (
          //   <RangePicker
          //     className="c7n-pieChart-filter-item"
          //     style={{ minWidth: 160 }}
          //     value={[startDate, endDate]}
          //     allowClear={false}
          //     disabledDate={current => current && current > moment().endOf('day')}
          //     onChange={(date, dateString) => {
          //       this.setState({
          //         startDate: moment(dateString[0]),
          //         endDate: moment(dateString[1]),
          //       });
          //       VersionReportStore.getPieDatas(AppState.currentMenuType.id, value, '', '', `${dateString[0]} 00:00:00`, `${dateString[1]} 23:59:59`);
          //     }}
          //   />
          // ) : (
          <Select 
            className="c7n-pieChart-filter-item"
            style={{ minWidth: 200 }}
            value={currentChooseDimension === 'version' ? (
              sprintAndVersion.version.find(item => item.versionId === currentVersionChoose) && sprintAndVersion.version.find(item => item.versionId === currentVersionChoose).name) : (
              sprintAndVersion.sprint.find(item => item.sprintId === currentSprintChoose) && sprintAndVersion.sprint.find(item => item.sprintId === currentSprintChoose).sprintName)
                } 
            onChange={this.handleSecondChooseChange}
            allowClear
          >
            {
                sprintAndVersion && currentChooseDimension && sprintAndVersion[currentChooseDimension] && sprintAndVersion[currentChooseDimension].map((item) => {
                  if (currentChooseDimension === 'version') {
                    return <Option key={item.versionId} value={item.versionId}>{item.name}</Option>;
                  }
                  if (currentChooseDimension === 'sprint') {
                    return <Option key={item.sprintId} value={item.sprintId}>{item.sprintName}</Option>;
                  }
                })
              }
          </Select>
            // )
        }      
      </div>

    );
  }

  handleChooseDimensionChange = (chooseDimension) => {
    const { value, sprintAndVersion } = this.state;
    this.setState({
      currentChooseDimension: chooseDimension,
    });

    this.setState({
      currentVersionChoose: sprintAndVersion.version[0] && sprintAndVersion.version[0].versionId,
      currentSprintChoose: sprintAndVersion.sprint[0] && sprintAndVersion.sprint[0].sprintId,
      startDate: '',
      endDate: '',
    });
    VersionReportStore.getPieDatas(AppState.currentMenuType.id, value, chooseDimension === 'sprint' ? sprintAndVersion.sprint[0] && sprintAndVersion.sprint[0].sprintId : '', chooseDimension === 'version' ? sprintAndVersion.version[0] && sprintAndVersion.version[0].versionId : '', '', '');
  }

  handleSecondChooseChange = (chooseValue) => {
    const { value, currentChooseDimension } = this.state;
    if (currentChooseDimension === 'version') {
      this.setState({
        currentVersionChoose: chooseValue,
      });
      VersionReportStore.getPieDatas(AppState.currentMenuType.id, value, '', chooseValue, '', '');
    }
    if (currentChooseDimension === 'sprint') {
      this.setState({
        currentSprintChoose: chooseValue,
      });
      VersionReportStore.getPieDatas(AppState.currentMenuType.id, value, chooseValue, '', '', '');
    }
  }

  render() {
    const {
      value, showOtherTooltip, chooseDimensionType, sprintAndVersion, currentChooseDimension, currentSprintChoose, currentVersionChoose, startDate, endDate,
    } = this.state;
    const data = VersionReportStore.getPieData;
    const sourceData = VersionReportStore.getSourceData;
    let total = 0;
    for (let i = 0; i < data.length; i += 1) {
      total += data[i].value;
    }
    const colors = VersionReportStore.getColors;
    const urlParams = AppState.currentMenuType;
    const type = [
      { title: '经办人', value: 'assignee' },
      { title: '模块', value: 'component' },
      { title: '问题类型', value: 'typeCode' },
      { title: '版本', value: 'version' },
      { title: '优先级', value: 'priority' },
      { title: '状态', value: 'status' },
      { title: '冲刺', value: 'sprint' },
      { title: '史诗', value: 'epic' },
      { title: '标签', value: 'label' },
    ];

    return (
      <Page className="pie-chart">
        <Header
          title="统计图"
          backPath={`/agile/${backUrl}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}
        >
          <SwitchChart
            history={this.props.history}
            current="pieReport"
          />
          <Button onClick={this.handelRefresh}>
            <Icon type="refresh" />
            {'刷新'}
          </Button>
        </Header>
        <Content
          title="统计图"
          description="根据指定字段以统计图呈现项目或筛选器下的问题，这可以使您一目了然地了解问题详情。"
          link="https://v0-16.choerodon.io/zh/docs/user-guide/report/agile-report/statistical/"
        >
          <Spin spinning={VersionReportStore.pieLoading}>
            <div className="c7n-pieChart-filter">
              <Select
                className="c7n-pieChart-filter-item"
                getPopupContainer={triggerNode => triggerNode.parentNode}
                defaultValue={value}
                value={value}
                label="统计类型"
                onChange={this.changeType}
              >
                {
                  type.map(item => (
                    <Option value={item.value} key={item.title}>{item.title}</Option>
                  ))
                }
              </Select>
              <Select
                className="c7n-pieChart-filter-item"
                style={{ minWidth: 70 }}
                label="选择维度"
                defaultValue={chooseDimensionType[0].name}
                value={chooseDimensionType.find(item => item.key === currentChooseDimension) && chooseDimensionType.find(item => item.key === currentChooseDimension).name}
                onChange={this.handleChooseDimensionChange}
                allowClear
              >
                {
                  chooseDimensionType.map(item => <Option key={item.key} value={item.key}>{item.name}</Option>)
                }
              </Select>
              {
                currentChooseDimension ? this.renderChooseDimension() : ''
              }
            </div>

            {data.length ? (
              <React.Fragment>
                <div style={{
                  display: 'flex', justifyContent: 'flex-start', alignItems: 'center',
                }}
                >
                  <ReactEchartsCore
                    ref={(pie) => { this.pie = pie; }}
                    style={{ width: '58%', height: 500 }}
                    echarts={echarts}
                    option={this.getOption()}
                  />

                  <div className="pie-otherTooltip" style={{ display: `${showOtherTooltip ? 'block' : 'none'}` }}>
                    <div className="pie-otherTooltip-wrap" />
                    <div className="pie-otherTooltip-item-wrap">
                      {this.renderOtherTooltip()}
                    </div>

                  </div>
                  <div className="pie-title">
                    <p className="pie-legend-title">数据统计</p>
                    <table>
                      <thead>
                        <tr>
                          <td style={{ width: '158px' }}>{this.state.type}</td>
                          <td style={{ width: '62px' }}>问题</td>
                          <td style={{ paddingRight: 35 }}>百分比</td>
                        </tr>
                      </thead>
                    </table>
                    <table className="pie-legend-tbody">
                      {
                        sourceData.map((item, index) => (
                          <tr>
                            <td style={{ width: '158px' }}>
                              <div className="pie-legend-icon" style={{ background: colors[index] }} />
                              <Tooltip title={item && item.name}>
                                <div className="pie-legend-text">{item.name ? (item.realName && item.realName || item.name) : '未分配'}</div>
                              </Tooltip>
                            </td>
                            <td style={{ width: '62px' }}>
                              <a
                                role="none"
                                onClick={this.handleLinkToIssue.bind(this, item)}
                              >
                                {item.value}
                              </a>
                            </td>
                            <td style={{ width: '62px', paddingRight: 15 }}>{`${(item.percent).toFixed(2)}%`}</td>
                          </tr>
                        ))
                      }
                    </table>
                  </div>
                </div>
              </React.Fragment>
            ) : <NoDataComponent title="问题" links={[{ name: '问题管理', link: '/agile/issue' }]} img={pic} /> }
          </Spin>

        </Content>
      </Page>
    );
  }
}

export default ReleaseDetail;
