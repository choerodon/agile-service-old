import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Modal } from 'choerodon-ui';
import './PI.scss';
import SprintContainer from './PIItemComponent/PIContainer';
import NoneSprint from './PIItemComponent/NonePI';

const { confirm } = Modal;

@observer
class PIItem extends Component {
  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  /**
   *删除冲刺事件
   *
   * @param {*} e
   * @memberof PIItem
   */
  handleDeleteSprint = (item, e) => {
    const that = this;
    const { store, refresh } = this.props;
    if (e.key === '0') {
      if (item.issueSearchDTOList && item.issueSearchDTOList.length > 0) {
        confirm({
          width: 560,
          wrapClassName: 'deleteConfirm',
          title: `删除冲刺${item.sprintName}`,
          content: (
            <div>
              <p style={{ marginBottom: 10 }}>请确认您要删除这个冲刺。</p>
              <p style={{ marginBottom: 10 }}>这个冲刺将会被彻底删除，冲刺中的任务将会被移动到待办事项中。</p>
            </div>
          ),
          onOk() {
            return that.props.store.axiosDeleteSprint(item.sprintId).then(() => {
              that.props.refresh();
            }).catch(() => {
            });
          },
          onCancel() {},
          okText: '删除',
          okType: 'danger',
        });
      } else {
        store.axiosDeleteSprint(item.sprintId).then(() => {
          refresh();
        }).catch(() => {
        });
      }
    }
  };

  render() {
    const {
      refresh, store, type, display,
    } = this.props;
    const arr = type === 'pi' ? store.getPiList : store.getSprintData;
    return (
      <div
        role="none"
        style={{
          width: '100%',
        }}
        onClick={() => {
          if (!store.isDragging) {
            store.onBlurClick();
          }
        }}
      >
        {
          arr.length
            ? arr.filter(s => display || s.statusCode === 'doing').map((item, index) => (
              <SprintContainer
                isCreated={item.isCreated}
                refresh={refresh}
                key={String(item.id)}
                data={item}
                type={type}
                store={store}
                index={index}
              />
            )) : <NoneSprint type={type} />
        }
        <SprintContainer
          key="0"
          data={store.getBacklogData}
          type={type === 'pi' ? 'PIBacklog' : 'backlog'}
          store={store}
          refresh={refresh}
        />
      </div>
    );
  }
}

export default PIItem;
