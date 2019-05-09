import axios from 'axios';
import { observable, action, computed } from 'mobx';
import { store, stores } from 'choerodon-front-boot';

@store('PageStore')
class PageStore {
  @observable apiGetway = '';

  @observable orgId = '';

  @observable page = [];

  @observable pageDetail = {
    content: [],
  };

  @computed get getPage() {
    return this.page.slice();
  }

  @action setPage(data) {
    this.page = data;
  }

  @computed get getPageDetail() {
    return this.pageDetail;
  }

  @action setPageDetail(data) {
    this.pageDetail = data;
  }

  @action updatePageDetail(field) {
    this.pageDetail.content = this.pageDetail.content.map((item) => {
      if (field.fieldId === item.fieldId) {
        return {
          ...item,
          objectVersionNumber: field.objectVersionNumber,
          display: field.display,
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

  loadPage = (page, size, filter) => axios.post(
    `${this.apiGetway}/page?page=${page}&size=${size}&organizationId=${this.orgId}`, filter,
  ).then((data) => {
    if (data && !data.failed) {
      this.setPage(data.content);
    } else {
      Choerodon.prompt(data.message);
    }
  });

  loadPageDetail = code => axios.get(
    `${this.apiGetway}/page_field/list?pageCode=${code}&organizationId=${this.orgId}`,
  ).then((data) => {
    if (data && !data.failed) {
      this.setPageDetail(data);
    } else {
      Choerodon.prompt(data.message);
    }
  });

  updateField = (fieldId, code, field) => axios.put(
    `${this.apiGetway}/page_field/${fieldId}?pageCode=${code}&organizationId=${this.orgId}`, field,
  ).then((data) => {
    if (data && !data.failed) {
      this.updatePageDetail(data);
    } else {
      Choerodon.prompt('请刷新后重试！');
    }
  });

  updateFieldOrder = (code, order) => axios.post(
    `${this.apiGetway}/page_field/adjust_order?pageCode=${code}&organizationId=${this.orgId}`, order,
  ).then((data) => {
    if (data && !data.failed) {
      return data;
    } else {
      Choerodon.prompt('请刷新后重试！');
      return null;
    }
  });
}

const pageStore = new PageStore();
export default pageStore;
