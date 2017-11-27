airbnbApp.controller('AllCustomerDetailsController',
function ($scope, $http) {
  console.log('Called the AllCustomerDetailsController');
  $http.get('http://localhost:8080/DynamoProject/rest/getCustomerDetails').success(function(data, status, headers, config) {
    console.log(data)
    $scope.customers = data;
  });
  }
);


airbnbApp.controller('UpdateCustomerController',
    function ($scope, $location, $routeParams,$http){
     console.log('Called the UpdateCustomerController');

     $scope.selectedCustomer = JSON.parse($routeParams.customer);

     $scope.submitUpdateForm = function(){
         if ($scope.customerUpdateForm.$valid) {
                   	    console.log('Called the UpdateCustomerController for updating data');
                          $http({
                                  method  : 'PUT',
                                  url     : 'http://localhost:8080/DynamoProject/rest/updateCustomer',
                                  data    : JSON.stringify({customerId : $scope.selectedCustomer.customerId, firstName : $scope.selectedCustomer.firstName,
                                             lastName : $scope.selectedCustomer.lastName, billingAddress : $scope.selectedCustomer.billingAddress}),  // pass in data as strings
                                  headers : { 'Content-Type': 'application/json' }  // set the headers so angular passing info as form data (not request payload)
                              })
                                  .success(function(data) {
                                      console.log(data);
                                      $scope.message = "Success";
                                      if (!data.success) {
                                      	// if not successful, bind errors to error variables
                                          console.log('Error while calling');
                                      } else {
                                      	// if successful, bind success message to message
                                          $scope.message = "Some error while adding the customer";
                                      }
                                  });
                   alert($scope.message);
                   $location.path('/customerDetails');

                 }
     }


     $scope.cancelUpdateForm = function(){
         $location.path('/customerDetails');
     }
     }
);

airbnbApp.controller('DeleteCustomerController',
function ($scope, $location, $routeParams,$http){
   $scope.customerId = $routeParams.customerId;
 console.log('Called the DeleteCustomerController');
     $http({
            method  : 'DELETE',
            url     : 'http://localhost:8080/DynamoProject/rest/deleteCustomer/'+$scope.customerId,
            data    : '',
            headers : { 'Content-Type': 'application/json' }  // set the headers so angular passing info as form data (not request payload)
          })
          .success(function(data) {
                console.log(data);
                $scope.message = "Success";
                if (!data.success) {
              	// if not successful, bind errors to error variables
                  console.log('Error while calling');
                } else {
                 	// if successful, bind success message to message
                $scope.message = "Some error while adding the customer";
                }
          });

              alert("Deleted customer successfully");
               $location.path('/customerDetails');
}
);



airbnbApp.controller('searchPropertyController',
    function ($scope, $location, $routeParams,$http){
     console.log('Called the Search Property Controller.');
     $scope.isResults  =  false;
     console.log($scope.city);
     $scope.listingDetailIndex = $routeParams.index;
     $scope.info = function (){
        $scope.listingDetail = $scope.listings[$scope.listingDetailIndex];
        $scope.listingDetail = $scope.listings[$scope.listingDetailIndex];
     }
     $scope.search = function(){

       console.log("Inside Search Function.")
       console.log($scope.city);

       $http.get('http://localhost:8080/DynamoProject/rest/getListings/'+$scope.city).success(function(data, status, headers, config) {
         console.log(data)
         $scope.listings = data;
         $scope.isResults = true;
         console.log(JSON.stringify(data));
       });
     };
    }
);

airbnbApp.controller('ViewListingController',function($scope,$routeParams,$http){
    $scope.listingId = $routeParams.listingId;
    console.log("Listing details = "+ $routeParams.listingId);
    console.log("View Listing Controller");
    $http.get('http://localhost:8080/DynamoProject/rest/getListingsDetails/'+$scope.listingId).success(function(data, status, headers, config) {
             console.log(data)
             $scope.listingDetails = data;
             $scope.isResults = true;
             console.log(JSON.stringify(data));
           });
});

airbnbApp.controller('AddListingController',
function($scope,$http) {
  console.log('Called the AddListingController');
  $scope.houseTypes = ["1 BedRoom Apartment","2 BedRoom Apartment","3 BedRoom Apartment"]
  $scope.houseType = "1 BedRoom Apartment";
  $scope.message = "";
  $scope.submitForm = function() {
  			// check to make sure the form is completely valid
  	if ($scope.listingForm.$valid) {
        $http({
                method  : 'POST',
                url     : 'http://localhost:8080/DynamoProject/rest/addListing',
                data    : JSON.stringify({name: $scope.listing.name,
                                          city: $scope.listing.city,
                                          houseType:$scope.houseType,
                                          location : $scope.listing.location,
                                          descriptor: $scope.listing.descriptor,
                                          pinCode: $scope.listing.pinCode,
                                          price: $scope.listing.price,
                                          address : $scope.listing.address}),  // pass in data as strings
                headers : { 'Content-Type': 'application/json' }  // set the headers so angular passing info as form data (not request payload)
            })
                .success(function(data) {
                    console.log(data);
                    $scope.listingForm.message = "Success";
                    $scope.listingForm.message = "Listing added successfully.";
                });
  	}
  }
}
);




