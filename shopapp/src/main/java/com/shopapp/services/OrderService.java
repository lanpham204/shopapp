package com.shopapp.services;

import com.shopapp.dtos.CartItemDTO;
import com.shopapp.exception.DataNotFoundException;
import com.shopapp.models.*;
import com.shopapp.repositories.OrderDetailRepository;
import com.shopapp.repositories.OrderRepository;
import com.shopapp.repositories.ProductRepository;
import com.shopapp.repositories.UserRepository;
import com.shopapp.response.OrderResponse;
import com.shopapp.dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IService<OrderResponse, OrderDTO, Long>{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse create(OrderDTO orderDTO) throws DataNotFoundException {
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found user with id: "+orderDTO.getUserId()));
        // tạo đối tượng map bằng ModelMapper
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(modelMapper -> modelMapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);
        order.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now(): orderDTO.getShippingDate();
        if( shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be least today !");
        }
        order.setActive(true);
        order.setShippingDate(shippingDate);
        orderRepository.save(order);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO :orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot found product with id: "+cartItemDTO.getProductId()));;
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(product.getPrice()*quantity);
            orderDetail.setNumberOfProducts(quantity);
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    public OrderResponse getById(Long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot found order with id: " + id));
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream().map(
                order -> modelMapper.map(order,OrderResponse.class)).toList();
    }

    @Override
    @Transactional
    public OrderResponse update(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found user with id: "+orderDTO.getUserId()));
        Order existingOrder = modelMapper.map(getById(id),Order.class);
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(modelMapper -> modelMapper.skip(Order::setId));
            modelMapper.map(orderDTO, existingOrder);

            orderRepository.save(existingOrder);
        return modelMapper.map(existingOrder,OrderResponse.class);
    }

    @Override
    @Transactional
    public void delete(Long id) throws DataNotFoundException {
        OrderResponse existingOrder = getById(id);
           existingOrder.setActive(false);
           orderRepository.save(modelMapper.map(existingOrder,Order.class));
    }
    public List<OrderResponse> getOrdersByUserId(Long userId) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot found user with id: "+userId));
        return orderRepository.findByUserId(userId).stream().map(
                order -> modelMapper.map(order,OrderResponse.class)).toList();
    }
    public Page<OrderResponse> getOrderByKeyword(String keyword, Pageable pageable) {
        return orderRepository.searchOrderByKeyword(keyword,pageable).map(
                order -> modelMapper.map(order,OrderResponse.class));
    }
}
