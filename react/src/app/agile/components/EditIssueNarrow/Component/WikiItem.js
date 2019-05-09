import React, { Component } from 'react';
import { Icon, Popconfirm } from 'choerodon-ui';
import { AppState } from 'choerodon-front-boot';
import './WikiItem.scss';


class Log extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
    };
  }

  componentDidMount() {
  }

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
          href={`${wikiHost}${wiki.wikiUrl}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          {wiki.wikiName}
        </a>
        <Popconfirm
          title="确认删除wiki链接吗？"
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

export default Log;
