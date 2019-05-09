import axios from 'axios';
import { observable, action, computed } from 'mobx';
import { store, stores } from 'choerodon-front-boot';

const { AppState } = stores;

@store('ObjectSchemeStore')
class ObjectSchemeStore {
  @observable apiGetway = '';

  @observable orgId = '';

  @observable objectScheme = [];

  @observable schemeDetail = {
    content: [],
  };

  @observable fieldType = [];

  @observable fieldContext = [];

  @observable field = {};

  @action setField(data) {
    this.field = data;
  }

  @computed get getField() {
    return this.field;
  }

  @computed get getFieldType() {
    return this.fieldType.slice();
  }

  @computed get getFieldContext() {
    return this.fieldContext.slice();
  }

  @action initLookupValue(fieldType = {}, fieldContext = {}) {
    this.fieldType = fieldType.lookupValues || [];
    this.fieldContext = fieldContext.lookupValues || [];
  }

  @computed get getObjectScheme() {
    return this.objectScheme.slice();
  }

  @action setObjectScheme(data) {
    this.objectScheme = data;
  }

  @computed get getSchemeDetail() {
    return this.schemeDetail;
  }

  @action setSchemeDetail(data) {
    this.schemeDetail = data;
  }

  @action updateSchemeDetail(field) {
    this.schemeDetail.content = this.schemeDetail.content.map((item) => {
      if (field.id === item.id) {
        return {
          ...item,
          objectVersionNumber: field.objectVersionNumber,
          required: field.required,
        };
      } else {
        return item;
      }
    });
  }

  @action initCurrentMenuType(data) {
    const { type, id, organizationId } = data;
    this.apiGetway = `/foundation/v1/${type}s/${id}`;
    this.orgId = organizationId;
  }

  loadObjectScheme = (page, size, filter) => axios.post(
    `${this.apiGetway}/object_scheme?page=${page}&size=${size}&organizationId=${this.orgId}`, filter,
  ).then((data) => {
    if (data && !data.failed) {
      this.setObjectScheme(data.content);
    } else {
      Choerodon.prompt(data.message);
    }
  });

  loadSchemeDetail = code => axios.get(
    `${this.apiGetway}/object_scheme_field/list?schemeCode=${code}&organizationId=${this.orgId}`,
  ).then((data) => {
    if (data && !data.failed) {
      this.setSchemeDetail(data);
    } else {
      Choerodon.prompt(data.message);
    }
  });

  loadFieldDetail = fieldId => axios.get(
    `${this.apiGetway}/object_scheme_field/${fieldId}?organizationId=${this.orgId}`,
  ).then((data) => {
    if (data) {
      this.setField(data);
    }
    return data;
  });

  loadLookupValue = code => axios.get(`/foundation/v1/organizations/${this.orgId}/lookup_values/${code}`);

  createField = field => axios.post(`${this.apiGetway}/object_scheme_field?organizationId=${this.orgId}`, field);

  deleteField = fieldId => axios.delete(`${this.apiGetway}/object_scheme_field/${fieldId}?organizationId=${this.orgId}`);

  checkName = (name, schemeCode) => axios.get(`${this.apiGetway}/object_scheme_field/check_name?name=${name}&organizationId=${this.orgId}&schemeCode=${schemeCode}`);

  checkCode = (code, schemeCode) => axios.get(`${this.apiGetway}/object_scheme_field/check_code?code=${code}&organizationId=${this.orgId}&schemeCode=${schemeCode}`);

  updateField = (fieldId, field) => axios.put(
    `${this.apiGetway}/object_scheme_field/${fieldId}?organizationId=${this.orgId}`, field,
  ).then((data) => {
    if (data && !data.failed) {
      this.updateSchemeDetail(data);
    } else {
      Choerodon.prompt('请刷新后重试！');
    }
  });
}

const objectSchemeStore = new ObjectSchemeStore();
export default objectSchemeStore;
