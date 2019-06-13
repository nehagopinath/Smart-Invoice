"use strict";

const app = angular.module('idpModule', ['ui.bootstrap']);

// Fix for unhandled rejections bug.
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

app.controller('IdpController', function($http, $location, $uibModal) {
    const idpApp = this;

    const apiBaseURL = "/api/example/";
    let peers = [];

    //Gets node name
    $http.get(apiBaseURL + "me").then((response) => idpApp.thisNode = response.data.me);

    //Gets node peers
    $http.get(apiBaseURL + "peers").then((response) => peers = response.data.peers);

    //Opens modal to enter Transaction data
    idpApp.openModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'idpAppModal.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                idpApp: () => idpApp,
                apiBaseURL: () => apiBaseURL,
                peers: () => peers
            }
        });
        modalInstance.result.then(() => {}, () => {});
    };


         //Opens modal to enter Transfer data
       idpApp.openTransfer = () => {
            const modalTransfer = $uibModal.open({
                templateUrl: 'idpAppModalTransfer.html',
                controller: 'TransferCtrl',
                controllerAs: 'modalTransfer',
                resolve: {
                    idpApp: () => idpApp,
                    apiBaseURL: () => apiBaseURL,
                    peers: () => peers
                }
            });

            modalTransfer.result.then(() => {}, () => {});
        };

         //Opens modal to enter data for Cash issuing
        idpApp.openIssueCash = () => {
                    const modalIssueCash = $uibModal.open({
                        templateUrl: 'idpAppIssueCash.html',
                        controller: 'ModalIssueCashCtrl',
                        controllerAs: 'modalIssueCash',
                        resolve: {
                            idpApp: () => idpApp,
                            apiBaseURL: () => apiBaseURL,
                            peers: () => peers
                        }
                    });
                     modalIssueCash.result.then(() => {}, () => {});
                  };

         //Opens modal to enter data for Confirming Transfer from Buyer side
        idpApp.openConfirmTransfer = () => {
                    const modalConfirm = $uibModal.open({
                        templateUrl: 'idpAppConfirmTransfer.html',
                        controller: 'ConfirmCtrl',
                        controllerAs: 'modalConfirm',
                        resolve: {
                            idpApp: () => idpApp,
                            apiBaseURL: () => apiBaseURL,
                            peers: () => peers
                        }
                    });

                    modalConfirm.result.then(() => {}, () => {});
                };
            //Opens modal to enter Clearing house confirmation
             idpApp.openClearTransfer = () => {
                               const modalClear = $uibModal.open({
                                   templateUrl: 'idpAppClear.html',
                                   controller: 'ModalClearCtrl',
                                   controllerAs: 'modalClear',
                                   resolve: {
                                       idpApp: () => idpApp,
                                       apiBaseURL: () => apiBaseURL,
                                       peers: () => peers
                                   }
                               });

                               modalClear.result.then(() => {}, () => {});
                           };
    //Gets transactions to display them on screen
    idpApp.getTransactions = () => $http.get(apiBaseURL + "transactions")
        .then((response) => idpApp.transactions = Object.keys(response.data)
            .map((key) => response.data[key].state.data)
            .reverse());
     //Gets transfers to display them on screen
     idpApp.getTransfers = () => $http.get(apiBaseURL + "transfers")
             .then((response) => idpApp.transfers = Object.keys(response.data)
                 .map((key) => response.data[key].state.data)
                 .reverse());
       //Gets Cash statements to display them on screen
       idpApp.getCash = () => $http.get(apiBaseURL + "cash")
               .then((response) => idpApp.cash = Object.keys(response.data)
                     .map((key) => response.data[key].state.data)
                      .reverse());

    idpApp.getTransactions();
    idpApp.getTransfers();
    idpApp.getCash();

});

app.controller('ModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalInstance = this;

    modalInstance.peers = peers;
    modalInstance.form = {};
    modalInstance.formError = false;

        // Validates and sends Transaction.
        modalInstance.create = function validateAndSendTransaction() {
            if (modalInstance.form.value <= 0) {
                modalInstance.formError = true;
            } else {
                modalInstance.formError = false;
                $uibModalInstance.close();

                let CREATE_TRANSACTIONS_PATH = apiBaseURL + "create-transaction"

                let createTransactionData = $.param({
                    cusipValue: modalInstance.form.cusip,
                    transactionAssetName : modalInstance.form.assetName,
                    transactionPurchaseCost : modalInstance.form.purchaseCost
                });

                let createTransactionHeaders = {
                    headers : {
                        "Content-Type": "application/x-www-form-urlencoded"
                    }
                };

                // Create Transaction and handles success / fail responses.
                $http.post(CREATE_TRANSACTIONS_PATH, createTransactionData, createTransactionHeaders).then(
                    modalInstance.displayMessage,
                    modalInstance.displayMessage
                );
            }
        };

    modalInstance.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create Transaction modal dialogue.
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the Transaction.
    function invalidFormInput() {
        return (modalInstance.form.cusip === undefined);
    }
});


// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});

