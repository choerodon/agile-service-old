import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { Icon, Popconfirm } from 'choerodon-ui';
import { AppState } from '@choerodon/boot';
import './WikiItem.scss';


class WikiItem extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
    };
  }

  componentDidMount() {
  }

  paramConverter = (url) => {
    const reg = /[^?&]([^=&#]+)=([^&#]*)/g;
    const retObj = {};
    url.match(reg).forEach((item) => {
      const [tempKey, paramValue] = item.split('=');
      const paramKey = tempKey[0] !== '&' ? tempKey : tempKey.substring(1);
      Object.assign(retObj, {
        [paramKey]: paramValue,
      });
    });
    return retObj;
  };

  getUrl = (id) => {
    const { origin } = window.location;
    const { location } = this.props;
    const { search } = location;
    const params = this.paramConverter(search);
    return `${origin}#/knowledge/project?docId=${id}&type=project&id=${params.id}&name=${params.name}&category=${params.category}&organizationId=${params.organizationId}`;
  };

  render() {
    const {
      wiki, wikiHost, type, onDeleteWiki,
    } = this.props;
    return (
      <div
        className="c7n-wikiItem"
      >
        <Icon type="filter_none" className="c7n-wikiItem-icon" />
        <a
          className={`c7n-wikiItem-text c7n-wikiItem-${type}`}
          href={this.getUrl(wiki.spaceId)}
          target="_blank"
          rel="noopener noreferrer"
        >
          {wiki.wikiName}
        </a>
        <Popconfirm
          title="确认删除文档关联吗？"
          onConfirm={() => onDeleteWiki(wiki.id)}
          okText="确认"
          cancelText="取消"
          placement="topRight"
          arrowPointAtCenter
        >
          <Icon type="delete" className="c7n-wikiItem-delete" />
        </Popconfirm>
      </div>
    );
  }
}

export default withRouter(WikiItem);
