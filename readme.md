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
*	No two BeautyServices can have the same title and be of the same PetType
*	No BeautyService can have a negative price
*	All BeautyServices need to be assigned to a PetType 
*	No Beauty Service can have a blank title

### US02 - Listing Beauty Services
As an user of the system or a guest
So that I can look up for information about which beauty services can interest me
I want to be able to list all beauty services provided by PetClinic

### US03 - Filtering Beauty Services
As an user of the system or a guest
So that I can search for the services that interest me more efficiently
I want to be able to filter beauty services by PetType

### US04 - Editing Beauty Services
As an administrator
So that I can keep up to date the information displayed on the web about a service of our clinic
I want to be able to update information of the services on the system
*	PetType cannot be changed
*	All the creating restrictions are also applied on updating

### US05 - Booking Beauty Service Visit
As an owner
So that my pet remains beautiful, healthy and happy
I want to be able to book a visit for a beauty service I’m interested in
*	You can’t book more than one visit at the same time for the same pet
*	You can only book visits for tomorrow at the earliest
*	Visit hours are divided in X minutes intervals (a different amount of time depending on each service)
*	You can’t select a time slot for a visit if the vet that provides that service is already booked on it.

### US06 - Removing Beauty Service Visit
As an owner
So that I can change my plans after booking a visit
I want to be able to withdraw my booked beauty service visits
* You can only remove it if one day before the booked day at the latest
* You can only remove it if it's yours (of your pet)
* If it's removed and a voucher was used, the voucher remains redeemed and unable to be used again

### US07 - Creating Discount Vouchers
As an administrator
So that I can make individual offers or solve voucher incidents
I want to be able to create a custom discount voucher for an owner, with a discount percentage and a description
* Discount percentage can't be either higher than 100% nor lower than 0%
* Description can't be empty

### US08 - Using Discount Vouchers
As an owner
So that I can pay less for a service I’m interested in
I want to redeem one of my vouchers while booking a beauty service.

*	Only non-used vouchers can be redeemed on a new visit, being marked as used immediately after.
*	No more than one voucher can be used on the same visit.
*	No voucher can be used if there’s a promotion set up for that service on that date (booked date, not booking date).
*	Voucher redemption is done during visit booking

### US09 - Listing Your Vouchers
As an owner
So that I can keep track of my vouchers
I want to display a list of them, with their info and sorted by date
*	Used vouchers don’t appear

### US10 - Listing an User's Vouchers
As an administrator
So that I can keep track of any user’s vouchers
I want to display a list of them, with their info and sorted by date
*	Used vouchers do appear

### US11 - Setting up Promotions
As an administrator
So that I can make a service more appealing during a certain time
I want to be able to set up promotions for any service
*	The end date needs to be after the start date, and neither of them can be past dates
*	The percentage of discount can’t be neither higher than 100 nor lower than 0
*	Percentage of discount, end date, start date and beuty service are all mandatory values
*	The promotion isn’t applied to already booked visits, even if the booked date is during the promotion

### US12 - Create Beauty Contests Automatically
As an administrator
So that I can create monthly contests without having to be constantly on the lookout
I want the system to automatically set them up a week before its month starts
*	Trigger for this action is still undecided, it will depend on the technology
*	Beauty contests can’t be created for a past month
*	Beauty contests can’t be created for a month that already has a beauty service created
*	Even though the trigger for the automation will happen a month before, it’s not a problem if beauty services are created sooner than that for any reason (although the system wont support it on its UI as of now, it can evolve and change in the future)

### US13 - Listing Beauty Contests
As an user of the system or a guest
So that I can browse and see information about pasts contests
I want to be able to list all beauty contests sorted by date
*	Future contests, if already created, are not listed if you’re not an administrator

### US14 - Displaying Beauty Contest Information
As an user of the system or a guest
So that I can see information about a contest, be it past or current
I want to be able to display detailed information about the selected contest
*	Moth, participants and winner (if elapsed and selected) are shown
*	Future contests, if already created, are not allowed to be displayed if you’re not an administrator

### US15 - Participating on a Beauty Contest
As an owner
So that me and my pet can have fun participating on a beauty contest
I want to be able to register a participation by adding a photo after a beauty service visit
*	You can only add a photo after the visit date has elapsed
*	You cannot add a photo if one has already been added before
*	You cannot add a photo if the date of the contest has already ended (usually, the month of the visit)

### US16 - Withdrawing from a Beauty Contest
As an owner
So that I can be able to change my mind about participating on a contest
I want to be able to withdraw my participation
* You can only withdraw your participation if it's yours
* You can only withdraw your participation if the contest has not elapsed

### US17 - Choosing a Beauty Contest Winner
As an administrator
So that contest winners can be chosen depending on the management’s criterion
I want to be able to choose a winner for a past contest
*	A winner can’t be chosen if the contest hasn’t ended
*	A winner can’t be chosen for a contest that already has a winner
*	A winner can’t be chosen if it hasn’t signed up in that contest (the beauty service visit took place during that month and a visit photo was uploaded)
*	Only an administrator can choose a winner for a contest

### US18 - Give Away a Voucher for each Beauty Service Visit
As an administrator
So that customers get an incentive to visit our clinic again
I want 5% discount vouchers to be given away automatically after every beauty service visit valued at 10€ or more
*	If the beauty service that was booked has a price lower than 10€, no discount voucher is given
*	The voucher is given at the time of the visit
*	Trigger for this action is still undecided, it will depend on the technology

### US19 - Give Away a Voucher for the Winner of a Contest
As an administrator
So that customers get involved in contests and our services are booked more often
I want the winner of a contest to earn a 50% discount voucher when selected as such
*	The voucher is automatically given at the winner selection
*	If the winner could not be selected (because of any of its listed reasons), the voucher is not given either

### US20 - Set Up Contest Final Week Promotion Automatically
As an administrator
So that I can engage owners in the contest and have a final push of service purchase at the end of the month
I want to automatically set up a promotion of 10% at the final natural week of the month
* It's not applied to previously booked services
*	Trigger for this action is still undecided, it will depend on the technology


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
