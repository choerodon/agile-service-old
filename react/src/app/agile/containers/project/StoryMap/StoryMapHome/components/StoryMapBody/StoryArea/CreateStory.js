import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'choerodon-ui';
import Card from '../Card';
import './CreateStory.scss';

class CreateStory extends Component {
  state = {
    adding: false,
  }

  handleBlur = (e) => {   
    const { value } = e.target;
    if (value) {
      // console.log('value');
    } else {
      this.setState({
        adding: false,
      });
    }
  }

  handleAddStoryClick=() => {
    this.setState({
      adding: true,
    });
  }

  render() {
    const { adding } = this.state;
    return (
      <Card
        style={{
          boxShadow: adding ? '0 0 4px -2px rgba(0,0,0,0.50), 0 2px 4px 0 rgba(0,0,0,0.13)' : '',
          borderRadius: 2,
          padding: 7,
          display: 'flex',
          justifyContent: 'center',
        }}
        className="c7nagile-StoryMap-CreateStory"
      >
        {
          adding 
            ? <Input autoFocus onBlur={this.handleBlur} placeholder="在此创建新内容" />
            : (
              <div className="c7nagile-StoryMap-CreateStory-btn">
                <span role="none" style={{ cursor: 'pointer', color: '#3F51B5' }} onClick={this.handleAddStoryClick}>新建问题</span>
                {' '}
                或
                {' '}
                <span style={{ cursor: 'pointer', color: '#3F51B5' }}>从需求池引入</span>
              </div>
            )
        }
      </Card>
    );
  }
}

CreateStory.propTypes = {

};

export default CreateStory;
