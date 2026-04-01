package com.weilai.express.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weilai.express.entity.Merchant;
import com.weilai.express.service.MerchantService;
import com.weilai.express.vo.MerchantListVO;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "商家管理")
@RestController
@RequestMapping("/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping
    @Operation(summary = "获取商家列表")
    public Result<List<MerchantListVO>> list() {
        List<Merchant> merchants = merchantService.list(
            new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, 1)
                .orderByDesc(Merchant::getSalesCount)
        );
        
        List<MerchantListVO> result = merchants.stream().map(merchant -> {
            MerchantListVO vo = new MerchantListVO();
            BeanUtils.copyProperties(merchant, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商家详情")
    public Result<MerchantListVO> detail(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        
        if (merchant == null) {
            return Result.error(404, "商家不存在");
        }
        
        MerchantListVO vo = new MerchantListVO();
        BeanUtils.copyProperties(merchant, vo);
        
        return Result.success(vo);
    }
}
