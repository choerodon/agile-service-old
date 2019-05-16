import React from 'react';
import PropTypes from 'prop-types';
import { Upload, Button, Icon } from 'choerodon-ui';
import { deleteFile } from '../../api/FileApi';
import './UploadControl.scss';

const propTypes = {
  // eslint-disable-next-line react/forbid-prop-types
  fileList: PropTypes.array,
  onChange: PropTypes.func,
};
class UploadControl extends React.Component {
  constructor(props) {
    super(props);
    this.state = {      
      fileList: props.fileList || [],
    };
  }

  static getDerivedStateFromProps(nextProps) {
    if ('fileList' in nextProps) {
      return {
        fileList: nextProps.fileList || [],   
      };
    }
    return null;
  }

  triggerChange = (fileList) => {
    const { onChange } = this.props;
    if (!('fileList' in this.props)) {
      this.setState({ fileList });
    }
    if (onChange) {
      onChange(fileList);
    }
  }

  render() {
    const {
      onChange, funcType,
    } = this.props;
    const { fileList } = this.state;   
    const props = {
      action: '//jsonplaceholder.typicode.com/posts/',
      multiple: true,
      beforeUpload: (file) => {
        if (file.size > 1024 * 1024 * 30) {
          Choerodon.prompt('文件不能超过30M');
          return false;
        } else if (fileList.length >= 10) {
          Choerodon.prompt('最多上传10个文件');
          return false;
        } else if (file.name && encodeURI(file.name).length > 210) {
          Choerodon.prompt('文件名过长');
          return false;
        } else {
          const tmp = file;
          tmp.status = 'done';
          this.triggerChange([...fileList, file]);          
        }
        return false;
      },
      onRemove: (file) => {
        const index = fileList.indexOf(file);
        const newFileList = fileList.slice();
        if (file.url) {
          deleteFile(file.uid)
            .then((response) => {
              if (response) {
                newFileList.splice(index, 1);
                onChange(newFileList);
                Choerodon.prompt('删除成功');
              }
            })
            .catch((error) => {
              if (error.response) {
                Choerodon.prompt(error.response.data.message);
              } else {
                Choerodon.prompt(error.message);
              }
            });
        } else {
          newFileList.splice(index, 1);
          onChange(newFileList);
        }
      },
    };
    return (
      <div className="sign-upload" style={{ marginTop: 20 }}>
        <div style={{ display: 'flex', marginBottom: '13px', alignItems: 'center' }}>
          <div style={{ fontWeight: 'bold' }}>附件</div>
        </div>
        <div style={{ marginTop: -38 }}>               
          <Upload
            {...props}
            fileList={fileList}
            className="upload-button"
          >
            <Button type={funcType || 'primary'}>
              <Icon type="file_upload" />
              {'上传附件'}
            </Button>
          </Upload>
        </div>
      </div>
     
    );
  }
}
UploadControl.propTypes = propTypes;
export default UploadControl;
