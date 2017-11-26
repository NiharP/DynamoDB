
var airbnbAppConfig = function($routeProvider) {
  console.log('airbnb app has been loaded');
  $routeProvider
    .when('/searchProperty', {
          controller: 'searchPropertyController',
          templateUrl: 'views/searchProperty.html'
        })
    .when('/addListing', {
      controller: 'AddListingController',
      templateUrl: 'views/addListing.html'
    })
    .when('/customerDetails', {
            controller: 'AllCustomerDetailsController',
            templateUrl: 'views/customerDetails.html'
    })
    .when('/updateCustomer/:customer',{
      controller: 'UpdateCustomerController',
      templateUrl: 'views/updateCustomer.html'
    })
   .when('/deleteCustomer/:customerId',{
        controller: 'DeleteCustomerController',
         templateUrl: 'views/customerDetails.html'
      });
};

var airbnbApp = angular.module('airbnbApp', []).config(airbnbAppConfig);
