import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Content } from 'choerodon-front-boot';
import { Select } from 'choerodon-ui';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';


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
      swimlaneBasedCode: selectValue || KanbanStore.getSwimLaneCode,
    };
    KanbanStore.axiosUpdateBoardDefault(data).then((res) => {
      KanbanStore.setSwimLaneCode(selectedValue);
      Choerodon.prompt('保存成功');
    }).catch((error) => {      
      Choerodon.prompt('保存失败');
    });
  }

  render() {
    const defaultSelect = KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard);
    return (
      <Content
        description="泳道是指看板中一横排的主板，基于横排对问题进行状态的流转。泳道类型可以在下面进行修改，并将自动保存。注意：修改泳道会修改看板的分组维度，同时修改看板样式。"
        style={{
          padding: 0,         
          height: '100%',
        }}
      >
        <Select
          style={{ width: 512 }}
          label="基础泳道在"
          defaultValue={KanbanStore.getSwimLaneCode || 'swimlane_none'}
          onChange={(value) => {
            this.setState({
              selectValue: value,
            }, () => {
              this.handleSave(defaultSelect);
            });
          }}
        >
          <Option value="feature">特性</Option>          
          <Option value="swimlane_none">无</Option>
        </Select>
      </Content>
    );
  }
}

export default SwimLanePage;
