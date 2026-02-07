package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapper.ProductMapper;
import com.service.ProductService;
import generator.domain.demo.Result;
import generator.domain.entity.Product;
import generator.domain.entity.ProductCategory;
import generator.domain.product.ProductListVO;
import generator.domain.product.ProductPageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final String PRODUCT_CACHE_PREFIX = "product:list:";
    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:info:";
    private static final String PRODUCT_CACHE_VALUE_PREFIX = "product:category:";
    private static final long CACHE_PRODUCT_LIST_EXPIRE_TIME = 10;
    private static final long CACHE_PRODUCT_ID_EXPIRE_TIME = 5;
    private static final long CACHE_PRODUCT_CATEGORY_EXPIRE_TIME = 10;

    private final ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    public ProductServiceImpl(ProductMapper productMapper, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 商品列表查询
     * @param productDTO
     * @return
     */
    @Override
    public Result<ProductListVO> getProducts(ProductPageDTO productDTO) throws JsonProcessingException  {
        // 1.构建唯一缓存键(仅包含非默认/非空参数)
        String conditionCacheKey = buildCacheKey(productDTO);

        // 2.优先查询Redis【条件缓存】
        List<Long> productIdList = null;
        try{
            String cacheValue = redisTemplate.opsForValue().get(conditionCacheKey);
            if(cacheValue != null && !cacheValue.isEmpty()){
                productIdList = objectMapper.readValue(cacheValue, new TypeReference<List<Long>>() {
                });
                log.info("Redis缓存命中，缓存键：{}，缓存值：{}", conditionCacheKey, cacheValue);
            }
        }catch (JsonProcessingException e){
            log.error("Redis缓存反序列化失败，缓存键：{}，异常：{}", conditionCacheKey, e.getMessage(), e);
        }catch (Exception e){
            log.error("Redis缓存查询异常，缓存键：{}，异常：{}", conditionCacheKey, e.getMessage(), e);
        }

        // 3.根据ID列表查询单个商品缓存,批量获取提升效率
        List<Product> productList = new ArrayList<>();
        List<Long> unHitProductIdList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(productIdList)){
            // 3.1 构建单个商品缓存键列表,批量查询Redis
            List<String> productCacheKeys = productIdList.stream()
                    .map(productId -> PRODUCT_CACHE_KEY_PREFIX + productId)
                    .collect(Collectors.toList());
            List<String> productCacheValues = redisTemplate.opsForValue().multiGet(productCacheKeys);

            // 3.2 解析缓存结果,分离命中/未命中
            for(int i = 0; i < productIdList.size(); i++){
                Long currentProductId = productIdList.get(i);
                String currentCacheValue = productCacheValues.get(i);

                if(currentCacheValue != null && !currentCacheValue.isEmpty()){
                    try {
                        Product product = objectMapper.readValue(currentCacheValue, Product.class);
                        productList.add(product);
                        log.info("Redis缓存命中，缓存键：{}，缓存值：{}", productCacheKeys.get(i), productCacheValues.get(i));
                    }catch (JsonProcessingException e){
                        log.error("Redis缓存反序列化失败，缓存键：{}，异常：{}", productCacheKeys.get(i), e.getMessage(), e);
                        unHitProductIdList.add(currentProductId);
                    }
                }else{
                    unHitProductIdList.add(currentProductId);
                }
            }
        }

        // 4.批量查询数据库
        List< Product> dbProductList = new ArrayList<>();
        boolean needRefreshConditionCache = CollectionUtils.isEmpty(productIdList);

        if(needRefreshConditionCache){
            // 条件缓存未命中,直接从数据库查询符合条件的完整商品列表
            try{
                dbProductList = productMapper.selectByCondition(productDTO);
                log.info("数据库查询完整商品列表成功，条件：{}", productDTO);

                if(!CollectionUtils.isEmpty(dbProductList)){
                    productIdList = dbProductList.stream()
                            .map(Product::getId)
                            .collect(Collectors.toList());
                }
            }catch (Exception e){
                log.error("数据库查询完整商品列表失败，条件：{}，异常：{}", productDTO, e.getMessage(), e);
                return Result.error(500, "数据库查询完整商品列表失败");
            }
        } else if (!CollectionUtils.isEmpty(unHitProductIdList)) {

            try{
                LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(Product::getId, unHitProductIdList);
                dbProductList = this.list(queryWrapper);
                log.info("数据库查询部分商品列表成功，条件：{}", productDTO);
            }catch (Exception e){
                log.error("数据库查询部分商品列表失败，条件：{}，异常：{}", productDTO, e.getMessage(), e);
                return Result.error(500, "数据库查询部分商品列表失败");
            }
        }

        // 5.将数据库查询的商品存入Redis缓存
        if(!CollectionUtils.isEmpty(dbProductList)){
            for(Product product : dbProductList){
                if (product == null) continue;
                try {
                    String productCacheKey = PRODUCT_CACHE_KEY_PREFIX + product.getId();
                    String productCacheValue = objectMapper.writeValueAsString(product);
                    redisTemplate.opsForValue().set(productCacheKey, productCacheValue, CACHE_PRODUCT_ID_EXPIRE_TIME, TimeUnit.MINUTES);
                    log.debug("Redis单个商品缓存写入成功，商品ID：{}，过期时间：{}分钟", product.getId(), CACHE_PRODUCT_ID_EXPIRE_TIME);
                }catch (JsonProcessingException e){
                    log.error("Redis单个商品缓存写入失败，商品ID：{}，异常：{}", product.getId(), e.getMessage(), e);
                }catch (Exception e){
                    log.error("Redis单个商品缓存写入异常，商品ID：{}，异常：{}", product.getId(), e.getMessage(), e);
                }
            }
            // 合并数据库查询结果到最终商品列表
            productList.addAll(dbProductList);
        }

        // 6.刷新条件缓存
        if(needRefreshConditionCache && !CollectionUtils.isEmpty(productIdList)){
            try {
                String conditionCacheValue = objectMapper.writeValueAsString(productIdList);
                redisTemplate.opsForValue().set(conditionCacheKey, conditionCacheValue, CACHE_PRODUCT_LIST_EXPIRE_TIME, TimeUnit.MINUTES);
                log.debug("Redis条件缓存写入成功，条件：{}，过期时间：{}分钟", productDTO, CACHE_PRODUCT_LIST_EXPIRE_TIME);
            }catch (JsonProcessingException e){
                log.error("Redis条件缓存写入失败，条件：{}，异常：{}", productDTO, e.getMessage(), e);
            }catch (Exception e){
                log.error("Redis条件缓存写入异常，条件：{}，异常：{}", productDTO, e.getMessage(), e);
            }
        }

        // 7.封装返回结果
        ProductListVO productListVO = new ProductListVO();
        productListVO.setRecords(productList);
        productListVO.setTotal(CollectionUtils.isEmpty(productIdList) ? 0L : (long) productIdList.size());
        productListVO.setSize(productDTO.getSize());
        productListVO.setPage(productDTO.getPage());
        productListVO.setCurrent(productDTO.getPage());

        return Result.success(productListVO);
    }

    /**
     * 生成缓存键
     */
    private String buildCacheKey(ProductPageDTO productDTO){
        StringBuilder conditionBuilder = new StringBuilder();

        // 分页参数
        if(productDTO.getPage() != null && productDTO.getPage() != 1){
            conditionBuilder.append("page=").append(productDTO.getPage()).append("_");
        }
        if(productDTO.getSize() != null && productDTO.getSize() != 12){
            conditionBuilder.append("size=").append(productDTO.getSize()).append("_");
        }

        // 关键词
        if(productDTO.getKeyword() != null && !productDTO.getKeyword().trim().isEmpty()){
            conditionBuilder.append("keyword=").append(productDTO.getKeyword().trim()).append("_");
        }
        // 分类ID
        if(productDTO.getCategoryId() != null && productDTO.getCategoryId() != 0){
            conditionBuilder.append("categoryId=").append(productDTO.getCategoryId()).append("_");
        }
        // 排序参数
        if(productDTO.getSortBy() != null && !productDTO.getSortBy().trim().isEmpty()){
            conditionBuilder.append("sortBy=").append(productDTO.getSortBy().trim()).append("_");
        }
        if(productDTO.getOrder() != null && !productDTO.getOrder().trim().isEmpty()){
            conditionBuilder.append("order=").append(productDTO.getOrder().trim()).append("_");
        }

        String conditionStr = conditionBuilder.toString();
        if(conditionStr.endsWith("_")){
            conditionStr = conditionStr.substring(0, conditionStr.length() - 1);
        }
        if(conditionStr.isEmpty()){
            conditionStr = "default";
        }
        return PRODUCT_CACHE_PREFIX + conditionStr;
    }

    /**
     * 商品详情查询
     * @param id
     * @return
     */
    @Override
    public Result<Product> getProductById(Long id) throws JsonProcessingException {
        if(id == null){
            return Result.error(400,"商品ID不能为空");
        }
        long productId = id;
        if(productId <= 0){
            return Result.error(400,"商品ID必须大于0");
        }

        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productId;
        String product = redisTemplate.opsForValue().get(cacheKey);
        if(product != null){
            log.info("从Redis缓存中获取数据成功,缓存键:{}",cacheKey);
            Product productVO = objectMapper.readValue(product, Product.class);
            return Result.success(productVO);
        }

        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getId,productId);
        Product productVO = this.getOne(queryWrapper);
        if(productVO == null){
            log.warn("商品ID{}不存在", productId);
            return Result.error(400,"商品不存在");
        }
        redisTemplate.opsForValue().set(cacheKey,objectMapper.writeValueAsString(productVO),CACHE_PRODUCT_ID_EXPIRE_TIME, TimeUnit.MINUTES);
        log.info("Redis缓存写入成功，缓存键：{}，过期时间：{}分钟", cacheKey, CACHE_PRODUCT_ID_EXPIRE_TIME);
        return Result.success(productVO);
    }

    /**
     * 商品分类列表查询
     * @return
     */
    @Override
    public Result<List<ProductCategory>> getProductCategories() throws JsonProcessingException {
        String cacheKey = PRODUCT_CACHE_VALUE_PREFIX;
        String productCategories = redisTemplate.opsForValue().get(cacheKey);
        List<ProductCategory> productCategoryList = null;
        if(productCategories != null){
            log.info("从Redis缓存中获取数据成功,缓存键:{}",cacheKey);
            productCategoryList = objectMapper.readValue(productCategories, new TypeReference<List<ProductCategory>>(){});
        }
        if(productCategoryList == null){
            List<String> categoryIds = null;
            categoryIds = productMapper.selectCategoryIds();

            productCategoryList = categoryIds.stream().map(categoryId ->{ ProductCategory productCategory = new ProductCategory();
                productCategory = productMapper.selectCategoryById(categoryId);
                return productCategory;
            }).collect(Collectors.toList());

            redisTemplate.opsForValue().set(cacheKey,objectMapper.writeValueAsString(productCategoryList),CACHE_PRODUCT_CATEGORY_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("Redis缓存写入成功，缓存键：{}，过期时间：{}分钟", cacheKey, CACHE_PRODUCT_CATEGORY_EXPIRE_TIME);
        }
        return Result.success(productCategoryList);
    }
}
