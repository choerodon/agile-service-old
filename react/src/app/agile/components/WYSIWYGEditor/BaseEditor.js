import React, { Component } from 'react';
import { Button, Icon } from 'choerodon-ui';
import PropTypes from 'prop-types';
import { isEqual } from 'lodash';
import ReactQuill, { Quill } from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import LightBox from 'react-image-lightbox';
import { randomWord } from '../../common/utils';
import ImageDrop from './ImageDrop';
import Link from './Link';
import './BaseEditor.scss';

Quill.register('modules/imageDrop', ImageDrop);
Quill.register('formats/link', Link);

const defaultStyle = {
  width: 498,
  height: 200,
  borderRight: 'none',
};
const defaultProps = {
  mode: 'edit',
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
  onSave: PropTypes.func,
  saveRef: PropTypes.func,
  autoFocus: PropTypes.bool,
  mode: PropTypes.oneOf([
    'edit', 'read',
  ]),
};

const ToolBar = ({ id, onFullScreenClick, hideFullScreen }) => (
  <div id={id || 'toolbar'}>
    <button type="button" className="ql-bold" />
    <button type="button" className="ql-italic" />
    <button type="button" className="ql-underline" />
    <button type="button" className="ql-strike" />
    <button type="button" className="ql-blockquote" />
    <button type="button" className="ql-list" value="ordered" />
    <button type="button" className="ql-list" value="bullet" />
    <button type="button" className="ql-image" />
    <button type="button" className="ql-link" /> 
    <select className="ql-color">
      {/* <option value="red" />
      <option value="green" />
      <option value="blue" />
      <option value="orange" />
      <option value="violet" />
      <option value="#d0d1d2" />
      <option selected /> */}
    </select> 

    {!hideFullScreen && (
      <button type="button" className="ql-fullScreen" style={{ outline: 'none' }} onClick={onFullScreenClick}>
        <Icon type="zoom_out_map" style={{ marginTop: -5 }} />
      </button>
    )}
  </div>
);
class BaseEditor extends Component {
  constructor(props) {
    super(props);
    this.state = {
      imgOpen: false,
      src: '',
      value: props.value || '',
    };
    this.value = props.value || '';
    this.toolBarId = randomWord(false, 32);
    this.modules = {
      toolbar: {
        container: `#${this.toolBarId}`,
      },
      imageDrop: true,
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

  componentDidMount() {
    const { autoFocus } = this.props;
    if (autoFocus && this.editor) {
      setTimeout(() => {
        this.editor.focus();
      });
    }
    document.addEventListener('click', this.handleOpenLightBox);
  }

  // 在这里将值更新为新的值
  componentDidUpdate(prevProps, prevState) {
    const { value } = this.props;
    if ('value' in this.props && !isEqual(this.value, value)) {
      this.editor.getEditor().setContents(value);
    }
  }
  
  componentWillUnmount() {
    document.removeEventListener('click', this.handleOpenLightBox);
  }

  setValue = (value) => {    
    // setContents会自动触发onChange
    this.editor.getEditor().setContents(value);
  }

  handleOpenLightBox = (e) => {
    e.stopPropagation();
    if (e.target.nodeName === 'IMG') {
      e.stopPropagation();
      this.setState({
        imgOpen: true,
        src: e.target.src,
      });
    }
  }

  saveRef = name => (ref) => {
    this[name] = ref;
    const { saveRef } = this.props;
    if (saveRef) {
      saveRef(ref);
    }
  }


  handleChange = (content, delta, source, editor) => {
    const { onChange } = this.props;
    const value = editor.getContents();
    this.value = value.ops;
    if (onChange && value && value.ops) {
      onChange(value.ops);
    }
  };

  empty = () => {
    const { onChange } = this.props;
    onChange(undefined);
  };

  render() {
    const {
      placeholder,
      toolbarHeight,
      style,
      bottomBar,
      onCancel,
      onSave,
      mode,
      loading,
      onFullScreenClick,
      hideFullScreen,
    } = this.props;
    const readOnly = mode === 'read';
    const {
      value, imgOpen, src,
    } = this.state;
    const newStyle = { ...defaultStyle, ...style };
    const editHeight = newStyle.height === '100%' ? `calc(100% - ${toolbarHeight || '42px'})` : (newStyle.height - (toolbarHeight || 42));
    return (
      <div className="c7n-quill-editor" style={{ width: '100%', height: '100%' }}>
        <div style={newStyle} className={`react-quill-editor react-quill-editor-${mode}`}>
          <ToolBar id={this.toolBarId} onFullScreenClick={onFullScreenClick} hideFullScreen={hideFullScreen} />
          <ReactQuill
            readOnly={readOnly}
            ref={this.saveRef('editor')}
            theme="snow"
            modules={this.modules}
            style={{ height: editHeight, width: '100%' }}
            placeholder={placeholder || '描述'}
            defaultValue={value}
            onChange={this.handleChange}
            bounds=".react-quill-editor"
          />
        </div>
        {
          bottomBar && !readOnly && (
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
                onClick={onCancel}
              >
                {'取消'}
              </Button>
              <Button
                type="primary"
                loading={loading}
                onClick={onSave}
              >
                {'保存'}
              </Button>
            </div>
          )
        }
        {
          imgOpen && (
            <LightBox
              mainSrc={src}
              onCloseRequest={() => this.setState({ imgOpen: false })}
              imageTitle="images"
            />
          )
        }
      </div>
    );
  }
}
BaseEditor.defaultProps = defaultProps;
BaseEditor.propTypes = propTypes;
export default BaseEditor;