app.controller('TransferCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalTransfer = this;

    modalTransfer.peers = peers;
    modalTransfer.form = {};
    modalTransfer.formError = false;

        // Validates and sends Transfer.
        modalTransfer.create = function validateAndSendTransaction() {
            if (modalTransfer.form.value <= 0) {
                modalTransfer.formError = true;
            } else {
                modalTransfer.formError = false;
                $uibModalInstance.close();

                 let CREATE_TRANSFER_PATH = apiBaseURL + "create-transfer"

                                let createTransferData = $.param({
                                    cusipTr: modalTransfer.form.cusipTr,
                                    transferBuyer : "O=SecurityBuyer,L=New York,C=US"
                                });

                                let createTransferHeaders = {
                                    headers : {
                                        "Content-Type": "application/x-www-form-urlencoded"
                                    }
                                };

                                // Creates Transfer and handles success / fail responses.
                                $http.post(CREATE_TRANSFER_PATH, createTransferData, createTransferHeaders).then(
                                    modalTransfer.displayMessage,
                                    modalTransfer.displayMessage
                                );
            }
        };

    modalTransfer.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create Transaction modal dialogue.
    modalTransfer.cancel = () => $uibModalInstance.dismiss();

    // Validate the Transaction. ToDo See buyer stuff
    function invalidFormInput() {
        return (modalTransfer.form.cusipTr === undefined);
    }
});

app.controller('ConfirmCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalConfirm = this;

    modalConfirm.peers = peers;
    modalConfirm.form = {};
    modalConfirm.formError = false;

        // Validates and sends IOU.
        modalConfirm.create = function validateAndSendTransaction() {
            if (modalConfirm.form.value <= 0) {
               modalConfirm.formError = true;
            } else {
                modalConfirm.formError = false;
                $uibModalInstance.close();

                 let CREATE_CONFIRM_PATH = apiBaseURL + "create-confirm"

                                let createConfirmData = $.param({
                                    linearId: modalConfirm.form.linearId,
                                    clearingHouse : "O=ClearingHouse,L=New York,C=US"
                                });

                                let createConfirmHeaders = {
                                    headers : {
                                        "Content-Type": "application/x-www-form-urlencoded"
                                    }
                                };

                                // Create Transaction and handles success / fail responses.
                                $http.post(CREATE_CONFIRM_PATH, createConfirmData, createConfirmHeaders).then(
                                    modalConfirm.displayMessage,
                                    modalConfirm.displayMessage
                                );
            }
        };



    modalConfirm.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create Transaction modal dialogue.
    modalConfirm.cancel = () => $uibModalInstance.dismiss();

    // Validate the Transaction. ToDo See buyer stuff
    function invalidFormInput() {
        return (modalConfirm.form.linearId === undefined);
    }
});

app.controller('ModalIssueCashCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalIssueCash = this;

    modalIssueCash.peers = peers;
    modalIssueCash.form = {};
    modalIssueCash.formError = false;



       // Validates and sends Transfer confirmation.
       modalIssueCash.create = function validateAndSendTransaction(){
            if (modalIssueCash.form.value <= 0) {
                modalIssueCash.formError = true;
            } else {
                modalIssueCash.formError = false;

                $uibModalInstance.close();

                let CREATE_ISSUE_PATH = apiBaseURL + 'create-issue';

                let createIssueData = $.param({
                       amount : modalIssueCash.form.amount,
                       issuerBank : "1234",
                       notary : "O=Notary,L=New York,C=US"
                 });

                 let createIssueHeaders = {
                         headers : {
                             "Content-Type": "application/x-www-form-urlencoded"
                               }
                       };

                  $http.post(CREATE_ISSUE_PATH,createIssueData,createIssueHeaders).then(
                                                   modalIssueCash.displayMessage,
                                                   modalIssueCash.displayMessage
                                                );


                                // Create Transaction and handles success / fail responses.

            }
        };

    modalIssueCash.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create Transaction modal dialogue.
    modalIssueCash.cancel = () => $uibModalInstance.dismiss();

    // Validate the Transaction.
    function invalidFormInput() {
        return (modalIssueCash.form.amount === undefined);
    }
});

app.controller('ModalClearCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalClear = this;

    modalClear.peers = peers;
    modalClear.form = {};
    modalClear.formError = false;



        // Runs Clearing house validation
        modalClear.create = function validateAndSendTransaction(){
            if (modalClear.form.value <= 0) {
                modalClear.formError = true;
            } else {
                 modalClear.formError = false;


                $uibModalInstance.close();

                 let CREATE_CLEAR_PATH = apiBaseURL + "create-clear"

                 let createClearData = $.param({
                       linearId : modalClear.form.linearId
                        });

                 let createClearHeaders = {
                          headers : {
                             "Content-Type": "application/x-www-form-urlencoded"
                               }
                   };

                // Create Transaction and handles success / fail responses.
                 $http.post(CREATE_CLEAR_PATH, createClearData, createClearHeaders).then(
                             modalClear.displayMessage,
                             modalClear.displayMessage
                    );

            }
        };

    modalClear.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create Clearing house modal dialogue.
    modalClear.cancel = () => $uibModalInstance.dismiss();

    // Validate
    function invalidFormInput() {
        return (modalClear.form.linearId === undefined);
    }
});


