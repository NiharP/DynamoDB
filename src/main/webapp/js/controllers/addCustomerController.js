airbnbApp.controller('AddCustomerController',
function($scope,$http) {
  console.log('Called the AddCustomerController');
  $scope.message = "Added successfully";
  $scope.submitForm = function() {
  			// check to make sure the form is completely valid
  	if ($scope.customerForm.$valid) {
        $http({
                method  : 'POST',
                url     : 'http://localhost:8080/DynamoProject/rest/addCustomer',
                data    : JSON.stringify({customerId : $scope.customer.customerId, firstName : $scope.customer.firstName,
                           lastName : $scope.customer.lastName, billingAddress : $scope.customer.address}),  // pass in data as strings
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
  	}
  }
}
);

