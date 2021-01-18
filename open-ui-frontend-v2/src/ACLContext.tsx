import React from 'react'

const ACLContext = React.createContext<Partial<{[key: string]: boolean}>>({});

export const ACLProvider = ACLContext.Provider;
export const ACLConsumer = ACLContext.Consumer;

export default ACLContext