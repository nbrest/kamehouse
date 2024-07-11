'use strict';

/**
 * DragonBallUserController.
 * 
 * @author nbrest
 */
angular.module('myApp').controller('dragonBallUserController', [ '$scope', 'dragonBallUserService', '$location', function(this: any, $scope, dragonBallUserService, $location) {
  
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
          const errorMessage = 'Error while fetching DragonBallUsers';
          handleApiErrorResponse(errorMessage, errResponse);
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
          const errorMessage = 'Error while creating DragonBallUser';
          handleApiErrorResponse(errorMessage, errResponse);
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
          const errorMessage = 'Error while updating DragonBallUser';
          handleApiErrorResponse(errorMessage, errResponse);
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
          const errorMessage = 'Error while deleting DragonBallUser';
          handleApiErrorResponse(errorMessage, errResponse);
        }
    );
  }

  /** Display api error */
  function handleApiErrorResponse(errorMessage, errResponse) {
    if (!kameHouse.core.isEmpty(errResponse.data) && !kameHouse.core.isEmpty(errResponse.data.message)) {
      errorMessage = errorMessage + " : " + errResponse.data.message;
    }
    kameHouse.logger.error(errorMessage, null);
    kameHouse.plugin.modal.basicModal.setHtml(errorMessage);
    kameHouse.plugin.modal.basicModal.appendHtml(kameHouse.util.dom.getBr());
    kameHouse.plugin.modal.basicModal.appendHtml(kameHouse.util.dom.getBr());
    kameHouse.plugin.modal.basicModal.appendHtml(createBackButton());
    kameHouse.plugin.modal.basicModal.open();
    redirectToErrorPage(errResponse.status);
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
    kameHouse.logger.info('id to be edited: ' + id, null);
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
    if (!kameHouse.core.isEmpty(statusCode)) {
      $location.path('/' + statusCode);
      // To display the error page content without redirecting use the following, but it
      // breaks the navigation with angular because I need to reload the page to go back to
      // where I was when the error was produced.
    }
  }

  /**
   * Create a button to go back to the previous page.
   */
  function createBackButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "btn go-back-btn-kh",
      },
      mobileClass: null,
      backgroundImg: null,
      html: "Back",
      data: null,
      click: () => { 
        history.back(); 
        kameHouse.plugin.modal.basicModal.close(); 
      }
    });
  }
}]);