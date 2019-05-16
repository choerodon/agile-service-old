import React, { Component } from 'react';
import { Button } from 'choerodon-ui';
import PropTypes from 'prop-types';
import ReactQuill, { Quill } from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import ImageDrop from './ImageDrop';
import './WYSIWYGEditor.scss';
import cls from '../CommonComponent/ClickOutSide';

Quill.register('modules/imageDrop', ImageDrop);
const modules = {
  toolbar: [
    ['bold', 'italic', 'underline', 'strike', 'blockquote'],
    [{ list: 'ordered' }, { list: 'bullet' }, 'image', 'link', { color: [] }],
  ],
  imageDrop: true,
};

const formats = [
  'bold',
  'italic',
  'underline',
  'strike',
  'blockquote',
  'list',
  'bullet',
  'link',
  'image',
  'color',
];

const defaultStyle = {
  width: 498,
  height: 200,
  borderRight: 'none',
};
const defaultProps = {

};

const propTypes = {
  // eslint-disable-next-line react/forbid-prop-types
  value: PropTypes.any,
  placeholder: PropTypes.string,
  toolbarHeight: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  style: PropTypes.shape({}),
  bottomBar: PropTypes.bool,
  onChange: PropTypes.func,
  handleDelete: PropTypes.func,
  handleSave: PropTypes.func,
  saveRef: PropTypes.func,
};
class WYSIWYGEditor extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      value: props.value || '',
    };
  }

  static getDerivedStateFromProps(nextProps) {
    if ('value' in nextProps) {
      return {
        value: nextProps.value,
      };
    }
    return null;
  }

  isHasImg = (delta) => {
    let pass = false;
    if (delta && delta.ops) {
      delta.ops.forEach((item) => {
        if (item.insert && item.insert.image) {
          pass = true;
        }
      });
    }
    return pass;
  };

  handleChange = (content, delta, source, editor) => {
    const { onChange } = this.props;
    const value = editor.getContents();
    if (onChange && value && value.ops) {
      onChange(value.ops);
    }
  };

  empty = () => {
    const { onChange } = this.props;
    onChange(undefined);
  };

  handleClickOutside = () => {
    const { handleClickOutSide } = this.props;
    if (handleClickOutSide) {
      handleClickOutSide();
    }
  };

  render() {
    const {
      placeholder,
      toolbarHeight,
      style,
      bottomBar,
      handleDelete,
      handleSave,
      saveRef,
    } = this.props;
    const { loading, value } = this.state;
    const newStyle = { ...defaultStyle, ...style };
    const editHeight = newStyle.height === '100%' ? `calc(100% - ${toolbarHeight || '42px'})` : (newStyle.height - (toolbarHeight || 42));
    return (
      <div style={{ width: '100%', height: '100%' }}>
        <div style={newStyle} className="react-quill-editor">
          <ReactQuill
            ref={saveRef}
            theme="snow"
            modules={modules}
            formats={formats}
            style={{ height: editHeight, width: '100%' }}
            placeholder={placeholder || '描述'}
            defaultValue={value}
            onChange={this.handleChange}
          />
        </div>
        {
          bottomBar && (
            <div
              style={{
                padding: '0 8px',
                border: '1px solid #ccc',
                borderTop: 'none',
                display: 'flex',
                justifyContent: 'flex-end',
              }}
            >
              <Button
                type="primary"
                onClick={() => {
                  this.empty();
                  handleDelete();
                }}
              >
                {'取消'}
              </Button>
              <Button
                type="primary"
                loading={loading}
                onClick={() => {
                  this.setState({ loading: true });
                  handleSave();
                }}
              >
                {'保存'}
              </Button>
            </div>
          )
        }
      </div>
    );
  }
}
WYSIWYGEditor.defaultProps = defaultProps;
WYSIWYGEditor.propTypes = propTypes;
export default cls(WYSIWYGEditor);
