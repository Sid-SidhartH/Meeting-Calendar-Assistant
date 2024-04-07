package com.example.withDatabase.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.withDatabase.entity.PincodeLocation;
@Repository
public interface PincodeLocationRepository extends JpaRepository<PincodeLocation,Integer> {

}