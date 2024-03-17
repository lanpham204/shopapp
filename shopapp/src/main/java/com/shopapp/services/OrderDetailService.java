package com.shopapp.services;

import com.shopapp.exception.DataNotFoundException;
import com.shopapp.models.Order;
import com.shopapp.models.OrderDetail;
import com.shopapp.models.Product;
import com.shopapp.repositories.OrderDetailRepository;
import com.shopapp.repositories.OrderRepository;
import com.shopapp.repositories.ProductRepository;
import com.shopapp.response.OrderDetailResponse;
import com.shopapp.dtos.OrderDetailDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IService<OrderDetailResponse, OrderDetailDTO,Long> {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Override
    public OrderDetailResponse create(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found order with id: "+orderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found product with id: "+orderDetailDTO.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .price(orderDetailDTO.getPrice())
                .color(orderDetailDTO.getColor())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .order(existingOrder)
                .product(existingProduct)
                .build();
        orderDetailRepository.save(orderDetail);
        return modelMapper.map(orderDetail, OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse getById(Long id) throws DataNotFoundException {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot found order detail with id: "+id));
        return modelMapper.map(existingOrderDetail,OrderDetailResponse.class);
    }

    @Override
    public List<OrderDetailResponse> getAll() {
        return orderDetailRepository.findAll()
                .stream().map(orderDetail -> modelMapper.map(orderDetail,OrderDetailResponse.class)).toList();
    }

    @Override
    @Transactional
    public OrderDetailResponse update(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found order with id: "+orderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot found product with id: "+orderDetailDTO.getProductId()));
        OrderDetail existingOrderDetail = modelMapper.map(getById(id),OrderDetail.class);
        OrderDetail orderDetail = OrderDetail.builder()
                .price(orderDetailDTO.getPrice())
                .color(orderDetailDTO.getColor())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .order(existingOrder)
                .product(existingProduct)
                .build();
        orderDetail.setId(existingOrderDetail.getId());
        orderDetailRepository.save(orderDetail);
        return modelMapper.map(orderDetail,OrderDetailResponse.class);
    }

    @Override
    @Transactional

    public void delete(Long id) throws DataNotFoundException {
            orderDetailRepository.deleteById(id);
    }
    public List<OrderDetailResponse> getAllByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId)
                .stream().map(orderDetail -> modelMapper.map(orderDetail,OrderDetailResponse.class)).toList();
    }
}
