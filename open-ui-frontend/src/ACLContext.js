import React from 'react'

const ACLContext = React.createContext();

export const ACLProvider = ACLContext.Provider
export const ACLConsumer = ACLContext.Consumer

export default ACLContext