import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Button, Select, Checkbox, Menu, Dropdown, Icon,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class SwitchSwimLine extends Component {
  handleSwitchSwinLine=({ key }) => {
    StoryMapStore.switchSwimLine(key);
  }

  render() {
    const { loading, storyMapData, swimLine } = StoryMapStore;
    const swimlanMenu = (
      <Menu onClick={this.handleSwitchSwinLine} selectable defaultSelectedKeys={[swimLine]}>
        <Menu.Item key="none">无泳道</Menu.Item>
        <Menu.Item key="version">版本泳道</Menu.Item>
        {/* <Menu.Item key="sprint">冲刺泳道</Menu.Item> */}
      </Menu>
    );
    return (     
      <Dropdown
        overlay={swimlanMenu}
        trigger={['click']}
        overlayClassName="modeMenu"
        placement="bottomCenter"
        getPopupContainer={triggerNode => triggerNode}
      >
        <Button>
          {swimLine === 'none' && '无泳道'}
          {swimLine === 'version' && '版本泳道'}
          {swimLine === 'sprint' && '冲刺泳道'}
          <Icon type="arrow_drop_down" />
        </Button>
      </Dropdown>
 
    );
  }
}

SwitchSwimLine.propTypes = {

};

export default SwitchSwimLine;
