import axios from 'axios';
import {
  observable, action, computed, toJS, reaction,
} from 'mobx';
import { store, stores } from '@choerodon/boot';

const { AppState } = stores;


class EditIssueStore {
  // issue
  @observable issue = {};

  @action setIssue(data) {
    this.issue = data;
  }

  @computed get getIssue() {
    return this.issue;
  }

  // fields
  @observable fields = [];

  @action setIssueFields(issue, fields) {
    this.fields = fields;
    this.issue = issue;
  }

  @computed get getFields() {
    return this.fields;
  }

  @observable issueTypes = [];

  // issue attribute
  @observable wiki = {};

  @observable workLogs = [];

  @observable dataLogs = [];

  @observable linkIssues = [];

  @observable branches = {};

  @action setIssueTypes(issueTypes) {
    this.issueTypes = issueTypes;
  }

  @computed get getIssueTypes() {
    return this.issueTypes;
  }

  @action setWiki(data) {
    this.wiki = data;
  }

  @computed get getWiki() {
    return this.wiki;
  }

  @action setWorkLogs(data) {
    this.workLogs = data;
  }

  @computed get getWorkLogs() {
    return this.workLogs.slice();
  }

  @action setDataLogs(data) {
    this.dataLogs = data;
  }

  @computed get getDataLogs() {
    return this.dataLogs;
  }

  @action setLinkIssues(data) {
    this.linkIssues = data;
  }

  @computed get getLinkIssues() {
    return this.linkIssues;
  }

  @action setBranches(data) {
    this.branches = data;
  }

  @computed get getBranches() {
    return this.branches;
  }

  @action initIssueAttribute(wiki, workLogs, dataLogs, linkIssues, branches) {
    this.wiki = wiki;
    this.workLogs = workLogs;
    this.dataLogs = dataLogs;
    this.linkIssues = linkIssues || [];
    this.branches = branches;
  }
}
export default new EditIssueStore();