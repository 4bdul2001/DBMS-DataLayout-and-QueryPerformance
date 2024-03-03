# DBMS-DataLayout-and-QueryPerformance

  Explores and implements different in-memory relational stores to measure the effect of data layout and indexing on query performance. To do this we develop a minimal query engine in Java. The engine will be able to load, generate data tables, and perform different types of queries on them. For the purpose of this experiment we will restrict our queries to the following: columnSum(), predicatedColumnSum(int threshold1, int threshold2), predicatedAllColumnsSum(int threshold) and predicatedUpdate(int threshold).

  Our system will implement 3 different types of in-memory relation stores, row store, column store, and indexed row store in which the indexing will be kept on the columns. We test the functionality and efficiency of these relational stores with respect to our four queries.
