import React, { Component } from 'react';
import { observer } from 'mobx-react';
import CreateFeature from '../../../../Feature/FeatureComponent/CreateFeature';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';

@observer
class CreateFeatureContainer extends Component {
  handleCancel=() => {
    KanbanStore.setCreateFeatureVisible(false);
  }


  render() {
    const visible = KanbanStore.createFeatureVisible;
    return <CreateFeature visible={visible} onCancel={this.handleCancel} {...this.props} />;
  }
} 

export default CreateFeatureContainer;
