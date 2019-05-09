import React, { Component } from 'react';
import { stores } from 'choerodon-front-boot';
import { withRouter } from 'react-router-dom';
import './nodata.scss';

const { AppState } = stores;

class NoDataComponent extends Component {
  linkToUrl(url) {
    const { history } = this.props;
    history.push(`${url}?type=project&id=${AppState.currentMenuType.id}&name=${AppState.currentMenuType.name}&organizationId=${AppState.currentMenuType.organizationId}`);
  }
  render() {
    const data = this.props.links;
    let linkDom = [];
    if (data.length) {
      if (data.length === 1) {
        linkDom = (<div className="nodata-description">请在
          <a onClick={this.linkToUrl.bind(this, data[0].link)} style={{ padding: '0 3px 0 3px' }} role="none">{data[0].name}</a>
          中创建一个{this.props.title} </div>);
      } else {
        linkDom = (<div className="nodata-description">请在
          {data.map((item, index) => (
            <React.Fragment>
              <a style={{ padding: '0 3px 0 3px' }} role="none" onClick={this.linkToUrl.bind(this, item.link)}>{item.name}</a>
              {index < data.length - 1 && '或'}
            </React.Fragment>
          ))}
          中创建一个{this.props.title}
        </div>);
      }
    }
    return (<div className="nodata-container">
      <img src={this.props.img} alt="" width={200} />
      <div className="nodata-content">
        <span className="nodata-text">当前项目无可用{this.props.title}</span>
        {linkDom}
      </div>
    </div>);
  }
}

export default withRouter(NoDataComponent);
