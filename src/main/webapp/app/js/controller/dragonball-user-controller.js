'use strict';

angular.module('myApp').controller('dragonBallUserController', [ '$scope', 'dragonBallUserService', '$location', function($scope, dragonBallUserService, $location) {
  var self = this;
  self.user = {
    id : null,
    username : '',
    email : '',
    age : 0,
    powerLevel : 0,
    stamina : 0
  };
  self.users = [];

  self.submit = submit;
  self.edit = edit;
  self.remove = remove;
  self.reset = reset;

  // Set CSRF security object
  self.csrf = {};
  self.csrf.token = getCsrfToken();
  self.csrf.header = getCsrfHeader();
  
  // Fetch dragonBallUsers from backend
  fetchAllDragonBallUsers();

  function fetchAllDragonBallUsers() {
    //console.log("fetchAllDragonBallUsers");
    dragonBallUserService.fetchAllDragonBallUsers()
      .then(
        function(d) {
          self.users = d;
        },
        function(errResponse) {
          console.error('Error while fetching DragonBallUsers');
          if (errResponse.status == 403) {
            //$location.url('/403');
            $location.path('/403');
          }
        }
    );
  }

  function createDragonBallUser(user) {
    dragonBallUserService.createDragonBallUser(user, self.csrf)
      .then(
        fetchAllDragonBallUsers,
        function(errResponse) {
          console.error('Error while creating DragonBallUser');
          if (errResponse.status == 403) {
            //$location.url('/403');
            $location.path('/403');
          }
        }
    );
  }

  function updateDragonBallUser(user, id) {
    dragonBallUserService.updateDragonBallUser(user, id, self.csrf)
      .then(
        fetchAllDragonBallUsers,
        function(errResponse) {
          console.error('Error while updating DragonBallUser');
          if (errResponse.status == 403) {
            //$location.url('/403');
            $location.path('/403');
          }
        }
    );
  }

  function deleteDragonBallUser(id) {
    dragonBallUserService.deleteDragonBallUser(id, self.csrf)
      .then(
        fetchAllDragonBallUsers,
        function(errResponse) {
          console.error('Error while deleting DragonBallUser');
          if (errResponse.status == 403) {
            //$location.url('/403');
            $location.path('/403');
          }
        }
    );
  }

  function submit() {
    if (self.user.id === null) {
      console.log('Saving New DragonBallUser', self.user);
      createDragonBallUser(self.user);
    } else {
      updateDragonBallUser(self.user, self.user.id);
      console.log('DragonBallUser updated with id ', self.user.id);
    }
    reset();
  }

  function edit(id) {
    console.log('id to be edited', id);
    for (var i = 0; i < self.users.length; i++) {
      if (self.users[i].id === id) {
        self.user = angular.copy(self.users[i]);
        break;
      }
    }
  }

  function remove(id) {
    console.log('id to be deleted', id);
    if (self.user.id === id) { //clean form if the user to be deleted is shown there.
      reset();
    }
    deleteDragonBallUser(id);
  }

  function reset() {
    self.user = {
      id : null,
      username : '',
      email : '',
      age : 0,
      powerLevel : 0,
      stamina : 0
    };
    $scope.myForm.$setPristine(); //reset Form
  }

  function getCsrfToken() {
    var token = $("meta[name='_csrf']").attr("content");
    //console.log("getCsrfToken: " + token);
    return token;
  }

  function getCsrfHeader() {
    var header = $("meta[name='_csrf_header']").attr("content");
    //console.log("getCsrfHeader: " + header);
    return header;
  }
} ]);