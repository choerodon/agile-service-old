import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import { updateIssue } from '../../../../../api/NewIssueApi';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;
const defaultList = ['0.5', '1', '2', '3', '4', '5', '8', '13'];

@inject('AppState')
@observer class FieldStoryPoint extends Component {
  constructor(props) {
    super(props);
    this.state = {
      newValue: undefined,
    };
  }

  componentDidMount() {
    const { store, field } = this.props;
    const issue = store.getIssue;
    const { fieldCode } = field;
    const { [fieldCode]: value } = issue;
    this.setState({
      newValue: value ? String(value) : undefined,
    });
  }

  componentWillReceiveProps(nextProps) {
    const { store, field } = nextProps;
    const issue = store.getIssue;
    const { fieldCode } = field;
    const { [fieldCode]: value } = issue;
    this.setState({
      newValue: value ? String(value) : undefined,
    });
  }

  handleChange = (value) => {
    const { newValue } = this.state;
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.setState({
        newValue: '0.5',
      });
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.setState({
        newValue: String(value).slice(0, 3), // 限制最长三位,
      });
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.setState({
        newValue: value.slice(0, -1),
      });
    } else {
      this.setState({
        newValue,
      });
    }
  };

  updateIssueField = () => {
    const { newValue } = this.state;
    const {
      store, onUpdate, reloadIssue, field,
    } = this.props;
    const issue = store.getIssue;
    const { fieldCode } = field;

    const {
      issueId, objectVersionNumber, [fieldCode]: oldValue,
    } = issue;
    if (oldValue !== newValue) {
      const obj = {
        issueId,
        objectVersionNumber,
        [fieldCode]: newValue === '' ? null : newValue,
      };
      updateIssue(obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
        });
    }
  };

  render() {
    const { newValue } = this.state;
    const { store, field } = this.props;
    const issue = store.getIssue;
    const { fieldCode, fieldName } = field;
    const { [fieldCode]: value } = issue;

    return (
      <div className="line-start mt-10" style={{ width: '100%' }}>
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {`${fieldName}：`}
          </span>
        </div>
        <div className="c7n-value-wrapper" style={{ width: '80px' }}>
          <TextEditToggle
            onSubmit={this.updateIssueField}
          >
            <Text>
              <div>
                {value ? `${value} ${fieldCode === 'storyPoints' ? '点' : '小时'}` : '无'}
              </div>
            </Text>
            <Edit>
              <Select
                value={newValue && newValue.toString()}
                mode="combobox"
                style={{ marginTop: 0, paddingTop: 0 }}
                onChange={e => this.handleChange(e)}
              >
                {defaultList.map(sp => (
                  <Option key={sp.toString()} value={sp}>
                    {sp}
                  </Option>
                ))}
              </Select>
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldStoryPoint));
