IGraph Data Access SDK for Java
================================================

IGraph API version v1

Overview
--------
This SDK contains wrapper code used to call the iGraph API from Java applications.


Prerequisites
-------------


Including the SDK in your project
---------------------------------

The easiest way to incorporate the SDK into your Java project is to use Maven. If you're using Maven already, simply add a new dependency to your `pom.xml`:

```xml
<dependency>
     <groupId>com.aliyun.igraph</groupId>
      <artifactId>aliyun-sdk-igraph</artifactId>
      <version>{lastest-sdk-version}</version>
</dependency>
```
Changes in version 1.0.6
-----------------------
* client 支持 5210 错误码的重试

Changes in version 1.0.5
-----------------------
* client connection 空闲连接的保留时长可配置，解决低流量场景下的链接重建问题
* netty 配置设置 TcpNoDelay 为 True

Changes in version 1.0.4
-----------------------
* 升级async-http-client到最新版本
* 升级logback-classic到1.2.0版本
* 升级junit到4.13.2版本
* 升级commons-io到2.11.0版本
* 所有http请求统一使用Post方法

Changes in version 1.0.3
-----------------------
* 重构异步查询代码，新增超时重试机制

Changes in version 1.0.2
-----------------------
* 补充RequestContext内容，优化汇报的异常信息

Changes in version 1.0.1
-----------------------
* 区分__.和普通step
* 补充 withSack、sack、sideEffect、get 接口
* 修复 bulkSet 格式返回结果末尾多一个逗号的问题
* get(BulkSet.class).getValue() 接口获取的类型由 Map<IGraphResult, Long> 转换为 Map<Result, Long>
* 增加T.id，用于获取实体的 pkey 字段值
* 精简依赖，简化代码，提升Element和Path的接口易用性
* 适配明文查询接口

Changes in version 1.0.0
-----------------------
iGraph SDK 首次发布，支持PG、Gremlin 语法

