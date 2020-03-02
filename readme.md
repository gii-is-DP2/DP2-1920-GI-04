# Spring PetClinic - Charming Whiskers Program

PetClinic is an existing veterinary platform focused on managing owners, pets, vets and visits on the clinic by that very same name. As means to develop new business lines, the clinic’s manager has decided to enter a new field in a planned and strategic approach, exploring how to offer new services by taking advantage of technological tools and processes that will be integrated into PetClinic. The field of choice was Pet Beauty Care, since it’s expected to become a growing market and has a high-income target audience.

Thus, a new approach to the business was designed by the name of “Charming Whiskers Program”, planning to use the PetClinic platform to manage and develop marketing techniques to incentivize recurrent consumer spending. Some of these techniques will be an online catalog of services, discount vouchers, timed promotions and beauty contests.


## Entities involved

### Beauty Service
A beauty Service offered by PetClinic. For example, “dog haircut” or “cat nail clipping”. It has a fixed price and is given by a certain Vet.
* It's related to PetType.
* It's related to Vet.

### Beauty Service Visit
A visit that an owner books for a beauty service on a certain date and for a certain Pet. Vouchers can be used. The actual priced is stored on booking. It serves as a participation for the current beauty contest of that month, if wanted (the owner can add a photo of the Pet after the visit in order to participate on the contest).
*	It’s related to BeautyService.
*	It’s related to Pet.

### Discount Voucher
Discount voucher that can be given to owners for a variety of reasons, either manually or programmatically, and that can be used when booking a new visit for a beauty service. For example, a 5% discount voucher after a visit, 50% discount voucher for winning a contest…
*	It’s related to BeautyService.
*	It’s related to BeautyServiceVisit.
*	It’s related to WorkshopAttendance.
*	It’s related to Owner.

### Promotion
Discount, during a certain period, on a certain beauty service.
*	It’s related to BeautyService.

### Beauty Contest
Beauty contest that lasts a month (one is held every month), and where a winner is chosen after the end of every month. Owners can enter their pets once for every beauty service visit they make during that month.
*	It’s related to BeautyServiceVisit (winner)

## User Stories

### US01 - Creating Beauty Service
As an administrator  
So that owners can have a catalogue of services to pay for  
I want to add new beauty services to the system.

Details:

*	No two BeautyServices can have the same title and be of the same PetType
*	No BeautyService can have a negative price
*	All BeautyServices need to be assigned to a PetType 
*	No Beauty Service can have a blank title
* Beauty services can be "enabled" or not, being shown in the page to owners and guests only if set as such

Use cases:

* Creating a beauty service with the title 'Grooming' and PetType 'Cat', and it is created successfully. - :heavy_check_mark:
* Creating a beauty service with the title 'Grooming' and PetType 'Dog', and it is created successfully. - :heavy_check_mark:
* Creating a beauty service with the title 'Grooming' and PetType 'Dog', and it is not created since a beauty service with that title and type was just created before. - :x:
* Creating a beauty service with a blank title and any no PetType selected, and it is not created successfully. - :x:

### US02 - Listing Beauty Services
As an user of the system or a guest  
So that I can look up for information about which beauty services can interest me  
I want to be able to list all beauty services provided by PetClinic

Use cases:

* Creating a beauty service with "enabled" set to true, and it appears on the listing. - :heavy_check_mark:
* Creating a beauty service with "enabled" set to false, and it doesn't appear on the listing. - :x:

### US03 - Filtering Beauty Services
As an user of the system or a guest  
So that I can search for the services that interest me more efficiently  
I want to be able to filter beauty services by PetType

Use cases:

* Creating a beauty service with "cat" PetType, and it appears on the filtering set on "cat". - :heavy_check_mark:
* Creating a beauty service with "dog" PetType, and it doesn't appear on the filtering set on "cat". - :x:

### US04 - Editing Beauty Services
As an administrator  
So that I can keep up to date the information displayed on the web about a service of our clinic  
I want to be able to update information of the services on the system

Details:

*	PetType cannot be changed
*	All the creating restrictions are also applied on updating
* If you change the "enabled" property to false, previously booked services remain unaffected even if the date is on the future.

Use cases:

* Edit a beauty service with a given name and enabled set to true, and change it to "Test A" and enabled set to false. It's successful. - :heavy_check_mark:
* Edit a beauty service with a given name, and change it to "Test A" and change the PetType. It's not successful. - :x:
* Edit a beauty service with a given name, and change it to "". It's not successful. - :x:

### US05 - Booking Beauty Service Visit
As an owner  
So that my pet remains beautiful, healthy and happy  
I want to be able to book a visit for a beauty service I’m interested in

Details:

