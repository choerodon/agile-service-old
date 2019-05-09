import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import PIHeader from './PIHeader';
import PIBody from './PIBody';
import NoneIssue from './NoneIssue';
import NoneBacklog from './NoneBacklog';
import BacklogHeader from './BacklogHeader';

const shouldContainTypeCode = ['feature'];

@inject('AppState')
@observer class SprintContainer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: true,
    };
    this.strategyMap = new Map([
      ['pi', this.renderPI],
      ['PIBacklog', this.renderBacklog],
    ]);
  }

  componentDidMount() {
    const { isCreated } = this.props;
    if (isCreated) {
      setTimeout(() => {
        this.ref.scrollIntoView();
        this.ref.style.background = 'white';
      }, 10, this);
    }
  }

  toggleSprint = () => {
    const { expand } = this.state;
    this.setState({
      expand: !expand,
    });
  };

  renderBacklog = (data) => {
    const { expand } = this.state;
    const { store, refresh } = this.props;
    const issueCount = store.getIssueMap.get('0') ? store.getIssueMap.get('0').length : 0;
    return (
      <div
        ref={(e) => {
          this.ref = e;
        }}
        style={{
          // background: isCreated ? '#eee' : 'white',
          transition: 'all 2s',
          width: '100%',
        }}
        // key={sprintItem.sprintId}
      >
        <BacklogHeader
          issueCount={issueCount}
          data={data}
          expand={expand}
          sprintId="0"
          toggleSprint={this.toggleSprint}
        />
        <PIBody
          featureTypeDTO={store.getIssueTypes.find(type => type.typeCode === 'feature')}
          defaultPriority={store.getDefaultPriority}
          issueCount={!!issueCount}
          expand={expand}
          piId="0"
          droppableId="backlogData"
          emptyIssueComponent={<NoneBacklog store={store} />}
          store={store}
          refresh={refresh}
        />
      </div>
    );
  };

  renderPI = (pi) => {
    const {
      refresh, isCreated, store, index, 
    } = this.props;
    const { expand } = this.state;
    const issueCount = store.getIssueMap.get(pi.id.toString()) ? store.getIssueMap.get(pi.id.toString()).length : 0;
    return (
      <div
        ref={(e) => {
          this.ref = e;
        }}
        style={{
          background: isCreated ? '#eee' : 'white',
          transition: 'all 2s',
          width: '100%',
        }}
        key={pi.id}
      >
        <PIHeader
          refresh={refresh}
          issueCount={issueCount}
          data={pi}
          expand={expand}
          piId={pi.id.toString()}
          toggleSprint={this.toggleSprint}
          store={store}
          index={index}
        />
        <PIBody
          featureTypeDTO={store.getIssueTypes.find(type => type.typeCode === 'feature')}         
          defaultPriority={store.getDefaultPriority}
          issueCount={!!issueCount}
          expand={expand}
          piId={pi.id.toString()}
          droppableId={pi.id.toString()}
          emptyIssueComponent={<NoneIssue type="pi" store={store} />}
          store={store}
          refresh={refresh}
        />
      </div>
    );
  };

  render() {
    const { data, type } = this.props;
    return this.strategyMap.get(type)(data);
  }
}

export default SprintContainer;
