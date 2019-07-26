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


  @observable createBranchShow = false;

  @observable commitShow = false;

  @observable mergeRequestShow = false;

  @observable workLogShow = false;

  @observable createSubTaskShow = false;

  @observable createSubBugShow = false;

  @observable copyIssueShow = false;

  @observable transformSubIssueShow = false;

  @observable transformFromSubIssueShow = false;

  @observable relateStoryShow = false;

  @observable assigneeShow = false;

  @observable changeParentShow = false;
  
  @observable detailShow = false;

  @action setCreateBranchShow(data) {
    this.createBranchShow = data;
  }

  @computed get getCreateBranchShow() {
    return this.createBranchShow;
  }

  @action setCommitShow(data) {
    this.commitShow = data;
  }

  @computed get getCommitShow() {
    return this.commitShow;
  }

  @action setMergeRequestShow(data) {
    this.mergeRequestShow = data;
  }

  @computed get getMergeRequestShow() {
    return this.mergeRequestShow;
  }

  @action setWorkLogShow(data) {
    this.workLogShow = data;
  }

  @computed get getWorkLogShow() {
    return this.workLogShow;
  }

  @action setCreateSubTaskShow(data) {
    this.createSubTaskShow = data;
  }

  @computed get getCreateSubTaskShow() {
    return this.createSubTaskShow;
  }

  @action setCreateSubBugShow(data) {
    this.createSubBugShow = data;
  }

  @computed get getCreateSubBugShow() {
    return this.createSubBugShow;
  }

  @action setCopyIssueShow(data) {
    this.copyIssueShow = data;
  }

  @computed get getCopyIssueShow() {
    return this.copyIssueShow;
  }

  @action setTransformSubIssueShow(data) {
    this.transformSubIssueShow = data;
  }

  @computed get getTransformSubIssueShow() {
    return this.transformSubIssueShow;
  }

  @action setTransformFromSubIssueShow(data) {
    this.transformFromSubIssueShow = data;
  }

  @computed get getTransformFromSubIssueShow() {
    return this.transformFromSubIssueShow;
  }

  @action setRelateStoryShow(data) {
    this.relateStoryShow = data;
  }

  @computed get getRelateStoryShow() {
    return this.relateStoryShow;
  }

  @action setAssigneeShow(data) {
    this.assigneeShow = data;
  }

  @computed get getAssigneeShow() {
    return this.assigneeShow;
  }

  @action setChangeParentShow(data) {
    this.changeParentShow = data;
  }

  @computed get getChangeParentShow() {
    return this.changeParentShow;
  }

  @action setDetailShow(detailShow) {
    this.detailShow = detailShow;
  }

  @computed get getDetailShow() {
    return this.detailShow;
  }
}
export default EditIssueStore;
