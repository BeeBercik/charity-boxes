package com.sii.charityBoxes.repositories;

import com.sii.charityBoxes.model.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
}
