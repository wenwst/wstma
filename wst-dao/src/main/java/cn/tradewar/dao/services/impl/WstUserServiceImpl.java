package cn.tradewar.dao.services.impl;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.core.consts.LogConst;
import cn.tradewar.dao.model.bo.WxBindMobileBo;
import cn.tradewar.dao.model.entity.WstUserEntity;
//import cn.tradewar.dao.mapper.WstUserMapper;
import cn.tradewar.dao.mapper.WstUserMapper;
import cn.tradewar.dao.services.WstUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class WstUserServiceImpl
        extends ServiceImpl<WstUserMapper, WstUserEntity>
        implements WstUserService{


    private final WstUserMapper wstUserMapper;

    @Autowired
    public WstUserServiceImpl(WstUserMapper wstUserMapper) {
        this.wstUserMapper = wstUserMapper;
    }


    /**
     * 通过微信的OpenId获取用户信息
     *
     * @param openId 微信OpenId
     * @return WstUserEntity
     */
    @Override
    public Optional<WstUserEntity> getUserByWxOpenId(String openId) {
        try {
            LambdaQueryWrapper<WstUserEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WstUserEntity::getWeixinOpenid, openId);
            wrapper.eq(WstUserEntity::getStatus, 0);
            return Optional.ofNullable(wstUserMapper.selectOne(wrapper));
        } catch (Exception e) {
            log.error(LogConst.EXCEPTION_FORMAT, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * 创建用户
     *
     * @param user WstUserEntity
     * @return WstUserEntity
     */
    @Override
    public Optional<WstUserEntity> createUser(WstUserEntity user) {
        try {
            int result =  wstUserMapper.insert(user);
            if(result > 0) {
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error(LogConst.EXCEPTION_FORMAT, e.getMessage(), e);
            return Optional.empty();
        }

    }

    /**
     * 更新用户
     *
     * @param user WstUserEntity
     * @return WstUserEntity
     */
    @Override
    public Optional<WstUserEntity> updateUser(WstUserEntity user) {
        try{
            int result =  wstUserMapper.updateById(user);
            if(result > 0) {
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error(LogConst.EXCEPTION_FORMAT, e.getMessage(), e);
            return Optional.empty();
        }
    }


    /**
     * 更新用户手机号
     *
     * @param bo WxBindMobileBo 包含用户ID和新的手机号
     * @return GenericResponse 更新结果的响应
     */
    @Override
    public GenericResponse updateUserMobile(WxBindMobileBo bo) {
        // 创建 LambdaUpdateWrapper 来构建更新条件
        LambdaUpdateWrapper<WstUserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WstUserEntity::getId, bo.getUserId());
        wrapper.set(WstUserEntity::getMobile, bo.getMobile());
        int result = wstUserMapper.update(wrapper);

        // 如果更新成功（影响行数大于 0），返回正常响应
        if(result > 0) {
            return GenericResponse.response(ServiceError.NORMAL);
        }

        return GenericResponse.response(ServiceError.WX_MOBILE_UPDATE_ERR);
    }


}
