---
title: Re Online Judge API
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# Re Online Judge API

基础接口文档，涵盖判题平台常用能力

Base URLs:

# Authentication

- HTTP Authentication, scheme: bearer

# teacher-controller

<a id="opIdget"></a>

## GET 教师-详情

GET /api/teachers/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |教师ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","phone":"string","email":"string","avatar":"string","title":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseTeacher](#schemaapiresponseteacher)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate"></a>

## PUT 教师-更新

PUT /api/teachers/{id}

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "title": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |教师ID|
|body|body|[TeacherUpsertRequest](#schemateacherupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","phone":"string","email":"string","avatar":"string","title":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseTeacher](#schemaapiresponseteacher)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete"></a>

## DELETE 教师-删除

DELETE /api/teachers/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |教师ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist"></a>

## GET 教师-分页列表

GET /api/teachers

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |页码|
|size|query|integer(int64)| 否 |每页条数|
|keyword|query|string| 否 |用户名/姓名/邮箱关键字|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"username":"string","password":"string","name":"string","sex":"string","phone":"string","email":"string","avatar":"string","title":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageTeacher](#schemaapiresponsepageteacher)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate"></a>

## POST 教师-创建

POST /api/teachers

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "title": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[TeacherUpsertRequest](#schemateacherupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","phone":"string","email":"string","avatar":"string","title":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseTeacher](#schemaapiresponseteacher)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# student-controller

<a id="opIdget_1"></a>

## GET 学生-详情

GET /api/students/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |学生ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","background":"string","ac":0,"submit":0,"school":"string","score":0,"lastLoginTime":"2019-08-24T14:15:22Z","lastLanguage":"string","createTime":"2019-08-24T14:15:22Z","lastVisitTime":"2019-08-24T14:15:22Z","dailyChallenge":"string","registerIp":"string","lastLoginIp":"string","bio":"string","isVerified":true,"updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseStudent](#schemaapiresponsestudent)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_1"></a>

## PUT 学生-更新

PUT /api/students/{id}

根据ID更新学生信息，密码为空则保持原值

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "background": "string",
  "school": "string",
  "score": 0,
  "bio": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |学生ID|
|body|body|[StudentUpsertRequest](#schemastudentupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","background":"string","ac":0,"submit":0,"school":"string","score":0,"lastLoginTime":"2019-08-24T14:15:22Z","lastLanguage":"string","createTime":"2019-08-24T14:15:22Z","lastVisitTime":"2019-08-24T14:15:22Z","dailyChallenge":"string","registerIp":"string","lastLoginIp":"string","bio":"string","isVerified":true,"updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseStudent](#schemaapiresponsestudent)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_1"></a>

## DELETE 学生-删除

DELETE /api/students/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |学生ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_2"></a>

## GET 学生-分页列表

GET /api/students

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|size|query|integer(int64)| 否 |每页条数|
|keyword|query|string| 否 |用户名/姓名/邮箱关键字|
|username|query|string| 否 |按用户名模糊搜索|
|email|query|string| 否 |按邮箱模糊搜索|
|page|query|integer(int64)| 否 |页码|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","background":"string","ac":0,"submit":0,"school":"string","score":0,"lastLoginTime":"2019-08-24T14:15:22Z","lastLanguage":"string","createTime":"2019-08-24T14:15:22Z","lastVisitTime":"2019-08-24T14:15:22Z","dailyChallenge":"string","registerIp":"string","lastLoginIp":"string","bio":"string","isVerified":true,"updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageStudent](#schemaapiresponsepagestudent)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_2"></a>

## POST 学生-创建

POST /api/students

创建新学生并可选设置初始密码

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "background": "string",
  "school": "string",
  "score": 0,
  "bio": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[StudentUpsertRequest](#schemastudentupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","background":"string","ac":0,"submit":0,"school":"string","score":0,"lastLoginTime":"2019-08-24T14:15:22Z","lastLanguage":"string","createTime":"2019-08-24T14:15:22Z","lastVisitTime":"2019-08-24T14:15:22Z","dailyChallenge":"string","registerIp":"string","lastLoginIp":"string","bio":"string","isVerified":true,"updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseStudent](#schemaapiresponsestudent)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# solution-controller

<a id="opIddetail_1"></a>

## GET 题解-详情

GET /api/solutions/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"userId":0,"problemId":0,"title":"string","content":"string","language":"string","createTime":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSolution](#schemaapiresponsesolution)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_2"></a>

## PUT 题解-更新

PUT /api/solutions/{id}

作者或管理员可修改

> Body 请求参数

```json
{
  "problemId": 0,
  "title": "string",
  "content": "string",
  "language": "string",
  "isActive": true,
  "authorId": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|body|body|[SolutionRequest](#schemasolutionrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"userId":0,"problemId":0,"title":"string","content":"string","language":"string","createTime":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSolution](#schemaapiresponsesolution)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_2"></a>

## DELETE 题解-删除

DELETE /api/solutions/{id}

作者或管理员可删除

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlistByProblem"></a>

## GET 题解-按题目列表

GET /api/problems/{problemId}/solutions

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|problemId|path|integer(int64)| 是 |none|
|page|query|integer(int64)| 否 |none|
|size|query|integer(int64)| 否 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"userId":0,"problemId":0,"title":"string","content":"string","language":"string","createTime":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageSolution](#schemaapiresponsepagesolution)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_3"></a>

## POST 题解-创建

POST /api/problems/{problemId}/solutions

学生或管理员可发布，管理员需指定 authorId 为学生ID，路径参数指定题目ID

> Body 请求参数

```json
{
  "problemId": 0,
  "title": "string",
  "content": "string",
  "language": "string",
  "isActive": true,
  "authorId": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|problemId|path|integer(int64)| 是 |none|
|body|body|[SolutionRequest](#schemasolutionrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"userId":0,"problemId":0,"title":"string","content":"string","language":"string","createTime":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSolution](#schemaapiresponsesolution)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_3"></a>

## GET 题解-列表

GET /api/solutions

学生/教师/管理员均可查看，可按题目或作者筛选

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |none|
|size|query|integer(int64)| 否 |none|
|problemId|query|integer(int64)| 否 |题目ID过滤|
|authorId|query|integer(int64)| 否 |作者学生ID过滤|
|includeInactive|query|boolean| 否 |是否包含未启用题解，仅管理员可用|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"userId":0,"problemId":0,"title":"string","content":"string","language":"string","createTime":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageSolution](#schemaapiresponsepagesolution)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# login-log-controller

<a id="opIdget_2"></a>

## GET 登录日志-详情

GET /api/login-logs/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |日志ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"role":"string","userId":0,"username":"string","ipAddress":"string","location":"string","userAgent":"string","device":"string","loginTime":"2019-08-24T14:15:22Z","logoutTime":"2019-08-24T14:15:22Z","success":true,"failReason":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLoginLog](#schemaapiresponseloginlog)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_3"></a>

## PUT 登录日志-更新

PUT /api/login-logs/{id}

> Body 请求参数

```json
{
  "role": "string",
  "userId": 0,
  "username": "string",
  "ipAddress": "string",
  "location": "string",
  "userAgent": "string",
  "device": "string",
  "loginTime": "2019-08-24T14:15:22Z",
  "logoutTime": "2019-08-24T14:15:22Z",
  "success": true,
  "failReason": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |日志ID|
|body|body|[LoginLogRequest](#schemaloginlogrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"role":"string","userId":0,"username":"string","ipAddress":"string","location":"string","userAgent":"string","device":"string","loginTime":"2019-08-24T14:15:22Z","logoutTime":"2019-08-24T14:15:22Z","success":true,"failReason":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLoginLog](#schemaapiresponseloginlog)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_3"></a>

## DELETE 登录日志-删除

DELETE /api/login-logs/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |日志ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_5"></a>

## GET 登录日志-分页列表

GET /api/login-logs

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |页码|
|size|query|integer(int64)| 否 |每页条数|
|role|query|string| 否 |角色过滤|
|userId|query|integer(int64)| 否 |用户ID过滤|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"role":"string","userId":0,"username":"string","ipAddress":"string","location":"string","userAgent":"string","device":"string","loginTime":"2019-08-24T14:15:22Z","logoutTime":"2019-08-24T14:15:22Z","success":true,"failReason":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageLoginLog](#schemaapiresponsepageloginlog)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_4"></a>

## POST 登录日志-创建

POST /api/login-logs

> Body 请求参数

```json
{
  "role": "string",
  "userId": 0,
  "username": "string",
  "ipAddress": "string",
  "location": "string",
  "userAgent": "string",
  "device": "string",
  "loginTime": "2019-08-24T14:15:22Z",
  "logoutTime": "2019-08-24T14:15:22Z",
  "success": true,
  "failReason": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[LoginLogRequest](#schemaloginlogrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"role":"string","userId":0,"username":"string","ipAddress":"string","location":"string","userAgent":"string","device":"string","loginTime":"2019-08-24T14:15:22Z","logoutTime":"2019-08-24T14:15:22Z","success":true,"failReason":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLoginLog](#schemaapiresponseloginlog)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# homework-controller

<a id="opIddetail_3"></a>

## GET 作业-详情

GET /api/homeworks/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"title":"string","classId":0,"startTime":"2019-08-24T14:15:22Z","endTime":"2019-08-24T14:15:22Z","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseHomework](#schemaapiresponsehomework)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_4"></a>

## PUT 作业-更新（教师/管理员）

PUT /api/homeworks/{id}

> Body 请求参数

```json
{
  "title": "string",
  "classId": 0,
  "startTime": "2019-08-24T14:15:22Z",
  "endTime": "2019-08-24T14:15:22Z",
  "description": "string",
  "isActive": true,
  "problemIds": [
    0
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|body|body|[HomeworkRequest](#schemahomeworkrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"title":"string","classId":0,"startTime":"2019-08-24T14:15:22Z","endTime":"2019-08-24T14:15:22Z","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseHomework](#schemaapiresponsehomework)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_4"></a>

## DELETE 作业-删除（教师/管理员）

DELETE /api/homeworks/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_6"></a>

## GET 作业-列表（学生/教师/管理员）

GET /api/homeworks

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |none|
|size|query|integer(int64)| 否 |none|
|classId|query|integer(int64)| 否 |按班级过滤|
|keyword|query|string| 否 |标题/描述关键字|
|activeOnly|query|boolean| 否 |是否仅返回启用作业|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"title":"string","classId":0,"startTime":"2019-08-24T14:15:22Z","endTime":"2019-08-24T14:15:22Z","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageHomework](#schemaapiresponsepagehomework)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_5"></a>

## POST 作业-创建（教师/管理员）

POST /api/homeworks

> Body 请求参数

```json
{
  "title": "string",
  "classId": 0,
  "startTime": "2019-08-24T14:15:22Z",
  "endTime": "2019-08-24T14:15:22Z",
  "description": "string",
  "isActive": true,
  "problemIds": [
    0
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[HomeworkRequest](#schemahomeworkrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"title":"string","classId":0,"startTime":"2019-08-24T14:15:22Z","endTime":"2019-08-24T14:15:22Z","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseHomework](#schemaapiresponsehomework)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlistProblems"></a>

## GET 作业-题目列表（学生可见）

GET /api/homeworks/{id}/problems

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":[{"id":0,"name":"string","createTime":"2019-08-24T14:15:22Z","acCount":0,"submitCount":0,"description":"string","descriptionInput":"string","descriptionOutput":"string","sampleInput":"string","sampleOutput":"string","hint":"string","dailyChallenge":"string","difficulty":"string","timeLimitMs":0,"memoryLimitKb":0,"source":"string","isActive":true,"updatedAt":"2019-08-24T14:15:22Z"}]}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListProblem](#schemaapiresponselistproblem)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdaddProblems"></a>

## POST 作业题目-批量新增（教师/管理员）

POST /api/homeworks/{id}/problems

> Body 请求参数

```json
{
  "problemIds": [
    0
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|body|body|[HomeworkProblemBatchRequest](#schemahomeworkproblembatchrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdremoveProblem"></a>

## DELETE 作业题目-删除（教师/管理员）

DELETE /api/homeworks/{id}/problems/{problemId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|problemId|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# discussion-controller

<a id="opIddetail_4"></a>

## GET 讨论-详情

GET /api/discussions/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"userId":0,"problemId":0,"title":"string","createTime":"2019-08-24T14:15:22Z","updateTime":"2019-08-24T14:15:22Z","content":"string","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseDiscussion](#schemaapiresponsediscussion)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_5"></a>

## PUT 讨论-更新（作者或管理员）

PUT /api/discussions/{id}

> Body 请求参数

```json
{
  "title": "string",
  "content": "string",
  "problemId": 0,
  "isActive": true,
  "authorId": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|body|body|[DiscussionRequest](#schemadiscussionrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"userId":0,"problemId":0,"title":"string","createTime":"2019-08-24T14:15:22Z","updateTime":"2019-08-24T14:15:22Z","content":"string","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseDiscussion](#schemaapiresponsediscussion)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_5"></a>

## DELETE 讨论-删除（作者或管理员）

DELETE /api/discussions/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_7"></a>

## GET 讨论-列表

GET /api/discussions

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |none|
|size|query|integer(int64)| 否 |none|
|problemId|query|integer(int64)| 否 |关联题目过滤|
|userId|query|integer(int64)| 否 |作者过滤|
|includeInactive|query|boolean| 否 |管理员专用：是否包含未启用讨论|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"userId":0,"problemId":0,"title":"string","createTime":"2019-08-24T14:15:22Z","updateTime":"2019-08-24T14:15:22Z","content":"string","isActive":true}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageDiscussion](#schemaapiresponsepagediscussion)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_6"></a>

## POST 讨论-创建（学生/管理员）

POST /api/discussions

管理员发布需指定 authorId 为学生ID

> Body 请求参数

```json
{
  "title": "string",
  "content": "string",
  "problemId": 0,
  "isActive": true,
  "authorId": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[DiscussionRequest](#schemadiscussionrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"userId":0,"problemId":0,"title":"string","createTime":"2019-08-24T14:15:22Z","updateTime":"2019-08-24T14:15:22Z","content":"string","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseDiscussion](#schemaapiresponsediscussion)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlistComments"></a>

## GET 讨论评论-列表

GET /api/discussions/{id}/comments

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":[{"id":0,"discussId":0,"userId":0,"content":"string","createTime":"2019-08-24T14:15:22Z"}]}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListDiscussionComment](#schemaapiresponselistdiscussioncomment)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreateComment"></a>

## POST 讨论评论-创建（学生）

POST /api/discussions/{id}/comments

> Body 请求参数

```json
{
  "content": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|body|body|[DiscussionCommentRequest](#schemadiscussioncommentrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"discussId":0,"userId":0,"content":"string","createTime":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseDiscussionComment](#schemaapiresponsediscussioncomment)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddeleteComment"></a>

## DELETE 讨论评论-删除（作者或管理员）

DELETE /api/discussions/comments/{commentId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|commentId|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# classes-controller

<a id="opIdget_3"></a>

## GET 班级-详情

GET /api/classes/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |班级ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","creatorId":0,"code":"string","startDate":"2019-08-24","endDate":"2019-08-24","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClasses](#schemaapiresponseclasses)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_6"></a>

## PUT 班级-更新

PUT /api/classes/{id}

> Body 请求参数

```json
{
  "name": "string",
  "creatorId": 0,
  "code": "string",
  "startDate": "2019-08-24",
  "endDate": "2019-08-24",
  "description": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |班级ID|
|body|body|[ClassesRequest](#schemaclassesrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","creatorId":0,"code":"string","startDate":"2019-08-24","endDate":"2019-08-24","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClasses](#schemaapiresponseclasses)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_6"></a>

## DELETE 班级-删除

DELETE /api/classes/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |班级ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_8"></a>

## GET 班级-分页列表

GET /api/classes

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |页码|
|size|query|integer(int64)| 否 |每页条数|
|keyword|query|string| 否 |名称/描述/邀请码关键字|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"name":"string","creatorId":0,"code":"string","startDate":"2019-08-24","endDate":"2019-08-24","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageClasses](#schemaapiresponsepageclasses)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_7"></a>

## POST 班级-创建

POST /api/classes

> Body 请求参数

```json
{
  "name": "string",
  "creatorId": 0,
  "code": "string",
  "startDate": "2019-08-24",
  "endDate": "2019-08-24",
  "description": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ClassesRequest](#schemaclassesrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","creatorId":0,"code":"string","startDate":"2019-08-24","endDate":"2019-08-24","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClasses](#schemaapiresponseclasses)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# classes-member-controller

<a id="opIdget_4"></a>

## GET 班级成员-详情

GET /api/classes-members/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |记录ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"classId":0,"memberType":"string","studentId":0,"teacherId":0,"joinedAt":"2019-08-24T14:15:22Z","leftAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClassesMember](#schemaapiresponseclassesmember)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_7"></a>

## PUT 班级成员-更新

PUT /api/classes-members/{id}

> Body 请求参数

```json
{
  "classId": 0,
  "memberType": "string",
  "studentId": 0,
  "teacherId": 0,
  "joinedAt": "2019-08-24T14:15:22Z",
  "leftAt": "2019-08-24T14:15:22Z"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |记录ID|
|body|body|[ClassesMemberRequest](#schemaclassesmemberrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"classId":0,"memberType":"string","studentId":0,"teacherId":0,"joinedAt":"2019-08-24T14:15:22Z","leftAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClassesMember](#schemaapiresponseclassesmember)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_7"></a>

## DELETE 班级成员-删除

DELETE /api/classes-members/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |记录ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_9"></a>

## GET 班级成员-分页列表

GET /api/classes-members

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |页码|
|size|query|integer(int64)| 否 |每页条数|
|classId|query|integer(int64)| 否 |班级ID过滤|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"classId":0,"memberType":"string","studentId":0,"teacherId":0,"joinedAt":"2019-08-24T14:15:22Z","leftAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageClassesMember](#schemaapiresponsepageclassesmember)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_8"></a>

## POST 班级成员-创建

POST /api/classes-members

> Body 请求参数

```json
{
  "classId": 0,
  "memberType": "string",
  "studentId": 0,
  "teacherId": 0,
  "joinedAt": "2019-08-24T14:15:22Z",
  "leftAt": "2019-08-24T14:15:22Z"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ClassesMemberRequest](#schemaclassesmemberrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"classId":0,"memberType":"string","studentId":0,"teacherId":0,"joinedAt":"2019-08-24T14:15:22Z","leftAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClassesMember](#schemaapiresponseclassesmember)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# admin-controller

<a id="opIdget_5"></a>

## GET 管理员-详情

GET /api/admins/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |管理员ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","lastLoginIp":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdmin](#schemaapiresponseadmin)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdate_8"></a>

## PUT 管理员-更新

PUT /api/admins/{id}

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |管理员ID|
|body|body|[AdminUpsertRequest](#schemaadminupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","lastLoginIp":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdmin](#schemaapiresponseadmin)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_8"></a>

## DELETE 管理员-删除

DELETE /api/admins/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |管理员ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlist_11"></a>

## GET 管理员-分页列表

GET /api/admins

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |页码，从1开始|
|size|query|integer(int64)| 否 |每页条数|
|keyword|query|string| 否 |用户名/姓名/邮箱关键字|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","lastLoginIp":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageAdmin](#schemaapiresponsepageadmin)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_9"></a>

## POST 管理员-创建

POST /api/admins

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AdminUpsertRequest](#schemaadminupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","password":"string","name":"string","sex":"string","birth":"2019-08-24","phone":"string","email":"string","avatar":"string","lastLoginIp":"string","lastLoginTime":"2019-08-24T14:15:22Z","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdmin](#schemaapiresponseadmin)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# problem-admin-controller

<a id="opIdupdateProblem"></a>

## PUT 题库-更新题目

PUT /api/admin/problems/{id}

> Body 请求参数

```json
{
  "name": "string",
  "description": "string",
  "descriptionInput": "string",
  "descriptionOutput": "string",
  "sampleInput": "string",
  "sampleOutput": "string",
  "hint": "string",
  "dailyChallenge": "string",
  "difficulty": "string",
  "timeLimitMs": 1,
  "memoryLimitKb": 1,
  "source": "string",
  "isActive": true
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |题目ID|
|body|body|[ProblemUpsertRequest](#schemaproblemupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","createTime":"2019-08-24T14:15:22Z","acCount":0,"submitCount":0,"description":"string","descriptionInput":"string","descriptionOutput":"string","sampleInput":"string","sampleOutput":"string","hint":"string","dailyChallenge":"string","difficulty":"string","timeLimitMs":0,"memoryLimitKb":0,"source":"string","isActive":true,"updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseProblem](#schemaapiresponseproblem)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddeleteProblem"></a>

## DELETE 题库-删除题目

DELETE /api/admin/problems/{id}

删除题目同时移除关联测试用例

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |题目ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdgetTestcase"></a>

## GET 题库-测试用例详情

GET /api/admin/problem-testcases/{testcaseId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|testcaseId|path|integer(int64)| 是 |测试用例ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"problemId":0,"inputData":"string","outputData":"string"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseProblemTestcase](#schemaapiresponseproblemtestcase)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdupdateTestcase"></a>

## PUT 题库-更新测试用例

PUT /api/admin/problem-testcases/{testcaseId}

> Body 请求参数

```json
{
  "inputData": "string",
  "outputData": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|testcaseId|path|integer(int64)| 是 |测试用例ID|
|body|body|[ProblemTestcaseRequest](#schemaproblemtestcaserequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"problemId":0,"inputData":"string","outputData":"string"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseProblemTestcase](#schemaapiresponseproblemtestcase)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddeleteTestcase"></a>

## DELETE 题库-删除测试用例

DELETE /api/admin/problem-testcases/{testcaseId}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|testcaseId|path|integer(int64)| 是 |测试用例ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreateProblem"></a>

## POST 题库-新增题目

POST /api/admin/problems

> Body 请求参数

```json
{
  "name": "string",
  "description": "string",
  "descriptionInput": "string",
  "descriptionOutput": "string",
  "sampleInput": "string",
  "sampleOutput": "string",
  "hint": "string",
  "dailyChallenge": "string",
  "difficulty": "string",
  "timeLimitMs": 1,
  "memoryLimitKb": 1,
  "source": "string",
  "isActive": true
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ProblemUpsertRequest](#schemaproblemupsertrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","createTime":"2019-08-24T14:15:22Z","acCount":0,"submitCount":0,"description":"string","descriptionInput":"string","descriptionOutput":"string","sampleInput":"string","sampleOutput":"string","hint":"string","dailyChallenge":"string","difficulty":"string","timeLimitMs":0,"memoryLimitKb":0,"source":"string","isActive":true,"updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseProblem](#schemaapiresponseproblem)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlistTestcases"></a>

## GET 题库-测试用例列表

GET /api/admin/problems/{id}/testcases

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |题目ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":[{"id":0,"problemId":0,"inputData":"string","outputData":"string"}]}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListProblemTestcase](#schemaapiresponselistproblemtestcase)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreateTestcase"></a>

## POST 题库-新增测试用例

POST /api/admin/problems/{id}/testcases

> Body 请求参数

```json
{
  "inputData": "string",
  "outputData": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |题目ID|
|body|body|[ProblemTestcaseRequest](#schemaproblemtestcaserequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"problemId":0,"inputData":"string","outputData":"string"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseProblemTestcase](#schemaapiresponseproblemtestcase)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# announcement-admin-controller

<a id="opIdupdate_9"></a>

## PUT 公告-更新，仅管理员

PUT /api/admin/announcements/{id}

> Body 请求参数

```json
{
  "title": "string",
  "content": "string",
  "time": "2019-08-24T14:15:22Z",
  "isPinned": true,
  "isActive": true
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|
|body|body|[AnnouncementRequest](#schemaannouncementrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"title":"string","content":"string","time":"2019-08-24T14:15:22Z","isPinned":true,"updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAnnouncement](#schemaapiresponseannouncement)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddelete_9"></a>

## DELETE 公告-删除，仅管理员

DELETE /api/admin/announcements/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_10"></a>

## POST 公告-创建，仅管理员

POST /api/admin/announcements

> Body 请求参数

```json
{
  "title": "string",
  "content": "string",
  "time": "2019-08-24T14:15:22Z",
  "isPinned": true,
  "isActive": true
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[AnnouncementRequest](#schemaannouncementrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"title":"string","content":"string","time":"2019-08-24T14:15:22Z","isPinned":true,"updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAnnouncement](#schemaapiresponseannouncement)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# submission-controller

<a id="opIdlist_1"></a>

## GET 提交记录-分页列表

GET /api/submissions

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |none|
|size|query|integer(int64)| 否 |none|
|problemId|query|integer(int64)| 否 |none|
|homeworkId|query|integer(int64)| 否 |none|
|studentId|query|integer(int64)| 否 |none|
|statusId|query|integer(int32)| 否 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"studentId":0,"problemId":0,"homeworkId":0,"languageId":0,"overallStatusId":0,"overallStatusCode":"string","overallStatusName":"string","passedCaseCount":0,"totalCaseCount":0,"score":0,"createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageSubmissionVO](#schemaapiresponsepagesubmissionvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcreate_1"></a>

## POST 提交代码（学生）

POST /api/submissions

> Body 请求参数

```json
{
  "problemId": 0,
  "homeworkId": 0,
  "studentId": 0,
  "languageId": 0,
  "sourceCode": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[SubmissionCreateRequest](#schemasubmissioncreaterequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"studentId":0,"problemId":0,"homeworkId":0,"languageId":0,"overallStatusId":0,"overallStatusCode":"string","overallStatusName":"string","passedCaseCount":0,"totalCaseCount":0,"score":0,"createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","sourceCode":"string","testcaseResults":[{"testcaseId":0,"statusId":0,"statusDescription":"string","stdout":"string","stderr":"string","compileOutput":"string","message":"string","timeUsed":0,"memoryUsed":0}]}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSubmissionDetailVO](#schemaapiresponsesubmissiondetailvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddetail"></a>

## GET 提交详情

GET /api/submissions/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"studentId":0,"problemId":0,"homeworkId":0,"languageId":0,"overallStatusId":0,"overallStatusCode":"string","overallStatusName":"string","passedCaseCount":0,"totalCaseCount":0,"score":0,"createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z","sourceCode":"string","testcaseResults":[{"testcaseId":0,"statusId":0,"statusDescription":"string","stdout":"string","stderr":"string","compileOutput":"string","message":"string","timeUsed":0,"memoryUsed":0}]}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSubmissionDetailVO](#schemaapiresponsesubmissiondetailvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# student-class-controller

<a id="opIdleaveClass"></a>

## POST 学生退出班级

POST /api/student/classes/leave

退出当前班级，释放一生一班约束

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdjoinClass"></a>

## POST 学生加入班级

POST /api/student/classes/join

学生通过邀请码加入班级，仅允许加入一个班级

> Body 请求参数

```json
{
  "code": "CODEA123"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[StudentJoinClassRequest](#schemastudentjoinclassrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","creatorId":0,"code":"string","startDate":"2019-08-24","endDate":"2019-08-24","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClasses](#schemaapiresponseclasses)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdcurrentClass"></a>

## GET 学生当前班级

GET /api/student/classes

返回学生已加入的班级信息

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","creatorId":0,"code":"string","startDate":"2019-08-24","endDate":"2019-08-24","description":"string","createdAt":"2019-08-24T14:15:22Z","updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseClasses](#schemaapiresponseclasses)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# 文件

<a id="opIduploadAvatar"></a>

## POST 上传头像

POST /api/files/avatar

仅支持正方形图片（png/jpg/jpeg/gif/webp），返回可访问URL

> Body 请求参数

```yaml
file: ""

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 否 |none|
|» file|body|string(binary)| 是 |头像文件|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"property1":"string","property2":"string"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMapStringString](#schemaapiresponsemapstringstring)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# 认证

<a id="opIdverifyEmail"></a>

## POST 验证邮箱并激活

POST /api/auth/verifyEmail

提交验证码，验证通过后创建账号并返回Token

> Body 请求参数

```json
{
  "username": "string",
  "code": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[VerifyEmailRequest](#schemaverifyemailrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","email":"string","avatar":"string","role":"string","token":"string","details":{"property1":{},"property2":{}}}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAuthUserVO](#schemaapiresponseauthuservo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdregister"></a>

## POST 注册(发送验证码或直接注册)

POST /api/auth/register

当开启邮箱验证时发送验证码；测试或关闭时直接注册并返回Token

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "name": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[RegisterRequest](#schemaregisterrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","email":"string","avatar":"string","role":"string","token":"string","details":{"property1":{},"property2":{}}}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAuthUserVO](#schemaapiresponseauthuservo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdforgotPasswordVerify"></a>

## POST 找回密码-验证并重置(学生)

POST /api/auth/password/forgot/verify

提交验证码和新密码，验证通过后更新密码并邮件通知

> Body 请求参数

```json
{
  "username": "string",
  "code": "string",
  "newPassword": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ForgotPasswordVerifyRequest](#schemaforgotpasswordverifyrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdforgotPasswordSend"></a>

## POST 找回密码-发送验证码(学生)

POST /api/auth/password/forgot/sendCode

根据用户名向绑定邮箱发送找回密码验证码

> Body 请求参数

```json
{
  "username": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ForgotPasswordSendCodeRequest](#schemaforgotpasswordsendcoderequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdchangePassword"></a>

## POST 修改密码(学生)

POST /api/auth/password/change

登录后，校验旧密码并设置新密码；更新后发送通知

> Body 请求参数

```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[ChangePasswordRequest](#schemachangepasswordrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlogout"></a>

## POST 注销

POST /api/auth/logout

记录登出时间

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdlogin"></a>

## POST 登录

POST /api/auth/login

用户名密码登录，返回Token

> Body 请求参数

```json
{
  "username": "string",
  "password": "string",
  "role": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[LoginRequest](#schemaloginrequest)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","email":"string","avatar":"string","role":"string","token":"string","details":{"property1":{},"property2":{}}}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAuthUserVO](#schemaapiresponseauthuservo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIdme"></a>

## GET 当前用户信息

GET /api/auth/users/me

根据 Token 返回当前登录用户信息

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"username":"string","email":"string","avatar":"string","role":"string","token":"string","details":{"property1":{},"property2":{}}}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAuthUserVO](#schemaapiresponseauthuservo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# problem-controller

<a id="opIdlist_4"></a>

## GET 题库-公开列表

GET /api/problems

支持按名称关键字、难度、挑战标签筛选，教师/管理员自动可见全部题目

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |页码，从1开始|
|size|query|integer(int64)| 否 |每页条数|
|keyword|query|string| 否 |按题目名称模糊搜索|
|difficulty|query|string| 否 |难度过滤：easy/medium/hard|
|dailyChallenge|query|string| 否 |日常挑战标识|
|activeOnly|query|boolean| 否 |是否仅返回启用题目|
|isActive|query|boolean| 否 |按启用状态过滤，管理员/教师可用|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"name":"string","createTime":"2019-08-24T14:15:22Z","acCount":0,"submitCount":0,"description":"string","descriptionInput":"string","descriptionOutput":"string","sampleInput":"string","sampleOutput":"string","hint":"string","dailyChallenge":"string","difficulty":"string","timeLimitMs":0,"memoryLimitKb":0,"source":"string","isActive":true,"updatedAt":"2019-08-24T14:15:22Z"}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageProblem](#schemaapiresponsepageproblem)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddetail_2"></a>

## GET 题库-公开详情

GET /api/problems/{id}

返回题目完整描述，未启用题目仅管理员/教师可见

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |题目ID|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"name":"string","createTime":"2019-08-24T14:15:22Z","acCount":0,"submitCount":0,"description":"string","descriptionInput":"string","descriptionOutput":"string","sampleInput":"string","sampleOutput":"string","hint":"string","dailyChallenge":"string","difficulty":"string","timeLimitMs":0,"memoryLimitKb":0,"source":"string","isActive":true,"updatedAt":"2019-08-24T14:15:22Z"}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseProblem](#schemaapiresponseproblem)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# announcement-controller

<a id="opIdlist_10"></a>

## GET 公告-列表

GET /api/announcements

学生/教师/管理员均可查看

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer(int64)| 否 |none|
|size|query|integer(int64)| 否 |none|
|pinnedOnly|query|boolean| 否 |是否只看置顶|
|keyword|query|string| 否 |标题关键字|
|includeInactive|query|boolean| 否 |是否包含未启用公告，管理员默认包含|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"records":[{"id":0,"title":"string","content":"string","time":"2019-08-24T14:15:22Z","isPinned":true,"updatedAt":"2019-08-24T14:15:22Z","isActive":true}],"total":0,"size":0,"current":0,"orders":[{"column":"string","asc":true}],"optimizeCountSql":true,"searchCount":true,"maxLimit":0,"countId":"string","pages":0}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageAnnouncement](#schemaapiresponsepageannouncement)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<a id="opIddetail_5"></a>

## GET 公告-详情

GET /api/announcements/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|integer(int64)| 是 |none|

> 返回示例

> 200 Response

```
{"code":0,"message":"string","data":{"id":0,"title":"string","content":"string","time":"2019-08-24T14:15:22Z","isPinned":true,"updatedAt":"2019-08-24T14:15:22Z","isActive":true}}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAnnouncement](#schemaapiresponseannouncement)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

# 数据模型

<h2 id="tocS_ApiResponseVoid">ApiResponseVoid</h2>

<a id="schemaapiresponsevoid"></a>
<a id="schema_ApiResponseVoid"></a>
<a id="tocSapiresponsevoid"></a>
<a id="tocsapiresponsevoid"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {}
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|object|false|none||none|

<h2 id="tocS_TeacherUpsertRequest">TeacherUpsertRequest</h2>

<a id="schemateacherupsertrequest"></a>
<a id="schema_TeacherUpsertRequest"></a>
<a id="tocSteacherupsertrequest"></a>
<a id="tocsteacherupsertrequest"></a>

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "title": "string"
}

```

教师创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|
|name|string|false|none||none|
|sex|string|false|none||none|
|phone|string|false|none||none|
|email|string|false|none||none|
|avatar|string|false|none||none|
|title|string|false|none||none|

<h2 id="tocS_ApiResponseTeacher">ApiResponseTeacher</h2>

<a id="schemaapiresponseteacher"></a>
<a id="schema_ApiResponseTeacher"></a>
<a id="tocSapiresponseteacher"></a>
<a id="tocsapiresponseteacher"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "username": "string",
    "password": "string",
    "name": "string",
    "sex": "string",
    "phone": "string",
    "email": "string",
    "avatar": "string",
    "title": "string",
    "lastLoginTime": "2019-08-24T14:15:22Z",
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Teacher](#schemateacher)|false|none||教师实体|

<h2 id="tocS_Teacher">Teacher</h2>

<a id="schemateacher"></a>
<a id="schema_Teacher"></a>
<a id="tocSteacher"></a>
<a id="tocsteacher"></a>

```json
{
  "id": 0,
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "title": "string",
  "lastLoginTime": "2019-08-24T14:15:22Z",
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

教师实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||教师ID|
|username|string|false|none||用户名|
|password|string|false|none||密码哈希|
|name|string|false|none||姓名|
|sex|string|false|none||性别|
|phone|string|false|none||手机号|
|email|string|false|none||邮箱|
|avatar|string|false|none||头像URL|
|title|string|false|none||职称|
|lastLoginTime|string(date-time)|false|none||最近登录时间|
|createdAt|string(date-time)|false|none||创建时间|
|updatedAt|string(date-time)|false|none||更新时间|

<h2 id="tocS_StudentUpsertRequest">StudentUpsertRequest</h2>

<a id="schemastudentupsertrequest"></a>
<a id="schema_StudentUpsertRequest"></a>
<a id="tocSstudentupsertrequest"></a>
<a id="tocsstudentupsertrequest"></a>

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "background": "string",
  "school": "string",
  "score": 0,
  "bio": "string"
}

```

学生信息

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|
|name|string|false|none||none|
|sex|string|false|none||none|
|birth|string(date)|false|none||none|
|phone|string|false|none||none|
|email|string|false|none||none|
|avatar|string|false|none||none|
|background|string|false|none||none|
|school|string|false|none||none|
|score|integer(int32)|false|none||none|
|bio|string|false|none||none|

<h2 id="tocS_ApiResponseStudent">ApiResponseStudent</h2>

<a id="schemaapiresponsestudent"></a>
<a id="schema_ApiResponseStudent"></a>
<a id="tocSapiresponsestudent"></a>
<a id="tocsapiresponsestudent"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "username": "string",
    "password": "string",
    "name": "string",
    "sex": "string",
    "birth": "2019-08-24",
    "phone": "string",
    "email": "string",
    "avatar": "string",
    "background": "string",
    "ac": 0,
    "submit": 0,
    "school": "string",
    "score": 0,
    "lastLoginTime": "2019-08-24T14:15:22Z",
    "lastLanguage": "string",
    "createTime": "2019-08-24T14:15:22Z",
    "lastVisitTime": "2019-08-24T14:15:22Z",
    "dailyChallenge": "string",
    "registerIp": "string",
    "lastLoginIp": "string",
    "bio": "string",
    "isVerified": true,
    "updatedAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Student](#schemastudent)|false|none||学生实体|

<h2 id="tocS_Student">Student</h2>

<a id="schemastudent"></a>
<a id="schema_Student"></a>
<a id="tocSstudent"></a>
<a id="tocsstudent"></a>

```json
{
  "id": 0,
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "background": "string",
  "ac": 0,
  "submit": 0,
  "school": "string",
  "score": 0,
  "lastLoginTime": "2019-08-24T14:15:22Z",
  "lastLanguage": "string",
  "createTime": "2019-08-24T14:15:22Z",
  "lastVisitTime": "2019-08-24T14:15:22Z",
  "dailyChallenge": "string",
  "registerIp": "string",
  "lastLoginIp": "string",
  "bio": "string",
  "isVerified": true,
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

学生实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||学生ID|
|username|string|false|none||用户名|
|password|string|false|none||密码哈希|
|name|string|false|none||姓名|
|sex|string|false|none||性别|
|birth|string(date)|false|none||生日|
|phone|string|false|none||电话|
|email|string|false|none||邮箱|
|avatar|string|false|none||头像URL|
|background|string|false|none||背景图URL|
|ac|integer(int32)|false|none||AC题数|
|submit|integer(int32)|false|none||提交次数|
|school|string|false|none||学校名称|
|score|integer(int32)|false|none||积分/分数|
|lastLoginTime|string(date-time)|false|none||最近登录时间|
|lastLanguage|string|false|none||最近使用语言|
|createTime|string(date-time)|false|none||创建时间|
|lastVisitTime|string(date-time)|false|none||最近访问时间|
|dailyChallenge|string|false|none||今日挑战标识|
|registerIp|string|false|none||注册IP|
|lastLoginIp|string|false|none||最近登录IP|
|bio|string|false|none||个人简介|
|isVerified|boolean|false|none||是否已验证|
|updatedAt|string(date-time)|false|none||更新时间|

<h2 id="tocS_SolutionRequest">SolutionRequest</h2>

<a id="schemasolutionrequest"></a>
<a id="schema_SolutionRequest"></a>
<a id="tocSsolutionrequest"></a>
<a id="tocssolutionrequest"></a>

```json
{
  "problemId": 0,
  "title": "string",
  "content": "string",
  "language": "string",
  "isActive": true,
  "authorId": 0
}

```

题解创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|problemId|integer(int64)|false|none||none|
|title|string|true|none||none|
|content|string|true|none||none|
|language|string|false|none||语言|
|isActive|boolean|false|none||是否启用|
|authorId|integer(int64)|false|none||管理员代学生发布时指定学生ID，仅创建时使用|

<h2 id="tocS_ApiResponseSolution">ApiResponseSolution</h2>

<a id="schemaapiresponsesolution"></a>
<a id="schema_ApiResponseSolution"></a>
<a id="tocSapiresponsesolution"></a>
<a id="tocsapiresponsesolution"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "userId": 0,
    "problemId": 0,
    "title": "string",
    "content": "string",
    "language": "string",
    "createTime": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "isActive": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Solution](#schemasolution)|false|none||题解实体|

<h2 id="tocS_Solution">Solution</h2>

<a id="schemasolution"></a>
<a id="schema_Solution"></a>
<a id="tocSsolution"></a>
<a id="tocssolution"></a>

```json
{
  "id": 0,
  "userId": 0,
  "problemId": 0,
  "title": "string",
  "content": "string",
  "language": "string",
  "createTime": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "isActive": true
}

```

题解实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||题解ID|
|userId|integer(int64)|false|none||发布者学生ID|
|problemId|integer(int64)|false|none||题目ID|
|title|string|false|none||标题|
|content|string|false|none||内容|
|language|string|false|none||语言|
|createTime|string(date-time)|false|none||创建时间|
|updatedAt|string(date-time)|false|none||更新时间|
|isActive|boolean|false|none||是否启用|

<h2 id="tocS_LoginLogRequest">LoginLogRequest</h2>

<a id="schemaloginlogrequest"></a>
<a id="schema_LoginLogRequest"></a>
<a id="tocSloginlogrequest"></a>
<a id="tocsloginlogrequest"></a>

```json
{
  "role": "string",
  "userId": 0,
  "username": "string",
  "ipAddress": "string",
  "location": "string",
  "userAgent": "string",
  "device": "string",
  "loginTime": "2019-08-24T14:15:22Z",
  "logoutTime": "2019-08-24T14:15:22Z",
  "success": true,
  "failReason": "string"
}

```

登录日志创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|role|string|true|none||none|
|userId|integer(int64)|true|none||none|
|username|string|true|none||none|
|ipAddress|string|false|none||none|
|location|string|false|none||none|
|userAgent|string|false|none||none|
|device|string|false|none||none|
|loginTime|string(date-time)|false|none||none|
|logoutTime|string(date-time)|false|none||none|
|success|boolean|false|none||none|
|failReason|string|false|none||none|

<h2 id="tocS_ApiResponseLoginLog">ApiResponseLoginLog</h2>

<a id="schemaapiresponseloginlog"></a>
<a id="schema_ApiResponseLoginLog"></a>
<a id="tocSapiresponseloginlog"></a>
<a id="tocsapiresponseloginlog"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "role": "string",
    "userId": 0,
    "username": "string",
    "ipAddress": "string",
    "location": "string",
    "userAgent": "string",
    "device": "string",
    "loginTime": "2019-08-24T14:15:22Z",
    "logoutTime": "2019-08-24T14:15:22Z",
    "success": true,
    "failReason": "string",
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[LoginLog](#schemaloginlog)|false|none||登录日志实体|

<h2 id="tocS_LoginLog">LoginLog</h2>

<a id="schemaloginlog"></a>
<a id="schema_LoginLog"></a>
<a id="tocSloginlog"></a>
<a id="tocsloginlog"></a>

```json
{
  "id": 0,
  "role": "string",
  "userId": 0,
  "username": "string",
  "ipAddress": "string",
  "location": "string",
  "userAgent": "string",
  "device": "string",
  "loginTime": "2019-08-24T14:15:22Z",
  "logoutTime": "2019-08-24T14:15:22Z",
  "success": true,
  "failReason": "string",
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

登录日志实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||日志ID|
|role|string|false|none||角色：admin/teacher/student|
|userId|integer(int64)|false|none||用户ID|
|username|string|false|none||用户名|
|ipAddress|string|false|none||IP地址|
|location|string|false|none||地理位置|
|userAgent|string|false|none||User-Agent|
|device|string|false|none||设备信息|
|loginTime|string(date-time)|false|none||登录时间|
|logoutTime|string(date-time)|false|none||登出时间|
|success|boolean|false|none||是否成功|
|failReason|string|false|none||失败原因|
|createdAt|string(date-time)|false|none||创建时间|
|updatedAt|string(date-time)|false|none||更新时间|

<h2 id="tocS_HomeworkRequest">HomeworkRequest</h2>

<a id="schemahomeworkrequest"></a>
<a id="schema_HomeworkRequest"></a>
<a id="tocShomeworkrequest"></a>
<a id="tocshomeworkrequest"></a>

```json
{
  "title": "string",
  "classId": 0,
  "startTime": "2019-08-24T14:15:22Z",
  "endTime": "2019-08-24T14:15:22Z",
  "description": "string",
  "isActive": true,
  "problemIds": [
    0
  ]
}

```

作业创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|title|string|true|none||none|
|classId|integer(int64)|true|none||none|
|startTime|string(date-time)|false|none||none|
|endTime|string(date-time)|false|none||none|
|description|string|false|none||none|
|isActive|boolean|false|none||none|
|problemIds|[integer]|false|none||none|

<h2 id="tocS_ApiResponseHomework">ApiResponseHomework</h2>

<a id="schemaapiresponsehomework"></a>
<a id="schema_ApiResponseHomework"></a>
<a id="tocSapiresponsehomework"></a>
<a id="tocsapiresponsehomework"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "title": "string",
    "classId": 0,
    "startTime": "2019-08-24T14:15:22Z",
    "endTime": "2019-08-24T14:15:22Z",
    "description": "string",
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "isActive": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Homework](#schemahomework)|false|none||作业实体|

<h2 id="tocS_Homework">Homework</h2>

<a id="schemahomework"></a>
<a id="schema_Homework"></a>
<a id="tocShomework"></a>
<a id="tocshomework"></a>

```json
{
  "id": 0,
  "title": "string",
  "classId": 0,
  "startTime": "2019-08-24T14:15:22Z",
  "endTime": "2019-08-24T14:15:22Z",
  "description": "string",
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "isActive": true
}

```

作业实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||作业ID|
|title|string|false|none||标题|
|classId|integer(int64)|false|none||所属班级ID|
|startTime|string(date-time)|false|none||开始时间|
|endTime|string(date-time)|false|none||结束时间|
|description|string|false|none||作业说明|
|createdAt|string(date-time)|false|none||创建时间|
|updatedAt|string(date-time)|false|none||更新时间|
|isActive|boolean|false|none||是否启用|

<h2 id="tocS_DiscussionRequest">DiscussionRequest</h2>

<a id="schemadiscussionrequest"></a>
<a id="schema_DiscussionRequest"></a>
<a id="tocSdiscussionrequest"></a>
<a id="tocsdiscussionrequest"></a>

```json
{
  "title": "string",
  "content": "string",
  "problemId": 0,
  "isActive": true,
  "authorId": 0
}

```

讨论创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|title|string|true|none||none|
|content|string|true|none||none|
|problemId|integer(int64)|false|none||none|
|isActive|boolean|false|none||none|
|authorId|integer(int64)|false|none||管理员代学生发布时指定学生ID，仅创建时使用|

<h2 id="tocS_ApiResponseDiscussion">ApiResponseDiscussion</h2>

<a id="schemaapiresponsediscussion"></a>
<a id="schema_ApiResponseDiscussion"></a>
<a id="tocSapiresponsediscussion"></a>
<a id="tocsapiresponsediscussion"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "userId": 0,
    "problemId": 0,
    "title": "string",
    "createTime": "2019-08-24T14:15:22Z",
    "updateTime": "2019-08-24T14:15:22Z",
    "content": "string",
    "isActive": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Discussion](#schemadiscussion)|false|none||讨论实体|

<h2 id="tocS_Discussion">Discussion</h2>

<a id="schemadiscussion"></a>
<a id="schema_Discussion"></a>
<a id="tocSdiscussion"></a>
<a id="tocsdiscussion"></a>

```json
{
  "id": 0,
  "userId": 0,
  "problemId": 0,
  "title": "string",
  "createTime": "2019-08-24T14:15:22Z",
  "updateTime": "2019-08-24T14:15:22Z",
  "content": "string",
  "isActive": true
}

```

讨论实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||讨论ID|
|userId|integer(int64)|false|none||发布者学生ID|
|problemId|integer(int64)|false|none||关联题目ID|
|title|string|false|none||标题|
|createTime|string(date-time)|false|none||创建时间|
|updateTime|string(date-time)|false|none||更新时间|
|content|string|false|none||讨论内容|
|isActive|boolean|false|none||是否启用|

<h2 id="tocS_ClassesRequest">ClassesRequest</h2>

<a id="schemaclassesrequest"></a>
<a id="schema_ClassesRequest"></a>
<a id="tocSclassesrequest"></a>
<a id="tocsclassesrequest"></a>

```json
{
  "name": "string",
  "creatorId": 0,
  "code": "string",
  "startDate": "2019-08-24",
  "endDate": "2019-08-24",
  "description": "string"
}

```

班级创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|name|string|true|none||none|
|creatorId|integer(int64)|false|none||none|
|code|string|false|none||none|
|startDate|string(date)|false|none||none|
|endDate|string(date)|false|none||none|
|description|string|false|none||none|

<h2 id="tocS_ApiResponseClasses">ApiResponseClasses</h2>

<a id="schemaapiresponseclasses"></a>
<a id="schema_ApiResponseClasses"></a>
<a id="tocSapiresponseclasses"></a>
<a id="tocsapiresponseclasses"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "name": "string",
    "creatorId": 0,
    "code": "string",
    "startDate": "2019-08-24",
    "endDate": "2019-08-24",
    "description": "string",
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Classes](#schemaclasses)|false|none||班级实体|

<h2 id="tocS_Classes">Classes</h2>

<a id="schemaclasses"></a>
<a id="schema_Classes"></a>
<a id="tocSclasses"></a>
<a id="tocsclasses"></a>

```json
{
  "id": 0,
  "name": "string",
  "creatorId": 0,
  "code": "string",
  "startDate": "2019-08-24",
  "endDate": "2019-08-24",
  "description": "string",
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

班级实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||班级ID|
|name|string|false|none||班级名称|
|creatorId|integer(int64)|false|none||创建者教师ID|
|code|string|false|none||班级邀请码|
|startDate|string(date)|false|none||开始日期|
|endDate|string(date)|false|none||结束日期|
|description|string|false|none||描述|
|createdAt|string(date-time)|false|none||创建时间|
|updatedAt|string(date-time)|false|none||更新时间|

<h2 id="tocS_ClassesMemberRequest">ClassesMemberRequest</h2>

<a id="schemaclassesmemberrequest"></a>
<a id="schema_ClassesMemberRequest"></a>
<a id="tocSclassesmemberrequest"></a>
<a id="tocsclassesmemberrequest"></a>

```json
{
  "classId": 0,
  "memberType": "string",
  "studentId": 0,
  "teacherId": 0,
  "joinedAt": "2019-08-24T14:15:22Z",
  "leftAt": "2019-08-24T14:15:22Z"
}

```

班级成员创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|classId|integer(int64)|true|none||none|
|memberType|string|true|none||none|
|studentId|integer(int64)|false|none||none|
|teacherId|integer(int64)|false|none||none|
|joinedAt|string(date-time)|false|none||none|
|leftAt|string(date-time)|false|none||none|

<h2 id="tocS_ApiResponseClassesMember">ApiResponseClassesMember</h2>

<a id="schemaapiresponseclassesmember"></a>
<a id="schema_ApiResponseClassesMember"></a>
<a id="tocSapiresponseclassesmember"></a>
<a id="tocsapiresponseclassesmember"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "classId": 0,
    "memberType": "string",
    "studentId": 0,
    "teacherId": 0,
    "joinedAt": "2019-08-24T14:15:22Z",
    "leftAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[ClassesMember](#schemaclassesmember)|false|none||班级成员关联实体|

<h2 id="tocS_ClassesMember">ClassesMember</h2>

<a id="schemaclassesmember"></a>
<a id="schema_ClassesMember"></a>
<a id="tocSclassesmember"></a>
<a id="tocsclassesmember"></a>

```json
{
  "id": 0,
  "classId": 0,
  "memberType": "string",
  "studentId": 0,
  "teacherId": 0,
  "joinedAt": "2019-08-24T14:15:22Z",
  "leftAt": "2019-08-24T14:15:22Z"
}

```

班级成员关联实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||记录ID|
|classId|integer(int64)|false|none||班级ID|
|memberType|string|false|none||成员类型: student/teacher|
|studentId|integer(int64)|false|none||学生ID|
|teacherId|integer(int64)|false|none||教师ID|
|joinedAt|string(date-time)|false|none||加入时间|
|leftAt|string(date-time)|false|none||离开时间|

<h2 id="tocS_AdminUpsertRequest">AdminUpsertRequest</h2>

<a id="schemaadminupsertrequest"></a>
<a id="schema_AdminUpsertRequest"></a>
<a id="tocSadminupsertrequest"></a>
<a id="tocsadminupsertrequest"></a>

```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string"
}

```

管理员创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|
|name|string|false|none||none|
|sex|string|false|none||none|
|birth|string(date)|false|none||none|
|phone|string|false|none||none|
|email|string|false|none||none|
|avatar|string|false|none||none|

<h2 id="tocS_Admin">Admin</h2>

<a id="schemaadmin"></a>
<a id="schema_Admin"></a>
<a id="tocSadmin"></a>
<a id="tocsadmin"></a>

```json
{
  "id": 0,
  "username": "string",
  "password": "string",
  "name": "string",
  "sex": "string",
  "birth": "2019-08-24",
  "phone": "string",
  "email": "string",
  "avatar": "string",
  "lastLoginIp": "string",
  "lastLoginTime": "2019-08-24T14:15:22Z",
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

管理员实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||管理员ID|
|username|string|false|none||用户名|
|password|string|false|none||密码哈希|
|name|string|false|none||姓名|
|sex|string|false|none||性别|
|birth|string(date)|false|none||出生日期|
|phone|string|false|none||手机号|
|email|string|false|none||邮箱|
|avatar|string|false|none||头像URL|
|lastLoginIp|string|false|none||最近登录IP|
|lastLoginTime|string(date-time)|false|none||最近登录时间|
|createdAt|string(date-time)|false|none||创建时间|
|updatedAt|string(date-time)|false|none||更新时间|

<h2 id="tocS_ApiResponseAdmin">ApiResponseAdmin</h2>

<a id="schemaapiresponseadmin"></a>
<a id="schema_ApiResponseAdmin"></a>
<a id="tocSapiresponseadmin"></a>
<a id="tocsapiresponseadmin"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "username": "string",
    "password": "string",
    "name": "string",
    "sex": "string",
    "birth": "2019-08-24",
    "phone": "string",
    "email": "string",
    "avatar": "string",
    "lastLoginIp": "string",
    "lastLoginTime": "2019-08-24T14:15:22Z",
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Admin](#schemaadmin)|false|none||管理员实体|

<h2 id="tocS_ProblemUpsertRequest">ProblemUpsertRequest</h2>

<a id="schemaproblemupsertrequest"></a>
<a id="schema_ProblemUpsertRequest"></a>
<a id="tocSproblemupsertrequest"></a>
<a id="tocsproblemupsertrequest"></a>

```json
{
  "name": "string",
  "description": "string",
  "descriptionInput": "string",
  "descriptionOutput": "string",
  "sampleInput": "string",
  "sampleOutput": "string",
  "hint": "string",
  "dailyChallenge": "string",
  "difficulty": "string",
  "timeLimitMs": 1,
  "memoryLimitKb": 1,
  "source": "string",
  "isActive": true
}

```

题目创建/更新请求体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|name|string|true|none||none|
|description|string|true|none||none|
|descriptionInput|string|true|none||none|
|descriptionOutput|string|true|none||none|
|sampleInput|string|true|none||none|
|sampleOutput|string|true|none||none|
|hint|string|true|none||none|
|dailyChallenge|string|false|none||none|
|difficulty|string|true|none||none|
|timeLimitMs|integer(int32)|false|none||none|
|memoryLimitKb|integer(int32)|false|none||none|
|source|string|false|none||none|
|isActive|boolean|false|none||none|

<h2 id="tocS_ApiResponseProblem">ApiResponseProblem</h2>

<a id="schemaapiresponseproblem"></a>
<a id="schema_ApiResponseProblem"></a>
<a id="tocSapiresponseproblem"></a>
<a id="tocsapiresponseproblem"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "name": "string",
    "createTime": "2019-08-24T14:15:22Z",
    "acCount": 0,
    "submitCount": 0,
    "description": "string",
    "descriptionInput": "string",
    "descriptionOutput": "string",
    "sampleInput": "string",
    "sampleOutput": "string",
    "hint": "string",
    "dailyChallenge": "string",
    "difficulty": "string",
    "timeLimitMs": 0,
    "memoryLimitKb": 0,
    "source": "string",
    "isActive": true,
    "updatedAt": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Problem](#schemaproblem)|false|none||题目实体|

<h2 id="tocS_Problem">Problem</h2>

<a id="schemaproblem"></a>
<a id="schema_Problem"></a>
<a id="tocSproblem"></a>
<a id="tocsproblem"></a>

```json
{
  "id": 0,
  "name": "string",
  "createTime": "2019-08-24T14:15:22Z",
  "acCount": 0,
  "submitCount": 0,
  "description": "string",
  "descriptionInput": "string",
  "descriptionOutput": "string",
  "sampleInput": "string",
  "sampleOutput": "string",
  "hint": "string",
  "dailyChallenge": "string",
  "difficulty": "string",
  "timeLimitMs": 0,
  "memoryLimitKb": 0,
  "source": "string",
  "isActive": true,
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

题目实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||题目ID|
|name|string|false|none||题目名称|
|createTime|string(date-time)|false|none||创建时间|
|acCount|integer(int32)|false|none||通过次数|
|submitCount|integer(int32)|false|none||提交次数|
|description|string|false|none||题目描述|
|descriptionInput|string|false|none||输入描述|
|descriptionOutput|string|false|none||输出描述|
|sampleInput|string|false|none||输入样例|
|sampleOutput|string|false|none||输出样例|
|hint|string|false|none||提示|
|dailyChallenge|string|false|none||日常挑战标识|
|difficulty|string|false|none||难度|
|timeLimitMs|integer(int32)|false|none||时间限制(ms)|
|memoryLimitKb|integer(int32)|false|none||内存限制(KB)|
|source|string|false|none||题目来源|
|isActive|boolean|false|none||是否启用|
|updatedAt|string(date-time)|false|none||更新时间|

<h2 id="tocS_ProblemTestcaseRequest">ProblemTestcaseRequest</h2>

<a id="schemaproblemtestcaserequest"></a>
<a id="schema_ProblemTestcaseRequest"></a>
<a id="tocSproblemtestcaserequest"></a>
<a id="tocsproblemtestcaserequest"></a>

```json
{
  "inputData": "string",
  "outputData": "string"
}

```

题目测试用例创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|inputData|string|true|none||none|
|outputData|string|true|none||none|

<h2 id="tocS_ApiResponseProblemTestcase">ApiResponseProblemTestcase</h2>

<a id="schemaapiresponseproblemtestcase"></a>
<a id="schema_ApiResponseProblemTestcase"></a>
<a id="tocSapiresponseproblemtestcase"></a>
<a id="tocsapiresponseproblemtestcase"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "problemId": 0,
    "inputData": "string",
    "outputData": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[ProblemTestcase](#schemaproblemtestcase)|false|none||题目测试用例实体|

<h2 id="tocS_ProblemTestcase">ProblemTestcase</h2>

<a id="schemaproblemtestcase"></a>
<a id="schema_ProblemTestcase"></a>
<a id="tocSproblemtestcase"></a>
<a id="tocsproblemtestcase"></a>

```json
{
  "id": 0,
  "problemId": 0,
  "inputData": "string",
  "outputData": "string"
}

```

题目测试用例实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||测试用例ID|
|problemId|integer(int64)|false|none||题目ID|
|inputData|string|false|none||输入数据|
|outputData|string|false|none||输出数据|

<h2 id="tocS_AnnouncementRequest">AnnouncementRequest</h2>

<a id="schemaannouncementrequest"></a>
<a id="schema_AnnouncementRequest"></a>
<a id="tocSannouncementrequest"></a>
<a id="tocsannouncementrequest"></a>

```json
{
  "title": "string",
  "content": "string",
  "time": "2019-08-24T14:15:22Z",
  "isPinned": true,
  "isActive": true
}

```

公告创建/更新请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|title|string|true|none||none|
|content|string|true|none||none|
|time|string(date-time)|false|none||发布时间|
|isPinned|boolean|false|none||是否置顶|
|isActive|boolean|false|none||是否启用|

<h2 id="tocS_Announcement">Announcement</h2>

<a id="schemaannouncement"></a>
<a id="schema_Announcement"></a>
<a id="tocSannouncement"></a>
<a id="tocsannouncement"></a>

```json
{
  "id": 0,
  "title": "string",
  "content": "string",
  "time": "2019-08-24T14:15:22Z",
  "isPinned": true,
  "updatedAt": "2019-08-24T14:15:22Z",
  "isActive": true
}

```

公告实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||公告ID|
|title|string|false|none||标题|
|content|string|false|none||内容|
|time|string(date-time)|false|none||发布时间|
|isPinned|boolean|false|none||是否置顶|
|updatedAt|string(date-time)|false|none||更新时间|
|isActive|boolean|false|none||是否启用|

<h2 id="tocS_ApiResponseAnnouncement">ApiResponseAnnouncement</h2>

<a id="schemaapiresponseannouncement"></a>
<a id="schema_ApiResponseAnnouncement"></a>
<a id="tocSapiresponseannouncement"></a>
<a id="tocsapiresponseannouncement"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "title": "string",
    "content": "string",
    "time": "2019-08-24T14:15:22Z",
    "isPinned": true,
    "updatedAt": "2019-08-24T14:15:22Z",
    "isActive": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[Announcement](#schemaannouncement)|false|none||公告实体|

<h2 id="tocS_SubmissionCreateRequest">SubmissionCreateRequest</h2>

<a id="schemasubmissioncreaterequest"></a>
<a id="schema_SubmissionCreateRequest"></a>
<a id="tocSsubmissioncreaterequest"></a>
<a id="tocssubmissioncreaterequest"></a>

```json
{
  "problemId": 0,
  "homeworkId": 0,
  "studentId": 0,
  "languageId": 0,
  "sourceCode": "string"
}

```

提交代码请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|problemId|integer(int64)|true|none||none|
|homeworkId|integer(int64)|false|none||none|
|studentId|integer(int64)|false|none||（仅管理员）代提交的学生ID|
|languageId|integer(int32)|true|none||none|
|sourceCode|string|true|none||none|

<h2 id="tocS_ApiResponseSubmissionDetailVO">ApiResponseSubmissionDetailVO</h2>

<a id="schemaapiresponsesubmissiondetailvo"></a>
<a id="schema_ApiResponseSubmissionDetailVO"></a>
<a id="tocSapiresponsesubmissiondetailvo"></a>
<a id="tocsapiresponsesubmissiondetailvo"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "studentId": 0,
    "problemId": 0,
    "homeworkId": 0,
    "languageId": 0,
    "overallStatusId": 0,
    "overallStatusCode": "string",
    "overallStatusName": "string",
    "passedCaseCount": 0,
    "totalCaseCount": 0,
    "score": 0,
    "createdAt": "2019-08-24T14:15:22Z",
    "updatedAt": "2019-08-24T14:15:22Z",
    "sourceCode": "string",
    "testcaseResults": [
      {
        "testcaseId": 0,
        "statusId": 0,
        "statusDescription": "string",
        "stdout": "string",
        "stderr": "string",
        "compileOutput": "string",
        "message": "string",
        "timeUsed": 0,
        "memoryUsed": 0
      }
    ]
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[SubmissionDetailVO](#schemasubmissiondetailvo)|false|none||提交详情|

<h2 id="tocS_SubmissionDetailVO">SubmissionDetailVO</h2>

<a id="schemasubmissiondetailvo"></a>
<a id="schema_SubmissionDetailVO"></a>
<a id="tocSsubmissiondetailvo"></a>
<a id="tocssubmissiondetailvo"></a>

```json
{
  "id": 0,
  "studentId": 0,
  "problemId": 0,
  "homeworkId": 0,
  "languageId": 0,
  "overallStatusId": 0,
  "overallStatusCode": "string",
  "overallStatusName": "string",
  "passedCaseCount": 0,
  "totalCaseCount": 0,
  "score": 0,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z",
  "sourceCode": "string",
  "testcaseResults": [
    {
      "testcaseId": 0,
      "statusId": 0,
      "statusDescription": "string",
      "stdout": "string",
      "stderr": "string",
      "compileOutput": "string",
      "message": "string",
      "timeUsed": 0,
      "memoryUsed": 0
    }
  ]
}

```

提交详情

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|studentId|integer(int64)|false|none||none|
|problemId|integer(int64)|false|none||none|
|homeworkId|integer(int64)|false|none||none|
|languageId|integer(int32)|false|none||none|
|overallStatusId|integer(int32)|false|none||none|
|overallStatusCode|string|false|none||none|
|overallStatusName|string|false|none||none|
|passedCaseCount|integer(int32)|false|none||none|
|totalCaseCount|integer(int32)|false|none||none|
|score|integer(int32)|false|none||none|
|createdAt|string(date-time)|false|none||none|
|updatedAt|string(date-time)|false|none||none|
|sourceCode|string|false|none||none|
|testcaseResults|[[SubmissionTestcaseResultVO](#schemasubmissiontestcaseresultvo)]|false|none||[提交-测试用例结果视图]|

<h2 id="tocS_SubmissionTestcaseResultVO">SubmissionTestcaseResultVO</h2>

<a id="schemasubmissiontestcaseresultvo"></a>
<a id="schema_SubmissionTestcaseResultVO"></a>
<a id="tocSsubmissiontestcaseresultvo"></a>
<a id="tocssubmissiontestcaseresultvo"></a>

```json
{
  "testcaseId": 0,
  "statusId": 0,
  "statusDescription": "string",
  "stdout": "string",
  "stderr": "string",
  "compileOutput": "string",
  "message": "string",
  "timeUsed": 0,
  "memoryUsed": 0
}

```

提交-测试用例结果视图

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|testcaseId|integer(int64)|false|none||none|
|statusId|integer(int32)|false|none||none|
|statusDescription|string|false|none||none|
|stdout|string|false|none||none|
|stderr|string|false|none||none|
|compileOutput|string|false|none||none|
|message|string|false|none||none|
|timeUsed|number|false|none||none|
|memoryUsed|integer(int32)|false|none||none|

<h2 id="tocS_StudentJoinClassRequest">StudentJoinClassRequest</h2>

<a id="schemastudentjoinclassrequest"></a>
<a id="schema_StudentJoinClassRequest"></a>
<a id="tocSstudentjoinclassrequest"></a>
<a id="tocsstudentjoinclassrequest"></a>

```json
{
  "code": "CODEA123"
}

```

学生加入班级请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|string|true|none||班级邀请码|

<h2 id="tocS_HomeworkProblemBatchRequest">HomeworkProblemBatchRequest</h2>

<a id="schemahomeworkproblembatchrequest"></a>
<a id="schema_HomeworkProblemBatchRequest"></a>
<a id="tocShomeworkproblembatchrequest"></a>
<a id="tocshomeworkproblembatchrequest"></a>

```json
{
  "problemIds": [
    0
  ]
}

```

作业题目批量操作请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|problemIds|[integer]|true|none||none|

<h2 id="tocS_ApiResponseMapStringString">ApiResponseMapStringString</h2>

<a id="schemaapiresponsemapstringstring"></a>
<a id="schema_ApiResponseMapStringString"></a>
<a id="tocSapiresponsemapstringstring"></a>
<a id="tocsapiresponsemapstringstring"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "property1": "string",
    "property2": "string"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|object|false|none||none|
|» **additionalProperties**|string|false|none||none|

<h2 id="tocS_DiscussionCommentRequest">DiscussionCommentRequest</h2>

<a id="schemadiscussioncommentrequest"></a>
<a id="schema_DiscussionCommentRequest"></a>
<a id="tocSdiscussioncommentrequest"></a>
<a id="tocsdiscussioncommentrequest"></a>

```json
{
  "content": "string"
}

```

讨论评论请求体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|content|string|true|none||none|

<h2 id="tocS_ApiResponseDiscussionComment">ApiResponseDiscussionComment</h2>

<a id="schemaapiresponsediscussioncomment"></a>
<a id="schema_ApiResponseDiscussionComment"></a>
<a id="tocSapiresponsediscussioncomment"></a>
<a id="tocsapiresponsediscussioncomment"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "discussId": 0,
    "userId": 0,
    "content": "string",
    "createTime": "2019-08-24T14:15:22Z"
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[DiscussionComment](#schemadiscussioncomment)|false|none||讨论评论实体|

<h2 id="tocS_DiscussionComment">DiscussionComment</h2>

<a id="schemadiscussioncomment"></a>
<a id="schema_DiscussionComment"></a>
<a id="tocSdiscussioncomment"></a>
<a id="tocsdiscussioncomment"></a>

```json
{
  "id": 0,
  "discussId": 0,
  "userId": 0,
  "content": "string",
  "createTime": "2019-08-24T14:15:22Z"
}

```

讨论评论实体

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||评论ID|
|discussId|integer(int64)|false|none||所属讨论ID|
|userId|integer(int64)|false|none||评论者学生ID|
|content|string|false|none||评论内容|
|createTime|string(date-time)|false|none||创建时间|

<h2 id="tocS_VerifyEmailRequest">VerifyEmailRequest</h2>

<a id="schemaverifyemailrequest"></a>
<a id="schema_VerifyEmailRequest"></a>
<a id="tocSverifyemailrequest"></a>
<a id="tocsverifyemailrequest"></a>

```json
{
  "username": "string",
  "code": "string"
}

```

邮箱验证码校验请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|code|string|true|none||none|

<h2 id="tocS_ApiResponseAuthUserVO">ApiResponseAuthUserVO</h2>

<a id="schemaapiresponseauthuservo"></a>
<a id="schema_ApiResponseAuthUserVO"></a>
<a id="tocSapiresponseauthuservo"></a>
<a id="tocsapiresponseauthuservo"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "id": 0,
    "username": "string",
    "email": "string",
    "avatar": "string",
    "role": "string",
    "token": "string",
    "details": {
      "property1": {},
      "property2": {}
    }
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[AuthUserVO](#schemaauthuservo)|false|none||认证用户信息及令牌|

<h2 id="tocS_AuthUserVO">AuthUserVO</h2>

<a id="schemaauthuservo"></a>
<a id="schema_AuthUserVO"></a>
<a id="tocSauthuservo"></a>
<a id="tocsauthuservo"></a>

```json
{
  "id": 0,
  "username": "string",
  "email": "string",
  "avatar": "string",
  "role": "string",
  "token": "string",
  "details": {
    "property1": {},
    "property2": {}
  }
}

```

认证用户信息及令牌

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||用户ID|
|username|string|false|none||用户名|
|email|string|false|none||邮箱|
|avatar|string|false|none||头像URL|
|role|string|false|none||角色 student|teacher|admin|
|token|string|false|none||带前缀的JWT令牌|
|details|object|false|none||完整用户属性快照，包含该角色实体所有字段|
|» **additionalProperties**|object|false|none||完整用户属性快照，包含该角色实体所有字段|

<h2 id="tocS_RegisterRequest">RegisterRequest</h2>

<a id="schemaregisterrequest"></a>
<a id="schema_RegisterRequest"></a>
<a id="tocSregisterrequest"></a>
<a id="tocsregisterrequest"></a>

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "name": "string"
}

```

用户注册请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||用户名|
|password|string|true|none||密码明文，将被哈希|
|email|string|false|none||邮箱|
|name|string|false|none||姓名|

<h2 id="tocS_ForgotPasswordVerifyRequest">ForgotPasswordVerifyRequest</h2>

<a id="schemaforgotpasswordverifyrequest"></a>
<a id="schema_ForgotPasswordVerifyRequest"></a>
<a id="tocSforgotpasswordverifyrequest"></a>
<a id="tocsforgotpasswordverifyrequest"></a>

```json
{
  "username": "string",
  "code": "string",
  "newPassword": "string"
}

```

找回密码-提交验证码并重置请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||用户名|
|code|string|true|none||邮箱验证码|
|newPassword|string|true|none||新密码明文，将被哈希|

<h2 id="tocS_ForgotPasswordSendCodeRequest">ForgotPasswordSendCodeRequest</h2>

<a id="schemaforgotpasswordsendcoderequest"></a>
<a id="schema_ForgotPasswordSendCodeRequest"></a>
<a id="tocSforgotpasswordsendcoderequest"></a>
<a id="tocsforgotpasswordsendcoderequest"></a>

```json
{
  "username": "string"
}

```

找回密码-发送验证码请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||用户名|

<h2 id="tocS_ChangePasswordRequest">ChangePasswordRequest</h2>

<a id="schemachangepasswordrequest"></a>
<a id="schema_ChangePasswordRequest"></a>
<a id="tocSchangepasswordrequest"></a>
<a id="tocschangepasswordrequest"></a>

```json
{
  "oldPassword": "string",
  "newPassword": "string"
}

```

修改密码请求

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|oldPassword|string|true|none||旧密码|
|newPassword|string|true|none||新密码|

<h2 id="tocS_LoginRequest">LoginRequest</h2>

<a id="schemaloginrequest"></a>
<a id="schema_LoginRequest"></a>
<a id="tocSloginrequest"></a>
<a id="tocsloginrequest"></a>

```json
{
  "username": "string",
  "password": "string",
  "role": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|username|string|true|none||none|
|password|string|true|none||none|
|role|string|false|none||登录身份: student|teacher|admin, 默认为 student|

<h2 id="tocS_ApiResponsePageTeacher">ApiResponsePageTeacher</h2>

<a id="schemaapiresponsepageteacher"></a>
<a id="schema_ApiResponsePageTeacher"></a>
<a id="tocSapiresponsepageteacher"></a>
<a id="tocsapiresponsepageteacher"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "username": "string",
        "password": "string",
        "name": "string",
        "sex": "string",
        "phone": "string",
        "email": "string",
        "avatar": "string",
        "title": "string",
        "lastLoginTime": "2019-08-24T14:15:22Z",
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageTeacher](#schemapageteacher)|false|none||none|

<h2 id="tocS_OrderItem">OrderItem</h2>

<a id="schemaorderitem"></a>
<a id="schema_OrderItem"></a>
<a id="tocSorderitem"></a>
<a id="tocsorderitem"></a>

```json
{
  "column": "string",
  "asc": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|column|string|false|none||none|
|asc|boolean|false|none||none|

<h2 id="tocS_PageTeacher">PageTeacher</h2>

<a id="schemapageteacher"></a>
<a id="schema_PageTeacher"></a>
<a id="tocSpageteacher"></a>
<a id="tocspageteacher"></a>

```json
{
  "records": [
    {
      "id": 0,
      "username": "string",
      "password": "string",
      "name": "string",
      "sex": "string",
      "phone": "string",
      "email": "string",
      "avatar": "string",
      "title": "string",
      "lastLoginTime": "2019-08-24T14:15:22Z",
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Teacher](#schemateacher)]|false|none||[教师实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageSubmissionVO">ApiResponsePageSubmissionVO</h2>

<a id="schemaapiresponsepagesubmissionvo"></a>
<a id="schema_ApiResponsePageSubmissionVO"></a>
<a id="tocSapiresponsepagesubmissionvo"></a>
<a id="tocsapiresponsepagesubmissionvo"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "studentId": 0,
        "problemId": 0,
        "homeworkId": 0,
        "languageId": 0,
        "overallStatusId": 0,
        "overallStatusCode": "string",
        "overallStatusName": "string",
        "passedCaseCount": 0,
        "totalCaseCount": 0,
        "score": 0,
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageSubmissionVO](#schemapagesubmissionvo)|false|none||none|

<h2 id="tocS_PageSubmissionVO">PageSubmissionVO</h2>

<a id="schemapagesubmissionvo"></a>
<a id="schema_PageSubmissionVO"></a>
<a id="tocSpagesubmissionvo"></a>
<a id="tocspagesubmissionvo"></a>

```json
{
  "records": [
    {
      "id": 0,
      "studentId": 0,
      "problemId": 0,
      "homeworkId": 0,
      "languageId": 0,
      "overallStatusId": 0,
      "overallStatusCode": "string",
      "overallStatusName": "string",
      "passedCaseCount": 0,
      "totalCaseCount": 0,
      "score": 0,
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[SubmissionVO](#schemasubmissionvo)]|false|none||[提交摘要]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_SubmissionVO">SubmissionVO</h2>

<a id="schemasubmissionvo"></a>
<a id="schema_SubmissionVO"></a>
<a id="tocSsubmissionvo"></a>
<a id="tocssubmissionvo"></a>

```json
{
  "id": 0,
  "studentId": 0,
  "problemId": 0,
  "homeworkId": 0,
  "languageId": 0,
  "overallStatusId": 0,
  "overallStatusCode": "string",
  "overallStatusName": "string",
  "passedCaseCount": 0,
  "totalCaseCount": 0,
  "score": 0,
  "createdAt": "2019-08-24T14:15:22Z",
  "updatedAt": "2019-08-24T14:15:22Z"
}

```

提交摘要

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer(int64)|false|none||none|
|studentId|integer(int64)|false|none||none|
|problemId|integer(int64)|false|none||none|
|homeworkId|integer(int64)|false|none||none|
|languageId|integer(int32)|false|none||none|
|overallStatusId|integer(int32)|false|none||none|
|overallStatusCode|string|false|none||none|
|overallStatusName|string|false|none||none|
|passedCaseCount|integer(int32)|false|none||none|
|totalCaseCount|integer(int32)|false|none||none|
|score|integer(int32)|false|none||none|
|createdAt|string(date-time)|false|none||none|
|updatedAt|string(date-time)|false|none||none|

<h2 id="tocS_ApiResponsePageStudent">ApiResponsePageStudent</h2>

<a id="schemaapiresponsepagestudent"></a>
<a id="schema_ApiResponsePageStudent"></a>
<a id="tocSapiresponsepagestudent"></a>
<a id="tocsapiresponsepagestudent"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "username": "string",
        "password": "string",
        "name": "string",
        "sex": "string",
        "birth": "2019-08-24",
        "phone": "string",
        "email": "string",
        "avatar": "string",
        "background": "string",
        "ac": 0,
        "submit": 0,
        "school": "string",
        "score": 0,
        "lastLoginTime": "2019-08-24T14:15:22Z",
        "lastLanguage": "string",
        "createTime": "2019-08-24T14:15:22Z",
        "lastVisitTime": "2019-08-24T14:15:22Z",
        "dailyChallenge": "string",
        "registerIp": "string",
        "lastLoginIp": "string",
        "bio": "string",
        "isVerified": true,
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageStudent](#schemapagestudent)|false|none||none|

<h2 id="tocS_PageStudent">PageStudent</h2>

<a id="schemapagestudent"></a>
<a id="schema_PageStudent"></a>
<a id="tocSpagestudent"></a>
<a id="tocspagestudent"></a>

```json
{
  "records": [
    {
      "id": 0,
      "username": "string",
      "password": "string",
      "name": "string",
      "sex": "string",
      "birth": "2019-08-24",
      "phone": "string",
      "email": "string",
      "avatar": "string",
      "background": "string",
      "ac": 0,
      "submit": 0,
      "school": "string",
      "score": 0,
      "lastLoginTime": "2019-08-24T14:15:22Z",
      "lastLanguage": "string",
      "createTime": "2019-08-24T14:15:22Z",
      "lastVisitTime": "2019-08-24T14:15:22Z",
      "dailyChallenge": "string",
      "registerIp": "string",
      "lastLoginIp": "string",
      "bio": "string",
      "isVerified": true,
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Student](#schemastudent)]|false|none||[学生实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageSolution">ApiResponsePageSolution</h2>

<a id="schemaapiresponsepagesolution"></a>
<a id="schema_ApiResponsePageSolution"></a>
<a id="tocSapiresponsepagesolution"></a>
<a id="tocsapiresponsepagesolution"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "userId": 0,
        "problemId": 0,
        "title": "string",
        "content": "string",
        "language": "string",
        "createTime": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "isActive": true
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageSolution](#schemapagesolution)|false|none||none|

<h2 id="tocS_PageSolution">PageSolution</h2>

<a id="schemapagesolution"></a>
<a id="schema_PageSolution"></a>
<a id="tocSpagesolution"></a>
<a id="tocspagesolution"></a>

```json
{
  "records": [
    {
      "id": 0,
      "userId": 0,
      "problemId": 0,
      "title": "string",
      "content": "string",
      "language": "string",
      "createTime": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "isActive": true
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Solution](#schemasolution)]|false|none||[题解实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageProblem">ApiResponsePageProblem</h2>

<a id="schemaapiresponsepageproblem"></a>
<a id="schema_ApiResponsePageProblem"></a>
<a id="tocSapiresponsepageproblem"></a>
<a id="tocsapiresponsepageproblem"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "name": "string",
        "createTime": "2019-08-24T14:15:22Z",
        "acCount": 0,
        "submitCount": 0,
        "description": "string",
        "descriptionInput": "string",
        "descriptionOutput": "string",
        "sampleInput": "string",
        "sampleOutput": "string",
        "hint": "string",
        "dailyChallenge": "string",
        "difficulty": "string",
        "timeLimitMs": 0,
        "memoryLimitKb": 0,
        "source": "string",
        "isActive": true,
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageProblem](#schemapageproblem)|false|none||none|

<h2 id="tocS_PageProblem">PageProblem</h2>

<a id="schemapageproblem"></a>
<a id="schema_PageProblem"></a>
<a id="tocSpageproblem"></a>
<a id="tocspageproblem"></a>

```json
{
  "records": [
    {
      "id": 0,
      "name": "string",
      "createTime": "2019-08-24T14:15:22Z",
      "acCount": 0,
      "submitCount": 0,
      "description": "string",
      "descriptionInput": "string",
      "descriptionOutput": "string",
      "sampleInput": "string",
      "sampleOutput": "string",
      "hint": "string",
      "dailyChallenge": "string",
      "difficulty": "string",
      "timeLimitMs": 0,
      "memoryLimitKb": 0,
      "source": "string",
      "isActive": true,
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Problem](#schemaproblem)]|false|none||[题目实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageLoginLog">ApiResponsePageLoginLog</h2>

<a id="schemaapiresponsepageloginlog"></a>
<a id="schema_ApiResponsePageLoginLog"></a>
<a id="tocSapiresponsepageloginlog"></a>
<a id="tocsapiresponsepageloginlog"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "role": "string",
        "userId": 0,
        "username": "string",
        "ipAddress": "string",
        "location": "string",
        "userAgent": "string",
        "device": "string",
        "loginTime": "2019-08-24T14:15:22Z",
        "logoutTime": "2019-08-24T14:15:22Z",
        "success": true,
        "failReason": "string",
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageLoginLog](#schemapageloginlog)|false|none||none|

<h2 id="tocS_PageLoginLog">PageLoginLog</h2>

<a id="schemapageloginlog"></a>
<a id="schema_PageLoginLog"></a>
<a id="tocSpageloginlog"></a>
<a id="tocspageloginlog"></a>

```json
{
  "records": [
    {
      "id": 0,
      "role": "string",
      "userId": 0,
      "username": "string",
      "ipAddress": "string",
      "location": "string",
      "userAgent": "string",
      "device": "string",
      "loginTime": "2019-08-24T14:15:22Z",
      "logoutTime": "2019-08-24T14:15:22Z",
      "success": true,
      "failReason": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[LoginLog](#schemaloginlog)]|false|none||[登录日志实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageHomework">ApiResponsePageHomework</h2>

<a id="schemaapiresponsepagehomework"></a>
<a id="schema_ApiResponsePageHomework"></a>
<a id="tocSapiresponsepagehomework"></a>
<a id="tocsapiresponsepagehomework"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "title": "string",
        "classId": 0,
        "startTime": "2019-08-24T14:15:22Z",
        "endTime": "2019-08-24T14:15:22Z",
        "description": "string",
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z",
        "isActive": true
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageHomework](#schemapagehomework)|false|none||none|

<h2 id="tocS_PageHomework">PageHomework</h2>

<a id="schemapagehomework"></a>
<a id="schema_PageHomework"></a>
<a id="tocSpagehomework"></a>
<a id="tocspagehomework"></a>

```json
{
  "records": [
    {
      "id": 0,
      "title": "string",
      "classId": 0,
      "startTime": "2019-08-24T14:15:22Z",
      "endTime": "2019-08-24T14:15:22Z",
      "description": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z",
      "isActive": true
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Homework](#schemahomework)]|false|none||[作业实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponseListProblem">ApiResponseListProblem</h2>

<a id="schemaapiresponselistproblem"></a>
<a id="schema_ApiResponseListProblem"></a>
<a id="tocSapiresponselistproblem"></a>
<a id="tocsapiresponselistproblem"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": [
    {
      "id": 0,
      "name": "string",
      "createTime": "2019-08-24T14:15:22Z",
      "acCount": 0,
      "submitCount": 0,
      "description": "string",
      "descriptionInput": "string",
      "descriptionOutput": "string",
      "sampleInput": "string",
      "sampleOutput": "string",
      "hint": "string",
      "dailyChallenge": "string",
      "difficulty": "string",
      "timeLimitMs": 0,
      "memoryLimitKb": 0,
      "source": "string",
      "isActive": true,
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[[Problem](#schemaproblem)]|false|none||[题目实体]|

<h2 id="tocS_ApiResponsePageDiscussion">ApiResponsePageDiscussion</h2>

<a id="schemaapiresponsepagediscussion"></a>
<a id="schema_ApiResponsePageDiscussion"></a>
<a id="tocSapiresponsepagediscussion"></a>
<a id="tocsapiresponsepagediscussion"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "userId": 0,
        "problemId": 0,
        "title": "string",
        "createTime": "2019-08-24T14:15:22Z",
        "updateTime": "2019-08-24T14:15:22Z",
        "content": "string",
        "isActive": true
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageDiscussion](#schemapagediscussion)|false|none||none|

<h2 id="tocS_PageDiscussion">PageDiscussion</h2>

<a id="schemapagediscussion"></a>
<a id="schema_PageDiscussion"></a>
<a id="tocSpagediscussion"></a>
<a id="tocspagediscussion"></a>

```json
{
  "records": [
    {
      "id": 0,
      "userId": 0,
      "problemId": 0,
      "title": "string",
      "createTime": "2019-08-24T14:15:22Z",
      "updateTime": "2019-08-24T14:15:22Z",
      "content": "string",
      "isActive": true
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Discussion](#schemadiscussion)]|false|none||[讨论实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponseListDiscussionComment">ApiResponseListDiscussionComment</h2>

<a id="schemaapiresponselistdiscussioncomment"></a>
<a id="schema_ApiResponseListDiscussionComment"></a>
<a id="tocSapiresponselistdiscussioncomment"></a>
<a id="tocsapiresponselistdiscussioncomment"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": [
    {
      "id": 0,
      "discussId": 0,
      "userId": 0,
      "content": "string",
      "createTime": "2019-08-24T14:15:22Z"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[[DiscussionComment](#schemadiscussioncomment)]|false|none||[讨论评论实体]|

<h2 id="tocS_ApiResponsePageClasses">ApiResponsePageClasses</h2>

<a id="schemaapiresponsepageclasses"></a>
<a id="schema_ApiResponsePageClasses"></a>
<a id="tocSapiresponsepageclasses"></a>
<a id="tocsapiresponsepageclasses"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "name": "string",
        "creatorId": 0,
        "code": "string",
        "startDate": "2019-08-24",
        "endDate": "2019-08-24",
        "description": "string",
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageClasses](#schemapageclasses)|false|none||none|

<h2 id="tocS_PageClasses">PageClasses</h2>

<a id="schemapageclasses"></a>
<a id="schema_PageClasses"></a>
<a id="tocSpageclasses"></a>
<a id="tocspageclasses"></a>

```json
{
  "records": [
    {
      "id": 0,
      "name": "string",
      "creatorId": 0,
      "code": "string",
      "startDate": "2019-08-24",
      "endDate": "2019-08-24",
      "description": "string",
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Classes](#schemaclasses)]|false|none||[班级实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageClassesMember">ApiResponsePageClassesMember</h2>

<a id="schemaapiresponsepageclassesmember"></a>
<a id="schema_ApiResponsePageClassesMember"></a>
<a id="tocSapiresponsepageclassesmember"></a>
<a id="tocsapiresponsepageclassesmember"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "classId": 0,
        "memberType": "string",
        "studentId": 0,
        "teacherId": 0,
        "joinedAt": "2019-08-24T14:15:22Z",
        "leftAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageClassesMember](#schemapageclassesmember)|false|none||none|

<h2 id="tocS_PageClassesMember">PageClassesMember</h2>

<a id="schemapageclassesmember"></a>
<a id="schema_PageClassesMember"></a>
<a id="tocSpageclassesmember"></a>
<a id="tocspageclassesmember"></a>

```json
{
  "records": [
    {
      "id": 0,
      "classId": 0,
      "memberType": "string",
      "studentId": 0,
      "teacherId": 0,
      "joinedAt": "2019-08-24T14:15:22Z",
      "leftAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[ClassesMember](#schemaclassesmember)]|false|none||[班级成员关联实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageAnnouncement">ApiResponsePageAnnouncement</h2>

<a id="schemaapiresponsepageannouncement"></a>
<a id="schema_ApiResponsePageAnnouncement"></a>
<a id="tocSapiresponsepageannouncement"></a>
<a id="tocsapiresponsepageannouncement"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "title": "string",
        "content": "string",
        "time": "2019-08-24T14:15:22Z",
        "isPinned": true,
        "updatedAt": "2019-08-24T14:15:22Z",
        "isActive": true
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageAnnouncement](#schemapageannouncement)|false|none||none|

<h2 id="tocS_PageAnnouncement">PageAnnouncement</h2>

<a id="schemapageannouncement"></a>
<a id="schema_PageAnnouncement"></a>
<a id="tocSpageannouncement"></a>
<a id="tocspageannouncement"></a>

```json
{
  "records": [
    {
      "id": 0,
      "title": "string",
      "content": "string",
      "time": "2019-08-24T14:15:22Z",
      "isPinned": true,
      "updatedAt": "2019-08-24T14:15:22Z",
      "isActive": true
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Announcement](#schemaannouncement)]|false|none||[公告实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponsePageAdmin">ApiResponsePageAdmin</h2>

<a id="schemaapiresponsepageadmin"></a>
<a id="schema_ApiResponsePageAdmin"></a>
<a id="tocSapiresponsepageadmin"></a>
<a id="tocsapiresponsepageadmin"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "records": [
      {
        "id": 0,
        "username": "string",
        "password": "string",
        "name": "string",
        "sex": "string",
        "birth": "2019-08-24",
        "phone": "string",
        "email": "string",
        "avatar": "string",
        "lastLoginIp": "string",
        "lastLoginTime": "2019-08-24T14:15:22Z",
        "createdAt": "2019-08-24T14:15:22Z",
        "updatedAt": "2019-08-24T14:15:22Z"
      }
    ],
    "total": 0,
    "size": 0,
    "current": 0,
    "orders": [
      {
        "column": "string",
        "asc": true
      }
    ],
    "optimizeCountSql": true,
    "searchCount": true,
    "optimizeJoinOfCountSql": true,
    "maxLimit": 0,
    "countId": "string",
    "pages": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[PageAdmin](#schemapageadmin)|false|none||none|

<h2 id="tocS_PageAdmin">PageAdmin</h2>

<a id="schemapageadmin"></a>
<a id="schema_PageAdmin"></a>
<a id="tocSpageadmin"></a>
<a id="tocspageadmin"></a>

```json
{
  "records": [
    {
      "id": 0,
      "username": "string",
      "password": "string",
      "name": "string",
      "sex": "string",
      "birth": "2019-08-24",
      "phone": "string",
      "email": "string",
      "avatar": "string",
      "lastLoginIp": "string",
      "lastLoginTime": "2019-08-24T14:15:22Z",
      "createdAt": "2019-08-24T14:15:22Z",
      "updatedAt": "2019-08-24T14:15:22Z"
    }
  ],
  "total": 0,
  "size": 0,
  "current": 0,
  "orders": [
    {
      "column": "string",
      "asc": true
    }
  ],
  "optimizeCountSql": true,
  "searchCount": true,
  "optimizeJoinOfCountSql": true,
  "maxLimit": 0,
  "countId": "string",
  "pages": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|records|[[Admin](#schemaadmin)]|false|none||[管理员实体]|
|total|integer(int64)|false|none||none|
|size|integer(int64)|false|none||none|
|current|integer(int64)|false|none||none|
|orders|[[OrderItem](#schemaorderitem)]|false|none||none|
|optimizeCountSql|boolean|false|none||none|
|searchCount|boolean|false|none||none|
|optimizeJoinOfCountSql|boolean|false|write-only||none|
|maxLimit|integer(int64)|false|none||none|
|countId|string|false|none||none|
|pages|integer(int64)|false|none||none|

<h2 id="tocS_ApiResponseListProblemTestcase">ApiResponseListProblemTestcase</h2>

<a id="schemaapiresponselistproblemtestcase"></a>
<a id="schema_ApiResponseListProblemTestcase"></a>
<a id="tocSapiresponselistproblemtestcase"></a>
<a id="tocsapiresponselistproblemtestcase"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": [
    {
      "id": 0,
      "problemId": 0,
      "inputData": "string",
      "outputData": "string"
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer(int32)|false|none||none|
|message|string|false|none||none|
|data|[[ProblemTestcase](#schemaproblemtestcase)]|false|none||[题目测试用例实体]|

