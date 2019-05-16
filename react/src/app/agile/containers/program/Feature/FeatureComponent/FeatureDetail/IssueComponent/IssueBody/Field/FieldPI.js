import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../../../../../components/TextEditToggle';

const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldPI extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
  }

  render() {
    const { store } = this.props;
    const issue = store.getIssue;
    const { closePi = [], activePi = {} } = issue;
    const { name, code } = activePi || {};
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'PI：'}
          </span>
        </div>
        <div className="c7n-value-wrapper" style={{ display: 'inline-block' }}>
          {
            closePi.length ? (
              <div>
                <span>已结束PI：</span>
                <span>
                  {_.map(closePi, pi => `${pi.name}-${pi.code}`).join(' , ')}
                </span>
                <br />
              </div>
            ) : null
          }
          {
            code ? (
              <div>
                <span>当前PI：</span>
                <span>
                  {`${name}-${code}`}
                </span>
              </div>
            ) : null
          }
          {
            !code && !closePi.length ? (
              <div>无</div>
            ) : null
          }
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldPI));
