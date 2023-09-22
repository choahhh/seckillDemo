package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.dao.GoodDetailMapper;
import com.example.seckilldemo.entity.po.GoodsDetail;
import com.example.seckilldemo.service.GoodsDetailService;
import org.springframework.stereotype.Service;

@Service
public class GoodDetailServiceImpl extends ServiceImpl<GoodDetailMapper, GoodsDetail> implements GoodsDetailService {

}
