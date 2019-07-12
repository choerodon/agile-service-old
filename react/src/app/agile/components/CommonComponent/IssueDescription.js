/* eslint-disable react/no-danger */
import React, { Component } from 'react';
import Lightbox from 'react-image-lightbox';
import './IssueDescription.scss';

class IssueDescription extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      src: '',
      open: false,
    };
  }

  componentDidMount() {
    const { data } = this.props;
    window.addEventListener('click', (e) => {
      if (e.target.nodeName === 'IMG' && data && data.search(e.target.src) > -1) {
        e.stopPropagation();
        this.open(e.target.src);
      }
    });
  }

  open = (src) => {
    this.setState({
      open: true,
      src,
    });
  };

  escape = str => str.replace(/<\/script/g, '<\\/script').replace(/<!--/g, '<\\!--');

  render() {
    const { open, src } = this.state;
    const { data } = this.props;
    return (
      <div className="c7n-read-delta" style={{ width: '100%', wordBreak: 'break-all' }}>
        <div dangerouslySetInnerHTML={{ __html: `${this.escape(data)}` }} />
        {
          open ? (
            <Lightbox
              mainSrc={src}
              onCloseRequest={() => this.setState({ open: false })}
              imageTitle="images"
            />
          ) : null
        }
      </div>
    );
  }
}

export default IssueDescription;
