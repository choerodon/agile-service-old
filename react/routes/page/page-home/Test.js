import React, { Component } from 'react';
import Store, { StoreProvider } from '../stores';
import PageHome from './PageHome';
export default function Test(props) {
    return (
        <Store.Consumer>
            {( value ) =>
             <PageHome value={value} />
            }
        </Store.Consumer>
    );
}