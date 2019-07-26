// 反馈按钮
import React, { useState, useEffect } from 'react';
import { axios, stores } from '@choerodon/boot';
import { Button } from 'choerodon-ui';
import './FeedbackButton.scss';
import FeedbackContent from './FeedbackContent';

const { AppState } = stores;
let customPage;
let customSize;

const FeedbackButton = WrappedComponent => ((props) => {
  const [visible, setVisible] = useState(false);

  const useFetch = (url) => {
    const [data, setData] = useState(null);
    const [page, setPage] = useState(customPage || 1);
    const [size, setSize] = useState(customSize || 10);
    const [totalNum, setTotal] = useState(0);
    const [feedbackTableLoading, setFeedbackTableLoading] = useState(true);

    const fetchUser = async (type = null, paginationPage = customPage || 1, paginationSize = customSize || 10) => {
      setFeedbackTableLoading(true);
      const {
        pageNum, pageSize, total, list,
      } = await axios.post(`${url}?page=${paginationPage}&size=${paginationSize}`, type ? {
        searchArgs: { typeList: [type] },
      } : {});
      setData(list);
      setPage(pageNum);
      setSize(pageSize);
      setTotal(total);
      setFeedbackTableLoading(false);
      customPage = pageNum;
      customSize = pageSize;
    };

    useEffect(() => {
      fetchUser();
    }, []);
    return {
      data, feedbackTableLoading, fetchUser, page, size, totalNum,
    };
  };

  const [type, setType] = useState(null);

  const {
    data, feedbackTableLoading, fetchUser, page, size, totalNum,
  } = useFetch(`/agile/v1/projects/${AppState.currentMenuType.id}/feedback/list`);

  const handleTabChange = (key) => {
    if (key === 'all') {
      setType(null);
    } else {
      setType(key);
    }
    fetchUser(key === 'all' ? null : key);
  };
  
  const handleTableChange = ({ current, pageSize }, filters, sorter, barFilters) => {
    customPage = current;
    customSize = pageSize;
    fetchUser(type, current, pageSize);
  };

  const openExport = () => {

  };

  const [delta, setDelta] = useState(false);
  const [feedbackContentLoading, setFeedbackContentLoading] = useState(false);
  const [fileList, setFileList] = useState([]);

  const resetCreateSidebar = () => {
    setVisible(false);
    setFeedbackContentLoading(false);
    setDelta('');
    setFileList([]);
  };


  const createFeedback = (obj) => {
    axios.post('/agile/v1/feedback', obj).then((res) => {
      if (fileList.length) {
        const formData = new FormData();
        fileList.forEach(file => formData.append('file', file));
        axios.post(`/agile/v1/feedback_attachment?feedbackId=${res.id}&token=47f9f36a-d327-498d-9d3f-b7afc289614f`, formData, {
          headers: { 'content-type': 'multipart/form-datal' },
        }).then(() => {
          resetCreateSidebar();
          fetchUser(type);
        });
      } else {
        resetCreateSidebar();
        fetchUser(type);
      }
    });
  };
  
  const handleFileListChange = (e) => {
    setFileList(e.fileList);
  };

  const handleWYSIWYGEditorChange = (value) => {
    setDelta(value);
  };

  const handleFeedbackTypeChange = () => {

  };
  

  return (
    <React.Fragment>
      <WrappedComponent 
        {...props} 
        handleTabChange={handleTabChange} 
        handleTableChange={handleTableChange}
        openExport={openExport}
        data={data}
        loading={feedbackTableLoading}
        fetchUser={fetchUser}
        page={page}
        size={size}
        totalNum={totalNum}
        type={type}
      />
      <Button
        className="feedback-btn"
        type="primary"
        funcType="raised"
        icon="message_notification"
        onClick={() => setVisible(true)}
      />
      { visible && (
        <FeedbackContent
          visible={visible}
          loading={feedbackContentLoading}
          delta={delta}
          fileList={fileList}
          onSetLoading={(loading) => { setFeedbackContentLoading(loading); }}
          onClose={(() => resetCreateSidebar())}
          onCreateFeedback={createFeedback}
          onFileListChange={handleFileListChange}
          onWYSIWYGEditorChange={handleWYSIWYGEditorChange}
          onFeedbackTypeChange={handleFeedbackTypeChange}
        />
      )}
    </React.Fragment>
  );
});

export default FeedbackButton;
