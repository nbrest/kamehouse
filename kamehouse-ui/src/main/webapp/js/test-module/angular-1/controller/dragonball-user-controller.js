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
  
  const self = this;
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
      createDragonBallUser(self.user);
    } else {
      updateDragonBallUser(self.user, self.user.id);
    }
    reset();
  }

  /**
   * Set the user to edit based on the id.
   */
  function edit(id) {
    console.log('id to be edited', id);
    for (const user of self.users) {
      if (user.id === id) {
        self.user = angular.copy(user);
        break;
      }
    }
  }

  /**
   * Set the user to remove based on the id.
   */
  function remove(id) {
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
      //const mainContent = $('#main-content');
      //domUtils.empty(mainContent);
      //domUtils.load(mainContent, '/kame-house/app/view/' + statusCode + '.html');
    }
  }
}]);