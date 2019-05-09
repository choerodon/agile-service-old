import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  loadIssueTypes, loadStatusList, loadPriorities, loadLabels, loadComponents, loadVersions, loadEpics, loadSprints, 
} from '../../api/NewIssueApi';
import { getUsers } from '../../api/CommonApi';
import { getAllPIList } from '../../api/PIApi';

const requests = {
  issueType: {
    textField: 'name',
    valueField: 'id',
    request: loadIssueTypes,
  },
  issueStatus: {
    textField: 'name',
    valueField: 'id',
    request: loadStatusList,
  },
  priority: {
    textField: 'name',
    valueField: 'id',
    request: loadPriorities,
  },
  user: {
    textField: 'name',
    valueField: 'id',
    request: getUsers,
  },
  sprint: {
    textField: 'sprintName',
    valueField: 'sprintId',
    request: loadSprints,
  },
  version: {
    textField: 'name',
    valueField: 'id',
    request: loadVersions,
  },
  label: {
    textField: 'labelName',
    valueField: 'labelId',
    request: loadLabels, 
  },
  component: {
    isContent: true,
    textField: 'name',
    valueField: 'componentId',
    request: loadComponents, 
  },
  epic: {
    textField: 'epicName',
    valueField: 'issueId',
    request: loadEpics, 
  },
  pi: {
    formatter: pi => ({ value: pi.id, text: `${pi.code}-${pi.name}` }),
    request: getAllPIList,     
  },
};
const propTypes = {
  fields: PropTypes.arrayOf(PropTypes.string),
};
const defaultProps = {
  fields: [],
};
function transform(type, data) {
  const {
    isContent, textField, valueField, formatter, 
  } = requests[type];
  const list = isContent ? data.content : data;
  if (formatter) {
    return list.map(formatter);
  } else {
    return list.map(item => ({
      text: item[textField],
      value: item[valueField].toString(),
    }));
  }
}

class FiltersProvider extends Component {
  state={
    filters: {},
  }


  componentDidMount() {
    const { fields } = this.props;
    const keys = fields.map(field => (typeof field === 'string' ? field : field.key));
    const args = fields.map(field => (typeof field === 'string' ? undefined : field.args));
    // console.log(args);
    const requestQueue = keys.map((key, i) => requests[key].request.apply(null, args[i]));
    Promise.all(requestQueue).then((values) => {
      const filters = {};
      values.forEach((value, i) => {
        filters[keys[i]] = transform(keys[i], value);
        transform(keys[i], value);
      });
      this.setState({
        filters,
      });
    });
  }

  render() {
    const { filters } = this.state;
    const { children } = this.props;
    return children(filters);
  }
}

FiltersProvider.propTypes = propTypes;
FiltersProvider.defaultProps = defaultProps;

export default FiltersProvider;
