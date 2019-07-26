import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { Icon, Popconfirm } from 'choerodon-ui';
import './DocItem.scss';


class DocItem extends Component {
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
      doc, type, onDeleteDoc,
    } = this.props;
    return (
      <div
        className="c7n-docItem"
      >
        <Icon type="filter_none" className="c7n-docItem-icon" />
        <a
          className={`c7n-docItem-text c7n-docItem-${type}`}
          href={this.getUrl(doc.spaceId)}
          target="_blank"
          rel="noopener noreferrer"
        >
          {doc.workSpaceVO ? doc.workSpaceVO.name : doc.wikiName}
        </a>
        <Popconfirm
          title="确认删除文档关联吗？"
          onConfirm={() => onDeleteDoc(doc.id)}
          okText="确认"
          cancelText="取消"
          placement="topRight"
          arrowPointAtCenter
        >
          <Icon type="delete" className="c7n-docItem-delete" />
        </Popconfirm>
      </div>
    );
  }
}

export default withRouter(DocItem);
