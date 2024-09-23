# 测试用例
Tags: tag1, tag2
Meta: key1 = value, key2 = value

## S100 微信授权Controller
* 101 微信登陆成功
  Tags: tag3
    * 登陆成功
    * 微信code为空
* 102 微信code为空
  Tags: tag4
    * Step 1
    * Step 2
* 103 登陆异常
  Tags: tag4
  * Step 1
  * Step 2
* 104 微信手机号授权成功
  Tags: tag5
  * Step 1
  * Step 2
* 105 微信手机和加密数据同时存在
  Tags: tag5
  * Step 1
  * Step 2
* 106 微信手机号授权encryptedData和iv参数缺失
  Tags: tag5
  * Step 1
  * Step 2
* 107 微信手机号授权code参数缺失
  Tags: tag5
  * Step 1: Create a `WxLoginBo` object with an empty `code` field and valid `encryptedData` and `iv` fields.
  * Step 2: Send a POST request to `/wx/auth/wxLoginPhone` with the `WxLoginBo` object.
  * Step 3: Assert that the response status is a client error (4xx) indicating the missing `code`.


## S101 微信手机号授权
* C3 Test Case 3
  Tags: tag5
    * Step 1
    * Step 2