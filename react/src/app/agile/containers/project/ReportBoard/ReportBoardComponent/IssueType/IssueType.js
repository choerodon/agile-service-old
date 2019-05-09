import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react';
import { stores, axios } from 'choerodon-front-boot';
import { withRouter } from 'react-router-dom';
import { Spin, Table } from 'choerodon-ui';
import _ from 'lodash';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
// import pic from './no_issue.png';
import pic from '../../../../../assets/image/emptyChart.svg';
import TypeTag from '../../../../../components/TypeTag';
import './IssueType.scss';

const { AppState } = stores;

class IssueType extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      issueTypeInfo: [],
    };
  }

  componentDidMount() {
    this.loadData();
  }

  getOption() {
    const { issueTypeInfo } = this.state;
    const option = {
      color: ['#9665E2', '#F7667F', '#FAD352', '#45A3FC', '#FFB100'],
      tooltip: {
        trigger: 'item',
        backgroundColor: '#fff',
        borderColor: '#ddd',
        borderWidth: 1,
        textStyle: {
          fontSize: 12,
          color: '#000',
        },
        formatter: value => `<div><span>问题：${value.data.value} 个</span><br/><span>百分比：${(value.data.percent.toFixed(2))}%</span></div>`,
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
          // name: '访问来源',
          type: 'pie',
          radius: '58%',
          center: ['45%', '50%'],
          data: issueTypeInfo,
        },
      ],
    };
    
    return option;
  }

  loadData = () => {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    this.setState({
      loading: true,
    });
    axios.get(`/agile/v1/projects/${projectId}/reports/pie_chart?organizationId=${orgId}&fieldName=typeCode`)
      .then((res) => {
        this.setState({
          loading: false,
        });
        if (res && res.length) {
          this.setState({
            issueTypeInfo: AppState.currentMenuType.category === 'PROGRAM' ? res : res.filter(item => item.jsonObject.icon !== 'agile_feature'),
          });
        }
      })
      .catch((e) => {
        this.setState({
          loading: false,
        });
        Choerodon.prompt('加载数据错误');
      });
  }
 
  renderContent() {
    const { loading, issueTypeInfo } = this.state;
    const colors = ['#9665E2', '#F7667F', '#FAD352', '#45A3FC', '#FFB100'];
    if (loading) {
      return (
        <div className="c7n-issueType-loading">
          <Spin />
        </div>
      );
    }
    if (!issueTypeInfo || !issueTypeInfo.length) {
      return (
        <div className="c7n-issueType-emptyBlock">
          <EmptyBlockDashboard
            pic={pic}
            des="当前没有问题"
          />
        </div>
      );
    }
    return (
      <div className="c7n-issueType-chart">
        <ReactEcharts 
          style={{ width: '60%', height: 308 }}
          option={this.getOption()}
        />
        <div className="c7n-issueType-chart-legend">
          <p>数据统计</p>
          <table>
            <thead>
              <tr>
                <td>问题类型</td>
                <td>问题</td>
                <td>百分比</td>
              </tr>
            </thead>
            <tbody>
              {
                issueTypeInfo.map((item, index) => (
                  <tr key={`tr_${_.random(0, 100)}`}>
                    <td style={{ width: '115px' }}>
                      <span
                        className="item-icon"
                        style={{ background: colors[index] }}
                      />
                      {item.name}
                    </td>
                    <td style={{ width: '94px' }}>
                      <a
                        role="none"
                        onClick={() => {
                          const urlParams = AppState.currentMenuType;
                          const {
                            type, id, organizationId,
                          } = urlParams;
                          const { history } = this.props;
                          history.push(
                            `/agile/issue?type=${type}&id=${id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${organizationId}&paramType=typeCode&paramId=${item.typeName}&paramName=${encodeURIComponent(`${item.name || '未分配'}下的问题`)}&paramUrl=reportBoard`,
                          );
                        }}
                      >
                        {item.value}
                      </a>
                    </td>
                    <td style={{ width: '50px' }}>
                      {`${item.percent.toFixed(2)}%`}
                    </td>
                  </tr>
                ))
              }
            </tbody>
          </table>
        </div>
      </div>
    );
  }

  render() {
    return (
      <div className="c7n-reportBoard-issueType">
        <div className="c7n-issueType-content">
          {this.renderContent()}
        </div>
      </div>
    );
  }
}

export default withRouter(IssueType);
