package cn.tradewar.dao.services;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.dao.model.bo.WxBindMobileBo;
import cn.tradewar.dao.model.entity.WstUserEntity;

import java.util.Optional;

public interface WstUserService{

     /**
      * 通过微信的OpenId获取用户信息
      * @param openId 微信OpenId
      * @return WstUserEntity
      */
     Optional<WstUserEntity> getUserByWxOpenId(String openId);

     /**
      * 创建用户
      * @param user WstUserEntity
      * @return WstUserEntity
      */
     Optional<WstUserEntity> createUser(WstUserEntity user);

     /**
      * 更新用户
      * @param user WstUserEntity
      * @return WstUserEntity
      */
     Optional<WstUserEntity> updateUser(WstUserEntity user);


     /**
      * 更新用户手机号
      *
      * @param bo WxBindMobileBo 包含用户ID和新的手机号
      * @return GenericResponse 更新结果的响应
      */
     GenericResponse updateUserMobile(WxBindMobileBo bo);
}