*	You can’t book more than one visit at the same time for the same pet.
*	You can only book visits for tomorrow at the earliest.
*	Visit hours are divided in X minutes intervals (a different amount of time depending on each service).
*	You can’t select a time slot for a visit if the vet that provides that service is already booked on it.

Use Cases:

* Book a beauty service visit for a free timeslot, for a pet you own, for a service of that PetType. It's successful. - :heavy_check_mark:
* Book a beauty service visit for a free timeslot, for a pet you own, for a service of other PetType. It's not successful. - :x:
* Book a beauty service visit for a free timeslot, for a pet you own, for a service of that PetType. It's successful. The, with another owner, do the same for that very same timeslot, and a another service of the same Vet. It's not successful. - :x:

### US06 - Removing Beauty Service Visit
As an owner  
So that I can change my plans after booking a visit  
I want to be able to withdraw my booked beauty service visits

Details:

* You can only remove it if one day before the booked day at the latest.
* You can only remove it if it's yours (of your pet).
* If it's removed and a voucher was used, the voucher remains redeemed and unable to be used again.

Use Cases:

* Book a beauty service visit for a free timeslot for tomorrow, for a pet you own, for a service of that PetType. It's successful. Remove it inmediately after. It's successful. - :heavy_check_mark:
* Book a beauty service visit for a free timeslot of today, for a pet you own, for a service of that PetType. It's successful. Remove it inmediately after. It's not successful. - :x:
* Book a beauty service visit for a free timeslot for tomorrow, for a pet you own, for a service of that PetType. It's successful. Remove it inmediately after logged as another user. It's not successful. - :x:

### US07 - Creating Discount Vouchers
As an administrator  
So that I can make individual offers or solve voucher incidents  
I want to be able to create a custom discount voucher for an owner, with a discount percentage and a description

Details:

* Discount percentage can't be either higher than 100% nor lower than 0%.
* Description can't be empty.

Use Cases:

* Create a discount voucher for an existing owner, with 15% discount percentage and the description "Test.". It's successful. - :heavy_check_mark:
* Create a discount voucher for an existing owner, with -1% discount percentage and the description "Test.". It's not successful. - :x:
* Create a discount voucher for an existing owner, with 15% discount percentage and the description "". It's not successful. - :x:

### US08 - Using Discount Vouchers
As an owner  
So that I can pay less for a service I’m interested in  
I want to redeem one of my vouchers while booking a beauty service.

Details:

*	Only non-used vouchers can be redeemed on a new visit, being marked as used immediately after.
*	No more than one voucher can be used on the same visit.
*	No voucher can be used if there’s a promotion set up for that service on that date (booked date, not booking date).
*	Voucher redemption is done during visit booking.

Use Cases:

* Book a beauty service visit and while doing it, assign a voucher to be redeemed, being it a non used voucher and being there no promotion set for that service and date. It's successful. - :heavy_check_mark:
* Book a beauty service visit and while doing it, assign a voucher to be redeemed, being it an used voucher and being there no promotion set for that service and date. It's not successful. - :x:
* Book a beauty service visit and while doing it, assign a voucher to be redeemed, being it a non used voucher and being there a promotion set for that service and date. It's not successful. - :x:

### US09 - Listing Your Vouchers
As an owner  
So that I can keep track of my vouchers  
I want to display a list of them, with their info and sorted by date

Details:

*	Used vouchers don’t appear.

Use Cases:

* As an administrator, create a voucher for an owner. It's successful. As an owner, list your vouchers. It appears listed. - :heavy_check_mark:
* As an administrator, create a voucher for an owner. It's successful. As an owner, use that voucher on a beauty service visit. Then, list your voucher. It doesn't appear listed. - :x:

### US10 - Listing an User's Vouchers
As an administrator  
So that I can keep track of any user’s vouchers  
I want to display a list of them, with their info and sorted by date

Details:

*	Used vouchers do appear.

Use Cases:

* As an administrator, create a voucher for an owner. It's successful. List that owner's vouchers. It appears listed. - :heavy_check_mark:
* As an administrator, create a voucher for an owner. It's successful. As an owner, use that voucher on a beauty service visit. Then, as an administrator, list that owner's vouchers. It appears listed. - :heavy_check_mark:
* As an administrator, create a voucher for an owner. It's successful. As another owner, list the first owner's vouchers. It's forbidden. - :x:

### US11 - Setting up Promotions
As an administrator  
So that I can make a service more appealing during a certain time  
I want to be able to set up promotions for any service

Details:

*	The end date needs to be after the start date, and neither of them can be past dates
*	The percentage of discount can’t be neither higher than 100 nor lower than 0
*	Percentage of discount, end date, start date and beuty service are all mandatory values
*	The promotion isn’t applied to already booked visits, even if the booked date is during the promotion

Use Cases:

