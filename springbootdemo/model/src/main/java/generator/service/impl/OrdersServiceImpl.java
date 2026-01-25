package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Entity.Orders;
import generator.service.OrdersService;
import generator.mapper.OrdersMapper;
import org.springframework.stereotype.Service;

/**
* @author 1569157760
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2026-01-20 21:15:03
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

}




