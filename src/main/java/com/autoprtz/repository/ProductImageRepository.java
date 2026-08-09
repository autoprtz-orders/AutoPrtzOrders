package com.autoprtz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

}