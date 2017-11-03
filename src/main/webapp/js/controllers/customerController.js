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

});