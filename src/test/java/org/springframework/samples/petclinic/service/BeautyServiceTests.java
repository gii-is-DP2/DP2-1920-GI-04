package org.springframework.samples.petclinic.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.BeautyServiceRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeautyServiceTests {
	
	protected BeautyServiceService beautyServiceService;
	
	
	// Auxiliar services
	@Mock
	protected PetService petService;
	
	@Mock
	protected VetService vetService;
	
	
	// Main service mock parameters
	@Mock
	protected AuthoritiesService authService;

	@Autowired
	protected BeautyServiceRepository beautyServiceRepository;
	

	// Mock setup
	@BeforeEach
	void setup() {
		
		this.beautyServiceService = new BeautyServiceService(authService, beautyServiceRepository);
		
		// Mock PetType 1
		PetType type = new PetType();
		type.setId(1);
		type.setName("cat");
		when(this.petService.findPetType(1)).thenReturn(type);
		
		// Mock PetType 2
		PetType type2 = new PetType();
		type2.setId(2);
		type2.setName("dog");
		when(this.petService.findPetType(2)).thenReturn(type2);
		
		// Mock Vet 1
		Vet vet = new Vet();
		vet.setId(1);
		vet.setFirstName("James");
		vet.setLastName("Carter");
		when(this.vetService.find(1)).thenReturn(vet);
		
		// Mock Vet 2
		Vet vet2 = new Vet();
		vet2.setId(2);
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");
		when(this.vetService.find(2)).thenReturn(vet2);
	}
	
	
	@ParameterizedTest
	@CsvSource({
		"Pet bathing, 1, 20, 1",
		"Pet bathing, 2, 0, 1",
		"A luxurious service aimed at people that feel comfortable with this title, 1, 100000, 1"
	})
	@DisplayName("Create proper Beauty Services")
	void createStandardBeautyService(String title, int petType, Double price, Integer vetId) {
		BeautyService service = this.beautyServiceService.create();
		service.setTitle(title);
		service.setType(petService.findPetType(petType));
		service.setPrice(price);
		service.setVet(vetService.find(vetId));
		service = this.beautyServiceService.save(service);
		assertThat(service != null && service.getId() > 0).isTrue();
		
	}
	
	@Test
	@DisplayName("Forbid creating Beauty Services with duplicate title and pet type")
	void createDuplicateBeautyService() {
		
		// Create first valid one
		BeautyService service = this.beautyServiceService.create();
		service.setTitle("Pet bathing");
		service.setType(petService.findPetType(1));
		service.setPrice(20.0);
		service.setVet(vetService.find(1));
		this.beautyServiceService.save(service);
		assertThat(service != null && service.getId() > 0).isTrue();
		
		// Create duplicated one
		BeautyService service2 = this.beautyServiceService.create();
		service2.setTitle("Pet bathing"); /* Same value */
		service2.setType(petService.findPetType(1)); /* Same value */
		service2.setPrice(55.5); /* Different value */
		service2.setVet(vetService.find(2)); /* Different value */
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceService.save(service2));
		assertThat(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null && (e.getCause().getCause().getMessage().contains("Unique index") || e.getCause().getCause().getMessage().contains("Duplicate entry"))).isTrue();
	}
	
	@Test
	@DisplayName("List enabled Beauty Services")
	void testListAllEnabledBeautyServices() {
		Collection<BeautyService> beautyServices = this.beautyServiceService.showBeautyServiceList(null);
		assertThat(beautyServices.size()).isEqualTo(5);
		Iterator<BeautyService> iterator = beautyServices.iterator();
		while(iterator.hasNext()) {
			BeautyService service = iterator.next();
			assertThat(service.isEnabled()).isTrue();
		}
	}
	
	@Test
	@DisplayName("Create and check on list enabled Beauty Services")
	void testCreateAndListEnabledBeautyServices() {
		// Create beauty service
		BeautyService service = this.beautyServiceService.create();
		service.setTitle("Pet bathing");
		service.setType(petService.findPetType(1));
		service.setPrice(20.0);
		service.setVet(vetService.find(1));

		BeautyService notEnabledService = this.beautyServiceService.create();
		notEnabledService.setTitle("Hair curling");
		notEnabledService.setType(petService.findPetType(1));
		notEnabledService.setPrice(20.0);
		notEnabledService.setVet(vetService.find(1));
		
		// Set enabled
		service.setEnabled(true);
		notEnabledService.setEnabled(false);
		
		this.beautyServiceService.save(service);
		this.beautyServiceService.save(notEnabledService);
		assertThat(service != null && service.getId() > 0).isTrue();
		assertThat(notEnabledService != null && notEnabledService.getId() > 0).isTrue();
		
		// Check if they appear
		Collection<BeautyService> beautyServices = this.beautyServiceService.showBeautyServiceList(null);
		assertThat(beautyServices.contains(service)).isTrue();
		assertThat(beautyServices.contains(notEnabledService)).isFalse();
	}
	
	@Test
	@DisplayName("List all Beauty Services")
	void testListAllBeautyServices() {
		when(this.authService.checkAdminAuth()).thenReturn(true);
		Collection<BeautyService> beautyServices = this.beautyServiceService.showBeautyServiceList(null);
		assertThat(beautyServices.size()).isEqualTo(6);
	}

	@ParameterizedTest
	@CsvSource({
		"1,3",
		"2,1",
		"3,1",
		"4,0"
	})
	@DisplayName("Filter Beauty Services by pet type")
	void testFilterBeautyServices(Integer petTypeId, Integer expectedResults) {
		Collection<BeautyService> beautyServices = this.beautyServiceService.showBeautyServiceList(petTypeId);
		assertThat(beautyServices.size()).isEqualTo(expectedResults);
		Iterator<BeautyService> iterator = beautyServices.iterator();
		while(iterator.hasNext()) {
			BeautyService service = iterator.next();
			assertThat(service.getType().getId() == petTypeId).isTrue();
		}
	}
	
	@Test
	@DisplayName("Create and check on filter Beauty Services by pet type")
	void testCreateAndFilterBeautyServices() {
		// Create beauty service
		BeautyService service = this.beautyServiceService.create();
		service.setTitle("Pet bathing");
		service.setPrice(20.0);
		service.setVet(vetService.find(1));

		BeautyService differentPetTypeService = this.beautyServiceService.create();
		differentPetTypeService.setTitle("Hair curling");
		differentPetTypeService.setPrice(20.0);
		differentPetTypeService.setVet(vetService.find(1));
		
		// Set pet type
		service.setType(petService.findPetType(1));
		differentPetTypeService.setType(petService.findPetType(2));
		
		service = this.beautyServiceService.save(service);
		differentPetTypeService = this.beautyServiceService.save(differentPetTypeService);
		assertThat(service != null && service.getId() > 0).isTrue();
		assertThat(differentPetTypeService != null && differentPetTypeService.getId() > 0).isTrue();
		
		// Check if they appear
		Collection<BeautyService> beautyServices = this.beautyServiceService.showBeautyServiceList(1);
		assertThat(beautyServices.contains(service)).isTrue();
		assertThat(beautyServices.contains(differentPetTypeService)).isFalse();
	}
	

	@Test
	@DisplayName("Edit Beauty Service")
	void testEditBeautyService() {
		// Get the service
		Integer serviceId = 1;
		BeautyService service = this.beautyServiceService.find(serviceId);
		assertThat(service != null).isTrue();
		
		// Modify some fields
		
		service.setEnabled(false);
		service.setPrice(200.0);
		service.setTitle("Pet bathing");
		service.setVet(this.vetService.find(2));
		
		// Edit the service
		
		service = this.beautyServiceService.save(service);
		
		assertThat(service != null && service.getId() == serviceId 
				&& !service.isEnabled()
				&& service.getPrice().equals(200.0)
				&& service.getTitle().equals("Pet bathing")
				&& service.getVet().getId() == 2)
		.isTrue();
	}
	

	@Test
	@DisplayName("Forbid editing the type of a Beauty Service")
	void testForbidEditBeautyServiceType() {
		// Get the service
		BeautyService service = this.beautyServiceService.find(1);
		assertThat(service != null).isTrue();
		assertThat(service.getType().getId() == 1).isTrue();
		
		// Copy object information
		BeautyService editData = new BeautyService();
		editData.setId(service.getId());
		editData.setEnabled(service.isEnabled());
		editData.setPrice(service.getPrice());
		editData.setVet(service.getVet());
		editData.setTitle("Pet bathing");

		// Modify pet type
		editData.setType(this.petService.findPetType(2));
		
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceService.edit(editData));
		assertThat(e.getMessage()).isEqualTo("beautyservice.error.edittype");
	}
	

	@Test
	@DisplayName("Forbid editing nonexisting Beauty Service")
	void testForbidEditNewBeautyService() {
		// Create beauty service
		BeautyService service = this.beautyServiceService.create();
		service.setTitle("Pet bathing");
		service.setType(petService.findPetType(1));
		service.setPrice(20.0);
		service.setVet(vetService.find(1));
		service.setId(9999999); /* false id */
		
		// Edit the service
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceService.edit(service));
		assertThat(e.getMessage()).isEqualTo("beautyservice.error.notfound");
	}
	
}
