import { observable, action, computed } from 'mobx';

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
  @observable doc = {};

  @observable workLogs = [];

  @observable dataLogs = [];

  @observable linkIssues = [];

  @observable branch = {};

  @action setIssueTypes(issueTypes) {
    this.issueTypes = issueTypes;
  }

  @computed get getIssueTypes() {
    return this.issueTypes;
  }

  @action setDoc(data) {
    this.doc = data;
  }

  @computed get getDoc() {
    return this.doc;
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

  @computed get getBranch() {
    return this.branch;
  }

  @action initIssueAttribute(doc, workLogs, dataLogs, linkIssues, branch) {
    this.doc = doc;
    this.workLogs = workLogs;
    this.dataLogs = dataLogs;
    this.linkIssues = linkIssues || [];
    this.branch = branch || {};
  }
}
export default EditIssueStore;
