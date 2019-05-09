import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Content, stores } from 'choerodon-front-boot';
import {
  Button, Select, Icon, message,
} from 'choerodon-ui';
import _ from 'lodash';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';

const { AppState } = stores;
const { Option } = Select;

@observer
class SwimLanePage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectValue: '',
    };
  }

  handleSave(select) {
    const { selectValue, selectedValue } = this.state;
    const data = {
      // objectVersionNumber: select.objectVersionNumber,
      boardId: select.boardId,
      swimlaneBasedCode: selectValue || ScrumBoardStore.getSwimLaneCode,
    };
    ScrumBoardStore.axiosUpdateBoardDefault(data).then((res) => {
      ScrumBoardStore.setSwimLaneCode(selectedValue);
      Choerodon.prompt('保存成功');
    }).catch((error) => {
      Choerodon.prompt('保存失败');
    });
  }

  render() {
    const defaultSelect = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard);
    return (
      <Content
        description="泳道是指看板中一横排的主板，基于横排对问题进行状态的流转。泳道类型可以在下面进行修改，并将自动保存。注意：修改泳道会修改看板的分组维度，同时修改看板样式。"
        style={{
          padding: 0,
          height: '100%',
        }}
        link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/manage-kanban/"
      >
        <Select
          style={{ width: 512 }}
          label="基础泳道在"
          defaultValue={ScrumBoardStore.getSwimLaneCode || 'parent_child'}
          onChange={(value) => {
            this.setState({
              selectValue: value,
            }, () => {
              this.handleSave(defaultSelect);
            });
          }}
        >
          <Option value="parent_child">故事</Option>
          <Option value="assignee">经办人</Option>
          <Option value="swimlane_epic">史诗</Option>
          <Option value="swimlane_none">无</Option>
        </Select>
      </Content>
    );
  }
}

export default SwimLanePage;
