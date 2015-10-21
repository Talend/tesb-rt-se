(function() {
    var app = angular.module('ebook', ['ngResource']);
    app.controller('BookController', function($scope, $http, $resource) {
        $scope.book = {};
        $scope.idSelectedBook = null;
    	$scope.reload = function() {
            $http.get("/cxf/ebook")
            .success(function (response) {$scope.books = response.book;});
         };
    
         $scope.select = function(bookId) {
             $scope.idSelectedBook = bookId;
             var Book = $resource('/cxf/ebook/:id', {id:'@id'});
             var book = Book.get({id:bookId}, function() {
                 $scope.book = book.book;
             });
             
          };
          
          $scope.send = function(bookId, recipient) {
        	  var Book = $resource('/cxf/ebook/:id', {id:'@id'});
              var result = Book.post({id:bookId}, function() {
                  alert(result);
              });
          }
          
        $scope.reload();
    });
})();
