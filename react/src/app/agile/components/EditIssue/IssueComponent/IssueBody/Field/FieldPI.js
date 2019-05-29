import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';

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
    const { activePi = {} } = issue;
    const name = activePi ? activePi.name : undefined;
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'PI：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            disabled
            // formKey="pi"
            // onSubmit={this.updateIssuePI}
            originData={name}
          >
            <Text>
              {
                name ? (
                  <div>
                    {name}
                  </div>
                ) : (
                  <div>
                    {'无'}
                  </div>
                )
              }
            </Text>
            <Edit>
              <span>{name}</span>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldPI));
