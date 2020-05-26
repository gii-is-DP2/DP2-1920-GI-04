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
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.BeautySolutionRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeautySolutionServiceTests {
	
	protected BeautySolutionService beautySolutionService;
	
	
	// Auxiliar services
	@Mock
	protected PetService petService;
	
	@Mock
	protected VetService vetService;
	
	
	// Main service mock parameters
	@Mock
	protected AuthoritiesService authService;

	@Autowired
	protected BeautySolutionRepository beautySolutionRepository;
	

	// Mock setup
	@BeforeEach
	void setup() {
		
		this.beautySolutionService = new BeautySolutionService(authService, beautySolutionRepository);
		
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
		"A luxurious solution aimed at people that feel comfortable with this title, 1, 100000, 1"
	})
	@DisplayName("Create proper Beauty Solutions")
	void createStandardBeautySolution(String title, int petType, Double price, Integer vetId) {
		BeautySolution solution = this.beautySolutionService.create();
		solution.setTitle(title);
		solution.setType(petService.findPetType(petType));
		solution.setPrice(price);
		solution.setVet(vetService.find(vetId));
		solution = this.beautySolutionService.save(solution);
		assertThat(solution != null && solution.getId() > 0).isTrue();
		
	}
	
	@Test
	@DisplayName("Forbid creating Beauty Solutions with duplicate title and pet type")
	void createDuplicateBeautySolution() {
		
		// Create first valid one
		BeautySolution solution = this.beautySolutionService.create();
		solution.setTitle("Pet bathing");
		solution.setType(petService.findPetType(1));
		solution.setPrice(20.0);
		solution.setVet(vetService.find(1));
		this.beautySolutionService.save(solution);
		assertThat(solution != null && solution.getId() > 0).isTrue();
		
		// Create duplicated one
		BeautySolution solution2 = this.beautySolutionService.create();
		solution2.setTitle("Pet bathing"); /* Same value */
		solution2.setType(petService.findPetType(1)); /* Same value */
		solution2.setPrice(55.5); /* Different value */
		solution2.setVet(vetService.find(2)); /* Different value */
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionService.save(solution2));
		assertThat(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null && (e.getCause().getCause().getMessage().contains("Unique index") || e.getCause().getCause().getMessage().contains("Duplicate entry"))).isTrue();
	}
	
	@Test
	@DisplayName("List enabled Beauty Solutions")
	void testListAllEnabledBeautySolutions() {
		Collection<BeautySolution> beautySolutions = this.beautySolutionService.showBeautySolutionList(null);
		assertThat(beautySolutions.size()).isEqualTo(5);
		Iterator<BeautySolution> iterator = beautySolutions.iterator();
		while(iterator.hasNext()) {
			BeautySolution solution = iterator.next();
			assertThat(solution.isEnabled()).isTrue();
		}
	}
	
	@Test
	@DisplayName("Create and check on list enabled Beauty Solutions")
	void testCreateAndListEnabledBeautySolutions() {
		// Create beauty solution
		BeautySolution solution = this.beautySolutionService.create();
		solution.setTitle("Pet bathing");
		solution.setType(petService.findPetType(1));
		solution.setPrice(20.0);
		solution.setVet(vetService.find(1));

		BeautySolution notEnabledSolution = this.beautySolutionService.create();
		notEnabledSolution.setTitle("Hair curling");
		notEnabledSolution.setType(petService.findPetType(1));
		notEnabledSolution.setPrice(20.0);
		notEnabledSolution.setVet(vetService.find(1));
		
		// Set enabled
		solution.setEnabled(true);
		notEnabledSolution.setEnabled(false);
		
		this.beautySolutionService.save(solution);
		this.beautySolutionService.save(notEnabledSolution);
		assertThat(solution != null && solution.getId() > 0).isTrue();
		assertThat(notEnabledSolution != null && notEnabledSolution.getId() > 0).isTrue();
		
		// Check if they appear
		Collection<BeautySolution> beautySolutions = this.beautySolutionService.showBeautySolutionList(null);
		assertThat(beautySolutions.contains(solution)).isTrue();
		assertThat(beautySolutions.contains(notEnabledSolution)).isFalse();
	}
	
	@Test
	@DisplayName("List all Beauty Solutions")
	void testListAllBeautySolutions() {
		when(this.authService.checkAdminAuth()).thenReturn(true);
		Collection<BeautySolution> beautySolutions = this.beautySolutionService.showBeautySolutionList(null);
		assertThat(beautySolutions.size()).isEqualTo(6);
	}

	@ParameterizedTest
	@CsvSource({
		"1,3",
		"2,1",
		"3,1",
		"4,0"
	})
	@DisplayName("Filter Beauty Solutions by pet type")
	void testFilterBeautySolutions(Integer petTypeId, Integer expectedResults) {
		Collection<BeautySolution> beautySolutions = this.beautySolutionService.showBeautySolutionList(petTypeId);
		assertThat(beautySolutions.size()).isEqualTo(expectedResults);
		Iterator<BeautySolution> iterator = beautySolutions.iterator();
		while(iterator.hasNext()) {
			BeautySolution solution = iterator.next();
			assertThat(solution.getType().getId() == petTypeId).isTrue();
		}
	}
	
	@Test
	@DisplayName("Create and check on filter Beauty Solutions by pet type")
	void testCreateAndFilterBeautySolutions() {
		// Create beauty solution
		BeautySolution solution = this.beautySolutionService.create();
		solution.setTitle("Pet bathing");
		solution.setPrice(20.0);
		solution.setVet(vetService.find(1));

		BeautySolution differentPetTypeSolution = this.beautySolutionService.create();
		differentPetTypeSolution.setTitle("Hair curling");
		differentPetTypeSolution.setPrice(20.0);
		differentPetTypeSolution.setVet(vetService.find(1));
		
		// Set pet type
		solution.setType(petService.findPetType(1));
		differentPetTypeSolution.setType(petService.findPetType(2));
		
		solution = this.beautySolutionService.save(solution);
		differentPetTypeSolution = this.beautySolutionService.save(differentPetTypeSolution);
		assertThat(solution != null && solution.getId() > 0).isTrue();
		assertThat(differentPetTypeSolution != null && differentPetTypeSolution.getId() > 0).isTrue();
		
		// Check if they appear
		Collection<BeautySolution> beautySolutions = this.beautySolutionService.showBeautySolutionList(1);
		assertThat(beautySolutions.contains(solution)).isTrue();
		assertThat(beautySolutions.contains(differentPetTypeSolution)).isFalse();
	}
	

	@Test
	@DisplayName("Edit Beauty Solution")
	void testEditBeautySolution() {
		// Get the solution
		Integer solutionId = 1;
		BeautySolution solution = this.beautySolutionService.find(solutionId);
		assertThat(solution != null).isTrue();
		
		// Modify some fields
		
		solution.setEnabled(false);
		solution.setPrice(200.0);
		solution.setTitle("Pet bathing");
		solution.setVet(this.vetService.find(2));
		
		// Edit the solution
		
		solution = this.beautySolutionService.save(solution);
		
		assertThat(solution != null && solution.getId() == solutionId 
				&& !solution.isEnabled()
				&& solution.getPrice().equals(200.0)
				&& solution.getTitle().equals("Pet bathing")
				&& solution.getVet().getId() == 2)
		.isTrue();
	}
	

	@Test
	@DisplayName("Forbid editing the type of a Beauty Solution")
	void testForbidEditBeautySolutionType() {
		// Get the solution
		BeautySolution solution = this.beautySolutionService.find(1);
		assertThat(solution != null).isTrue();
		assertThat(solution.getType().getId() == 1).isTrue();
		
		// Copy object information
		BeautySolution editData = new BeautySolution();
		editData.setId(solution.getId());
		editData.setEnabled(solution.isEnabled());
		editData.setPrice(solution.getPrice());
		editData.setVet(solution.getVet());
		editData.setTitle("Pet bathing");

		// Modify pet type
		editData.setType(this.petService.findPetType(2));
		
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionService.edit(editData));
		assertThat(e.getMessage()).isEqualTo("beautysolution.error.edittype");
	}
	

	@Test
	@DisplayName("Forbid editing nonexisting Beauty Solution")
	void testForbidEditNewBeautySolution() {
		// Create beauty solution
		BeautySolution solution = this.beautySolutionService.create();
		solution.setTitle("Pet bathing");
		solution.setType(petService.findPetType(1));
		solution.setPrice(20.0);
		solution.setVet(vetService.find(1));
		solution.setId(9999999); /* false id */
		
		// Edit the solution
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionService.edit(solution));
		assertThat(e.getMessage()).isEqualTo("beautysolution.error.notfound");
	}
	
}
