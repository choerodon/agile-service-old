import React, { Component } from 'react';
import { Icon, Button } from 'choerodon-ui';
import EditArtNameSidebar from './EditArtNameSidebar';

class ArtInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      editNameVisible: false,
    };
  }

  handleEditArtNameOk = (newName) => {
    const { onSubmit } = this.props;
    onSubmit({ name: newName });
    this.setState({
      editNameVisible: false,
    });
  }

  handleEditArtNameCancel = () => {
    this.setState({
      editNameVisible: false,
    });
  }

  render() {
    const {
      name, startBtnVisible, stopBtnVisible, onStartArtBtnClick, onStopArtBtnClick, 
    } = this.props;
    const { editNameVisible } = this.state;
    return (
      <div style={{
        display: 'flex', justifyContent: 'space-between', fontSize: '18px', fontWeight: 500, margin: '0 0 20px', 
      }}
      >
        <div className="c7n-artInfo-name">
          <span>{name}</span>
          <Icon
            role="none"
            type="mode_edit mlr-3 pointer"
            style={{
              color: '#3F51B5',
              marginLeft: 5,
              cursor: 'pointer',
            }}
            onClick={() => {
              this.setState({
                editNameVisible: true,
              });
            }}
          />
        </div>
        <div className="c7n-artInfo-btn">
          {startBtnVisible && <Button type="primary" funcType="raised" onClick={onStartArtBtnClick}>启动火车</Button>}
          {stopBtnVisible && <Button funcType="raised" style={{ marginLeft: 10 }} onClick={onStopArtBtnClick}>停止火车</Button>}
        </div>
        <EditArtNameSidebar
          name={name}
          visible={editNameVisible}
          onOk={this.handleEditArtNameOk}
          onCancel={this.handleEditArtNameCancel}
        /> 
      </div>
    );
  }
}

export default ArtInfo;
