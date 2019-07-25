import React from 'react';
import {
  asyncRouter,
} from '@choerodon/boot';
import IsInProgramStore from '../IsInProgramStore';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';

const COMPONENTINDEX = asyncRouter(() => import('./components/Component'));
const FASTSEARCHINDEX = asyncRouter(() => import('./components/FastSearch'));
const ISSUELINKINDEX = asyncRouter(() => import('./components/IssueLink'));
const MESSAGENOTIFICATION = asyncRouter(() => import('./components/MessageNotification'));

class SETTINGSIndex extends React.Component {
  componentDidCatch(error, info) {
    // Choerodon.prompt(error.message);
  }

  componentDidMount() {
    // 切换项目查是否在项目群中
    RunWhenProjectChange(IsInProgramStore.refresh);
    IsInProgramStore.refresh();
  }

  render() {
    return (
      <div>
        <MESSAGENOTIFICATION />
      </div>
    );
  }
}

export default SETTINGSIndex;
