import {
  observable, action, computed,
} from 'mobx';
import { getProjectsInProgram } from '../../../api/CommonApi';

class IsInProgramStore {
  @observable isInProgram = false;
  
  @observable program = false;


  refresh=() => {
    getProjectsInProgram().then((program) => {
      this.setIsInProgram(Boolean(program));
      this.setProgram(program);     
    });
  }

  @action setIsInProgram(isInProgram) {
    this.isInProgram = isInProgram;
  }

  @action setProgram(program) {
    this.program = program;
  }

  @computed get getIsInProgram() {
    return this.isInProgram;
  }
}


export default new IsInProgramStore();
