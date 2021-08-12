'use strict';

/**
 * DragonBallUserController.
 * 
 * @author nbrest
 */
angular.module('myApp').controller('dragonBallUserController', [ '$scope', 'dragonBallUserService', '$location', function($scope, dragonBallUserService, $location) {
  
  this.submit = submit;
  this.edit = edit;
  this.remove = remove;
  this.reset = reset;
  
  let self = this;
  this.user = {
    id : null,
    username : '',
    email : '',
    age : 0,
    powerLevel : 0,
    stamina : 0
  };
  this.users = [];
  
  // Fetch dragonBallUsers from backend
  fetchAllDragonBallUsers();

  /**
   * Fetch all DragonBallUsers.
   */
  function fetchAllDragonBallUsers() {
    //console.log("fetchAllDragonBallUsers");
    dragonBallUserService.fetchAllDragonBallUsers()
      .then(
        (data) => self.users = data,
        (errResponse) => {
          console.error('Error while fetching DragonBallUsers');
          redirectToErrorPage(errResponse.status);
        }
    );
  }

  /**
   * Create a DragonBallUser.
   */
  function createDragonBallUser(user) {
    dragonBallUserService.createDragonBallUser(user)
      .then(
        () => fetchAllDragonBallUsers(),
        (errResponse) => {
          console.error('Error while creating DragonBallUser');
          redirectToErrorPage(errResponse.status);
        }
    );
  }

  /**
   * Update a DragonBallUser.
   */
  function updateDragonBallUser(user, id) {
    dragonBallUserService.updateDragonBallUser(user, id)
      .then(
        () => fetchAllDragonBallUsers(),
        (errResponse) => {
          console.error('Error while updating DragonBallUser');
          //console.log(JSON.stringify(errResponse));
          redirectToErrorPage(errResponse.status);
        }
    );
  }

  /**
   * Delete a DragonBallUser.
   */
  function deleteDragonBallUser(id) {
    dragonBallUserService.deleteDragonBallUser(id)
      .then(
        () => fetchAllDragonBallUsers(),
        (errResponse) => {
          console.error('Error while deleting DragonBallUser');
          redirectToErrorPage(errResponse.status);
        }
    );
  }

  /**
   * Submit function to create or update a DragonBallUser.
   */
  function submit() {
    if (self.user.id === null) {
      //console.log('Saving New DragonBallUser', self.user);
      createDragonBallUser(self.user);
    } else {
      updateDragonBallUser(self.user, self.user.id);
      //console.log('DragonBallUser updated with id ', self.user.id);
    }
    reset();
  }

  /**
   * Set the user to edit based on the id.
   */
  function edit(id) {
    console.log('id to be edited', id);
    for (let i = 0; i < self.users.length; i++) {
      if (self.users[i].id === id) {
        self.user = angular.copy(self.users[i]);
        break;
      }
    }
  }

  /**
   * Set the user to remove based on the id.
   */
  function remove(id) {
    //console.log('id to be deleted', id);
    if (self.user.id === id) { //clean form if the user to be deleted is shown there.
      reset();
    }
    deleteDragonBallUser(id);
  }

  /**
   * Reset the user view.
   */
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
  
  /**
   * Redirect to error page based on the status code.
   */
  function redirectToErrorPage(statusCode) {
    if (!isEmpty(statusCode)) {
      $location.path('/' + statusCode);
      // To display the error page content without redirecting use the following, but it
      // breaks the navigation with angular because I need to reload the page to go back to
      // where I was when the error was produced.
      //var mainContent = $('#main-content');
      //domUtils.empty(mainContent);
      //domUtils.load(mainContent, '/kame-house/app/view/' + statusCode + '.html');
    }
  }
}]);