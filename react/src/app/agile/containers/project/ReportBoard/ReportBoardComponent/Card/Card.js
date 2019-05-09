import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { stores } from 'choerodon-front-boot';
import { Icon } from 'choerodon-ui';
import './Card.scss';

const { AppState } = stores;

class Card extends Component {
  handleClick() {
    const { link, history, sprintId } = this.props;
    const urlParams = AppState.currentMenuType;
    history.push(`/agile/${link}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}${sprintId !== undefined ? (`&sprintId=${sprintId}`) : ''}&paramUrl=reportboard`);
  }

  render() {
    const {
      title, children, link,
    } = this.props;
    return (
      <div className="c7n-sprintDashboard-card">
        <div className="card-wrap">
          <header>
            <h1 className="text-overflow-hidden">
              <span>
                {title}
              </span>
            </h1>
            <span className="center" />
            {
              link ? (
                <span>
                  <Icon
                    type="arrow_forward"
                    style={{ cursor: 'pointer' }}
                    onClick={this.handleClick.bind(this)}
                  />
                </span>
              ) : null
            }
          </header>
          <section style={{ padding: '0 16px 1px' }}>
            {children}
          </section>
        </div>
      </div>
    );
  }
}
export default withRouter(Card);
