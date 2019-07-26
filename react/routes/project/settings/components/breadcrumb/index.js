import React from 'react';
import { Breadcrumb } from 'choerodon-ui';

function BreadcrumbIndex(props) {
  const { list, separator } = props.data;

  return (
    <Breadcrumb separator={separator} style={{ fontSize: '.2rem' }}>
      <Breadcrumb.Item key={list[0].id}>{list[0].name}</Breadcrumb.Item>
      {
        list.slice(1, list.length - 1).map(({ name, id, href }) => (
          <Breadcrumb.Item key={id} href={href}>{name}</Breadcrumb.Item>
        ))
      }
      <Breadcrumb.Item key={list[list.length - 1].id}>{list[list.length - 1].name}</Breadcrumb.Item>
    </Breadcrumb>
  );
}

export default BreadcrumbIndex;
