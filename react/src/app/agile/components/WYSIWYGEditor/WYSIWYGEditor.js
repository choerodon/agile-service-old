import React, { Component } from 'react';
import { Button } from 'choerodon-ui';
import PropTypes from 'prop-types';
import ReactQuill, { Quill } from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import LightBox from 'react-image-lightbox';
import ImageDrop from './ImageDrop';
import Link from './Link';
// import mention from './mention';
import './WYSIWYGEditor.scss';
import cls from '../CommonComponent/ClickOutSide';

const atValues = [
  { id: 1, value: '汪汪哇' },
  { id: 2, value: '王坤奇' },
  { id: 10, value: '真的吗' },
];
const hashValues = [
  { id: 3, value: '啊啊啊' },
  { id: 4, value: '真的吗' },
];
Quill.register('modules/imageDrop', ImageDrop);
Quill.register('formats/link', Link);
// Quill.register('modules/mention', mention);
const modules = {
  toolbar: [
    ['bold', 'italic', 'underline', 'strike', 'blockquote'],
    [{ list: 'ordered' }, { list: 'bullet' }, 'image', 'link', { color: [] }],
  ],
  // mention: {
  //   allowedChars: /^[A-Za-z\s\u4e00-\u9fa5]*$/,
  //   mentionDenotationChars: ['@', '#'],
  //   source(searchTerm, renderList, mentionChar) {
  //     let values;

  //     if (mentionChar === '@') {
  //       values = atValues;
  //     } else {
  //       values = hashValues;
  //     }

  //     if (searchTerm.length === 0) {
  //       renderList(values, searchTerm);
  //     } else {
  //       const matches = [];
  //       for (let i = 0; i < values.length; i += 1) {
  //         // eslint-disable-next-line no-bitwise
  //         if (~values[i].value.toLowerCase().indexOf(searchTerm.toLowerCase())) {
  //           matches.push(values[i]);
  //         }
  //       }
  //       renderList(matches, searchTerm);
  //     }
  //   },
  // },
  imageDrop: true,
};
// "[{"insert":{"mention":{"index":"0","denotationChar":"@","id":"1","value":"Fredrik Sundqvist"}}},{"insert":" \n"}]"
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
  handleDelete: PropTypes.func,
  handleSave: PropTypes.func,
  saveRef: PropTypes.func,
  autoFocus: PropTypes.bool,
  mode: PropTypes.oneOf([
    'edit', 'read',
  ]),
};
class WYSIWYGEditor extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      imgOpen: false,
      src: '',
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

  componentDidMount() {
    const { autoFocus } = this.props;
    if (autoFocus && this.editor) {
      setTimeout(() => {
        this.editor.focus();
      });
    }
    document.addEventListener('click', this.handleOpenLightBox);
  }

  componentWillUnmount() {
    document.removeEventListener('click', this.handleOpenLightBox);
  }
  
  handleOpenLightBox=(e) => {
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
      mode,
    } = this.props;
    const readOnly = mode === 'read';
    const {
      loading, value, imgOpen, src, 
    } = this.state;
    const newStyle = { ...defaultStyle, ...style };
    const editHeight = newStyle.height === '100%' ? `calc(100% - ${toolbarHeight || '42px'})` : (newStyle.height - (toolbarHeight || 42));
    return (
      <div style={{ width: '100%', height: '100%' }}>
        <div style={newStyle} className={`react-quill-editor react-quill-editor-${mode}`}>
          <ReactQuill
            readOnly={readOnly}
            ref={this.saveRef('editor')}
            theme="snow"
            modules={modules}
            // formats={formats}
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
WYSIWYGEditor.defaultProps = defaultProps;
WYSIWYGEditor.propTypes = propTypes;
export default cls(WYSIWYGEditor);
