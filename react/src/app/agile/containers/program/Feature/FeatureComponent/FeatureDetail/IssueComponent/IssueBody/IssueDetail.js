import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button } from 'choerodon-ui';
import _ from 'lodash';
import { injectIntl, FormattedMessage } from 'react-intl';
import IssueField from './IssueField';
import VisibleStore from '../../../../../../../stores/common/visible/VisibleStore';

@inject('AppState')
@observer class IssueCommit extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  componentDidMount() {
  }

  render() {
    const detailShow = VisibleStore.getDetailShow;
    return (
      <div className="c7n-details">
        <div id="detail">
          <div className="c7n-title-wrapper" style={{ marginTop: 0 }}>
            <div className="c7n-title-left">
              <Icon type="error_outline c7n-icon-title" />
              <span>详情</span>
            </div>
            <div style={{
              flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
            }}
            />
            <div className="c7n-title-right" style={{ marginLeft: '14px', position: 'relative' }}>
              <Button className="leftBtn" funcType="flat" onClick={() => VisibleStore.setDetailShow(!detailShow)}>
                <Icon type={detailShow ? 'arrow_drop_down' : 'baseline-arrow_left'} style={{ marginRight: 2 }} />
                <span>{detailShow ? '隐藏详情' : '显示详情'}</span>
              </Button>              
            </div>
          </div>
          <IssueField {...this.props} />
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueCommit));
