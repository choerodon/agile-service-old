import React, { Component } from 'react';
import {
  Modal, Form, Input, Select, Icon, Button, DatePicker,
} from 'choerodon-ui';
import { Content, stores, axios } from 'choerodon-front-boot';
import moment from 'moment';
import _ from 'lodash';
import { NumericInput } from '../../../../../components/CommonComponent';
import './Filter.scss';

const { Sidebar } = Modal;
const { TextArea } = Input;
const { Option } = Select;
const { AppState } = stores;
const FormItem = Form.Item;

let sign = -1;

class AddComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      origin: {},
      arr: [],
      o: [],
      originUsers: [],
      originStatus: [],
      originPriorities: [],
      originEpics: [],
      originSprints: [],
      originLabels: [],
      originComponents: [],
      originVersions: [],
      originTypes: [],
      loading: false,
      filters: [
        {
          prop: undefined,
          rule: undefined,
          value: undefined,
        },
      ],
      quickFilterFiled: [],
      deleteItem: [],
      deleteFlag: false,
      originFilterName: '',
    };
  }

  componentDidMount() {
    const { filterId } = this.props;
    this.loadQuickFilterFiled();
    this.loadQuickFilter();
    this.loadFilter(filterId);
  }

  getFilterGroup = (filter) => {
    /* eslint-disable */
    // [=, !=, in, notIn]
    const equal_notEqual_in_notin = new Set(['priority', 'issue_type', 'status']);
    // [=, !=, in, notIn, is, isNot]
    const equal_notEqual_in_notIn_is_isNot= new Set(['assignee', 'reporter', 'created_user', 'last_updated_user', 'epic', 'sprint', 'label', 'component', 'influence_version', 'fix_version']);
    // [>, >=, <, <=]
    const greater_greaterAndEqual_lessThan_lessThanAndEqual = new Set(['last_update_date', 'creation_date']);
    // [>, >=, <, <=, is, isNot]
    const greater_greaterAndEqual_lessThan_lessThanAndEqual_is_isNot_equal = new Set(['story_point', 'remain_time']);
    /* eslint-enable */

    if (equal_notEqual_in_notin.has(filter)) {
      return 'is (=,!=,in,notin)';
    } else if (greater_greaterAndEqual_lessThan_lessThanAndEqual.has(filter)) {
      return 'is (>,>=,<,<=)';
    } else if (equal_notEqual_in_notIn_is_isNot.has(filter)) {
      return 'is (=,!=,in,notin,is,isNot)';
    } else if (greater_greaterAndEqual_lessThan_lessThanAndEqual_is_isNot_equal.has(filter)) {
      return 'is (>,>=,<,<=,is,isNot)';
    }
    return null;
  };

  getOperation = (filter) => {
    const operationGroupBase = [
      [
        {
          value: '=',
          text: '等于',
        },
        {
          value: '!=',
          text: '不等于',
        },
        {
          value: 'in',
          text: '包含',
        },
        {
          value: 'notIn',
          text: '不包含',
        },
      ],
      [
        {
          value: '>',
          text: '大于',
        },
        {
          value: '>=',
          text: '大于或等于',
        },
        {
          value: '<',
          text: '小于',
        },
        {
          value: '<=',
          text: '小于或等于',
        },
      ],
    ];
    const operationGroupAdv = [
      [
        ...operationGroupBase[0],
        {
          value: 'is',
          text: '是',
        },
        {
          value: 'isNot',
          text: '不是',
        },
      ],
      [
        ...operationGroupBase[1],
        {
          value: 'is',
          text: '是',
        },
        {
          value: 'isNot',
          text: '不是',
        },
        {
          value: '=',
          text: '等于',
        },
      ],
    ];
    switch (this.getFilterGroup(filter)) {
      case 'is (=,!=,in,notin)':
        return operationGroupBase[0];
      case 'is (>,>=,<,<=)':
        return operationGroupBase[1];
      case 'is (=,!=,in,notin,is,isNot)':
        return operationGroupAdv[0];
      case 'is (>,>=,<,<=,is,isNot)':
        return operationGroupAdv[1];
      default:
        return [];
    }
    // const OPERATION_FILTER = {
    //   priority: ['=', '!=', 'in', 'notIn'],
    //   issue_type: ['=', '!=', 'in', 'notIn'],
    //   status: ['=', '!=', 'in', 'notIn'],
    //   assignee: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   reporter: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   created_user: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   last_updated_user: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   epic: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   sprint: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   label: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   component: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   influence_version: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   fix_version: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
    //   creation_date: ['>', '>=', '<', '<='],
    //   last_update_date: ['>', '>=', '<', '<='],
    //   story_point: ['<', '<=', '=', '>=', '>', 'is', 'isNot'],
    //   remain_time: ['<', '<=', '=', '>=', '>', 'is', 'isNot'],
    // };
    // return OPERATION_FILTER[filter] || [];
  };

  /**
   *校验快速搜索名称是否重复
   *
   * @memberof AddComponent
   */
  checkSearchNameRepeat = (rule, value, callback) => {
    const { originFilterName } = this.state;
    if (originFilterName === value) {
      callback();
    } else {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/check_name?quickFilterName=${value}`)
        .then((res) => {
          if (res) {
            callback('快速搜索名称重复');
          } else {
            callback();
          }
        });
    }
  };

  loadFilter = (id) => {
    const { filterId } = this.props;
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/${id || filterId}`)
      .then((res) => {
        if (res && res.description) {
          const description = res.description.split('+').slice(0, -3).join('+') || '';
          const obj = JSON.parse(res.description.split('+').slice(-1));
          this.setState({
            arr: this.transformInit(obj.arr || []),
            o: obj.o || [],
            origin: {
              ...res,
              description,
            },
            originFilterName: res.name,
          });
        }
      });
  };

  loadQuickFilterFiled = () => {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/fields`)
      .then((res) => {
        this.setState({
          quickFilterFiled: res,
        });
      });
  };

  tempOption = (filter, addEmpty) => {
    const { state } = this;
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const OPTION_FILTER = {
      assignee: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      priority: {
        url: `/issue/v1/projects/${projectId}/priority/list_by_org`,
        prop: '',
        id: 'id',
        name: 'name',
        state: 'originPriorities',
      },
      status: {
        url: `/issue/v1/projects/${projectId}/schemes/query_status_by_project_id?apply_type=agile`,
        prop: '',
        id: 'id',
        name: 'name',
        state: 'originStatus',
      },
      reporter: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      created_user: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      last_updated_user: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      epic: {
        url: `/agile/v1/projects/${projectId}/issues/epics/select_data`,
        prop: '',
        id: 'issueId',
        name: 'epicName',
        state: 'originEpics',
      },
      sprint: {
        // post
        url: `/agile/v1/projects/${projectId}/sprint/names`,
        prop: '',
        id: 'sprintId',
        name: 'sprintName',
        state: 'originSprints',
      },
      label: {
        url: `/agile/v1/projects/${projectId}/issue_labels`,
        prop: '',
        id: 'labelId',
        name: 'labelName',
        state: 'originLabels',
      },
      component: {
        url: `/agile/v1/projects/${projectId}/component`,
        prop: '',
        id: 'componentId',
        name: 'name',
        state: 'originComponents',
      },
      influence_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
        state: 'originVersions',
      },
      fix_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
        state: 'originVersions',
      },
      issue_type: {
        url: '',
        prop: '',
        id: 'typeCode',
        name: 'name',
        state: 'originTypes',
      },
    };
    const arr = state[[OPTION_FILTER[filter].state]].map(v => (
      <Option key={v[OPTION_FILTER[filter].id]} value={v[OPTION_FILTER[filter].id]}>
        {v[OPTION_FILTER[filter].name]}
      </Option>
    ));
    if (addEmpty) {
      arr.unshift(
        <Option key="null" value="null">












          无
                </Option>,
      );
    }
    return arr;
  };

  transformOperation = (value) => {
    const OPERATION = {
      '=': '=',
      '!=': '!=',
      in: 'in',
      'not in': 'notIn',
      is: 'is',
      'is not': 'isNot',
      '<': '<',
      '<=': '<=',
      '>': '>',
      '>=': '>=',
    };
    return OPERATION[value];
  };

  transformOperation2 = (value) => {
    const OPERATION = {
      '=': '=',
      '!=': '!=',
      in: 'in',
      notIn: 'not in',
      is: 'is',
      isNot: 'is not',
      '<': '<',
      '<=': '<=',
      '>': '>',
      '>=': '>=',
    };
    return OPERATION[value];
  };

  getValue = (value, filter) => {
    const type = Object.prototype.toString.call(value);
    if (filter === 'priority') {
      if (type === '[object Array]') {
        const v = _.map(value, 'key');
        const vv = v.map(e => `${e}`);
        return `(${vv.join(',')})`;
      } else {
        const v = value.key;
        return `${v}`;
      }
    } else if (filter === 'issue_type') {
      if (type === '[object Array]') {
        const v = _.map(value, 'key');
        const vv = v.map(e => `'${e}'`);
        return `(${vv.join(',')})`;
      } else {
        const v = value.key;
        return `'${v}'`;
      }
    } else if (type === '[object Array]') {
      const v = _.map(value, 'key');
      return `(${v.join(',')})`;
    } else if (type === '[object Object]') {
      if (value.key) {
        const v = value.key;
        if (Object.prototype.toString.call(v) === '[object Number]') {
          return v;
        } else if (Object.prototype.toString.call(v) === '[object String]') {
          return v;
        }
      } else {
        return value.format('YYYY-MM-DD HH:mm:ss');
      }
    } else {
      return value;
    }
    return '';
  };

  getLabel = (value) => {
    if (Object.prototype.toString.call(value) === '[object Array]') {
      const v = _.map(value, 'label');
      return `[${v.join(',')}]`;
    } else if (Object.prototype.toString.call(value) === '[object Object]') {
      if (value.key) {
        const v = value.label;
        if (Object.prototype.toString.call(v) === '[object Number]') {
          return v;
        } else if (Object.prototype.toString.call(v) === '[object String]') {
          return v;
        }
      } else {
        return value.format('YYYY-MM-DD HH:mm:ss');
      }
    } else {
      return value;
    }
    return '';
  };

  transformInit(arr) {
    return arr.map((a, i) => ({
      fieldCode: a.fieldCode,
      operation: a.operation,
      value: this.transformInitialValue(i, a.fieldCode, a.operation, a.value),
    }));
  }

  transformInitialValue(index, filter, operation, value) {
    const { state } = this;
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const OPTION_FILTER = {
      assignee: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      priority: {
        url: `/issue/v1/projects/${projectId}/priority/list_by_org`,
        prop: '',
        id: 'id',
        name: 'name',
        state: 'originPriorities',
      },
      status: {
        url: `/issue/v1/projects/${projectId}/schemes/query_status_by_project_id?apply_type=agile`,
        prop: '',
        id: 'id',
        name: 'name',
        state: 'originStatus',
      },
      reporter: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      created_user: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      last_updated_user: {
        url: `/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
        state: 'originUsers',
      },
      epic: {
        url: `/agile/v1/projects/${projectId}/issues/epics/select_data`,
        prop: '',
        id: 'issueId',
        name: 'epicName',
        state: 'originEpics',
      },
      sprint: {
        // post
        url: `/agile/v1/projects/${projectId}/sprint/names`,
        prop: '',
        id: 'sprintId',
        name: 'sprintName',
        state: 'originSprints',
      },
      label: {
        url: `/agile/v1/projects/${projectId}/issue_labels`,
        prop: '',
        id: 'labelId',
        name: 'labelName',
        state: 'originLabels',
      },
      component: {
        url: `/agile/v1/projects/${projectId}/component`,
        prop: '',
        id: 'componentId',
        name: 'name',
        state: 'originComponents',
      },
      influence_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
        state: 'originVersions',
      },
      fix_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
        state: 'originVersions',
      },
      issue_type: {
        url: '',
        prop: '',
        id: 'typeCode',
        name: 'name',
        state: 'originTypes',
      },
    };
    if (sign === index) {
      if (operation === 'in' || operation === 'notIn') {
        sign = -1;
        return [];
      } else {
        sign = -1;
        return undefined;
      }
    }
    if (filter === 'creation_date' || filter === 'last_update_date') {
      // return moment
      return moment(value, 'YYYY-MM-DD HH:mm:ss');
    }
    if (operation === 'is' || operation === 'isNot' || operation === 'is not') {
      return ({
        key: "'null'",
        label: '空',
      });
    }
    if (filter === 'story_point' || filter === 'remain_time') {
      return value;
    }
    if (filter === 'priority') {
      if (operation === 'in' || operation === 'notIn' || operation === 'not in') {
        const arr = value.slice(1, -1).split(',');
        return arr.map((v) => {
          const priority = _.find(state[OPTION_FILTER[filter].state], { id: v * 1 });
          return {
            key: v * 1,
            label: priority ? priority.name : v,
          };
        });
      } else {
        const k = value;
        const priority = _.find(state[OPTION_FILTER[filter].state], { [OPTION_FILTER[filter].id]: k * 1 });
        return ({
          key: k,
          label: priority ? priority.name : k,
        });
      }
    } else if (filter === 'issue_type') {
      if (operation === 'in' || operation === 'notIn' || operation === 'not in') {
        const arr = value.slice(1, -1).split(',');
        return arr.map(v => ({
          key: v.slice(1, -1),
          label: _.find(state[OPTION_FILTER[filter].state],
            { [OPTION_FILTER[filter].id]: v.slice(1, -1) }).name,
        }));
      } else {
        const k = value.slice(1, -1);
        return ({
          key: k,
          label: _.find(state[OPTION_FILTER[filter].state],
            { [OPTION_FILTER[filter].id]: k }).name,
        });
      }
    } else if (operation === 'in' || operation === 'notIn' || operation === 'not in') {
      const arr = value.slice(1, -1).split(',');
      return arr.map(v => ({
        key: v * 1,
        label: _.find(state[OPTION_FILTER[filter].state],
          { [OPTION_FILTER[filter].id]: v * 1 })
          ? _.find(state[OPTION_FILTER[filter].state],
            { [OPTION_FILTER[filter].id]: v * 1 })[OPTION_FILTER[filter].name]
          : undefined,
      }));
    } else {
      const k = value * 1;
      return ({
        key: k,
        label: _.find(state[OPTION_FILTER[filter].state],
          { [OPTION_FILTER[filter].id]: k })
          ? _.find(state[OPTION_FILTER[filter].state],
            { [OPTION_FILTER[filter].id]: k })[OPTION_FILTER[filter].name]
          : undefined,
      });
    }
  }

  handleOk(e) {
    e.preventDefault();
    const { form, onOk, filterId } = this.props;
    const {
      deleteItem, quickFilterFiled, origin, arr, deleteFlag,
    } = this.state;
    form.validateFieldsAndScroll((err, values, modify) => {
      if (!err && (modify || deleteFlag)) {
        const arrCopy = [];
        const expressQueryArr = [];
        const o = [];
        const f = arr.slice();
        f.forEach((v, i) => {
          if (deleteItem.indexOf(i) !== -1) {
            return;
          }
          const a = {
            fieldCode: values[`filter-${i}-prop`],
            operation: this.transformOperation2(values[`filter-${i}-rule`]),
            value: this.getValue(values[`filter-${i}-value`], values[`filter-${i}-prop`]),
          };
          if (i) {
            o.push(values[`filter-${i}-ao`]);
            expressQueryArr.push(values[`filter-${i}-ao`].toUpperCase());
          }
          arrCopy.push(a);
          expressQueryArr.push(_.find(quickFilterFiled,
            { fieldCode: a.fieldCode }).name);
          expressQueryArr.push(a.operation);
          expressQueryArr.push(this.getLabel(values[`filter-${i}-value`]));
        });
        const d = new Date();
        const json = JSON.stringify({
          arr: arrCopy,
          o,
        });
        const obj = {
          childIncluded: true,
          objectVersionNumber: origin.objectVersionNumber,
          expressQuery: expressQueryArr.join(' '),
          name: values.name,
          description: `${values.description || ''}+++${json}`,
          projectId: AppState.currentMenuType.id,
          quickFilterValueDTOList: arrCopy,
          relationOperations: o,
        };
        this.setState({
          loading: true,
        });
        axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/${filterId}`, obj)
          .then((res) => {
            this.setState({
              loading: false,
            });
            onOk();
          });
      }
    });
  }

  loadQuickFilter() {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/iam/v1/projects/${AppState.currentMenuType.id}/users?page=0&size=9999`).then(res => this.setState({ originUsers: res.content }));
    axios.get(`/issue/v1/projects/${projectId}/priority/list_by_org`).then(res => this.setState({ originPriorities: res }));
    axios.get(`/issue/v1/projects/${projectId}/schemes/query_status_by_project_id?apply_type=agile`).then(res => this.setState({ originStatus: res }));
    axios.get(`/agile/v1/projects/${projectId}/issues/epics/select_data`).then(res => this.setState({ originEpics: res }));
    axios.post(`/agile/v1/projects/${projectId}/sprint/names`).then(res => this.setState({ originSprints: res }));
    axios.get(`/agile/v1/projects/${projectId}/issue_labels`).then(res => this.setState({ originLabels: res }));
    axios.get(`/agile/v1/projects/${projectId}/component`).then(res => this.setState({ originComponents: res }));
    axios.post(`/agile/v1/projects/${projectId}/product_version/names`).then(res => this.setState({ originVersions: res }));
    axios.get(`/issue/v1/projects/${projectId}/schemes/query_issue_types?apply_type=agile`).then(res => this.setState({ originTypes: res }));
  }

  renderOperation(filter, index) {
    const { form } = this.props;
    if (!filter) {
      return (
        <Select label="关系" />
      );
    } else {
      return (
        <Select
          label="关系"
          onChange={(v) => {
            sign = index;
            const str = `filter-${index}-value`;
            let value;
            if (v === 'in' || v === 'notIn' || v === 'not in') {
              value = [];
            } else {
              value = undefined;
            }
            form.setFieldsValue({
              [str]: value,
            });
          }}
        >
          {
            this.getOperation(filter).map(v => (
              <Option key={v.value} value={v.value}>{v.text}</Option>
            ))
          }
        </Select>
      );
    }
  }

  renderValue(filter, opera) {
    let operation;
    if (opera === 'not in') {
      operation = 'notIn';
    } else if (opera === 'is not') {
      operation = 'isNot';
    } else {
      operation = opera;
    }
    if (!filter || !operation) {
      return (
        <Select label="值" />
      );
    } else if (['assignee', 'priority', 'status', 'reporter', 'created_user', 'last_update_user', 'epic', 'sprint', 'label', 'component', 'influence_version', 'fix_version', 'issue_type'].indexOf(filter) > -1) {
      // select
      if (['=', '!='].indexOf(operation) > -1) {
        // return normal value
        return (
          <Select
            label="值"
            labelInValue
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
          >
            {this.tempOption(filter, false)}
          </Select>
        );
      } else if (['is', 'isNot'].indexOf(operation) > -1) {
        // return value add empty
        return (
          <Select
            label="值"
            labelInValue
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
          >
            <Option key="'null'" value="'null'">
              {'空'}
            </Option>
          </Select>
        );
      } else {
        // return multiple value
        return (
          <Select
            label="值"
            labelInValue
            mode="multiple"
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
          >
            {this.tempOption(filter, false)}
          </Select>
        );
      }
    } else if (['creation_date', 'last_update_date'].indexOf(filter) > -1) {
      // time
      // return data picker
      return (
        <DatePicker
          label="值"
          format="YYYY-MM-DD HH:mm:ss"
          showTime
        />
      );
    } else {
      // story points && remainning time
      // return number input
      return !(operation === 'is' || operation === 'isNot') ? (
        <NumericInput
          label="值"
          style={{ lineHeight: '22px', marginBottom: 0, width: 100 }}
        />
      ) : (
        <Select
          label="值"
          labelInValue
          filter
          optionFilterProp="children"
          filterOption={(input, option) => option.props.children.toLowerCase()
            .indexOf(input.toLowerCase()) >= 0}
        >
          <Option key="'null'" value="'null'">
            {'空'}
          </Option>
        </Select>
      );
    }
  }

  render() {
    const { form, onCancel } = this.props;
    const {
      loading, origin, deleteItem, o, arr, quickFilterFiled,
    } = this.state;
    const { getFieldDecorator } = form;
    return (
      <Sidebar
        className="c7n-filter"
        title="修改快速搜索"
        okText="修改"
        cancelText="取消"
        visible
        confirmLoading={loading}
        onOk={this.handleOk.bind(this)}
        onCancel={onCancel}
      >
        <Content
          style={{
            padding: 0,
            width: 700,
          }}
          title={`在项目“${AppState.currentMenuType.name}”中修改快速搜索`}
          description="通过定义快速搜索，可以在待办事项和活跃冲刺的快速搜索工具栏生效，帮助您更好的筛选过滤问题面板。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/quick-search/"
        >
          <Form layout="vertical">
            <FormItem style={{ width: 520 }}>
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '名称必填',
                }, {
                  validator: this.checkSearchNameRepeat,
                }],
                initialValue: origin.name,
              })(
                <Input
                  label="名称"
                  maxLength={10}
                />,
              )}
            </FormItem>
            {
              arr.map((filter, index) => (
                <div key={index.toString()}>
                  {
                    deleteItem.indexOf(index) === -1 && (
                      <div>
                        {
                          index !== 0 && (
                            <FormItem style={{ width: 80, display: 'inline-block', marginRight: 10 }}>
                              {getFieldDecorator(`filter-${index}-ao`, {
                                rules: [{
                                  required: true,
                                  message: '关系为必选字段',
                                }],
                                initialValue: o[index - 1],
                              })(
                                <Select label="关系">
                                  <Option key="and" value="and">且</Option>
                                  <Option key="or" value="or">或</Option>
                                </Select>,
                              )}
                            </FormItem>
                          )
                        }
                        <FormItem style={{ width: 120, display: 'inline-block', marginRight: 10 }}>
                          {getFieldDecorator(`filter-${index}-prop`, {
                            rules: [{
                              required: true,
                              message: '属性不可为空',
                            }],
                            initialValue: arr[index].fieldCode,
                          })(
                            <Select
                              label="属性"
                              onChange={() => {
                                form.setFieldsValue({
                                  [`filter-${index}-rule`]: undefined,
                                  [`filter-${index}-value`]: undefined,
                                });
                              }}
                            >
                              {
                                quickFilterFiled.map(v => (
                                  <Option key={v.fieldCode} value={v.fieldCode}>{v.name}</Option>
                                ))
                              }
                            </Select>,
                          )}
                        </FormItem>
                        <FormItem style={{ width: 80, display: 'inline-block', marginRight: 10 }}>
                          {getFieldDecorator(`filter-${index}-rule`, {
                            rules: [{
                              required: true,
                              message: '关系不可为空',
                            }],
                            initialValue: this.transformOperation(arr[index].operation),
                          })(
                            this.renderOperation(form.getFieldValue(`filter-${index}-prop`), index),
                          )}
                        </FormItem>
                        <FormItem style={{ width: 300, display: 'inline-block' }}>
                          {getFieldDecorator(`filter-${index}-value`, {
                            rules: [{
                              required: true,
                              message: '值不可为空',
                            }],
                            initialValue: arr[index].value,
                          })(
                            this.renderValue(form.getFieldValue(`filter-${index}-prop`), form.getFieldValue(`filter-${index}-rule`)),
                          )}
                        </FormItem>
                        {
                          index ? (
                            <Button
                              shape="circle"
                              style={{ margin: 10 }}
                              onClick={() => {
                                const arrCopy = deleteItem.slice();
                                arrCopy.push(index);
                                this.setState({
                                  deleteItem: arrCopy,
                                  deleteFlag: true,
                                });
                              }}
                            >
                              <Icon type="delete" />
                            </Button>
                          ) : null
                        }
                      </div>
                    )
                  }
                </div>
              ))
            }
            <Button
              type="primary"
              funcType="flat"
              onClick={() => {
                const arrCopy = arr.slice();
                arrCopy.push({
                  prop: undefined,
                  rule: undefined,
                  value: undefined,
                });
                this.setState({
                  arr: arrCopy,
                });
              }}
            >
              <Icon type="add icon" />
              <span>添加属性</span>
            </Button>
            <FormItem style={{ width: 520 }}>
              {getFieldDecorator('description', {
                initialValue: origin.description,
              })(
                <TextArea label="描述" autosize maxLength={30} />,
              )}
            </FormItem>
          </Form>

        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(AddComponent);
