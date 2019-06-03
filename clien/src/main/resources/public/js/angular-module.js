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

    $http.get(apiBaseURL + "me").then((response) => idpApp.thisNode = response.data.me);

    $http.get(apiBaseURL + "peers").then((response) => peers = response.data.peers);

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

             idpApp.openClearTransfer = () => {
                               const modalConfirm = $uibModal.open({
                                   templateUrl: 'idpAppClear.html',
                                   controller: 'ModalClearCtrl',
                                   controllerAs: 'modalClear',
                                   resolve: {
                                       idpApp: () => idpApp,
                                       apiBaseURL: () => apiBaseURL,
                                       peers: () => peers
                                   }
                               });

                               modalConfirm.result.then(() => {}, () => {});
                           };

    idpApp.getTransactions = () => $http.get(apiBaseURL + "transactions")
        .then((response) => idpApp.transactions = Object.keys(response.data)
            .map((key) => response.data[key].state.data)
            .reverse());

     idpApp.getTransfers = () => $http.get(apiBaseURL + "transfers")
             .then((response) => idpApp.transfers = Object.keys(response.data)
                 .map((key) => response.data[key].state.data)
                 .reverse());
       idpApp.getCash = () => $http.get(apiBaseURL + "cash")
               .then((response) => idpApp.cash = Object.keys(response.data)
                     .map((key) => response.data[key].state.data)
                      .reverse());

    /*idpApp.getMyTransactions = () => $http.get(apiBaseURL + "my-transactions")
        .then((response) => idpApp.mytransactions = Object.keys(response.data)
            .map((key) => response.data[key].state.data)
            .reverse());*/

    idpApp.getTransactions();
    idpApp.getTransfers();
    idpApp.getCash();
    //idpApp.getMyTransactions();
});

app.controller('ModalInstanceCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalInstance = this;

    modalInstance.peers = peers;
    modalInstance.form = {};
    modalInstance.formError = false;

        // Validates and sends IOU.
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

    // Validate the Transaction. ToDo See buyer stuff
    function invalidFormInput() {
        return (modalInstance.form.cusip === undefined);
    }
});

 //isNaN(modalInstance.form.purchaseCost) ||
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

        // Validates and sends IOU.
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

                                // Create Transaction and handles success / fail responses.
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



        // Validates and sends IOU.
       modalIssueCash.create = () => {
            if (modalIssueCash.form.value <= 0) {
                modalIssueCash.formError = true;
            } else {
                modalIssueCash.formError = false;

                 /*amount : modalIssueCash.form.amount;
                 issuerBank : "1234";
                 notary : "O=Notary,L=New York,C=US";*/

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

    // Validate the Transaction. ToDo See buyer stuff
    function invalidFormInput() {
        return (modalIssueCash.form.amount === undefined);
    }
});

app.controller('ModalClearCtrl', function ($http, $location, $uibModalInstance, $uibModal, idpApp, apiBaseURL, peers) {
    const modalClear = this;

    modalClear.peers = peers;
    modalClear.form = {};
    modalClear.formError = false;



        // Validates and sends IOU.
        modalClear.create = () => {
            if (modalClear.form.value <= 0) {
                modalClear.formError = true;
            } else {
                 modalClear.formError = false;

                 /*amount : modalIssueCash.form.amount;
                 issuerBank : "1234";
                 notary : "O=Notary,L=New York,C=US";*/

                $uibModalInstance.close();

                 let CREATE_CLEAR_PATH = apiBaseURL + "create-clear"

                                             let createClearData = $.param({
                                                   linearrId: modalClear.form.linearrId,
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


                                // Create Transaction and handles success / fail responses.

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

    // Close create Transaction modal dialogue.
    modalClear.cancel = () => $uibModalInstance.dismiss();

    // Validate the Transaction. ToDo See buyer stuff
    function invalidFormInput() {
        return (modalClear.form.linerrId === undefined);
    }
});