* As an administrator, create a promotion with proper dates and 15% discount. It's successful. As an owner, book a service visit for the same service both during the promotion and not. The difference between prices should be of 15% - :heavy_check_mark:
* As an owner, book a service visit for a certain date. It's successful. As an administrator, create a promotion with proper dates that fall into the previously booked date and 15% discount. It's successful. The price before creating the promotion and after creating the promotion can't be different. - :x:
* As an administrator, create a promotion with past dates and 15% discount. It's not successful. - :x:
* As an administrator, create a promotion with proper dates and 101% discount. It's not successful. - :x:

### US12 - Create Beauty Contests Automatically
As an administrator  
So that I can create monthly contests without having to be constantly on the lookout  
I want the system to automatically set them up a week before its month starts

Details:

*	Trigger for this action is still undecided, it will depend on the technology
*	Beauty contests can’t be created for a past month
*	Beauty contests can’t be created for a month that already has a beauty service created
*	Even though the trigger for the automation will happen a week before, it’s not a problem if beauty services are created sooner than that for any reason (although the system wont support that kind of action on its UI as of now, it can evolve and change in the future)

Use Cases:

* Create a beauty contest for the next month, being it not created yet. It's successful. - :heavy_check_mark:
* Create a beauty contest for the next month, being it already created. It's not successful. - :x:
* Create a beauty contest for the past month, being it not created yet. It's not successful. - :x:

### US13 - Listing Beauty Contests
As an user of the system or a guest  
So that I can browse and see information about past contests  
I want to be able to list all beauty contests sorted by date

Details:

*	Future contests, if already created, are not listed if you’re not an administrator

Use Cases:

* Create a beauty contest for the current month, being it not created yet. It's successful. As an owner, list contests. It appears listed. - :heavy_check_mark:
* Create a beauty contest for the next month, being it not created yet. It's successful. As an owner, list contests. It does not appear - :x:
* Create a beauty contest for the next month, being it not created yet. It's successful. As an administrator, list contests. It appears listed - :heavy_check_mark:

### US14 - Displaying Beauty Contest Information
As an user of the system or a guest  
So that I can see information about a contest, be it past or current  
I want to be able to display detailed information about the selected contest

Details:

*	Month, participants and winner (if elapsed and selected) are shown
*	Future contests, if already created, are not allowed to be displayed if you’re not an administrator

Use Cases:

* Create a beauty contest for the current month, being it not created yet. It's successful. As an owner, try to display it. It's successful. - :heavy_check_mark:
* Create a beauty contest for the next month, being it not created yet. It's successful. As an owner, try to display it. It's not successful - :x:
* Create a beauty contest for the next month, being it not created yet. It's successful. As an administrator, try to display it. It's successful - :heavy_check_mark:

### US15 - Participating on a Beauty Contest
As an owner  
So that me and my pet can have fun participating on a beauty contest  
I want to be able to register a participation by adding a photo after a beauty service visit

Details:

*	You can only add a photo after the visit date has elapsed
*	You cannot add a photo if one has already been added before
*	You cannot add a photo if the date of the contest has already ended (usually, the month of the visit)

Use Cases:

* Register participation with to the current month's contest, having a visit (already past) in this month with no participation registered. It's successful. :heavy_check_mark:
* Register participation with to the current month's contest, having a visit (not yet past) in this month with no participation registered. It's not successful. :x:
* Register participation with to the past month's contest, having a visit (already past) in this month with no participation registered. It's not successful. :x:
* Register participation with to the current month's contest, having a visit (already past) in the past month with no participation registered. It's not successful. :x:
* Register participation with to the current month's contest, having a visit (already past) in this month with a participation already registered. It's not successful. :x:

### US16 - Withdrawing from a Beauty Contest
As an owner  
So that I can be able to change my mind about participating on a contest  
I want to be able to withdraw my participation

Details:

* You can only withdraw your participation if it's yours
* You can only withdraw your participation if the contest has not elapsed

Use Cases:

* Withdraw a participation to the current month's contest of a visit done on that month. It's successful. :heavy_check_mark:
* Withdraw a participation to the past month's contest of a visit done on that month. It's not successful. :x:

### US17 - Choosing a Beauty Contest Winner
As an administrator  
So that contest winners can be chosen depending on the management’s criterion  
I want to be able to choose a winner for a past contest

Details:

*	A winner can’t be chosen if the contest hasn’t ended
*	A winner can’t be chosen for a contest that already has a winner
*	A winner can’t be chosen if it hasn’t signed up in that contest (the beauty service visit took place during that month and a visit photo was uploaded)
*	Only an administrator can choose a winner for a contest

Use Cases:

