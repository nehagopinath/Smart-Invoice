<h1>Smart-Invoice

Smart invoice system using Corda


<h2> Steps to start the Application

1. To build: 
./gradlew deployNodes
2. To run CORDA nodes:
./build/nodes/runnodes
OR
cd ./build/nodes/"node you want to run manually"
java -jar corda.jar


<h2> Steps to execute the Application


a.	Create invoice

1. To create the invoice of 10000 USD , execute the following command in the interactive shell of Seller party -
flow start com.template.cordapp.seller.flows.CreateAssetStateFlow$Initiator cusip: "CUSIP222", assetName: "Ship 25%", purchaseCost: $10000

2. To verify invoice created - 
run vaultQuery contractStateType: com.template.cordapp.state.Asset

b.	Create invoice transfer

1. To create the transfer to the Buyer party –
flow start com.template.cordapp.seller.flows.CreateAssetTransferRequestInitiatorFlow cusip: "CUSIP222", securityBuyer: "O=SecurityBuyer,L=New York,C=US"

2. To verify if the transfer was initiated execute the following in both Seller and Buyer shells (the transaction involves both the parties so the invoice transfer state should be updated on both parties)–
run vaultQuery contractStateType: com.template.cordapp.state.AssetTransfer

c.	Confirm invoice transfer

1. Every transaction that is created by the corda internally receives a ” linearId” – a unique identifier used to identify the transaction. Copy the linearID field from the previous command. To confirm the transfer, substitute the “lin-id” with the linearID that was copied in the command below and execute the flow in Buyer shell. After this flow execution you can see that the transfer status changed from “pending confirmation” to “Pending” –
flow start com.template.cordapp.buyer.flows.ConfirmAssetTransferRequestInitiatorFlow linearId: "<<lin-id>>", clearingHouse: "O=ClearingHouse,L=New York,C=US"

d.	Invoice settlement:
1. To settle the invoice, we now have to issue some dummy cash tokens to Buyer party (This step is standard flow that is provided by the CORDA for demonstrating every POC. But in real time this could be a bypassed by adding a separate Bank node and using the cash tokens from that node). To issue 20000 USD to buyer party with authorization by the Notary execute the following in Buyer shell -
flow start net.corda.finance.flows.CashIssueFlow amount: $20000, issuerBankPartyRef: 1234, notary: "O=Notary,L=New York,C=US" 

2. Now for the settlement, clearing node is authorized by the Buyer to transfer the money after verifying the invoice according to the contract specification. Execute the following flow in clearing node by replacing the <<lin-id>> with the linearID that was copied earlier –
flow start com.template.cordapp.clearinghouse.flows.AssetSettlementInitiatorFlow linearId: "<<lin-id>>"
After this flow, transfer is expected to be complete and the ledger is updated with this transaction and the state of the invoice with new owner.

3. At this point if the application executed as expected, the Seller party will now have the Money in its CashState. To verify execute the following on Seller party shell –
run vaultQuery contractStateType: net.corda.finance.contracts.asset.Cash$State

4. To check the state of the invoice and the owner –
run vaultQuery contractStateType: com.template.cordapp.state.Asset


<h2> Run the application UI

To run the front end (execute the following commands in 3 different shells):
1. ./gradlew runPartySellerServer
2. ./gradlew runPartyBuyerServer
3. ./gradlew runPartyClearServer

Upon running the above commands, the port that is opened is displayed in the console logs. Note the port (ex: 50006) and open the chrome browser and type the following on the browser’s url bar –
1. To run seller party web page
localhost:<port-number>/index.html
2. To run buyer party web page
localhost:<port-number>/client.html
3. To run clearing node web page (This can automated, due to time scoping of the project this is left as one of improvements that can be done further)
Localhost:<port-number>/clear.html


    
