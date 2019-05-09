import React, { Component } from 'react';
import { observer } from 'mobx-react';
// 用于追踪 Mobx 引起的渲染，非性能调优时可注释
// import { trace } from 'mobx';
import {
  Page, Header, Content, stores, axios,
} from 'choerodon-front-boot';
import {
  Button, Icon, Select, DatePicker, Modal, Input, Form, Tooltip,
} from 'choerodon-ui';
import _ from 'lodash';
import './Issue.scss';
import moment from 'moment';
import { _allowStateChangesInsideComputed } from 'mobx';
import IssueStore from '../../../../stores/project/sprint/IssueStore/IssueStore';
import IssueFilterControler from '../IssueFilterControler';
import ImportIssue from '../ImportIssue';
import AdvancedSearch from '../AdvancedSearch';
import FilterManage from '../FilterManage';
import SaveFilterModal from '../SaveFilterModal';

// CSS 利用相邻兄弟选择器注入
import ExpandCssControler from '../ExpandCssControler';
// Table
import IssueTable from '../IssueTable/IssueTable';
// 任务详情
import ExpandWideCard from '../ExpandWideCard';
// 创建问题按钮
import CreateIssueModal from '../CreateIssueModal';
// 导出用例
import ExportIssue from '../ExportIssue';

import QuickSearch from '../../../../components/QuickSearch';

const FileSaver = require('file-saver');

const { AppState } = stores;
const { Option } = Select;
const { RangePicker } = DatePicker;
const FormItem = Form.Item;
@observer
class Issue extends Component {
  /**
   * @param props
   * 新建一个 filterControler 类，用来管理 ajax 请求所发出的对象
   */
  constructor(props) {
    super(props);
    this.filterControler = new IssueFilterControler();
  }

  /**
   * 处理传入的 Param（如果有的话）
   * 利用 filterControler 类中的 refresh 方法发出初始化请求（包含优先级，状态，类型，标签数据）
   */
  componentDidMount() {    
    this.axiosGetProjectInfo().then(() => {
      const { location } = this.props;
      if (location.search.indexOf('param') !== -1) {
        this.filterControler.paramConverter(location.search);
      }
      this.filterControler.refresh('init').then((data) => {
        if (data.failed) {
          Choerodon.prompt(data.message);
        } else {
          IssueStore.setCurrentSetting(data);
        }
      }).catch((e) => {
        Choerodon.prompt(e);
      });

      IssueStore.axiosGetMyFilterList().then((res) => {
        const paramFilter = IssueStore.getFilterMap.get('paramFilter');
        if (Object.keys(paramFilter).length) {
          this.filterControler.cache.set('userFilter', paramFilter);
          this.filterControler.cache.set('filter', paramFilter);
          IssueStore.setFilterMap(this.filterControler.cache);
          IssueStore.setEmptyBtnVisible(true);
          IssueStore.judgeConditionWithFilter();
        }
      });
    });
    IssueStore.setFilterListVisible(false);
  }

  /**
   * 清除 filterMap 的数据，清除 BarFilter（展示 Table Filter）内容
   */
  componentWillUnmount() {
    document.getElementsByClassName('page-body')[0].style.overflow = '';
    this.filterControler = new IssueFilterControler();
    IssueStore.resetFilterSelect(this.filterControler, true);
    this.filterControler.resetCacheMap();
    IssueStore.setDefaultTableShowColumns(); // 列配置恢复默认
  }

  /**
   * 刷新函数
   */
  Refresh = () => {
    this.filterControler = new IssueFilterControler();
    IssueStore.setLoading(true);
    this.filterControler.refresh('refresh').then((data) => {
      if (data.failed) {
        Choerodon.prompt(data.message);
      } else {
        IssueStore.refreshTrigger(data);
      }
    });
  };

  openExport=() => {
    IssueStore.setExportModalVisible(true);
  };

  axiosGetProjectInfo = () => new Promise((resolve, reject) => axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/project_info`).then((res) => {
    IssueStore.setCreateStartDate('');
    IssueStore.setProjectInfo(res);
    resolve();
  }))

  saveRef = name => (ref) => {
    this[name] = ref;
  };

  /**
   * 快速搜索函数（内容变动时触发）
   * @param boolean => onlyMeChecked 点击仅我的
   * @param boolean => onlyStoryChecked 点击仅故事
   * @param Array => moreChecked 点击其余选项
   */
  onQuickSearchChange = (onlyMeChecked, onlyStoryChecked, moreChecked) => {
    this.filterControler = new IssueFilterControler();
    this.filterControler.quickSearchFilterUpdate(
      onlyMeChecked,
      onlyStoryChecked,
      moreChecked,
      AppState.userInfo.id,
    );
    IssueStore.setLoading(true);
    this.filterControler.update().then(
      (res) => {
        IssueStore.updateFiltedIssue({
          current: res.number + 1,
          pageSize: res.size,
          total: res.totalElements,
        }, res.content);
      },
    );
  };

  onHideIssue = () => {
    this.issueTable.onHideIssue();
  };

  // ExpandCssControler => 用于向 IssueTable 注入 CSS 样式
  render() {
    // 清除整页滚动条
    if (document && document.getElementsByClassName('page-body').length) {
      // document.getElementsByClassName('page-body')[0].style.overflow = 'hidden';
    }

    return (
      <Page
        className="c7n-Issue"
        service={['agile-service.issue.deleteIssue', 'agile-service.issue.listIssueWithSub']}
      >
        <Header
          title="问题管理"
          backPath={IssueStore.getBackUrl}
        >
          <Button
            className="leftBtn"
            funcType="flat"
            onClick={() => {
              IssueStore.createQuestion(true);
            }}
          >
            <Icon type="playlist_add icon" />
            <span>创建问题</span>
          </Button>
          <Button className="leftBtn" funcType="flat" onClick={() => this.importIssue.open()}>
            <Icon type="archive icon" />
            <span>导入问题</span>
          </Button>
          <Button className="leftBtn" funcType="flat" onClick={this.openExport}>
            <Icon type="get_app icon" />
            <span>导出</span>
          </Button>
          <Button
            funcType="flat"
            onClick={() => {
              this.Refresh();
            }}
          >
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content className="c7n-Issue">
          <ExportIssue />
          <div style={{ height: 48 }}>
            <div className="c7n-Issue-search">
              <QuickSearch
                style={{ paddingLeft: 24 }}
                onQuickSearchChange={this.onQuickSearchChange}
                quickSearchAllowClear
              />
              <div className="c7n-advancedSearch">
                <AdvancedSearch />
                <SaveFilterModal />
                <FilterManage />
              </div>
            </div>
          </div>
          <div style={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
            
            {/* <ExpandCssControler /> */}
            
            <div
              className="c7n-content-issue"
              style={{
                display: 'block',
                position: 'relative',
                padding: '0px 18px',
                overflowY: 'scroll',
              }}
            >
              
              <IssueTable filterControler={this.filterControler} ref={this.saveRef('issueTable')} />
            </div>
            <ExpandWideCard
              onHideIssue={this.onHideIssue}
              issueRefresh={this.Refresh}
            />
          </div>
          <CreateIssueModal />
          <ImportIssue ref={this.saveRef('importIssue')} onFinish={this.Refresh} />
        </Content>
      </Page>
    );
  }
}

export default Form.create()(Issue);
