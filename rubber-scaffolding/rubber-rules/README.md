# Summary
There are many existing standards for rules engines. Most of them are made for input via a layman from a multitude of input sources like Excel and tables. Things like performance, scalability, inheritence, and fault tolerance were things that I saw less or simply not emphasized. This document aims to outline needed features to handle the processing of rules in a large scale system yielding massive data sets in the petabytes that must be deterministic, transactional, and performant. Last must not least it must be easy for users to manage rules as well as validate the rules at any time. Ideally all effort should be put in to minimize user effort at any point in the process.

#  Priorities

This system will be designed from the ground up with priorities in this order (highest to lowest priority):

1. Performance - Execution performance
1. Easy of use (programming / validating) - Easy to program rules, test rules, and validate them
1. Flexibility and extensibility - Change and extend components
1. Accountability - Derived data, auditing, Everything will come from a clear source
1. Security - Lock down features, data with ACL type restrictions

#  Existing Technologies? Technology congestion
The reason why we can't just go ahead and use already existing technologies is because rules handling in a high performance distributed system will be very different than a single node execution environment. Things like conflicts, scopes, sessions, transactions need to be enforced and synchronized to the extent that is needed by the rules and their governing systems.

Rules should be capable of being defined by many different means as the user types may vary. Some users may prefer to create rules on the fly via an interactive shell. Other user may like to write rules in a specific rules file format that can be written in a very simple syntax. Other users may want a visual aid, flowchart, decision table, or other input options. Ultimately the methodology of creating the rules themselves should be less emphasized rather than what the rules define and what they may do. 

A proper system architecture should be capable of scaling indefinitely.

## Built to build upon
Ultimately the implementation itself should be possible to be unimportant to the user as this rules engine is more about defining an approach for handling rules in a distributed environment in a way that there should be a client-server model whereas the client may create and invoke rules. The server in turn will execute the rules and follow through with a possible outcome. A workflow engine is technically a separate device than a rules engine. They are often bound together but are not required to do so. In this case, the rules engine will be able to leverage implementation specific extensions as well as base features. Such base features would be a simple key/value store. Message handling on a simple scale. Ultimately the rules engine would yield external resources and calls based on met criteria. An example may be following an invocation, a AWS lambda call is invoked, or an HTTP GET call is made. A rule may invoke an action as simple as outputting a log message, or setting a session variable.

A rules engine is a way to take common complex processing out of a single application and provide a global environment in which it may execute. For large organizations a common global environment will be far more convenient to deal with than many execution environments. This engine is distributed in its persisted data as well as its processing context in order to be fault tolerant and highly performant.

Rules exist as living data and have a lifecycle. There are rules that govern them and events that identify aspects of their life. The goal is to create a extensible engine that balances flexibility without hindering security or performance and accessibility. Many organizations would appreciate and require very strict security controls on who may invoke which roles, others may create them and so on. All security concerns will be optional. 

##  Not a workflow
This is not a workflow system. It can be used to create a workflow system much like Drools was used to create jBPM. Rules are much more abstract than workflows and that is their beauty. They are simple conditional invocations that may be linked together and bound under stateful and global contexts.

##  Context
A context is a real defined in which a rule is associated and executed within. A rule when invoked may have many contextual implications that affect its outcome. The topmost context in the execution environment is the global context. This is a system-wide context that is transactional across all executions below it. Organizations and sub-groups may have many nested contexts limited to their scope. Rules are not created in a context. Rather, rules are stored in a repository and users, groups, or actions with the appropriate priviliges may invoke those rules. The context that is applied to a rule is determined at the time of execution. A notion of closure exists within a given context. Such that the local variables are retained and are evaluated in the context that they are invoked in up. That means that inner contexts have the capacity to override outer contexts. This is much like how an object may extend and override its parent in OOP. The concept of `final` also applies to this. A context may set certain principles, if they are rules, methods, actions, variables or any other construct as `final` if it chooses to. This is more of a design approach rather than security constraint. For security constraints `final` is discouraged. Rather privileged based access may be set on a contextual level. 

# Requirements

* Maven 3
* Java 8