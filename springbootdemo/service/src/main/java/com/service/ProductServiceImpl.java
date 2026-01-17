package com.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapper.ProductMapper;
import generator.domain.Entity.Product;
import generator.domain.Entity.ProductCategory;
import generator.domain.demo.Result;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService{

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
    public Result<ProductListVO> getProducts(ProductDTO productDTO) throws JsonProcessingException  {
        // 1.构建唯一缓存键(仅包含非默认/非空参数)
        String cacheKey = buildCacheKey(productDTO);

        // 2.优先查询Redis缓存
        try{
            String cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if(cacheValue != null && !cacheValue.isEmpty()){
                List< Product> products = objectMapper.readValue(cacheValue, new TypeReference<List<Product>>(){});
                log.info("从Redis缓存中获取数据成功,缓存键:{}",cacheKey);
                ProductListVO productListVO = new ProductListVO();
                productListVO.setRecords(products);
                productListVO.setTotal((long) products.size());
                productListVO.setPage(productDTO.getPage());
                productListVO.setSize(productDTO.getSize());
                productListVO.setCurrent(productDTO.getPage());
                return Result.success(productListVO);
            }
        }catch (JsonProcessingException e){
            log.info("Redis缓存反序列化失败，缓存键：{}，异常：{}", cacheKey, e.getMessage(), e);
        }catch (Exception e){
            log.error("Redis缓存查询异常，缓存键：{}，异常：{}", cacheKey, e.getMessage(), e);
        }

        // 3.查询数据库
        List<Product> productList = null;
        try{
            productList = productMapper.selectByCondition(productDTO);
            log.info("从数据库中获取数据成功,缓存键:{}",cacheKey);
        }catch (Exception e){
            log.error("从数据库中获取数据失败,缓存键:{}",cacheKey,e);
            return Result.error(500,"商品列表查询失败");
        }

        // 4.将数据库结果存入Redis
        try{
            String cacheValue = objectMapper.writeValueAsString(productList);
            redisTemplate.opsForValue().set(cacheKey,cacheValue,CACHE_PRODUCT_LIST_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("Redis缓存写入成功，缓存键：{}，过期时间：{}分钟", cacheKey, CACHE_PRODUCT_LIST_EXPIRE_TIME);
        }catch (JsonProcessingException e){
            log.error("Redis缓存序列化失败，缓存键：{}，异常：{}", cacheKey, e.getMessage(), e);
        }catch (Exception e){
            log.error("Redis缓存写入异常，缓存键：{}，异常：{}", cacheKey, e.getMessage(), e);
        }

        ProductListVO productListVO = new ProductListVO();
        productListVO.setRecords(productList);
        productListVO.setTotal((long) productList.size());
        productListVO.setPage(productDTO.getPage());
        productListVO.setSize(productDTO.getSize());

        return Result.success(productListVO);
    }

    /**
     * 生成缓存键
     */
    private String buildCacheKey(ProductDTO productDTO){
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
