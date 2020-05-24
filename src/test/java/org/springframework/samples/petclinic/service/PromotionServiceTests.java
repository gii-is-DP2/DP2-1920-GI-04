package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.repository.PromotionRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PromotionServiceTests {
	
	protected PromotionService promotionService;
	
	// Auxiliar services
	@Mock
	protected BeautyServiceService beautyServiceService;

	// Main service mock parameters
	@Autowired
	protected PromotionRepository promotionRepository;
	
	// Mock setup
	@BeforeEach
	void setup() {
		this.promotionService = new PromotionService(promotionRepository, beautyServiceService);
		
		BeautyService service = new BeautyService();
		service.setId(1);
		when(this.beautyServiceService.find(1)).thenReturn(service);
	}

	@Test
	@DisplayName("Create Promotion")
	void testCreatePromotion() {
		
		// Create promotion
		Promotion promotion = this.promotionService.create(1);
		promotion.setDiscount(24);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
		promotion.setEndDate(LocalDateTime.now().plus(8, ChronoUnit.DAYS));
		promotion = this.promotionService.save(promotion);

		assertThat(promotion.getId()).isGreaterThan(0);
		assertThat(promotion.getDiscount()).isEqualTo(24);
		
	}

	@Test
	@DisplayName("Forbid create promotion on same service and date")
	void testForbidCreatePromotionOnSameServiceAndDate() {
		
		LocalDateTime start = LocalDateTime.now().plus(1, ChronoUnit.DAYS);
		LocalDateTime end = LocalDateTime.now().plus(8, ChronoUnit.DAYS);
		
		// Create promotion
		Promotion promotion = this.promotionService.create(1);
		promotion.setDiscount(24);
		promotion.setStartDate(start);
		promotion.setEndDate(end);
		promotion = this.promotionService.save(promotion);
		
		// Create promotion with same date and service
		Promotion promotion2 = this.promotionService.create(1);
		promotion2.setDiscount(39);
		promotion2.setStartDate(start);
		promotion2.setEndDate(end);
		
		Throwable e = assertThrows(Throwable.class, () -> this.promotionService.save(promotion2));
		assertThat(e.getMessage()).isEqualTo("promotion.error.overlappeddate");
		
	}

	@Test
	@DisplayName("Forbide create promotion on same service and similar date")
	void testForbidCreatePromotionOnSameServiceAndSimilarDate() {
		
		// Create promotion
		Promotion promotion = this.promotionService.create(1);
		promotion.setDiscount(24);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
		promotion.setEndDate(LocalDateTime.now().plus(8, ChronoUnit.DAYS));
		promotion = this.promotionService.save(promotion);
		
		// Create promotion with similar date and service
		Promotion promotion2 = this.promotionService.create(1);
		promotion2.setDiscount(39);
		promotion2.setStartDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		promotion2.setEndDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));
		
		Throwable e = assertThrows(Throwable.class, () -> this.promotionService.save(promotion2));
		assertThat(e.getMessage()).isEqualTo("promotion.error.overlappeddate");
		
	}

	@Test
	@DisplayName("Forbide create promotion on same service and similar date 2")
	void testForbidCreatePromotionOnSameServiceAndSimilarDate2() {
		
		// Create promotion
		Promotion promotion = this.promotionService.create(1);
		promotion.setDiscount(24);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
		promotion.setEndDate(LocalDateTime.now().plus(8, ChronoUnit.DAYS));
		promotion = this.promotionService.save(promotion);
		
		// Create promotion with similar date and service
		Promotion promotion2 = this.promotionService.create(1);
		promotion2.setDiscount(39);
		promotion.setStartDate(LocalDateTime.now().plus(12, ChronoUnit.HOURS));
		promotion.setEndDate(LocalDateTime.now().plus(9, ChronoUnit.DAYS));
		promotion2.setStartDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		promotion2.setEndDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));
		
		Throwable e = assertThrows(Throwable.class, () -> this.promotionService.save(promotion2));
		assertThat(e.getMessage()).isEqualTo("promotion.error.overlappeddate");
		
	}

	@Test
	@DisplayName("Create and find promotion")
	void testFindPromotion() {
		
		// Store previous number of promotions
		Collection<Promotion> foundPromotions = this.promotionService.findAllServicePromotions(1);
		Integer previousNumber = foundPromotions.size();
		
		// Create promotion
		Promotion promotion = this.promotionService.create(1);
		promotion.setDiscount(24);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
		promotion.setEndDate(LocalDateTime.now().plus(8, ChronoUnit.DAYS));
		promotion = this.promotionService.save(promotion);
		
		Promotion foundPromotion = this.promotionService.findServiceCurrentPromotion(1, LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		assertThat(foundPromotion).isEqualTo(promotion);
		foundPromotions = this.promotionService.findAllServicePromotions(1);
		assertThat(foundPromotions.size() - previousNumber).isEqualTo(1);
		assertThat(foundPromotions).contains(promotion);
		
	}

	@Test
	@DisplayName("Create promotion but not found on selected date")
	void testPromotionNotFound() {
		
		// Store previous number of promotions
		Collection<Promotion> foundPromotions = this.promotionService.findAllServicePromotions(1);
		Integer previousNumber = foundPromotions.size();
		
		// Create promotion
		Promotion promotion = this.promotionService.create(1);
		promotion.setDiscount(24);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
		promotion.setEndDate(LocalDateTime.now().plus(8, ChronoUnit.DAYS));
		promotion = this.promotionService.save(promotion);
		
		Promotion foundPromotion = this.promotionService.findServiceCurrentPromotion(1, LocalDateTime.now().plus(20, ChronoUnit.DAYS));
		assertThat(foundPromotion).isNotEqualTo(promotion);
		foundPromotions = this.promotionService.findAllServicePromotions(1);
		assertThat(foundPromotions.size() - previousNumber).isEqualTo(1);
		assertThat(foundPromotions).contains(promotion);
		
	}
	
	
}
