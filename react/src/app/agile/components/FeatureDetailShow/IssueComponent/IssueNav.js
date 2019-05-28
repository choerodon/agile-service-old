import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Tooltip, Icon } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';

const navList = [
  'detail', 'des', 'attachment', 'wiki',
  'commit', 'log', 'data_log', 'sub_task',
  'link_task', 'branch',
];
const navIcon = {
  detail: {
    name: '详情',
    icon: 'error_outline',
  },
  des: {
    name: '描述',
    icon: 'subject',
  },
  attachment: {
    name: '附件',
    icon: 'attach_file',
  },
  wiki: {
    name: 'Wiki文档',
    icon: 'library_books',
  },
  commit: {
    name: '评论',
    icon: 'sms_outline',
  },
  log: {
    name: '工作日志',
    icon: 'work_log',
  },
  data_log: {
    name: '活动日志',
    icon: 'insert_invitation',
  },
  sub_task: {
    name: '子任务',
    icon: 'filter_none',
  },
  link_task: {
    name: '问题链接',
    icon: 'link',
  },
  branch: {
    name: '开发',
    icon: 'branch',
  },
};

const noDisplay = {
  feature: ['sub_task', 'wiki', 'branch', 'log', 'link_task'],
  issue_epic: ['sub_task', 'branch', 'log', 'link_task'],
};
let sign = true;

@inject('AppState')
@observer class IssueNav extends Component {
  constructor(props) {
    super(props);
    this.sign = false;
    this.state = {
      nav: 'detail',
    };
  }

  componentDidMount() {
    document.getElementById('scroll-area').addEventListener('scroll', (e) => {
      if (sign) {
        const { nav } = this.state;
        const currentNav = this.getCurrentNav(e);
        if (nav !== currentNav && currentNav) {
          this.setState({
            nav: currentNav,
          });
        }
      }
    });
  }

  isInLook = (ele) => {
    const a = ele.offsetTop;
    const target = document.getElementById('scroll-area');
    return a + ele.offsetHeight > target.scrollTop;
  };

  getCurrentNav = () => {
    const { typeCode } = this.props;
    const eles = navList.filter(
      item => (noDisplay[typeCode] ? noDisplay[typeCode].indexOf(item) === -1 : true),
    );
    return _.find(eles, i => this.isInLook(document.getElementById(i)));
  };

  scrollToAnchor = (anchorName) => {
    if (anchorName) {
      const anchorElement = document.getElementById(anchorName);
      if (anchorElement) {
        sign = false;
        anchorElement.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
          inline: 'end',
        });
        setTimeout(() => {
          sign = true;
        }, 2000);
      }
    }
  };

  render() {
    const { nav } = this.state;
    const {
      typeCode, intl,
    } = this.props;

    return (
      <ul className="c7n-nav-ul" style={{ padding: 0 }}>
        {navList.filter(
          item => (noDisplay[typeCode] ? noDisplay[typeCode].indexOf(item) === -1 : true),
        ).map(navItem => (
          <Tooltip placement="right" title={intl.formatMessage({ id: `issue.${navItem}` })} key={navItem}>
            <li id={`${navItem}-nav`} className={`c7n-li ${nav === navItem ? 'c7n-li-active' : ''}`}>
              <Icon
                type={`${navIcon[navItem] && navIcon[navItem].icon} c7n-icon-li`}
                role="none"
                onClick={() => {
                  this.setState({ nav: navItem });
                  this.scrollToAnchor(navItem);
                }}
              />
            </li>
          </Tooltip>
        ))
        }
      </ul>
    );
  }
}

export default withRouter(injectIntl(IssueNav));
