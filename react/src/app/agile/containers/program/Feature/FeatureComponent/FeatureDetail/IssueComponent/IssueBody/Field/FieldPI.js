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
    const piList = closePi;
    if (code) {
      piList.push({
        name,
        code,
      });
    }
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'PI：'}
          </span>
        </div>
        <div className="c7n-value-wrapper" style={{ display: 'inline-block' }}>
          {
            piList.length ? (
              <div>
                <span>
                  {_.map(piList, pi => `${pi.code}-${pi.name}`).join(' , ')}
                </span>
              </div>
            ) : <div>无</div>
          }
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldPI));
