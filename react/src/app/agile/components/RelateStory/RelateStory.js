import React, { Component } from 'react';
import { stores, axios, Content } from 'choerodon-front-boot';
import { find, debounce } from 'lodash';
import { Select, Form, Modal } from 'choerodon-ui';
import {
  updateIssue, loadIssueTypes, loadIssues, loadIssue, 
} from '../../api/NewIssueApi';
import TypeTag from '../TypeTag';
import './RelateStory.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const FormItem = Form.Item;
let sign = false;

class RelateStory extends Component {
  debounceFilterIssues = debounce((input) => {
    this.loadIssues();
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      selectLoading: true,
      storys: [],
    };
  }

  componentDidMount() {
    this.init();
  }


  onFilterChange(input) {
    if (!sign) {
      this.loadIssues();
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  setInitValue=(isInit) => {
    if (isInit) {
      const { form: { setFieldsValue }, issue } = this.props;
      setFieldsValue({
        relateIssueId: issue.relateIssueId,
      });
    }
  }

  loadIssues=(isInit) => {
    if (this.storyType) {
      this.setState({
        selectLoading: true,
      });
      loadIssues(0, 10, this.filters)
        .then((res) => {
          const storys = res.content;
          if (storys) {
            const { issue } = this.props;
            if (issue.relateIssueId) {
              if (!find(storys, { issueId: issue.relateIssueId })) {
                if (!this.relateIssue) {
                  loadIssue(issue.relateIssueId).then((story) => {
                    if (story) {
                      this.relateIssue = story;
                      this.setState({
                        storys: [story, ...storys],
                        selectLoading: false,
                      }, this.setInitValue.bind(this, isInit));
                    }
                  });
                } else {
                  this.setState({
                    storys: [this.relateIssue, ...storys],
                    selectLoading: false,
                  }, this.setInitValue.bind(this, isInit));
                }
              } else {
                this.setState({
                  selectLoading: false,
                  storys: res.content,
                }, this.setInitValue.bind(this, isInit));
              }
            } else {
              this.setState({
                selectLoading: false,
                storys: res.content,
              });
            }
          }
        });
    }
  }

  handleRelateStory = () => {
    const { form, issue, onOk } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const { relateIssueId } = values;       
        this.setState({ createLoading: true });
        const { issueId, objectVersionNumber } = issue;
        updateIssue({   
          issueId,       
          objectVersionNumber,
          relateIssueId: relateIssueId || 0,
        })
          .then((res) => {
            this.setState({ createLoading: false });
            onOk();
          });
      }
    });
  };

  init() {
    this.setState({
      selectLoading: true,
    });
    loadIssueTypes().then((issueTypes) => {
      const storyType = issueTypes.find(type => type.typeCode === 'story');
      if (storyType) {
        this.storyType = storyType;
        this.filters = {
          advancedSearchArgs: {
            issueTypeId: [storyType.id],        
          },     
        };
        this.loadIssues(true);
      }
    });
  }

  render() {
    const {
      form, visible, onCancel, 
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      createLoading, selectLoading, storys,
    } = this.state;  
    return (
      <Sidebar
        className="c7n-RelateStory"
        title="关联故事"
        visible={visible || false}
        onOk={this.handleRelateStory}
        onCancel={onCancel}
        okText="确定"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <Content
          style={{ padding: 0 }}
          title="为Bug关联故事"
          description="请在下面选择所要关联的故事。"
        >
          <Form layout="vertical">
            <FormItem label="故事" style={{ width: 520 }}>
              {getFieldDecorator('relateIssueId', {})(
                <Select
                  label="故事"
                  allowClear                  
                  dropdownClassName="issueSelectDropDown"
                  loading={selectLoading}                 
                  filter
                  filterOption={false}
                  onFilterChange={this.onFilterChange.bind(this)}      
                  getPopupContainer={() => document.getElementsByClassName('c7n-RelateStory')[0]}          
                >
                  {storys.map(issue => (
                    <Option                
                      value={issue.issueId}
                    >
                      <div style={{ display: 'inline-block', width: '100%' }}>
                        <div style={{
                          display: 'flex',
                          width: '100%',
                          flex: 1,
                        }}
                        >
                          <div>
                            <TypeTag
                              data={issue.issueTypeDTO}
                            />
                          </div>
                          <div style={{
                            paddingLeft: 12, paddingRight: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', 
                          }}
                          >
                            {issue.issueNum}
                          </div>
                          <div style={{ overflow: 'hidden', flex: 1 }}>
                            <p style={{
                              paddingRight: '25px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset', 
                            }}
                            >
                              {issue.summary}
                            </p>
                          </div>
                        </div>
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(RelateStory);
