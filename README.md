# IIBTesting
Sample test utility to check XML->XML flows within IBM IIB flow.\n
Uses Groovy, Spock and Camel to send JMS message to IIB flow and wait for response\n
Request includes custom JMS header to redirect IIB flow to do MQ-reply if set and return modified data back to test\n
Requires local IIB flow deployed (check mwq.properties for exact connection details)\n
Change input queue as required\n
Use core-testing-library (IIB subflow and ESQL code) to check for existence of JMS header and do MQ-reply if true\n