* Choose a winner of the past month's contest, which has no winner selected yet. It's successful. :heavy_check_mark:
* Choose a winner of the current month's contest, which has no winner selected yet. It's not successful. :x:
* Choose a winner of the next month's contest, which has no winner selected yet. It's not successful. :x:
* Choose a winner of the current month's contest, which already has a winner selected. It's not successful. :x:

### US18 - Give Away a Voucher for each Beauty Service Visit
As an administrator  
So that customers get an incentive to visit our clinic again  
I want 5% discount vouchers to be given away automatically after every beauty service visit valued at 10€ or more

Details:

*	If the beauty service that was booked has a price lower than 10€, no discount voucher is given
*	The voucher is given at the time of the visit
*	Trigger for this action is still undecided, it will depend on the technology

Use Cases:

* As an owner, list your vouchers. Book a beauty service visit valued at 15€. Wait for that visit to take place. After that, list your vouchers again. There has to be only one new voucher valued at 5% discount. It's successful. :heavy_check_mark:
* As an owner, list your vouchers. Book a beauty service visit valued at 9€. Wait for that visit to take place. After that, list your vouchers again. There has to be no new voucher. It's successful(ly not found). :x:


### US19 - Give Away a Voucher for the Winner of a Contest
As an administrator  
So that customers get involved in contests and our services are booked more often  
I want the winner of a contest to earn a 50% discount voucher when selected as such

Details:

*	The voucher is automatically given at the winner selection
*	If the winner could not be selected (because of any of its listed reasons), the voucher is not given either

Use Cases:

* Have various participants on the current month's contest. Wait for the month to end. After that, choose a winner. List that owner's vouchers before and after selecting him as a winner. There must only be one more voucher valued at 50% discount. It's successful. :heavy_check_mark:
* Have various participants on the current month's contest. Wait for the month to end. After that, choose a winner. List another owner's vouchers before and after selecting the first one as a winner. Check if there's any new voucher. There's no new voucher. :x:

### US20 - Set Up Contest Final Week Promotion Automatically
As an administrator  
So that I can engage owners in the contest and have a final push of service purchase at the end of the month  
I want to automatically set up a promotion of 10% at the final natural week of the month

Details:

* It's not applied to previously booked services
*	Trigger for this action is still undecided, it will depend on the technology

Use cases:

* As an owner, book a new beauty service visit the day before the last week of the month. When the last week starts, book that same service again. The second time the price should be 10% less. The promotion is effective. :heavy_check_mark:
* As an owner, book a new beauty service visit two days before the last week of the month. At the next day, book that same service again. The price should be exactly the same. The promotion is not effective. :x:

# Sprint Planning
Sprint | User Story | Asignee
------ | ---------- | -------------
Sprint 2 | US01 | Youssef San José Khamer
Sprint 2 | US02 | Youssef San José Khamer
Sprint 2 | US03 | Youssef San José Khamer
Sprint 2 | US04 | Youssef San José Khamer
Sprint 2 | US05 | Youssef San José Khamer
Sprint 2 | US06 | Youssef San José Khamer
Sprint 2 | US07 | Youssef San José Khamer
Sprint 2 | US08 | Youssef San José Khamer
Sprint 2 | US09 | Youssef San José Khamer
Sprint 2 | US10 | Youssef San José Khamer
Sprint 2 | US11 | Youssef San José Khamer
Sprint 2 | US18 | Youssef San José Khamer
Sprint 2 | US19 | Youssef San José Khamer
Sprint 2 | US20 | Youssef San José Khamer
Sprint 3 | US12 | Youssef San José Khamer
Sprint 3 | US14 | Youssef San José Khamer
Sprint 3 | US15 | Youssef San José Khamer
Sprint 4 | US13 | Youssef San José Khamer
Sprint 4 | US16 | Youssef San José Khamer
Sprint 4 | US17 | Youssef San José Khamer

# License

The Spring PetClinic sample application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

[spring-petclinic]: https://github.com/spring-projects/spring-petclinic
[spring-framework-petclinic]: https://github.com/spring-petclinic/spring-framework-petclinic
[spring-petclinic-angularjs]: https://github.com/spring-petclinic/spring-petclinic-angularjs 
[javaconfig branch]: https://github.com/spring-petclinic/spring-framework-petclinic/tree/javaconfig
[spring-petclinic-angular]: https://github.com/spring-petclinic/spring-petclinic-angular
[spring-petclinic-microservices]: https://github.com/spring-petclinic/spring-petclinic-microservices
[spring-petclinic-reactjs]: https://github.com/spring-petclinic/spring-petclinic-reactjs
[spring-petclinic-graphql]: https://github.com/spring-petclinic/spring-petclinic-graphql
[spring-petclinic-kotlin]: https://github.com/spring-petclinic/spring-petclinic-kotlin
[spring-petclinic-rest]: https://github.com/spring-petclinic/spring-petclinic-rest
