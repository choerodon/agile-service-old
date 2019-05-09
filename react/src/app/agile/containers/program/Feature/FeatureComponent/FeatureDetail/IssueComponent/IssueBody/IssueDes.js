import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import WYSIWYGEditor from '../../../../../../../components/WYSIWYGEditor';
import { text2Delta, delta2Html, returnBeforeTextUpload } from '../../../../../../../common/utils';
import { IssueDescription } from '../../../../../../../components/CommonComponent';
import FullEditor from '../../../../../../../components/FullEditor';
import { updateIssue } from '../../../../../../../api/NewIssueApi';

@inject('AppState')
@observer class IssueCommit extends Component {
  constructor(props) {
    super(props);
    this.state = {
      editDesShow: false,
      description: '',
      editDes: '',
      edit: false,
    };
  }

  componentDidMount() {
    const { store } = this.props;
    const { description } = store.getIssue;
    this.setState({
      editDes: description,
      description,
    });
  }

  componentWillReceiveProps(nextProps) {
    const { store } = nextProps;
    const { description } = store.getIssue;
    this.setState({
      description,
      editDes: description,
      editDesShow: false,
    });
  }

  updateIssueDes = () => {
    const { editDes } = this.state;
    const { store, reloadIssue } = this.props;
    const { issueId, objectVersionNumber } = store.getIssue;
    const obj = {
      issueId,
      objectVersionNumber,
    };
    if (editDes) {
      returnBeforeTextUpload(editDes, obj, updateIssue, 'description')
        .then(() => {
          if (reloadIssue) {
            reloadIssue();
          }
        });
    }
    this.setState({
      editDesShow: false,
    });
  };

  renderDes() {
    const { editDesShow, editDes, description } = this.state;
    if (editDesShow === undefined) {
      return null;
    }
    if (!description || editDesShow) {
      return (
        <div
          className="line-start mt-10 two-to-one"
        >
          <div style={{
            width: '100%',
            position: 'absolute',
            top: 0,
            bottom: 0, 
          }}
          >
            <WYSIWYGEditor
              bottomBar
              value={text2Delta(editDes)}
              style={{ height: '100%', width: '100%' }}
              onChange={(value) => {
                this.setState({ editDes: value });
              }}
              handleDelete={() => {
                this.setState({
                  editDesShow: false,
                  editDes: description,
                });
              }}
              handleSave={() => {
                this.setState({
                  editDesShow: false,
                  description: editDes || '',
                });
                this.updateIssueDes();
              }}
              handleClickOutSide={() => {
                this.setState({
                  editDesShow: false,
                  description: editDes || '',
                });
                this.updateIssueDes();
              }}
            />
          </div>
        </div>
      );
    } else {
      const delta = delta2Html(description);
      return (
        <div className="c7n-content-wrapper">
          <div
            className="mt-10 c7n-description"
            role="none"
          >
            <IssueDescription data={delta} />
          </div>
        </div>
      );
    }
  }

  render() {
    const { edit, description, editDes } = this.state;

    const callback = (value) => {
      this.setState({
        description: value,
        editDes: value,
        edit: false,
      }, () => {
        this.updateIssueDes();
      });
    };

    return (
      <div id="des" style={{ marginBottom: 65 }}>
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="subject c7n-icon-title" />
            <span>描述</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          <div className="c7n-title-right" style={{ marginLeft: '14px', position: 'relative' }}>
            <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ edit: true })}>
              <Icon type="zoom_out_map icon" style={{ marginRight: 2 }} />
              <span>全屏编辑</span>
            </Button>
            <Icon
              className="c7n-des-edit"
              style={{ position: 'absolute', top: 8, right: -20 }}
              role="none"
              type="mode_edit mlr-3 pointer"
              onClick={() => {
                this.setState({
                  editDesShow: true,
                  editDes: description,
                });
              }}
            />
          </div>
        </div>
        {this.renderDes()}
        {
          edit ? (
            <FullEditor
              initValue={text2Delta(editDes)}
              visible={edit}
              onCancel={() => this.setState({ edit: false })}
              onOk={callback}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueCommit));
