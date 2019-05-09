import {
  observable, action, computed,
} from 'mobx';
import { store } from 'choerodon-front-boot';

@store('VisibleStore')
class VisibleStore {
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
}

const visibleStore = new VisibleStore();
export default visibleStore;
