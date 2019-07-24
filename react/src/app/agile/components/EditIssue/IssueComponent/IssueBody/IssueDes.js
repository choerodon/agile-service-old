import React, {
  useState, useEffect, useContext,
} from 'react';
import { Icon, Button, Tooltip } from 'choerodon-ui';

import WYSIWYGEditor from '../../../WYSIWYGEditor';
import { text2Delta, delta2Html, returnBeforeTextUpload } from '../../../../common/utils';
import { IssueDescription } from '../../../CommonComponent';
import FullEditor from '../../../FullEditor';
import { updateIssue } from '../../../../api/NewIssueApi';
import EditIssueContext from '../../stores';

const IssueDes = ({ reloadIssue }) => {
  const [editDesShow, setEditDesShow] = useState(false);
  const [fullEdit, setFullEdit] = useState(false);
  const [editDes, setEditDes] = useState('');
  const { store, disabled } = useContext(EditIssueContext);
  const { description } = store.getIssue;
  useEffect(() => {    
    setEditDes(description);
    setEditDesShow(false);
  }, [description]);

  const updateIssueDes = (value) => {
    const { issueId, objectVersionNumber } = store.getIssue;
    const obj = {
      issueId,
      objectVersionNumber,
    };
    const newValue = value || editDes;  
    if (newValue) {
      returnBeforeTextUpload(newValue, obj, updateIssue, 'description')
        .then(() => {
          if (reloadIssue) {
            reloadIssue(issueId);
          }
        });
    }
    setEditDesShow(false);   
    setFullEdit(false); 
  };

  const renderDes = () => {
    if (editDesShow === undefined) {
      return null;
    }
    if (!description || editDesShow) {
      return (
        editDesShow && (
          <div
            className="line-start mt-10 two-to-one"
          >
            <div style={{
              width: '100%',
              position: 'absolute',
              top: 0,
              bottom: 0,
              marginBottom: 25,
            }}
            >
              <WYSIWYGEditor
                autoFocus
                bottomBar
                value={text2Delta(editDes)}
                style={{
                  height: '100%', width: '100%',
                }}
                onChange={(value) => {
                  setEditDes(value);                 
                }}
                handleDelete={() => {
                  setEditDesShow(false);
                  setEditDes(description);                  
                }}
                handleSave={() => {
                  setEditDesShow(false);                             
                  updateIssueDes();
                }}
              />
            </div>
          </div>
        )
      );
    } else {
      const delta = delta2Html(description);
      return (
        <div className="c7n-content-wrapper" style={{ maxHeight: 400, overflow: 'auto' }}>
          <div
            className="mt-10 c7n-description"
            role="none"
          >
            <IssueDescription data={delta} />
          </div>
        </div>
      );
    }
  };


  const callback = (value) => { 
    updateIssueDes(value);   
  };

  return (
    <div id="des">
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">
          <Icon type="subject c7n-icon-title" />
          <span>描述</span>
        </div>
        <div style={{
          flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
        }}
        />
        {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px', position: 'relative' }}>
            <Tooltip title="全屏编辑" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => setFullEdit(true)}>
                <Icon type="zoom_out_map icon" style={{ marginRight: 2 }} />
              </Button>
            </Tooltip>
            <Tooltip title="编辑" getPopupContainer={triggerNode => triggerNode.parentNode.parentNode}>
              <Button
                style={{ padding: '0 6px' }}
                className="leftBtn"
                funcType="flat"
                onClick={() => {
                  setEditDesShow(true);
                  setEditDes(description);
                }}
              >
                <Icon
                  className="c7n-des-fullEdit"
                  role="none"
                  type="mode_edit icon"
                />
              </Button>
            </Tooltip>
          </div>
        )}
      </div>
      {renderDes()}
      {
        fullEdit ? (
          <FullEditor
            autoFocus
            initValue={text2Delta(editDes)}
            visible={fullEdit}
            onCancel={() => setFullEdit(false)}
            onOk={callback}
          />
        ) : null
      }
    </div>
  );
};

export default IssueDes;
