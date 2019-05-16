import React from 'react';
import PropTypes from 'prop-types';
import { Upload, Button, Icon } from 'choerodon-ui';
import { deleteFile } from '../../api/FileApi';

class UploadButton extends React.Component {
  static propTypes = {
    onRemove: PropTypes.func,
    beforeUpload: PropTypes.func,
  };

  constructor(props, context) {
    super(props, context);
    this.state = {};
  }

  render() {
    const {
      fileList, onBeforeUpload, onRemove, funcType,
    } = this.props;
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
          // check name length, the name in the database will
          // like `file_uuid_encodeURI(file.name)`,
          // uuid's length is 32
          // the total could save is 255
          // so select length of encodeURI(file.name)
          // 255 - 32 - 6 = 217 -> 210

          Choerodon.prompt('文件名过长');
          return false;
        } else {
          const tmp = file;
          tmp.status = 'done';
          if (onBeforeUpload) {
            if (fileList.length > 0) {
              onBeforeUpload(fileList.slice().concat(file));
            } else {
              onBeforeUpload([file]);
            }
          }
        }
        return false;
      },
      onRemove: (file) => {
        const index = fileList.indexOf(file);
        const newFileList = fileList.slice();
        if (file.url && onRemove) {
          deleteFile(file.uid)
            .then((response) => {
              if (response) {
                newFileList.splice(index, 1);
                onRemove(newFileList);
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
          onRemove(newFileList);
        }
      },
    };
    return (
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
    );
  }
}

export default UploadButton;
